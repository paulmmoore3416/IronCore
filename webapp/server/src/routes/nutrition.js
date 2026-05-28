import express from 'express';
import Meal from '../models/Meal.js';
import { authenticateToken } from '../middleware/auth.js';
import axios from 'axios';

const router = express.Router();

// Get all meals for user
router.get('/meals', authenticateToken, async (req, res) => {
  try {
    const { date, mealTime } = req.query;
    
    let query = { userId: req.user.id };
    
    if (date) {
      const startOfDay = new Date(date);
      startOfDay.setHours(0, 0, 0, 0);
      const endOfDay = new Date(date);
      endOfDay.setHours(23, 59, 59, 999);
      query.date = { $gte: startOfDay, $lte: endOfDay };
    } else {
      // Default to today
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const tomorrow = new Date(today);
      tomorrow.setDate(tomorrow.getDate() + 1);
      query.date = { $gte: today, $lt: tomorrow };
    }
    
    if (mealTime) {
      query.mealTime = mealTime;
    }
    
    const meals = await Meal.find(query).sort({ date: -1, mealTime: 1 });
    
    res.json({ meals });
  } catch (error) {
    console.error('Error fetching meals:', error);
    res.status(500).json({ error: 'Failed to fetch meals' });
  }
});

// Get nutrition stats
router.get('/stats', authenticateToken, async (req, res) => {
  try {
    const today = new Date();
    const dailySummary = await Meal.getDailySummary(req.user.id, today);
    
    // Get user's targets (these should come from user profile in production)
    const targets = {
      calories: 2500,
      protein: 150,
      carbs: 250,
      fat: 80
    };
    
    const stats = {
      dailyCalories: Math.round(dailySummary.totalCalories),
      dailyProtein: Math.round(dailySummary.totalProtein),
      dailyCarbs: Math.round(dailySummary.totalCarbs),
      dailyFat: Math.round(dailySummary.totalFat),
      targetCalories: targets.calories,
      targetProtein: targets.protein,
      targetCarbs: targets.carbs,
      targetFat: targets.fat,
      mealCount: dailySummary.mealCount
    };
    
    res.json({ stats });
  } catch (error) {
    console.error('Error fetching nutrition stats:', error);
    res.status(500).json({ error: 'Failed to fetch nutrition stats' });
  }
});

// Get meal by ID
router.get('/meals/:id', authenticateToken, async (req, res) => {
  try {
    const meal = await Meal.findOne({
      _id: req.params.id,
      userId: req.user.id
    });
    
    if (!meal) {
      return res.status(404).json({ error: 'Meal not found' });
    }
    
    res.json({ meal });
  } catch (error) {
    console.error('Error fetching meal:', error);
    res.status(500).json({ error: 'Failed to fetch meal' });
  }
});

// Create new meal
router.post('/meals', authenticateToken, async (req, res) => {
  try {
    const {
      name,
      mealTime,
      calories,
      protein,
      carbs,
      fat,
      fiber,
      sugar,
      sodium,
      ingredients,
      recipe,
      prepTime,
      cookTime,
      servings,
      imageUrl,
      cuisine,
      dietaryTags,
      date
    } = req.body;
    
    const meal = await Meal.create({
      userId: req.user.id,
      name,
      mealTime: mealTime || 'breakfast',
      calories: calories || 0,
      protein: protein || 0,
      carbs: carbs || 0,
      fat: fat || 0,
      fiber,
      sugar,
      sodium,
      ingredients: ingredients || [],
      recipe,
      prepTime,
      cookTime,
      servings: servings || 1,
      imageUrl,
      cuisine,
      dietaryTags: dietaryTags || [],
      date: date || new Date(),
      consumed: false
    });
    
    res.status(201).json({ meal, message: 'Meal created successfully' });
  } catch (error) {
    console.error('Error creating meal:', error);
    res.status(500).json({ error: 'Failed to create meal' });
  }
});

// Update meal
router.put('/meals/:id', authenticateToken, async (req, res) => {
  try {
    const meal = await Meal.findOneAndUpdate(
      { _id: req.params.id, userId: req.user.id },
      { $set: req.body },
      { new: true, runValidators: true }
    );
    
    if (!meal) {
      return res.status(404).json({ error: 'Meal not found' });
    }
    
    res.json({ meal, message: 'Meal updated successfully' });
  } catch (error) {
    console.error('Error updating meal:', error);
    res.status(500).json({ error: 'Failed to update meal' });
  }
});

// Delete meal
router.delete('/meals/:id', authenticateToken, async (req, res) => {
  try {
    const meal = await Meal.findOneAndDelete({
      _id: req.params.id,
      userId: req.user.id
    });
    
    if (!meal) {
      return res.status(404).json({ error: 'Meal not found' });
    }
    
    res.json({ message: 'Meal deleted successfully' });
  } catch (error) {
    console.error('Error deleting meal:', error);
    res.status(500).json({ error: 'Failed to delete meal' });
  }
});

// Mark meal as consumed
router.post('/meals/:id/consume', authenticateToken, async (req, res) => {
  try {
    const meal = await Meal.findOneAndUpdate(
      { _id: req.params.id, userId: req.user.id },
      { 
        $set: { 
          consumed: true,
          consumedAt: new Date()
        }
      },
      { new: true }
    );
    
    if (!meal) {
      return res.status(404).json({ error: 'Meal not found' });
    }
    
    res.json({ meal, message: 'Meal marked as consumed' });
  } catch (error) {
    console.error('Error marking meal as consumed:', error);
    res.status(500).json({ error: 'Failed to mark meal as consumed' });
  }
});

// Generate AI meal plan
router.post('/generate-plan', authenticateToken, async (req, res) => {
  try {
    const { cuisine = 'American', dietaryPreference = 'Standard' } = req.body;
    
    // Use Ollama for AI generation
    const ollamaUrl = process.env.OLLAMA_BASE_URL || 'http://localhost:11434';
    
    const prompt = `Generate a complete daily meal plan with 7 meals for ${cuisine} cuisine following a ${dietaryPreference} diet.

For each meal, provide:
1. Meal name
2. Meal time (breakfast, morning-snack, lunch, afternoon-snack, dinner, evening-snack, or post-workout)
3. Calories (realistic number)
4. Protein in grams
5. Carbs in grams
6. Fat in grams
7. List of 5-8 main ingredients
8. Brief recipe instructions (2-3 sentences)
9. Prep time in minutes
10. Cook time in minutes

Format as JSON array with this structure:
[
  {
    "name": "Meal Name",
    "mealTime": "breakfast",
    "calories": 450,
    "protein": 25,
    "carbs": 50,
    "fat": 15,
    "ingredients": ["ingredient1", "ingredient2"],
    "recipe": "Brief instructions",
    "prepTime": 10,
    "cookTime": 15
  }
]

Make meals realistic, balanced, and appropriate for the meal time. Ensure total daily calories are around 2200-2500.`;

    const aiResponse = await axios.post(`${ollamaUrl}/api/generate`, {
      model: 'llama2',
      prompt: prompt,
      stream: false
    });
    
    let mealsData = [];
    try {
      const responseText = aiResponse.data.response;
      const jsonMatch = responseText.match(/\[[\s\S]*\]/);
      if (jsonMatch) {
        mealsData = JSON.parse(jsonMatch[0]);
      }
    } catch (parseError) {
      console.error('Error parsing AI response:', parseError);
      // Fallback to sample meals
      mealsData = generateSampleMeals(cuisine, dietaryPreference);
    }
    
    // Fetch images from Unsplash for each meal
    const unsplashAccessKey = process.env.UNSPLASH_ACCESS_KEY;
    
    // Create meals in database
    const createdMeals = [];
    for (const mealData of mealsData) {
      let imageUrl = null;
      
      if (unsplashAccessKey) {
        try {
          const imageResponse = await axios.get('https://api.unsplash.com/search/photos', {
            params: {
              query: `${mealData.name} food`,
              per_page: 1,
              orientation: 'landscape'
            },
            headers: {
              'Authorization': `Client-ID ${unsplashAccessKey}`
            }
          });
          
          if (imageResponse.data.results && imageResponse.data.results.length > 0) {
            imageUrl = imageResponse.data.results[0].urls.regular;
          }
        } catch (imageError) {
          console.error('Error fetching image:', imageError.message);
        }
      }
      
      const meal = await Meal.create({
        userId: req.user.id,
        name: mealData.name,
        mealTime: mealData.mealTime,
        calories: mealData.calories,
        protein: mealData.protein,
        carbs: mealData.carbs,
        fat: mealData.fat,
        ingredients: mealData.ingredients || [],
        recipe: mealData.recipe,
        prepTime: mealData.prepTime,
        cookTime: mealData.cookTime,
        servings: 1,
        imageUrl,
        cuisine,
        dietaryTags: [dietaryPreference.toLowerCase()],
        date: new Date(),
        consumed: false,
        isAIGenerated: true
      });
      
      createdMeals.push(meal);
    }
    
    res.json({ 
      meals: createdMeals,
      message: `Generated ${createdMeals.length} meals for ${cuisine} cuisine`
    });
  } catch (error) {
    console.error('Error generating meal plan:', error);
    res.status(500).json({ error: 'Failed to generate meal plan' });
  }
});

// Get weekly nutrition trends
router.get('/trends/weekly', authenticateToken, async (req, res) => {
  try {
    const trends = await Meal.getWeeklyTrends(req.user.id);
    res.json({ trends });
  } catch (error) {
    console.error('Error fetching weekly trends:', error);
    res.status(500).json({ error: 'Failed to fetch weekly trends' });
  }
});

// Helper function to generate sample meals
function generateSampleMeals(cuisine, dietaryPreference) {
  const mealTemplates = {
    breakfast: [
      { name: 'Protein Pancakes', calories: 450, protein: 30, carbs: 50, fat: 12 },
      { name: 'Egg White Omelette', calories: 350, protein: 35, carbs: 15, fat: 18 },
      { name: 'Greek Yogurt Bowl', calories: 400, protein: 25, carbs: 45, fat: 15 }
    ],
    lunch: [
      { name: 'Grilled Chicken Salad', calories: 550, protein: 45, carbs: 35, fat: 22 },
      { name: 'Turkey Wrap', calories: 500, protein: 40, carbs: 45, fat: 18 },
      { name: 'Quinoa Bowl', calories: 480, protein: 25, carbs: 60, fat: 15 }
    ],
    dinner: [
      { name: 'Salmon with Vegetables', calories: 600, protein: 50, carbs: 40, fat: 25 },
      { name: 'Lean Steak & Sweet Potato', calories: 650, protein: 55, carbs: 50, fat: 22 },
      { name: 'Chicken Stir Fry', calories: 580, protein: 48, carbs: 55, fat: 18 }
    ]
  };
  
  return [
    { ...mealTemplates.breakfast[0], mealTime: 'breakfast', ingredients: ['eggs', 'protein powder', 'banana', 'oats'], recipe: 'Mix ingredients and cook on griddle.', prepTime: 5, cookTime: 10 },
    { ...mealTemplates.lunch[0], mealTime: 'lunch', ingredients: ['chicken breast', 'mixed greens', 'tomatoes', 'cucumber', 'olive oil'], recipe: 'Grill chicken and toss with fresh vegetables.', prepTime: 10, cookTime: 15 },
    { ...mealTemplates.dinner[0], mealTime: 'dinner', ingredients: ['salmon fillet', 'broccoli', 'asparagus', 'lemon', 'garlic'], recipe: 'Bake salmon with vegetables and seasonings.', prepTime: 10, cookTime: 20 },
    { name: 'Protein Shake', mealTime: 'morning-snack', calories: 250, protein: 30, carbs: 20, fat: 8, ingredients: ['protein powder', 'banana', 'almond milk'], recipe: 'Blend all ingredients.', prepTime: 2, cookTime: 0 },
    { name: 'Apple with Almond Butter', mealTime: 'afternoon-snack', calories: 200, protein: 5, carbs: 25, fat: 10, ingredients: ['apple', 'almond butter'], recipe: 'Slice apple and serve with almond butter.', prepTime: 2, cookTime: 0 },
    { name: 'Greek Yogurt', mealTime: 'evening-snack', calories: 150, protein: 15, carbs: 12, fat: 5, ingredients: ['greek yogurt', 'berries'], recipe: 'Top yogurt with fresh berries.', prepTime: 2, cookTime: 0 },
    { name: 'Post-Workout Shake', mealTime: 'post-workout', calories: 300, protein: 40, carbs: 30, fat: 5, ingredients: ['whey protein', 'banana', 'oats', 'water'], recipe: 'Blend all ingredients for recovery.', prepTime: 3, cookTime: 0 }
  ];
}

export default router;