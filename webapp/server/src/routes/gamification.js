import express from 'express';
import { authenticateToken as auth } from '../middleware/auth.js';

const router = express.Router();

// Get gamification stats
router.get('/stats', auth, async (req, res) => {
  try {
    const stats = {
      rank: 42,
      totalXP: 12450,
      level: 15,
      badgesEarned: 24,
      totalBadges: 48,
      currentStreak: 14,
      longestStreak: 45,
      recentAchievements: [
        {
          id: 'ach1',
          title: '100 Workouts Milestone',
          description: 'Completed 100 total workouts',
          icon: '🏆',
          xp: 500,
          date: '2026-05-25',
        },
        {
          id: 'ach2',
          title: '2-Week Streak',
          description: 'Maintained a 14-day workout streak',
          icon: '🔥',
          xp: 200,
          date: '2026-05-28',
        },
        {
          id: 'ach3',
          title: 'Strength Master',
          description: 'Achieved 300+ lb deadlift',
          icon: '💪',
          xp: 300,
          date: '2026-05-20',
        },
      ],
    };

    res.json({ stats });
  } catch (error) {
    console.error('Error fetching gamification stats:', error);
    res.status(500).json({ error: 'Failed to fetch gamification stats' });
  }
});

// Get badges
router.get('/badges', auth, async (req, res) => {
  try {
    const { category = 'all' } = req.query;

    const allBadges = [
      // Strength Badges
      {
        id: 'str1',
        name: 'First Lift',
        description: 'Complete your first strength workout',
        icon: '💪',
        category: 'strength',
        earned: true,
        earnedDate: '2026-01-15',
        progress: 100,
      },
      {
        id: 'str2',
        name: 'Iron Warrior',
        description: 'Squat 225 lbs',
        icon: '🦾',
        category: 'strength',
        earned: true,
        earnedDate: '2026-03-20',
        progress: 100,
      },
      {
        id: 'str3',
        name: 'Deadlift King',
        description: 'Deadlift 400 lbs',
        icon: '👑',
        category: 'strength',
        earned: true,
        earnedDate: '2026-05-28',
        progress: 100,
      },
      {
        id: 'str4',
        name: 'Bench Boss',
        description: 'Bench press 300 lbs',
        icon: '🏋️',
        category: 'strength',
        earned: false,
        progress: 75,
      },

      // Endurance Badges
      {
        id: 'end1',
        name: 'First Mile',
        description: 'Run your first mile',
        icon: '🏃',
        category: 'endurance',
        earned: true,
        earnedDate: '2026-01-20',
        progress: 100,
      },
      {
        id: 'end2',
        name: '5K Champion',
        description: 'Complete a 5K run',
        icon: '🎽',
        category: 'endurance',
        earned: true,
        earnedDate: '2026-04-10',
        progress: 100,
      },
      {
        id: 'end3',
        name: 'Marathon Ready',
        description: 'Run 26.2 miles',
        icon: '🏅',
        category: 'endurance',
        earned: false,
        progress: 35,
      },

      // Consistency Badges
      {
        id: 'con1',
        name: 'Week Warrior',
        description: '7-day workout streak',
        icon: '🔥',
        category: 'consistency',
        earned: true,
        earnedDate: '2026-02-01',
        progress: 100,
      },
      {
        id: 'con2',
        name: 'Month Master',
        description: '30-day workout streak',
        icon: '📅',
        category: 'consistency',
        earned: true,
        earnedDate: '2026-04-15',
        progress: 100,
      },
      {
        id: 'con3',
        name: 'Year Legend',
        description: '365-day workout streak',
        icon: '🌟',
        category: 'consistency',
        earned: false,
        progress: 15,
      },

      // Milestone Badges
      {
        id: 'mil1',
        name: '10 Workouts',
        description: 'Complete 10 workouts',
        icon: '🎯',
        category: 'milestone',
        earned: true,
        earnedDate: '2026-01-25',
        progress: 100,
      },
      {
        id: 'mil2',
        name: '50 Workouts',
        description: 'Complete 50 workouts',
        icon: '🎖️',
        category: 'milestone',
        earned: true,
        earnedDate: '2026-03-15',
        progress: 100,
      },
      {
        id: 'mil3',
        name: '100 Workouts',
        description: 'Complete 100 workouts',
        icon: '🏆',
        category: 'milestone',
        earned: true,
        earnedDate: '2026-05-25',
        progress: 100,
      },
      {
        id: 'mil4',
        name: '500 Workouts',
        description: 'Complete 500 workouts',
        icon: '👑',
        category: 'milestone',
        earned: false,
        progress: 20,
      },

      // Social Badges
      {
        id: 'soc1',
        name: 'Social Butterfly',
        description: 'Add 10 friends',
        icon: '👥',
        category: 'social',
        earned: true,
        earnedDate: '2026-02-10',
        progress: 100,
      },
      {
        id: 'soc2',
        name: 'Challenge Master',
        description: 'Win 5 challenges',
        icon: '🥇',
        category: 'social',
        earned: false,
        progress: 60,
      },

      // Achievement Badges
      {
        id: 'ach1',
        name: 'Early Bird',
        description: 'Complete 10 morning workouts',
        icon: '🌅',
        category: 'achievement',
        earned: true,
        earnedDate: '2026-03-01',
        progress: 100,
      },
      {
        id: 'ach2',
        name: 'Night Owl',
        description: 'Complete 10 evening workouts',
        icon: '🌙',
        category: 'achievement',
        earned: false,
        progress: 40,
      },
      {
        id: 'ach3',
        name: 'Nutrition Pro',
        description: 'Log meals for 30 days',
        icon: '🥗',
        category: 'achievement',
        earned: true,
        earnedDate: '2026-04-20',
        progress: 100,
      },
    ];

    const badges = category === 'all' 
      ? allBadges 
      : allBadges.filter(b => b.category === category);

    res.json({ badges });
  } catch (error) {
    console.error('Error fetching badges:', error);
    res.status(500).json({ error: 'Failed to fetch badges' });
  }
});

// Get streak information
router.get('/streak', auth, async (req, res) => {
  try {
    const streak = {
      current: 14,
      longest: 45,
      lastWorkout: '2026-05-28',
      streakHistory: Array.from({ length: 30 }, (_, i) => {
        const date = new Date();
        date.setDate(date.getDate() - (29 - i));
        return {
          date: date.toISOString().split('T')[0],
          completed: i >= 16, // Last 14 days completed
        };
      }),
    };

    res.json({ streak });
  } catch (error) {
    console.error('Error fetching streak:', error);
    res.status(500).json({ error: 'Failed to fetch streak' });
  }
});

// Get level information
router.get('/level', auth, async (req, res) => {
  try {
    const level = {
      level: 15,
      title: 'Iron Warrior',
      currentXP: 2450,
      nextLevelXP: 3000,
      totalXP: 12450,
      progressPercent: 82,
      nextTitle: 'Steel Champion',
      xpToNextLevel: 550,
    };

    res.json({ level });
  } catch (error) {
    console.error('Error fetching level info:', error);
    res.status(500).json({ error: 'Failed to fetch level info' });
  }
});

// Award XP
router.post('/award-xp', auth, async (req, res) => {
  try {
    const { amount, reason } = req.body;

    if (!amount || amount <= 0) {
      return res.status(400).json({ error: 'Valid XP amount is required' });
    }

    const result = {
      success: true,
      xpAwarded: amount,
      reason: reason || 'Activity completed',
      newTotalXP: 12450 + amount,
      leveledUp: false,
    };

    res.json(result);
  } catch (error) {
    console.error('Error awarding XP:', error);
    res.status(500).json({ error: 'Failed to award XP' });
  }
});

// Unlock badge
router.post('/unlock-badge', auth, async (req, res) => {
  try {
    const { badgeId } = req.body;

    if (!badgeId) {
      return res.status(400).json({ error: 'Badge ID is required' });
    }

    const result = {
      success: true,
      badgeId,
      xpAwarded: 100,
      message: 'Badge unlocked!',
    };

    res.json(result);
  } catch (error) {
    console.error('Error unlocking badge:', error);
    res.status(500).json({ error: 'Failed to unlock badge' });
  }
});

// Get leaderboard
router.get('/leaderboard', auth, async (req, res) => {
  try {
    const { type = 'xp', period = 'all-time' } = req.query;

    const leaderboard = Array.from({ length: 10 }, (_, i) => ({
      rank: i + 1,
      userId: `user${i + 1}`,
      username: `Athlete${i + 1}`,
      avatar: `https://i.pravatar.cc/150?img=${i + 1}`,
      value: type === 'xp' ? 15000 - (i * 1000) : 50 - (i * 3),
      level: 20 - i,
      badges: 30 - (i * 2),
    }));

    res.json({ leaderboard, type, period });
  } catch (error) {
    console.error('Error fetching leaderboard:', error);
    res.status(500).json({ error: 'Failed to fetch leaderboard' });
  }
});

export default router;
