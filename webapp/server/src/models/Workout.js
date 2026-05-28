import mongoose from 'mongoose';

const exerciseSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  sets: Number,
  reps: Number,
  weight: Number,
  duration: Number, // in seconds
  distance: Number, // in meters
  rest: Number, // rest time in seconds
  notes: String,
  completed: {
    type: Boolean,
    default: false
  }
});

const workoutSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  name: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ['strength', 'cardio', 'flexibility', 'sports', 'general'],
    default: 'general'
  },
  modality: {
    type: String,
    enum: [
      'weight-training',
      'calisthenics',
      'running',
      'cycling',
      'swimming',
      'hiking',
      'yoga',
      'boxing',
      'crossfit',
      'pilates',
      'rowing',
      'martial-arts',
      'general'
    ],
    default: 'general'
  },
  muscleGroups: [{
    type: String,
    enum: ['chest', 'back', 'shoulders', 'arms', 'legs', 'abs', 'full-body']
  }],
  exercises: [exerciseSchema],
  duration: {
    type: Number, // in minutes
    default: 0
  },
  calories: {
    type: Number,
    default: 0
  },
  distance: {
    type: Number, // in meters
    default: 0
  },
  heartRate: {
    avg: Number,
    max: Number,
    min: Number,
    zones: {
      warmup: Number,
      fatBurn: Number,
      cardio: Number,
      peak: Number
    }
  },
  intensity: {
    type: String,
    enum: ['low', 'moderate', 'high', 'extreme'],
    default: 'moderate'
  },
  notes: String,
  date: {
    type: Date,
    default: Date.now,
    index: true
  },
  startTime: Date,
  endTime: Date,
  completed: {
    type: Boolean,
    default: false
  },
  rating: {
    type: Number,
    min: 1,
    max: 5
  },
  location: {
    type: String
  },
  weather: {
    temperature: Number,
    conditions: String
  },
  equipment: [String],
  tags: [String],
  isTemplate: {
    type: Boolean,
    default: false
  },
  templateId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Workout'
  }
}, {
  timestamps: true
});

// Indexes for better query performance
workoutSchema.index({ userId: 1, date: -1 });
workoutSchema.index({ userId: 1, modality: 1 });
workoutSchema.index({ userId: 1, completed: 1 });

// Virtual for workout duration in hours
workoutSchema.virtual('durationHours').get(function() {
  return this.duration / 60;
});

// Method to calculate total volume (for strength training)
workoutSchema.methods.calculateVolume = function() {
  return this.exercises.reduce((total, exercise) => {
    if (exercise.sets && exercise.reps && exercise.weight) {
      return total + (exercise.sets * exercise.reps * exercise.weight);
    }
    return total;
  }, 0);
};

// Method to calculate workout intensity score
workoutSchema.methods.calculateIntensityScore = function() {
  let score = 0;
  
  // Factor in duration
  score += Math.min(this.duration / 60, 2) * 20; // Max 40 points for 2+ hours
  
  // Factor in calories
  score += Math.min(this.calories / 500, 1) * 30; // Max 30 points for 500+ calories
  
  // Factor in heart rate
  if (this.heartRate && this.heartRate.avg) {
    const hrPercentage = this.heartRate.avg / 180; // Assuming max HR of 180
    score += hrPercentage * 30; // Max 30 points
  }
  
  return Math.round(score);
};

// Static method to get user's workout summary
workoutSchema.statics.getUserSummary = async function(userId, days = 30) {
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - days);
  
  const workouts = await this.find({
    userId,
    date: { $gte: startDate },
    completed: true
  });
  
  return {
    totalWorkouts: workouts.length,
    totalDuration: workouts.reduce((sum, w) => sum + w.duration, 0),
    totalCalories: workouts.reduce((sum, w) => sum + w.calories, 0),
    totalDistance: workouts.reduce((sum, w) => sum + w.distance, 0),
    avgDuration: workouts.length > 0 
      ? Math.round(workouts.reduce((sum, w) => sum + w.duration, 0) / workouts.length)
      : 0,
    modalityBreakdown: workouts.reduce((acc, w) => {
      acc[w.modality] = (acc[w.modality] || 0) + 1;
      return acc;
    }, {})
  };
};

const Workout = mongoose.model('Workout', workoutSchema);

export default Workout;
