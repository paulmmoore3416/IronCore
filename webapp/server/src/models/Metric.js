import mongoose from 'mongoose';

const metricSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  
  // Metric type
  type: {
    type: String,
    required: true,
    enum: [
      'steps',
      'heart_rate',
      'weight',
      'active_calories',
      'sleep',
      'hydration',
      'spo2',
      'respiratory_rate',
      'blood_pressure',
      'body_temperature',
      'workout_session',
      'recovery_score'
    ],
    index: true
  },
  
  // Metric value (flexible for different types)
  value: {
    type: mongoose.Schema.Types.Mixed,
    required: true
  },
  
  // Unit of measurement
  unit: String,
  
  // Timestamp of the metric
  timestamp: {
    type: Date,
    required: true,
    index: true
  },
  
  // Source of the data
  source: {
    type: String,
    enum: ['mobile', 'wearable', 'auto', 'manual', 'google_fit', 'health_connect'],
    default: 'manual'
  },
  
  // Device information
  deviceId: String,
  deviceName: String,
  
  // Additional metadata
  metadata: {
    type: Map,
    of: mongoose.Schema.Types.Mixed
  },
  
  // For workout sessions
  workoutDetails: {
    duration: Number, // in minutes
    exercises: [{
      name: String,
      sets: Number,
      reps: String,
      weight: String,
      rest: String
    }],
    modality: String,
    intensity: String,
    notes: String
  },
  
  // For sleep data
  sleepDetails: {
    stages: [{
      stage: {
        type: String,
        enum: ['awake', 'light', 'deep', 'rem']
      },
      duration: Number, // in minutes
      startTime: Date,
      endTime: Date
    }],
    quality: Number, // 0-100
    interruptions: Number
  },
  
  // Sync status
  synced: {
    type: Boolean,
    default: false
  },
  syncedAt: Date,
  
  createdAt: {
    type: Date,
    default: Date.now,
    index: true
  }
}, {
  timestamps: true
});

// Compound indexes for efficient queries
metricSchema.index({ userId: 1, type: 1, timestamp: -1 });
metricSchema.index({ userId: 1, timestamp: -1 });
metricSchema.index({ userId: 1, type: 1, createdAt: -1 });

// Static method to get metrics by date range
metricSchema.statics.getByDateRange = function(userId, type, startDate, endDate) {
  return this.find({
    userId,
    type,
    timestamp: {
      $gte: startDate,
      $lte: endDate
    }
  }).sort({ timestamp: 1 });
};

// Static method to get latest metric
metricSchema.statics.getLatest = function(userId, type) {
  return this.findOne({ userId, type }).sort({ timestamp: -1 });
};

// Static method to get daily aggregates
metricSchema.statics.getDailyAggregates = function(userId, type, days = 7) {
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - days);
  
  return this.aggregate([
    {
      $match: {
        userId: new mongoose.Types.ObjectId(userId),
        type,
        timestamp: { $gte: startDate }
      }
    },
    {
      $group: {
        _id: {
          $dateToString: { format: '%Y-%m-%d', date: '$timestamp' }
        },
        avgValue: { $avg: '$value' },
        minValue: { $min: '$value' },
        maxValue: { $max: '$value' },
        count: { $sum: 1 }
      }
    },
    {
      $sort: { _id: 1 }
    }
  ]);
};

// Method to mark as synced
metricSchema.methods.markSynced = function() {
  this.synced = true;
  this.syncedAt = new Date();
  return this.save();
};

export default mongoose.model('Metric', metricSchema);
