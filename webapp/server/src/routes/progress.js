import express from 'express';
import { authenticateToken as auth } from '../middleware/auth.js';

const router = express.Router();

// Get user goals
router.get('/goals', auth, async (req, res) => {
  try {
    const goals = [
      {
        id: 'goal1',
        title: 'Squat 315 lbs',
        description: 'Achieve a 315 lb squat for 1 rep max',
        category: 'strength',
        current: 275,
        target: 315,
        unit: 'lbs',
        completed: false,
        daysRemaining: 45,
        percentComplete: 87,
        startDate: '2026-03-01',
        targetDate: '2026-07-15',
      },
      {
        id: 'goal2',
        title: 'Lose 10 lbs',
        description: 'Reduce body weight from 185 to 175 lbs',
        category: 'weight',
        current: 178,
        target: 175,
        unit: 'lbs',
        completed: false,
        daysRemaining: 30,
        percentComplete: 70,
        startDate: '2026-04-01',
        targetDate: '2026-06-30',
      },
      {
        id: 'goal3',
        title: 'Run 5K under 25 minutes',
        description: 'Complete a 5K run in under 25 minutes',
        category: 'endurance',
        current: 27.5,
        target: 25,
        unit: 'min',
        completed: false,
        daysRemaining: 60,
        percentComplete: 50,
        startDate: '2026-03-15',
        targetDate: '2026-08-01',
      },
    ];

    res.json({ goals });
  } catch (error) {
    console.error('Error fetching goals:', error);
    res.status(500).json({ error: 'Failed to fetch goals' });
  }
});

// Get personal records
router.get('/personal-records', auth, async (req, res) => {
  try {
    const records = [
      {
        exercise: 'Squat',
        value: 275,
        unit: 'lbs',
        date: '2026-05-25',
        improvement: 10,
        previousBest: 265,
      },
      {
        exercise: 'Bench Press',
        value: 225,
        unit: 'lbs',
        date: '2026-05-20',
        improvement: 5,
        previousBest: 220,
      },
      {
        exercise: 'Deadlift',
        value: 405,
        unit: 'lbs',
        date: '2026-05-28',
        improvement: 15,
        previousBest: 390,
      },
      {
        exercise: '5K Run',
        value: 27.5,
        unit: 'min',
        date: '2026-05-15',
        improvement: 1.5,
        previousBest: 29,
      },
      {
        exercise: 'Pull-ups',
        value: 15,
        unit: 'reps',
        date: '2026-05-22',
        improvement: 2,
        previousBest: 13,
      },
      {
        exercise: 'Plank',
        value: 3.5,
        unit: 'min',
        date: '2026-05-18',
        improvement: 0.5,
        previousBest: 3,
      },
    ];

    res.json({ records });
  } catch (error) {
    console.error('Error fetching personal records:', error);
    res.status(500).json({ error: 'Failed to fetch personal records' });
  }
});

// Get milestones
router.get('/milestones', auth, async (req, res) => {
  try {
    const milestones = [
      {
        id: 'mile1',
        title: '100 Workouts Completed',
        description: 'Reached 100 total workouts this year',
        date: '2026-05-20',
        category: 'consistency',
      },
      {
        id: 'mile2',
        title: 'First 300+ lb Deadlift',
        description: 'Achieved first deadlift over 300 lbs',
        date: '2026-04-15',
        category: 'strength',
      },
      {
        id: 'mile3',
        title: '30-Day Streak',
        description: 'Maintained 30-day workout streak',
        date: '2026-05-10',
        category: 'consistency',
      },
      {
        id: 'mile4',
        title: 'Lost 10 lbs',
        description: 'Successfully lost 10 lbs of body weight',
        date: '2026-03-25',
        category: 'weight',
      },
    ];

    res.json({ milestones });
  } catch (error) {
    console.error('Error fetching milestones:', error);
    res.status(500).json({ error: 'Failed to fetch milestones' });
  }
});

// Get progress trends
router.get('/trends', auth, async (req, res) => {
  try {
    const { period = '30d' } = req.query;
    const days = period === '7d' ? 7 : period === '30d' ? 30 : 90;
    
    const trends = Array.from({ length: days }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (days - i - 1));
      
      return {
        date: date.toISOString().split('T')[0],
        strength: Math.floor(70 + Math.random() * 20),
        endurance: Math.floor(65 + Math.random() * 25),
        weight: 178 + (Math.random() - 0.5) * 3,
      };
    });

    res.json({ trends, period });
  } catch (error) {
    console.error('Error fetching progress trends:', error);
    res.status(500).json({ error: 'Failed to fetch progress trends' });
  }
});

// Create new goal
router.post('/goals', auth, async (req, res) => {
  try {
    const { title, description, category, target, unit, targetDate } = req.body;

    if (!title || !target || !targetDate) {
      return res.status(400).json({ error: 'Title, target, and target date are required' });
    }

    const goal = {
      id: Date.now().toString(),
      userId: req.userId,
      title,
      description: description || '',
      category: category || 'general',
      current: 0,
      target,
      unit: unit || '',
      completed: false,
      startDate: new Date(),
      targetDate,
      createdAt: new Date(),
    };

    res.json({ 
      success: true, 
      message: 'Goal created successfully',
      goal 
    });
  } catch (error) {
    console.error('Error creating goal:', error);
    res.status(500).json({ error: 'Failed to create goal' });
  }
});

// Update goal progress
router.patch('/goals/:id', auth, async (req, res) => {
  try {
    const goalId = req.params.id;
    const { current, completed } = req.body;

    res.json({ 
      success: true, 
      message: 'Goal updated successfully',
      goalId,
      current,
      completed,
    });
  } catch (error) {
    console.error('Error updating goal:', error);
    res.status(500).json({ error: 'Failed to update goal' });
  }
});

// Log personal record
router.post('/personal-records', auth, async (req, res) => {
  try {
    const { exercise, value, unit, notes } = req.body;

    if (!exercise || !value) {
      return res.status(400).json({ error: 'Exercise and value are required' });
    }

    const record = {
      id: Date.now().toString(),
      userId: req.userId,
      exercise,
      value,
      unit: unit || '',
      notes: notes || '',
      date: new Date(),
    };

    res.json({ 
      success: true, 
      message: 'Personal record logged!',
      record 
    });
  } catch (error) {
    console.error('Error logging personal record:', error);
    res.status(500).json({ error: 'Failed to log personal record' });
  }
});

// Get goal statistics
router.get('/stats', auth, async (req, res) => {
  try {
    const stats = {
      totalGoals: 12,
      completedGoals: 8,
      activeGoals: 4,
      completionRate: 67,
      totalPRs: 24,
      thisMonthPRs: 3,
      currentStreak: 14,
      longestStreak: 45,
    };

    res.json({ stats });
  } catch (error) {
    console.error('Error fetching goal statistics:', error);
    res.status(500).json({ error: 'Failed to fetch goal statistics' });
  }
});

export default router;
