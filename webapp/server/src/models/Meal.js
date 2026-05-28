import mongoose from 'mongoose';

const mealSchema = new mongoose.Schema({
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
  mealTime: {
    type: String,
    enum: [
      'breakfast',
      'morning-snack',
      'lunch',
      'afternoon-snack',
      'dinner',
      'evening-snack',
      'pre-workout',
      'post-workout'
    ],
    required: true,
    index: true
  },
  calories: {
    type: Number,
    required: true,
    min: 0
  },
  protein: {
    type: Number,
    required: true,
    min: 0
  },
  carbs: {
    type: Number,
    required: true,
    min: 0
  },
  fat: {
    type: Number,
    required: true,
    min: 0
  },
  fiber: {
    type: Number,
    default: 0
  },
  sugar: {
    type: Number,
    default: 0
  },
  sodium: {
    type: Number,
    default: 0
  },
  ingredients: [{
    type: String
  }],
  recipe: {
    type: String
  },
  prepTime: {
    type: Number // in minutes
  },
  cookTime: {
    type: Number // in minutes
  },
  servings: {
    type: Number,
    default: 1
  },
  imageUrl: {
    type: String
  },
  cuisine: {
    type: String
  },
  dietaryTags: [{
    type: String,
    enum: [
      'vegetarian',
      'vegan',
      'gluten-free',
      'dairy-free',
      'keto',
      'paleo',
      'low-carb',
      'high-protein',
      'pescatarian'
    ]
  }],
  date: {
    type: Date,
    default: Date.now,
    index: true
  },
  consumed: {
    type: Boolean,
    default: false
  },
  consumedAt: {
    type: Date
  },
  rating: {
    type: Number,
    min: 1,
    max: 5
  },
  notes: {
    type: String
  },
  isAIGenerated: {
    type: Boolean,
    default: false
  }
}, {
  timestamps: true
});

// Indexes for better query performance
mealSchema.index({ userId: 1, date: -1 });
mealSchema.index({ userId: 1, mealTime: 1 });
mealSchema.index({ userId: 1, consumed: 1 });

// Virtual for total macros
mealSchema.virtual('totalMacros').get(function() {
  return this.protein + this.carbs + this.fat;
});

// Method to calculate calories from macros (if not provided)
mealSchema.methods.calculateCalories = function() {
  return (this.protein * 4) + (this.carbs * 4) + (this.fat * 9);
};

// Static method to get daily nutrition summary
mealSchema.statics.getDailySummary = async function(userId, date = new Date()) {
  const startOfDay = new Date(date);
  startOfDay.setHours(0, 0, 0, 0);
  
  const endOfDay = new Date(date);
  endOfDay.setHours(23, 59, 59, 999);
  
  const meals = await this.find({
    userId,
    date: { $gte: startOfDay, $lte: endOfDay },
    consumed: true
  });
  
  return {
    totalCalories: meals.reduce((sum, m) => sum + m.calories, 0),
    totalProtein: meals.reduce((sum, m) => sum + m.protein, 0),
    totalCarbs: meals.reduce((sum, m) => sum + m.carbs, 0),
    totalFat: meals.reduce((sum, m) => sum + m.fat, 0),
    totalFiber: meals.reduce((sum, m) => sum + (m.fiber || 0), 0),
    mealCount: meals.length,
    meals: meals
  };
};

// Static method to get weekly nutrition trends
mealSchema.statics.getWeeklyTrends = async function(userId) {
  const today = new Date();
  const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
  
  const meals = await this.find({
    userId,
    date: { $gte: weekAgo },
    consumed: true
  });
  
  const dailyData = {};
  
  meals.forEach(meal => {
    const dateKey = meal.date.toISOString().split('T')[0];
    if (!dailyData[dateKey]) {
      dailyData[dateKey] = {
        date: dateKey,
        calories: 0,
        protein: 0,
        carbs: 0,
        fat: 0,
        meals: 0
      };
    }
    
    dailyData[dateKey].calories += meal.calories;
    dailyData[dateKey].protein += meal.protein;
    dailyData[dateKey].carbs += meal.carbs;
    dailyData[dateKey].fat += meal.fat;
    dailyData[dateKey].meals += 1;
  });
  
  return Object.values(dailyData).sort((a, b) => 
    new Date(a.date) - new Date(b.date)
  );
};

const Meal = mongoose.model('Meal', mealSchema);

export default Meal;
