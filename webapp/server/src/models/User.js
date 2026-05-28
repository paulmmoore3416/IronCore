import mongoose from 'mongoose';
import bcrypt from 'bcryptjs';

const userSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
    trim: true
  },
  password: {
    type: String,
    required: function() {
      return !this.googleId;
    }
  },
  name: {
    type: String,
    required: true
  },
  googleId: {
    type: String,
    unique: true,
    sparse: true
  },
  profilePicture: String,
  
  // User profile data
  profile: {
    age: Number,
    weight: Number,
    height: Number,
    gender: {
      type: String,
      enum: ['male', 'female', 'other']
    },
    activityLevel: {
      type: String,
      enum: ['sedentary', 'light', 'moderate', 'active', 'very_active']
    },
    goals: [String],
    unitSystem: {
      type: String,
      enum: ['metric', 'imperial'],
      default: 'metric'
    }
  },
  
  // Google integrations
  googleTokens: {
    accessToken: String,
    refreshToken: String,
    expiryDate: Date
  },
  
  googleIntegrations: {
    calendar: { type: Boolean, default: false },
    drive: { type: Boolean, default: false },
    fit: { type: Boolean, default: false },
    keep: { type: Boolean, default: false }
  },
  
  // Device connections
  devices: [{
    deviceId: String,
    deviceType: {
      type: String,
      enum: ['mobile', 'wearable', 'auto']
    },
    deviceName: String,
    lastSeen: Date,
    isOnline: { type: Boolean, default: false }
  }],
  
  // Preferences
  preferences: {
    theme: {
      type: String,
      enum: ['light', 'dark', 'auto'],
      default: 'dark'
    },
    notifications: {
      email: { type: Boolean, default: true },
      push: { type: Boolean, default: true },
      workout: { type: Boolean, default: true },
      nutrition: { type: Boolean, default: true },
      recovery: { type: Boolean, default: true }
    },
    privacy: {
      shareWorkouts: { type: Boolean, default: false },
      shareProgress: { type: Boolean, default: false },
      showOnLeaderboard: { type: Boolean, default: true }
    }
  },
  
  // Subscription/Premium
  subscription: {
    tier: {
      type: String,
      enum: ['free', 'pro', 'elite'],
      default: 'free'
    },
    startDate: Date,
    endDate: Date,
    autoRenew: { type: Boolean, default: false }
  },
  
  lastLogin: Date,
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

// Hash password before saving
userSchema.pre('save', async function(next) {
  if (!this.isModified('password')) return next();
  
  try {
    const salt = await bcrypt.genSalt(10);
    this.password = await bcrypt.hash(this.password, salt);
    next();
  } catch (error) {
    next(error);
  }
});

// Compare password method
userSchema.methods.comparePassword = async function(candidatePassword) {
  return bcrypt.compare(candidatePassword, this.password);
};

// Update device status
userSchema.methods.updateDeviceStatus = function(deviceId, isOnline) {
  const device = this.devices.find(d => d.deviceId === deviceId);
  if (device) {
    device.isOnline = isOnline;
    device.lastSeen = new Date();
  }
  return this.save();
};

// Add or update device
userSchema.methods.addOrUpdateDevice = function(deviceData) {
  const existingDevice = this.devices.find(d => d.deviceId === deviceData.deviceId);
  
  if (existingDevice) {
    Object.assign(existingDevice, deviceData);
    existingDevice.lastSeen = new Date();
  } else {
    this.devices.push({
      ...deviceData,
      lastSeen: new Date(),
      isOnline: true
    });
  }
  
  return this.save();
};

// Get connected devices
userSchema.methods.getConnectedDevices = function() {
  return this.devices.filter(d => d.isOnline);
};

export default mongoose.model('User', userSchema);
