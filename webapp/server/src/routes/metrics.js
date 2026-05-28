import express from 'express';
import { authenticateToken as auth } from '../middleware/auth.js';

const router = express.Router();

// Get dashboard metrics overview
router.get('/dashboard', auth, async (req, res) => {
  try {
    const dashboard = {
      heartRate: 72,
      calories: 2450,
      sleepScore: 87,
      steps: 11250,
      activeMinutes: 62,
      distance: 8.5,
      waterIntake: 2.1,
      weight: 178,
    };

    res.json({ dashboard });
  } catch (error) {
    console.error('Error fetching dashboard metrics:', error);
    res.status(500).json({ error: 'Failed to fetch dashboard metrics' });
  }
});

// Get health score
router.get('/health-score', auth, async (req, res) => {
  try {
    const score = {
      overall: 85,
      cardiovascular: 88,
      strength: 82,
      flexibility: 78,
      recovery: 86,
      nutrition: 84,
      sleep: 87,
    };

    res.json({ score });
  } catch (error) {
    console.error('Error fetching health score:', error);
    res.status(500).json({ error: 'Failed to fetch health score' });
  }
});

// Get body composition data
router.get('/body-composition', auth, async (req, res) => {
  try {
    const composition = {
      weight: 178,
      bodyFat: 15,
      muscleMass: 152,
      boneMass: 12,
      waterPercentage: 62,
      bmi: 23.5,
      visceralFat: 8,
    };

    res.json({ composition });
  } catch (error) {
    console.error('Error fetching body composition:', error);
    res.status(500).json({ error: 'Failed to fetch body composition' });
  }
});

// Get trends for a specific metric
router.get('/trends/:metricType', auth, async (req, res) => {
  try {
    const { metricType } = req.params;
    const { period = '30d' } = req.query;

    // Generate mock trend data based on metric type and period
    const dataPoints = period === '7d' ? 7 : period === '30d' ? 30 : period === '90d' ? 90 : 365;
    
    const trends = Array.from({ length: dataPoints }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (dataPoints - i - 1));
      
      let value;
      switch (metricType) {
        case 'steps':
          value = Math.floor(8000 + Math.random() * 8000);
          break;
        case 'heart-rate':
          value = Math.floor(65 + Math.random() * 15);
          break;
        case 'calories':
          value = Math.floor(2000 + Math.random() * 1000);
          break;
        case 'sleep':
          value = Math.floor(6 + Math.random() * 3);
          break;
        case 'weight':
          value = 178 + (Math.random() - 0.5) * 2;
          break;
        case 'water':
          value = Math.floor(1.5 + Math.random() * 1.5);
          break;
        default:
          value = Math.random() * 100;
      }
      
      return {
        date: date.toISOString().split('T')[0],
        value: Math.round(value * 10) / 10,
      };
    });

    res.json({ trends, metricType, period });
  } catch (error) {
    console.error('Error fetching trends:', error);
    res.status(500).json({ error: 'Failed to fetch trends' });
  }
});

// Get heart rate zones distribution
router.get('/heart-rate-zones', auth, async (req, res) => {
  try {
    const zones = [
      { name: 'Rest', min: 50, max: 100, percentage: 25, minutes: 180 },
      { name: 'Fat Burn', min: 100, max: 130, percentage: 35, minutes: 252 },
      { name: 'Cardio', min: 130, max: 160, percentage: 25, minutes: 180 },
      { name: 'Peak', min: 160, max: 190, percentage: 15, minutes: 108 },
    ];

    res.json({ zones });
  } catch (error) {
    console.error('Error fetching heart rate zones:', error);
    res.status(500).json({ error: 'Failed to fetch heart rate zones' });
  }
});

// Get activity summary
router.get('/activity-summary', auth, async (req, res) => {
  try {
    const { period = 'week' } = req.query;
    
    const summary = {
      totalSteps: 78750,
      totalCalories: 17150,
      totalActiveMinutes: 434,
      totalDistance: 59.5,
      averageHeartRate: 72,
      workoutsCompleted: 5,
      restDays: 2,
      period,
    };

    res.json({ summary });
  } catch (error) {
    console.error('Error fetching activity summary:', error);
    res.status(500).json({ error: 'Failed to fetch activity summary' });
  }
});

// Get personal records
router.get('/personal-records', auth, async (req, res) => {
  try {
    const records = {
      maxSteps: { value: 22450, date: '2026-05-15' },
      longestRun: { value: 15.2, unit: 'km', date: '2026-05-10' },
      maxHeartRate: { value: 185, unit: 'bpm', date: '2026-05-20' },
      bestSleepScore: { value: 98, date: '2026-05-18' },
      maxCalories: { value: 3850, date: '2026-05-12' },
      longestStreak: { value: 14, unit: 'days', endDate: '2026-05-25' },
    };

    res.json({ records });
  } catch (error) {
    console.error('Error fetching personal records:', error);
    res.status(500).json({ error: 'Failed to fetch personal records' });
  }
});

// Get achievements
router.get('/achievements', auth, async (req, res) => {
  try {
    const achievements = [
      {
        id: 'streak-7',
        name: '7-Day Streak',
        description: 'Workout every day for 7 days',
        icon: 'trophy',
        color: 'yellow',
        unlockedAt: '2026-05-21',
      },
      {
        id: 'steps-100k',
        name: '100K Steps',
        description: 'Walk 100,000 steps in a week',
        icon: 'bolt',
        color: 'blue',
        unlockedAt: '2026-05-18',
      },
      {
        id: 'calorie-master',
        name: 'Calorie Master',
        description: 'Hit calorie target for 30 consecutive days',
        icon: 'fire',
        color: 'green',
        unlockedAt: '2026-05-15',
      },
      {
        id: 'early-bird',
        name: 'Early Bird',
        description: 'Complete 10 morning workouts',
        icon: 'sun',
        color: 'orange',
        unlockedAt: '2026-05-10',
      },
    ];

    res.json({ achievements });
  } catch (error) {
    console.error('Error fetching achievements:', error);
    res.status(500).json({ error: 'Failed to fetch achievements' });
  }
});

// Log a new metric entry
router.post('/log', auth, async (req, res) => {
  try {
    const { metricType, value, timestamp, notes } = req.body;

    if (!metricType || value === undefined) {
      return res.status(400).json({ error: 'Metric type and value are required' });
    }

    // Mock saving metric
    const metric = {
      id: Date.now().toString(),
      userId: req.userId,
      metricType,
      value,
      timestamp: timestamp || new Date(),
      notes: notes || '',
    };

    res.json({ 
      success: true, 
      message: 'Metric logged successfully',
      metric 
    });
  } catch (error) {
    console.error('Error logging metric:', error);
    res.status(500).json({ error: 'Failed to log metric' });
  }
});

// Get metric history
router.get('/history/:metricType', auth, async (req, res) => {
  try {
    const { metricType } = req.params;
    const { limit = 50, offset = 0 } = req.query;

    // Mock history data
    const history = Array.from({ length: parseInt(limit) }, (_, i) => {
      const date = new Date();
      date.setHours(date.getHours() - i);
      
      return {
        id: `metric-${i}`,
        metricType,
        value: Math.random() * 100,
        timestamp: date.toISOString(),
        notes: i % 5 === 0 ? 'Feeling great!' : '',
      };
    });

    res.json({ 
      history,
      total: 500,
      limit: parseInt(limit),
      offset: parseInt(offset),
    });
  } catch (error) {
    console.error('Error fetching metric history:', error);
    res.status(500).json({ error: 'Failed to fetch metric history' });
  }
});

// Get weekly comparison
router.get('/weekly-comparison', auth, async (req, res) => {
  try {
    const comparison = {
      currentWeek: {
        steps: 78750,
        calories: 17150,
        activeMinutes: 434,
        workouts: 5,
      },
      previousWeek: {
        steps: 72300,
        calories: 16200,
        activeMinutes: 398,
        workouts: 4,
      },
      changes: {
        steps: '+8.9%',
        calories: '+5.9%',
        activeMinutes: '+9.0%',
        workouts: '+25%',
      },
    };

    res.json({ comparison });
  } catch (error) {
    console.error('Error fetching weekly comparison:', error);
    res.status(500).json({ error: 'Failed to fetch weekly comparison' });
  }
});

// Get sleep analysis
router.get('/sleep-analysis', auth, async (req, res) => {
  try {
    const { period = '7d' } = req.query;
    
    const analysis = {
      averageDuration: 7.5,
      averageQuality: 87,
      deepSleepPercentage: 22,
      remSleepPercentage: 25,
      lightSleepPercentage: 53,
      averageBedtime: '23:15',
      averageWakeTime: '06:45',
      sleepDebt: -0.5,
      consistency: 85,
    };

    res.json({ analysis, period });
  } catch (error) {
    console.error('Error fetching sleep analysis:', error);
    res.status(500).json({ error: 'Failed to fetch sleep analysis' });
  }
});

// Get recovery metrics
router.get('/recovery', auth, async (req, res) => {
  try {
    const recovery = {
      recoveryScore: 86,
      hrv: 65,
      restingHeartRate: 58,
      sleepQuality: 87,
      muscleRecovery: 82,
      readiness: 'High',
      recommendation: 'Good day for intense training',
    };

    res.json({ recovery });
  } catch (error) {
    console.error('Error fetching recovery metrics:', error);
    res.status(500).json({ error: 'Failed to fetch recovery metrics' });
  }
});

export default router;