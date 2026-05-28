import express from 'express';
import Workout from '../models/Workout.js';
import { authenticateToken } from '../middleware/auth.js';

const router = express.Router();

// Get all workouts for user
router.get('/', authenticateToken, async (req, res) => {
  try {
    const workouts = await Workout.find({ userId: req.user.id })
      .sort({ date: -1 })
      .limit(50);
    
    res.json({ workouts });
  } catch (error) {
    console.error('Error fetching workouts:', error);
    res.status(500).json({ error: 'Failed to fetch workouts' });
  }
});

// Get workout stats
router.get('/stats', authenticateToken, async (req, res) => {
  try {
    const workouts = await Workout.find({ userId: req.user.id });
    
    const stats = {
      totalWorkouts: workouts.length,
      totalDuration: workouts.reduce((sum, w) => sum + (w.duration || 0), 0),
      totalCalories: workouts.reduce((sum, w) => sum + (w.calories || 0), 0),
      weeklyStreak: calculateWeeklyStreak(workouts),
      favoriteModality: getFavoriteModality(workouts),
      weeklyWorkouts: getWeeklyWorkouts(workouts),
      monthlyProgress: getMonthlyProgress(workouts),
    };
    
    res.json({ stats });
  } catch (error) {
    console.error('Error fetching workout stats:', error);
    res.status(500).json({ error: 'Failed to fetch workout stats' });
  }
});

// Get workout by ID
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const workout = await Workout.findOne({
      _id: req.params.id,
      userId: req.user.id
    });
    
    if (!workout) {
      return res.status(404).json({ error: 'Workout not found' });
    }
    
    res.json({ workout });
  } catch (error) {
    console.error('Error fetching workout:', error);
    res.status(500).json({ error: 'Failed to fetch workout' });
  }
});

// Create new workout
router.post('/', authenticateToken, async (req, res) => {
  try {
    const {
      name,
      type,
      modality,
      muscleGroups,
      exercises,
      duration,
      calories,
      distance,
      heartRate,
      notes,
      date
    } = req.body;
    
    const workout = await Workout.create({
      userId: req.user.id,
      name: name || `${type} Workout`,
      type: type || 'general',
      modality: modality || 'general',
      muscleGroups: muscleGroups || [],
      exercises: exercises || [],
      duration: duration || 0,
      calories: calories || 0,
      distance: distance || 0,
      heartRate: heartRate || {},
      notes: notes || '',
      date: date || new Date(),
      completed: true
    });
    
    res.status(201).json({ workout, message: 'Workout created successfully' });
  } catch (error) {
    console.error('Error creating workout:', error);
    res.status(500).json({ error: 'Failed to create workout' });
  }
});

// Update workout
router.put('/:id', authenticateToken, async (req, res) => {
  try {
    const workout = await Workout.findOneAndUpdate(
      { _id: req.params.id, userId: req.user.id },
      { $set: req.body },
      { new: true, runValidators: true }
    );
    
    if (!workout) {
      return res.status(404).json({ error: 'Workout not found' });
    }
    
    res.json({ workout, message: 'Workout updated successfully' });
  } catch (error) {
    console.error('Error updating workout:', error);
    res.status(500).json({ error: 'Failed to update workout' });
  }
});

// Delete workout
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const workout = await Workout.findOneAndDelete({
      _id: req.params.id,
      userId: req.user.id
    });
    
    if (!workout) {
      return res.status(404).json({ error: 'Workout not found' });
    }
    
    res.json({ message: 'Workout deleted successfully' });
  } catch (error) {
    console.error('Error deleting workout:', error);
    res.status(500).json({ error: 'Failed to delete workout' });
  }
});

// Get workout templates
router.get('/templates/all', authenticateToken, async (req, res) => {
  try {
    const templates = [
      {
        id: 'push-day',
        name: 'Push Day',
        modality: 'weight-training',
        muscleGroups: ['chest', 'shoulders', 'triceps'],
        exercises: [
          { name: 'Bench Press', sets: 4, reps: 8, weight: 0 },
          { name: 'Overhead Press', sets: 3, reps: 10, weight: 0 },
          { name: 'Incline Dumbbell Press', sets: 3, reps: 12, weight: 0 },
          { name: 'Lateral Raises', sets: 3, reps: 15, weight: 0 },
          { name: 'Tricep Dips', sets: 3, reps: 12, weight: 0 },
        ]
      },
      {
        id: 'pull-day',
        name: 'Pull Day',
        modality: 'weight-training',
        muscleGroups: ['back', 'biceps'],
        exercises: [
          { name: 'Deadlifts', sets: 4, reps: 6, weight: 0 },
          { name: 'Pull-ups', sets: 3, reps: 10, weight: 0 },
          { name: 'Barbell Rows', sets: 4, reps: 8, weight: 0 },
          { name: 'Face Pulls', sets: 3, reps: 15, weight: 0 },
          { name: 'Bicep Curls', sets: 3, reps: 12, weight: 0 },
        ]
      },
      {
        id: 'leg-day',
        name: 'Leg Day',
        modality: 'weight-training',
        muscleGroups: ['legs'],
        exercises: [
          { name: 'Squats', sets: 4, reps: 8, weight: 0 },
          { name: 'Romanian Deadlifts', sets: 3, reps: 10, weight: 0 },
          { name: 'Leg Press', sets: 3, reps: 12, weight: 0 },
          { name: 'Leg Curls', sets: 3, reps: 12, weight: 0 },
          { name: 'Calf Raises', sets: 4, reps: 15, weight: 0 },
        ]
      },
      {
        id: 'hiit-cardio',
        name: 'HIIT Cardio',
        modality: 'running',
        muscleGroups: ['full-body'],
        exercises: [
          { name: 'Sprint Intervals', sets: 8, duration: 30, rest: 30 },
          { name: 'Burpees', sets: 4, reps: 15 },
          { name: 'Mountain Climbers', sets: 4, duration: 45 },
          { name: 'Jump Squats', sets: 4, reps: 20 },
        ]
      },
      {
        id: 'yoga-flow',
        name: 'Yoga Flow',
        modality: 'yoga',
        muscleGroups: ['full-body'],
        exercises: [
          { name: 'Sun Salutations', sets: 5 },
          { name: 'Warrior Sequence', duration: 300 },
          { name: 'Balance Poses', duration: 180 },
          { name: 'Cool Down Stretches', duration: 300 },
        ]
      }
    ];
    
    res.json({ templates });
  } catch (error) {
    console.error('Error fetching templates:', error);
    res.status(500).json({ error: 'Failed to fetch templates' });
  }
});

// Helper functions
function calculateWeeklyStreak(workouts) {
  if (workouts.length === 0) return 0;
  
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  let streak = 0;
  let currentDate = new Date(today);
  
  for (let i = 0; i < 7; i++) {
    const hasWorkout = workouts.some(w => {
      const workoutDate = new Date(w.date);
      workoutDate.setHours(0, 0, 0, 0);
      return workoutDate.getTime() === currentDate.getTime();
    });
    
    if (hasWorkout) {
      streak++;
    }
    
    currentDate.setDate(currentDate.getDate() - 1);
  }
  
  return streak;
}

function getFavoriteModality(workouts) {
  if (workouts.length === 0) return 'None';
  
  const modalityCounts = {};
  workouts.forEach(w => {
    const modality = w.modality || 'general';
    modalityCounts[modality] = (modalityCounts[modality] || 0) + 1;
  });
  
  let maxCount = 0;
  let favorite = 'None';
  
  Object.entries(modalityCounts).forEach(([modality, count]) => {
    if (count > maxCount) {
      maxCount = count;
      favorite = modality.charAt(0).toUpperCase() + modality.slice(1);
    }
  });
  
  return favorite;
}

function getWeeklyWorkouts(workouts) {
  const today = new Date();
  const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
  
  return workouts.filter(w => new Date(w.date) >= weekAgo).length;
}

function getMonthlyProgress(workouts) {
  const today = new Date();
  const monthAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000);
  
  const monthlyWorkouts = workouts.filter(w => new Date(w.date) >= monthAgo);
  
  return {
    workouts: monthlyWorkouts.length,
    totalDuration: monthlyWorkouts.reduce((sum, w) => sum + (w.duration || 0), 0),
    totalCalories: monthlyWorkouts.reduce((sum, w) => sum + (w.calories || 0), 0),
  };
}

export default router;
