import User from '../models/User.js';
import Metric from '../models/Metric.js';

// Store active connections
const activeConnections = new Map();

export const initializeSocketHandlers = (io, logger) => {
  io.on('connection', (socket) => {
    logger.info(`🔌 Client connected: ${socket.id}`);
    
    // Authentication
    socket.on('authenticate', async (data) => {
      try {
        const { userId, deviceId, deviceType, deviceName } = data;
        
        if (!userId || !deviceId) {
          socket.emit('auth_error', { message: 'Missing authentication data' });
          return;
        }
        
        // Find user and update device status
        const user = await User.findById(userId);
        if (!user) {
          socket.emit('auth_error', { message: 'User not found' });
          return;
        }
        
        // Add or update device
        await user.addOrUpdateDevice({
          deviceId,
          deviceType,
          deviceName,
          isOnline: true
        });
        
        // Store connection
        socket.userId = userId;
        socket.deviceId = deviceId;
        activeConnections.set(socket.id, { userId, deviceId, deviceType });
        
        // Join user's room for targeted broadcasts
        socket.join(`user:${userId}`);
        
        // Notify all user's devices about device status change
        io.to(`user:${userId}`).emit('device_status_changed', {
          devices: user.devices.map(d => ({
            deviceId: d.deviceId,
            deviceType: d.deviceType,
            deviceName: d.deviceName,
            isOnline: d.isOnline,
            lastSeen: d.lastSeen
          }))
        });
        
        socket.emit('authenticated', {
          message: 'Successfully authenticated',
          deviceId,
          connectedDevices: user.getConnectedDevices().length
        });
        
        logger.info(`✅ Device authenticated: ${deviceType} (${deviceId}) for user ${userId}`);
      } catch (error) {
        logger.error('Authentication error:', error);
        socket.emit('auth_error', { message: 'Authentication failed' });
      }
    });
    
    // Sync metrics from device
    socket.on('sync_metrics', async (data) => {
      try {
        const { userId, metrics } = data;
        
        if (!userId || !Array.isArray(metrics)) {
          socket.emit('sync_error', { message: 'Invalid sync data' });
          return;
        }
        
        // Batch insert metrics
        const metricsToInsert = metrics.map(m => ({
          ...m,
          userId,
          deviceId: socket.deviceId,
          source: activeConnections.get(socket.id)?.deviceType || 'unknown',
          synced: true,
          syncedAt: new Date()
        }));
        
        const result = await Metric.insertMany(metricsToInsert, { ordered: false });
        
        // Broadcast to all user's devices
        io.to(`user:${userId}`).emit('metrics_updated', {
          count: result.length,
          types: [...new Set(metrics.map(m => m.type))],
          timestamp: new Date()
        });
        
        socket.emit('sync_success', {
          synced: result.length,
          timestamp: new Date()
        });
        
        logger.info(`📊 Synced ${result.length} metrics for user ${userId}`);
      } catch (error) {
        logger.error('Sync error:', error);
        socket.emit('sync_error', { message: 'Sync failed' });
      }
    });
    
    // Real-time metric update (single metric)
    socket.on('metric_update', async (data) => {
      try {
        const { userId, metric } = data;
        
        if (!userId || !metric) {
          return;
        }
        
        // Save metric
        const newMetric = await Metric.create({
          ...metric,
          userId,
          deviceId: socket.deviceId,
          source: activeConnections.get(socket.id)?.deviceType || 'unknown',
          synced: true,
          syncedAt: new Date()
        });
        
        // Broadcast to all user's devices except sender
        socket.to(`user:${userId}`).emit('metric_received', {
          metric: newMetric,
          source: socket.deviceId
        });
        
        logger.debug(`📈 Real-time metric update: ${metric.type} for user ${userId}`);
      } catch (error) {
        logger.error('Metric update error:', error);
      }
    });
    
    // Heartbeat to keep connection alive
    socket.on('heartbeat', () => {
      socket.emit('heartbeat_ack', { timestamp: new Date() });
    });
    
    // Request device status
    socket.on('get_device_status', async () => {
      try {
        if (!socket.userId) {
          socket.emit('device_status_error', { message: 'Not authenticated' });
          return;
        }
        
        const user = await User.findById(socket.userId);
        if (user) {
          socket.emit('device_status', {
            devices: user.devices.map(d => ({
              deviceId: d.deviceId,
              deviceType: d.deviceType,
              deviceName: d.deviceName,
              isOnline: d.isOnline,
              lastSeen: d.lastSeen
            }))
          });
        }
      } catch (error) {
        logger.error('Get device status error:', error);
        socket.emit('device_status_error', { message: 'Failed to get device status' });
      }
    });
    
    // Disconnect handling
    socket.on('disconnect', async () => {
      try {
        const connection = activeConnections.get(socket.id);
        
        if (connection) {
          const { userId, deviceId } = connection;
          
          // Update device status
          const user = await User.findById(userId);
          if (user) {
            await user.updateDeviceStatus(deviceId, false);
            
            // Notify other devices
            io.to(`user:${userId}`).emit('device_status_changed', {
              devices: user.devices.map(d => ({
                deviceId: d.deviceId,
                deviceType: d.deviceType,
                deviceName: d.deviceName,
                isOnline: d.isOnline,
                lastSeen: d.lastSeen
              }))
            });
          }
          
          activeConnections.delete(socket.id);
          logger.info(`🔌 Device disconnected: ${deviceId} for user ${userId}`);
        } else {
          logger.info(`🔌 Client disconnected: ${socket.id}`);
        }
      } catch (error) {
        logger.error('Disconnect handling error:', error);
      }
    });
    
    // Error handling
    socket.on('error', (error) => {
      logger.error('Socket error:', error);
    });
  });
  
  // Periodic cleanup of stale connections
  setInterval(async () => {
    try {
      const staleThreshold = new Date(Date.now() - 5 * 60 * 1000); // 5 minutes
      
      const users = await User.find({
        'devices.isOnline': true,
        'devices.lastSeen': { $lt: staleThreshold }
      });
      
      for (const user of users) {
        let updated = false;
        user.devices.forEach(device => {
          if (device.isOnline && device.lastSeen < staleThreshold) {
            device.isOnline = false;
            updated = true;
          }
        });
        
        if (updated) {
          await user.save();
          io.to(`user:${user._id}`).emit('device_status_changed', {
            devices: user.devices.map(d => ({
              deviceId: d.deviceId,
              deviceType: d.deviceType,
              deviceName: d.deviceName,
              isOnline: d.isOnline,
              lastSeen: d.lastSeen
            }))
          });
        }
      }
    } catch (error) {
      logger.error('Cleanup error:', error);
    }
  }, 60000); // Run every minute
  
  logger.info('✅ Socket.IO handlers initialized');
};

export const getActiveConnections = () => {
  return Array.from(activeConnections.values());
};

export const getConnectionCount = () => {
  return activeConnections.size;
};
