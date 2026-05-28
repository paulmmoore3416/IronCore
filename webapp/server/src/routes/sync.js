import express from 'express';
import { authenticateToken } from './auth.js';

const router = express.Router();

router.use(authenticateToken);

// Sync status endpoint
router.get('/status', async (req, res) => {
  try {
    const userId = req.user.id;
    
    res.json({
      status: 'connected',
      userId,
      timestamp: new Date(),
      serverTime: new Date().toISOString()
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

export default router;
