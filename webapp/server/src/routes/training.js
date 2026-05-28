import express from 'express';
import { authenticateToken as auth } from '../middleware/auth.js';

const router = express.Router();

// Get active programs for user
router.get('/active', auth, async (req, res) => {
  try {
    // Mock active programs
    const programs = [
      {
        id: 'prog1',
        name: 'Muscle Growth Program',
        currentWeek: 4,
        totalWeeks: 12,
        completedWorkouts: 15,
        totalWorkouts: 48,
        nextWorkout: 'Upper Body A',
        adherence: 92,
        daysRemaining: 56,
      },
    ];

    res.json({ programs });
  } catch (error) {
    console.error('Error fetching active programs:', error);
    res.status(500).json({ error: 'Failed to fetch active programs' });
  }
});

// Get program details
router.get('/programs/:id', auth, async (req, res) => {
  try {
    const programId = req.params.id;
    
    // Mock program details
    const program = {
      id: programId,
      name: 'Muscle Growth Program',
      description: 'Hypertrophy-focused program with progressive overload for maximum muscle gains.',
      duration: '12 weeks',
      level: 'Intermediate',
      goal: 'Muscle Growth',
      workoutsPerWeek: 4,
      currentWeek: 4,
      totalWeeks: 12,
      completedWorkouts: 15,
      totalWorkouts: 48,
      adherence: 92,
      weeks: Array.from({ length: 12 }, (_, i) => ({
        weekNumber: i + 1,
        phase: i < 2 ? 'Adaptation' : i < 6 ? 'Hypertrophy' : i < 9 ? 'Strength' : 'Power',
        workouts: [
          { id: `w${i}-1`, name: 'Upper Body A', completed: i < 4 },
          { id: `w${i}-2`, name: 'Lower Body A', completed: i < 4 },
          { id: `w${i}-3`, name: 'Upper Body B', completed: i < 4 },
          { id: `w${i}-4`, name: 'Lower Body B', completed: i < 4 },
        ],
      })),
    };

    res.json({ program });
  } catch (error) {
    console.error('Error fetching program details:', error);
    res.status(500).json({ error: 'Failed to fetch program details' });
  }
});

// Start a program
router.post('/programs/:id/start', auth, async (req, res) => {
  try {
    const programId = req.params.id;
    
    // Mock starting program
    res.json({ 
      success: true, 
      message: 'Program started successfully',
      programId,
      startDate: new Date(),
    });
  } catch (error) {
    console.error('Error starting program:', error);
    res.status(500).json({ error: 'Failed to start program' });
  }
});

// Complete a workout in a program
router.post('/programs/:programId/workouts/:workoutId/complete', auth, async (req, res) => {
  try {
    const { programId, workoutId } = req.params;
    
    // Mock completing workout
    res.json({ 
      success: true, 
      message: 'Workout completed',
      programId,
      workoutId,
      completedAt: new Date(),
    });
  } catch (error) {
    console.error('Error completing workout:', error);
    res.status(500).json({ error: 'Failed to complete workout' });
  }
});

// Get program templates
router.get('/templates', auth, async (req, res) => {
  try {
    const templates = [
      {
        id: 'beginner-strength',
        name: 'Beginner Strength Builder',
        duration: '8 weeks',
        level: 'Beginner',
        goal: 'Build Foundation',
        workoutsPerWeek: 3,
        description: 'Perfect for those new to strength training. Focus on form and building a solid foundation.',
      },
      {
        id: 'intermediate-hypertrophy',
        name: 'Muscle Growth Program',
        duration: '12 weeks',
        level: 'Intermediate',
        goal: 'Muscle Growth',
        workoutsPerWeek: 4,
        description: 'Hypertrophy-focused program with progressive overload for maximum muscle gains.',
      },
      {
        id: 'advanced-powerlifting',
        name: 'Powerlifting Peaking',
        duration: '16 weeks',
        level: 'Advanced',
        goal: 'Strength',
        workoutsPerWeek: 5,
        description: 'Advanced program designed to peak your squat, bench, and deadlift for competition.',
      },
    ];

    res.json({ templates });
  } catch (error) {
    console.error('Error fetching templates:', error);
    res.status(500).json({ error: 'Failed to fetch templates' });
  }
});

// Get progressive overload stats
router.get('/progressive-overload', auth, async (req, res) => {
  try {
    const stats = {
      volumeLoad: 125450,
      volumeChange: 12,
      avgIntensity: 78,
      intensityChange: 5,
      recoveryScore: 86,
      weeklyVolume: [
        { week: 1, volume: 95000 },
        { week: 2, volume: 102000 },
        { week: 3, volume: 110000 },
        { week: 4, volume: 125450 },
      ],
    };

    res.json({ stats });
  } catch (error) {
    console.error('Error fetching progressive overload stats:', error);
    res.status(500).json({ error: 'Failed to fetch progressive overload stats' });
  }
});

// Create custom program
router.post('/programs/custom', auth, async (req, res) => {
  try {
    const { name, duration, workoutsPerWeek, goal, exercises } = req.body;

    if (!name || !duration || !workoutsPerWeek) {
      return res.status(400).json({ error: 'Name, duration, and workouts per week are required' });
    }

    // Mock creating custom program
    const program = {
      id: Date.now().toString(),
      name,
      duration,
      workoutsPerWeek,
      goal: goal || 'Custom',
      exercises: exercises || [],
      createdAt: new Date(),
      userId: req.userId,
    };

    res.json({ 
      success: true, 
      message: 'Custom program created',
      program 
    });
  } catch (error) {
    console.error('Error creating custom program:', error);
    res.status(500).json({ error: 'Failed to create custom program' });
  }
});

// Update program settings
router.patch('/programs/:id/settings', auth, async (req, res) => {
  try {
    const programId = req.params.id;
    const { frequency, duration, level } = req.body;

    // Mock updating settings
    res.json({ 
      success: true, 
      message: 'Program settings updated',
      programId,
      settings: { frequency, duration, level },
    });
  } catch (error) {
    console.error('Error updating program settings:', error);
    res.status(500).json({ error: 'Failed to update program settings' });
  }
});

// Get periodization phases
router.get('/periodization', auth, async (req, res) => {
  try {
    const phases = [
      { name: 'Anatomical Adaptation', weeks: 2, focus: 'Form & Technique', intensity: 'Low' },
      { name: 'Hypertrophy', weeks: 4, focus: 'Muscle Growth', intensity: 'Moderate' },
      { name: 'Strength', weeks: 3, focus: 'Max Strength', intensity: 'High' },
      { name: 'Power', weeks: 2, focus: 'Explosive Power', intensity: 'Very High' },
      { name: 'Peaking', weeks: 1, focus: 'Competition Prep', intensity: 'Max' },
    ];

    res.json({ phases });
  } catch (error) {
    console.error('Error fetching periodization phases:', error);
    res.status(500).json({ error: 'Failed to fetch periodization phases' });
  }
});

// Get training principles
router.get('/principles', auth, async (req, res) => {
  try {
    const principles = [
      {
        name: 'Progressive Overload',
        description: 'Gradually increase weight, reps, or sets over time',
        importance: 'Critical',
      },
      {
        name: 'Specificity',
        description: 'Train movements and energy systems specific to your goals',
        importance: 'High',
      },
      {
        name: 'Recovery',
        description: 'Allow adequate rest between sessions for adaptation',
        importance: 'Critical',
      },
      {
        name: 'Variation',
        description: 'Periodically change exercises and rep ranges',
        importance: 'Moderate',
      },
    ];

    res.json({ principles });
  } catch (error) {
    console.error('Error fetching training principles:', error);
    res.status(500).json({ error: 'Failed to fetch training principles' });
  }
});

export default router;
