import express from 'express';
import { authenticateToken as auth } from '../middleware/auth.js';

const router = express.Router();

// Get activity feed
router.get('/feed', auth, async (req, res) => {
  try {
    // Mock data for now - replace with actual database queries
    const activities = [
      {
        id: '1',
        userId: 'user1',
        userName: 'Sarah Johnson',
        type: 'workout',
        title: 'Morning Run',
        duration: '45 min',
        calories: 450,
        distance: '8.5 km',
        likes: 12,
        comments: 3,
        timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
      },
      {
        id: '2',
        userId: 'user2',
        userName: 'Mike Chen',
        type: 'workout',
        title: 'Upper Body Strength',
        duration: '60 min',
        calories: 380,
        distance: null,
        likes: 8,
        comments: 2,
        timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000),
      },
      {
        id: '3',
        userId: 'user3',
        userName: 'Emma Davis',
        type: 'workout',
        title: 'Yoga Flow',
        duration: '30 min',
        calories: 150,
        distance: null,
        likes: 15,
        comments: 5,
        timestamp: new Date(Date.now() - 6 * 60 * 60 * 1000),
      },
    ];

    res.json({ activities });
  } catch (error) {
    console.error('Error fetching activity feed:', error);
    res.status(500).json({ error: 'Failed to fetch activity feed' });
  }
});

// Get leaderboard
router.get('/leaderboard', auth, async (req, res) => {
  try {
    const period = req.query.period || 'weekly';
    
    // Mock leaderboard data
    const leaderboard = [
      { rank: 1, userId: 'user1', name: 'Sarah Johnson', points: 15420, trend: '+250' },
      { rank: 2, userId: 'user2', name: 'Mike Chen', points: 14890, trend: '+180' },
      { rank: 3, userId: 'user3', name: 'Emma Davis', points: 13560, trend: '+320' },
      { rank: 4, userId: req.userId, name: 'You', points: 12450, trend: '+250' },
      { rank: 5, userId: 'user4', name: 'Alex Rodriguez', points: 11230, trend: '+190' },
    ];

    res.json({ leaderboard, period });
  } catch (error) {
    console.error('Error fetching leaderboard:', error);
    res.status(500).json({ error: 'Failed to fetch leaderboard' });
  }
});

// Get active challenges
router.get('/challenges', auth, async (req, res) => {
  try {
    // Mock challenges data
    const challenges = [
      {
        id: 'challenge1',
        type: 'steps',
        name: 'Step Challenge',
        target: 100000,
        unit: 'steps',
        progress: 65000,
        participants: 24,
        daysRemaining: 7,
        active: true,
      },
      {
        id: 'challenge2',
        type: 'workouts',
        name: 'Workout Streak',
        target: 7,
        unit: 'days',
        progress: 5,
        participants: 18,
        daysRemaining: 7,
        active: true,
      },
      {
        id: 'challenge3',
        type: 'calories',
        name: 'Calorie Burn',
        target: 20000,
        unit: 'cal',
        progress: 12500,
        participants: 32,
        daysRemaining: 7,
        active: true,
      },
    ];

    res.json({ challenges });
  } catch (error) {
    console.error('Error fetching challenges:', error);
    res.status(500).json({ error: 'Failed to fetch challenges' });
  }
});

// Join a challenge
router.post('/challenges/:id/join', auth, async (req, res) => {
  try {
    const challengeId = req.params.id;
    
    // Mock joining challenge
    res.json({ 
      success: true, 
      message: 'Successfully joined challenge',
      challengeId 
    });
  } catch (error) {
    console.error('Error joining challenge:', error);
    res.status(500).json({ error: 'Failed to join challenge' });
  }
});

// Get friends list
router.get('/friends', auth, async (req, res) => {
  try {
    // Mock friends data
    const friends = [
      { id: 'friend1', name: 'Sarah Johnson', status: 'online', lastActivity: 'Completed 5K run' },
      { id: 'friend2', name: 'Mike Chen', status: 'offline', lastActivity: 'Gym session 2h ago' },
      { id: 'friend3', name: 'Emma Davis', status: 'online', lastActivity: 'Yoga class' },
      { id: 'friend4', name: 'Alex Rodriguez', status: 'offline', lastActivity: 'Cycling 5h ago' },
    ];

    res.json({ friends });
  } catch (error) {
    console.error('Error fetching friends:', error);
    res.status(500).json({ error: 'Failed to fetch friends' });
  }
});

// Like an activity
router.post('/activities/:id/like', auth, async (req, res) => {
  try {
    const activityId = req.params.id;
    
    // Mock liking activity
    res.json({ 
      success: true, 
      message: 'Activity liked',
      activityId,
      likes: Math.floor(Math.random() * 20) + 1
    });
  } catch (error) {
    console.error('Error liking activity:', error);
    res.status(500).json({ error: 'Failed to like activity' });
  }
});

// Post a comment on an activity
router.post('/activities/:id/comment', auth, async (req, res) => {
  try {
    const activityId = req.params.id;
    const { comment } = req.body;
    
    if (!comment) {
      return res.status(400).json({ error: 'Comment text is required' });
    }

    // Mock posting comment
    res.json({ 
      success: true, 
      message: 'Comment posted',
      activityId,
      comment: {
        id: Date.now().toString(),
        userId: req.userId,
        text: comment,
        timestamp: new Date(),
      }
    });
  } catch (error) {
    console.error('Error posting comment:', error);
    res.status(500).json({ error: 'Failed to post comment' });
  }
});

// Share an activity
router.post('/activities/:id/share', auth, async (req, res) => {
  try {
    const activityId = req.params.id;
    
    // Mock sharing activity
    res.json({ 
      success: true, 
      message: 'Activity shared',
      activityId,
      shareUrl: `https://ironcore.app/activity/${activityId}`
    });
  } catch (error) {
    console.error('Error sharing activity:', error);
    res.status(500).json({ error: 'Failed to share activity' });
  }
});

// Send friend request
router.post('/friends/request', auth, async (req, res) => {
  try {
    const { userId } = req.body;
    
    if (!userId) {
      return res.status(400).json({ error: 'User ID is required' });
    }

    // Mock sending friend request
    res.json({ 
      success: true, 
      message: 'Friend request sent',
      userId
    });
  } catch (error) {
    console.error('Error sending friend request:', error);
    res.status(500).json({ error: 'Failed to send friend request' });
  }
});

// Get user stats for social profile
router.get('/stats', auth, async (req, res) => {
  try {
    const stats = {
      globalRank: 247,
      percentile: 5,
      totalPoints: 12450,
      weeklyPoints: 250,
      friendsCount: 24,
      activeFriends: 12,
      activeChallenges: 3,
      challengesCompleted: 15,
      totalWorkouts: 156,
      totalDistance: 1250.5,
      totalCalories: 185000,
    };

    res.json({ stats });
  } catch (error) {
    console.error('Error fetching social stats:', error);
    res.status(500).json({ error: 'Failed to fetch social stats' });
  }
});

export default router;