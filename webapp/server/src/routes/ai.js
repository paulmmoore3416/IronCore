import express from 'express';
import { authenticateToken as auth } from '../middleware/auth.js';

const router = express.Router();

// Get AI recommendations
router.get('/recommendations', auth, async (req, res) => {
  try {
    const { category = 'all' } = req.query;
    
    // Mock AI recommendations based on category
    const allRecommendations = [
      {
        id: 'rec1',
        category: 'workout',
        title: 'Increase Training Volume',
        description: 'Based on your recovery metrics, you can handle 10-15% more volume. Consider adding an extra set to your main lifts.',
        priority: 'high',
        impact: 'High',
        effort: 'Medium',
        expectedImprovement: 15,
        color: 'orange',
        actionable: true,
      },
      {
        id: 'rec2',
        category: 'nutrition',
        title: 'Optimize Post-Workout Nutrition',
        description: 'Your protein intake is slightly low. Aim for 30-40g of protein within 2 hours after training for optimal recovery.',
        priority: 'high',
        impact: 'High',
        effort: 'Low',
        expectedImprovement: 12,
        color: 'red',
        actionable: true,
      },
      {
        id: 'rec3',
        category: 'recovery',
        title: 'Improve Sleep Consistency',
        description: 'Your sleep schedule varies by 2+ hours. Try to maintain consistent bed and wake times for better recovery.',
        priority: 'medium',
        impact: 'High',
        effort: 'Medium',
        expectedImprovement: 18,
        color: 'purple',
        actionable: true,
      },
      {
        id: 'rec4',
        category: 'form',
        title: 'Squat Depth Improvement',
        description: 'Video analysis shows you\'re consistently 2-3 inches above parallel. Focus on mobility work and lighter weights to achieve proper depth.',
        priority: 'high',
        impact: 'Very High',
        effort: 'High',
        expectedImprovement: 25,
        color: 'green',
        actionable: true,
      },
      {
        id: 'rec5',
        category: 'injury',
        title: 'Address Shoulder Imbalance',
        description: 'Your right shoulder shows 15% less strength than left. Add unilateral exercises and mobility work to prevent injury.',
        priority: 'high',
        impact: 'Critical',
        effort: 'Medium',
        expectedImprovement: 20,
        color: 'yellow',
        actionable: true,
      },
      {
        id: 'rec6',
        category: 'performance',
        title: 'Periodize Your Training',
        description: 'You\'ve been training at high intensity for 8 weeks. Consider a deload week to prevent overtraining and boost performance.',
        priority: 'medium',
        impact: 'High',
        effort: 'Low',
        expectedImprovement: 10,
        color: 'blue',
        actionable: true,
      },
    ];

    const recommendations = category === 'all' 
      ? allRecommendations 
      : allRecommendations.filter(r => r.category === category);

    res.json({ recommendations });
  } catch (error) {
    console.error('Error fetching AI recommendations:', error);
    res.status(500).json({ error: 'Failed to fetch AI recommendations' });
  }
});

// Get AI insights
router.get('/insights', auth, async (req, res) => {
  try {
    const insights = {
      confidence: 92,
      totalInsights: 24,
      improvements: 15,
      trends: {
        strength: 'increasing',
        endurance: 'stable',
        recovery: 'improving',
        nutrition: 'needs_attention',
      },
      predictions: {
        nextPR: '2 weeks',
        plateauRisk: 'low',
        injuryRisk: 'low',
      },
    };

    res.json({ insights });
  } catch (error) {
    console.error('Error fetching AI insights:', error);
    res.status(500).json({ error: 'Failed to fetch AI insights' });
  }
});

// Get form analysis
router.get('/form-analysis', auth, async (req, res) => {
  try {
    const analysis = [
      {
        exercise: 'Barbell Squat',
        date: '2026-05-27',
        status: 'warning',
        feedback: 'Depth is slightly above parallel. Focus on hip mobility and ankle flexibility.',
        corrections: [
          'Perform goblet squats to practice depth',
          'Add hip flexor stretches to warm-up',
          'Consider using squat shoes or heel wedges',
        ],
      },
      {
        exercise: 'Bench Press',
        date: '2026-05-26',
        status: 'good',
        feedback: 'Excellent form! Bar path is optimal and shoulder positioning is safe.',
        corrections: [],
      },
      {
        exercise: 'Deadlift',
        date: '2026-05-25',
        status: 'warning',
        feedback: 'Lower back rounding detected at heavier weights. Reduce load and focus on bracing.',
        corrections: [
          'Practice bracing with lighter weights',
          'Strengthen core with planks and dead bugs',
          'Consider using a belt for heavy sets',
        ],
      },
    ];

    res.json({ analysis });
  } catch (error) {
    console.error('Error fetching form analysis:', error);
    res.status(500).json({ error: 'Failed to fetch form analysis' });
  }
});

// Chat with AI
router.post('/chat', auth, async (req, res) => {
  try {
    const { message } = req.body;

    if (!message) {
      return res.status(400).json({ error: 'Message is required' });
    }

    // Mock AI response based on message content
    let response = '';
    const lowerMessage = message.toLowerCase();

    if (lowerMessage.includes('protein') || lowerMessage.includes('nutrition')) {
      response = 'For optimal muscle growth and recovery, aim for 0.8-1g of protein per pound of body weight daily. Distribute this across 4-5 meals, with 30-40g post-workout. Good sources include lean meats, fish, eggs, dairy, and plant-based options like legumes and tofu.';
    } else if (lowerMessage.includes('sleep') || lowerMessage.includes('recovery')) {
      response = 'Quality sleep is crucial for recovery. Aim for 7-9 hours per night with consistent bed/wake times. Keep your room cool (65-68°F), dark, and quiet. Avoid screens 1 hour before bed and consider magnesium supplementation if you have trouble falling asleep.';
    } else if (lowerMessage.includes('workout') || lowerMessage.includes('training')) {
      response = 'Based on your current training data, I recommend focusing on progressive overload by increasing weight by 2.5-5% when you can complete all sets with good form. Consider periodizing your training with 3-4 week blocks of different intensities to prevent plateaus.';
    } else if (lowerMessage.includes('injury') || lowerMessage.includes('pain')) {
      response = 'If you\'re experiencing pain, it\'s important to address it immediately. Reduce training intensity, focus on mobility work, and consider consulting a physical therapist. Never train through sharp or persistent pain. Recovery is more important than pushing through discomfort.';
    } else {
      response = 'I\'m here to help with training, nutrition, recovery, and injury prevention. Based on your recent activity, I recommend focusing on consistency and progressive overload. What specific aspect would you like to discuss?';
    }

    res.json({ 
      response,
      timestamp: new Date(),
      confidence: 0.92,
    });
  } catch (error) {
    console.error('Error in AI chat:', error);
    res.status(500).json({ error: 'Failed to process chat message' });
  }
});

// Get workout suggestions
router.get('/workout-suggestions', auth, async (req, res) => {
  try {
    const suggestions = [
      {
        id: 'sug1',
        type: 'exercise',
        title: 'Add Romanian Deadlifts',
        reason: 'Your hamstring development is lagging behind quads. RDLs will help balance leg development.',
        sets: 3,
        reps: '8-12',
        intensity: 'moderate',
      },
      {
        id: 'sug2',
        type: 'exercise',
        title: 'Include Face Pulls',
        reason: 'To prevent shoulder injuries and improve posture, add face pulls 2-3x per week.',
        sets: 3,
        reps: '15-20',
        intensity: 'light',
      },
      {
        id: 'sug3',
        type: 'modification',
        title: 'Reduce Bench Press Frequency',
        reason: 'You\'re benching 4x per week which may be causing shoulder fatigue. Reduce to 2-3x.',
        impact: 'injury_prevention',
      },
    ];

    res.json({ suggestions });
  } catch (error) {
    console.error('Error fetching workout suggestions:', error);
    res.status(500).json({ error: 'Failed to fetch workout suggestions' });
  }
});

// Analyze workout performance
router.post('/analyze-workout', auth, async (req, res) => {
  try {
    const { workoutId } = req.body;

    if (!workoutId) {
      return res.status(400).json({ error: 'Workout ID is required' });
    }

    const analysis = {
      workoutId,
      overallScore: 85,
      strengths: [
        'Excellent progressive overload on main lifts',
        'Good exercise selection for goals',
        'Appropriate rest periods',
      ],
      improvements: [
        'Consider adding more hamstring work',
        'Increase core training frequency',
        'Add mobility work to warm-up',
      ],
      nextWorkout: {
        focus: 'Upper Body Push',
        suggestedExercises: ['Bench Press', 'Overhead Press', 'Dips', 'Lateral Raises'],
        estimatedDuration: 60,
      },
    };

    res.json({ analysis });
  } catch (error) {
    console.error('Error analyzing workout:', error);
    res.status(500).json({ error: 'Failed to analyze workout' });
  }
});

// Get injury risk assessment
router.get('/injury-risk', auth, async (req, res) => {
  try {
    const assessment = {
      overallRisk: 'low',
      riskScore: 25,
      factors: [
        {
          name: 'Training Volume',
          status: 'good',
          score: 15,
          recommendation: 'Current volume is appropriate for your recovery capacity.',
        },
        {
          name: 'Movement Quality',
          status: 'warning',
          score: 35,
          recommendation: 'Some form issues detected. Focus on technique before increasing weight.',
        },
        {
          name: 'Recovery',
          status: 'good',
          score: 20,
          recommendation: 'Sleep and recovery metrics are optimal.',
        },
        {
          name: 'Muscle Imbalances',
          status: 'warning',
          score: 30,
          recommendation: 'Right shoulder shows weakness. Add unilateral work.',
        },
      ],
      preventionTips: [
        'Maintain current sleep schedule',
        'Add more unilateral exercises',
        'Focus on mobility work',
        'Consider a deload week every 4-6 weeks',
      ],
    };

    res.json({ assessment });
  } catch (error) {
    console.error('Error fetching injury risk:', error);
    res.status(500).json({ error: 'Failed to fetch injury risk assessment' });
  }
});

export default router;