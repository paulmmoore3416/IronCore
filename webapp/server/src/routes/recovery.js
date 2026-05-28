import express from 'express';
import { authenticateToken as auth } from '../middleware/auth.js';

const router = express.Router();

// Get recovery score
router.get('/score', auth, async (req, res) => {
  try {
    const score = {
      score: 86,
      hrv: 65,
      restingHR: 58,
      sleepQuality: 87,
      muscleRecovery: 82,
      readiness: 'High',
      recommendation: 'Good day for intense training',
      trend: 'improving',
    };

    res.json(score);
  } catch (error) {
    console.error('Error fetching recovery score:', error);
    res.status(500).json({ error: 'Failed to fetch recovery score' });
  }
});

// Get sleep data
router.get('/sleep', auth, async (req, res) => {
  try {
    const { date } = req.query;
    
    const sleepData = {
      date: date || new Date().toISOString().split('T')[0],
      quality: 87,
      duration: 7.75, // hours
      efficiency: 92,
      bedtime: '22:30',
      wakeTime: '06:15',
      stages: {
        awake: 5,
        light: 50,
        deep: 25,
        rem: 20,
      },
      interruptions: 2,
      restfulness: 8.5,
    };

    res.json(sleepData);
  } catch (error) {
    console.error('Error fetching sleep data:', error);
    res.status(500).json({ error: 'Failed to fetch sleep data' });
  }
});

// Get HRV trends
router.get('/hrv-trends', auth, async (req, res) => {
  try {
    const { period = '7d' } = req.query;
    const days = period === '7d' ? 7 : period === '30d' ? 30 : 90;
    
    const trends = Array.from({ length: days }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (days - i - 1));
      
      return {
        date: date.toISOString().split('T')[0],
        hrv: Math.floor(55 + Math.random() * 20),
        rhr: Math.floor(56 + Math.random() * 8),
      };
    });

    res.json({ trends, period });
  } catch (error) {
    console.error('Error fetching HRV trends:', error);
    res.status(500).json({ error: 'Failed to fetch HRV trends' });
  }
});

// Get sleep trends
router.get('/sleep-trends', auth, async (req, res) => {
  try {
    const { period = '7d' } = req.query;
    const days = period === '7d' ? 7 : period === '30d' ? 30 : 90;
    
    const trends = Array.from({ length: days }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - (days - i - 1));
      
      return {
        date: date.toISOString().split('T')[0],
        duration: 6.5 + Math.random() * 2,
        quality: Math.floor(75 + Math.random() * 20),
        deep: Math.floor(20 + Math.random() * 10),
        rem: Math.floor(18 + Math.random() * 10),
      };
    });

    res.json({ trends, period });
  } catch (error) {
    console.error('Error fetching sleep trends:', error);
    res.status(500).json({ error: 'Failed to fetch sleep trends' });
  }
});

// Log recovery metrics
router.post('/log', auth, async (req, res) => {
  try {
    const { 
      sleepQuality, 
      muscleSoreness, 
      stressLevel, 
      energyLevel, 
      mood,
      notes 
    } = req.body;

    // Mock logging recovery metrics
    const entry = {
      id: Date.now().toString(),
      userId: req.userId,
      sleepQuality,
      muscleSoreness,
      stressLevel,
      energyLevel,
      mood,
      notes: notes || '',
      timestamp: new Date(),
    };

    res.json({ 
      success: true, 
      message: 'Recovery metrics logged',
      entry 
    });
  } catch (error) {
    console.error('Error logging recovery metrics:', error);
    res.status(500).json({ error: 'Failed to log recovery metrics' });
  }
});

// Get recovery recommendations
router.get('/recommendations', auth, async (req, res) => {
  try {
    const recommendations = [
      {
        category: 'Sleep',
        priority: 'high',
        title: 'Optimize Sleep Schedule',
        description: 'Try to maintain consistent sleep and wake times, even on weekends.',
        actionable: true,
      },
      {
        category: 'Nutrition',
        priority: 'medium',
        title: 'Post-Workout Nutrition',
        description: 'Consume protein within 2 hours after training for optimal recovery.',
        actionable: true,
      },
      {
        category: 'Active Recovery',
        priority: 'medium',
        title: 'Light Movement',
        description: 'Consider a 20-30 minute walk or light swim on rest days.',
        actionable: true,
      },
      {
        category: 'Stress Management',
        priority: 'high',
        title: 'Breathing Exercises',
        description: 'Practice 5-10 minutes of deep breathing to improve HRV.',
        actionable: true,
      },
    ];

    res.json({ recommendations });
  } catch (error) {
    console.error('Error fetching recommendations:', error);
    res.status(500).json({ error: 'Failed to fetch recommendations' });
  }
});

// Get recovery history
router.get('/history', auth, async (req, res) => {
  try {
    const { limit = 30 } = req.query;
    
    const history = Array.from({ length: parseInt(limit) }, (_, i) => {
      const date = new Date();
      date.setDate(date.getDate() - i);
      
      return {
        date: date.toISOString().split('T')[0],
        recoveryScore: Math.floor(70 + Math.random() * 25),
        hrv: Math.floor(55 + Math.random() * 20),
        rhr: Math.floor(56 + Math.random() * 8),
        sleepQuality: Math.floor(75 + Math.random() * 20),
        readiness: Math.random() > 0.3 ? 'High' : Math.random() > 0.5 ? 'Moderate' : 'Low',
      };
    });

    res.json({ history });
  } catch (error) {
    console.error('Error fetching recovery history:', error);
    res.status(500).json({ error: 'Failed to fetch recovery history' });
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
      period,
    };

    res.json({ analysis });
  } catch (error) {
    console.error('Error fetching sleep analysis:', error);
    res.status(500).json({ error: 'Failed to fetch sleep analysis' });
  }
});

// Get readiness score breakdown
router.get('/readiness-breakdown', auth, async (req, res) => {
  try {
    const breakdown = {
      overall: 86,
      factors: [
        { name: 'Sleep Quality', score: 87, weight: 30, contribution: 26.1 },
        { name: 'HRV', score: 85, weight: 25, contribution: 21.25 },
        { name: 'Resting Heart Rate', score: 88, weight: 20, contribution: 17.6 },
        { name: 'Recovery Time', score: 82, weight: 15, contribution: 12.3 },
        { name: 'Activity Balance', score: 86, weight: 10, contribution: 8.6 },
      ],
    };

    res.json({ breakdown });
  } catch (error) {
    console.error('Error fetching readiness breakdown:', error);
    res.status(500).json({ error: 'Failed to fetch readiness breakdown' });
  }
});

export default router;
