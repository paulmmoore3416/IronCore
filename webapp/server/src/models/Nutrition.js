import mongoose from 'mongoose';

const mealSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  cuisine: String,
  description: String,
  
  // Nutritional information
  nutrition: {
    calories: Number,
    protein: Number,
    carbs: Number,
    fat: Number,
    fiber: Number,
    sugar: Number,
    sodium: Number
  },
  
  // Ingredients
  ingredients: [{
    name: String,
    amount: String,
    unit: String
  }],
  
  // Instructions
  instructions: [String],
  
  // Timing
  prepTime: Number, // in minutes
  cookTime: Number, // in minutes
  totalTime: Number, // in minutes
  
  // Servings
  servings: Number,
  
  // Images
  images: [{
    url: String,
    source: String, // 'unsplash', 'user', 'ai'
    alt: String
  }],
  
  // Tags
  tags: [String],
  dietary: [String], // 'vegetarian', 'vegan', 'gluten-free', etc.
  
  // AI generated flag
  aiGenerated: {
    type: Boolean,
    default: false
  },
  
  // Rating
  rating: {
    type: Number,
    min: 0,
    max: 5
  }
});

const nutritionSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  
  date: {
    type: Date,
    required: true,
    index: true
  },
  
  // Daily goals
  goals: {
    calories: Number,
    protein: Number,
    carbs: Number,
    fat: Number,
    hydration: Number // in ml
  },
  
  // Consumed meals
  meals: [{
    type: {
      type: String,
      enum: ['breakfast', 'lunch', 'dinner', 'snack'],
      required: true
    },
    time: Date,
    meal: mealSchema,
    portionSize: {
      type: Number,
      default: 1
    },
    notes: String
  }],
  
  // Hydration tracking
  hydration: {
    consumed: {
      type: Number,
      default: 0
    },
    logs: [{
      amount: Number,
      time: Date
    }]
  },
  
  // Supplements
  supplements: [{
    name: String,
    dosage: String,
    time: Date,
    notes: String
  }],
  
  // Daily totals (calculated)
  totals: {
    calories: Number,
    protein: Number,
    carbs: Number,
    fat: Number,
    fiber: Number,
    hydration: Number
  },
  
  // Meal plan reference
  mealPlanId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'MealPlan'
  },
  
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

// Compound index for efficient queries
nutritionSchema.index({ userId: 1, date: -1 });

// Calculate daily totals before saving
nutritionSchema.pre('save', function(next) {
  const totals = {
    calories: 0,
    protein: 0,
    carbs: 0,
    fat: 0,
    fiber: 0,
    hydration: this.hydration.consumed || 0
  };
  
  this.meals.forEach(meal => {
    const portion = meal.portionSize || 1;
    if (meal.meal && meal.meal.nutrition) {
      totals.calories += (meal.meal.nutrition.calories || 0) * portion;
      totals.protein += (meal.meal.nutrition.protein || 0) * portion;
      totals.carbs += (meal.meal.nutrition.carbs || 0) * portion;
      totals.fat += (meal.meal.nutrition.fat || 0) * portion;
      totals.fiber += (meal.meal.nutrition.fiber || 0) * portion;
    }
  });
  
  this.totals = totals;
  next();
});

// Static method to get nutrition by date range
nutritionSchema.statics.getByDateRange = function(userId, startDate, endDate) {
  return this.find({
    userId,
    date: {
      $gte: startDate,
      $lte: endDate
    }
  }).sort({ date: -1 });
};

// Static method to get today's nutrition
nutritionSchema.statics.getToday = function(userId) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);
  
  return this.findOne({
    userId,
    date: {
      $gte: today,
      $lt: tomorrow
    }
  });
};

// Method to add meal
nutritionSchema.methods.addMeal = function(mealData) {
  this.meals.push(mealData);
  return this.save();
};

// Method to add hydration
nutritionSchema.methods.addHydration = function(amount) {
  this.hydration.consumed += amount;
  this.hydration.logs.push({
    amount,
    time: new Date()
  });
  return this.save();
};

const mealPlanSchema = new mongoose.Schema({
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
  
  description: String,
  
  // Duration
  startDate: Date,
  endDate: Date,
  duration: Number, // in days
  
  // Daily meal plans
  days: [{
    dayNumber: Number,
    meals: [mealSchema]
  }],
  
  // Goals for this plan
  goals: {
    calories: Number,
    protein: Number,
    carbs: Number,
    fat: Number,
    purpose: String // 'weight_loss', 'muscle_gain', 'maintenance', etc.
  },
  
  // Cuisine preferences
  cuisineType: String,
  dietaryRestrictions: [String],
  
  // AI generated
  aiGenerated: {
    type: Boolean,
    default: false
  },
  aiModel: String,
  
  // Status
  active: {
    type: Boolean,
    default: true
  },
  
  createdAt: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

mealPlanSchema.index({ userId: 1, active: 1, createdAt: -1 });

export const Nutrition = mongoose.model('Nutrition', nutritionSchema);
export const MealPlan = mongoose.model('MealPlan', mealPlanSchema);
