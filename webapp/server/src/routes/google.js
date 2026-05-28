import express from 'express';
import { google } from 'googleapis';
import User from '../models/User.js';
import { authenticateToken } from './auth.js';

const router = express.Router();

router.use(authenticateToken);

// Helper to get OAuth2 client
const getOAuth2Client = (user) => {
  const oauth2Client = new google.auth.OAuth2(
    process.env.GOOGLE_CLIENT_ID,
    process.env.GOOGLE_CLIENT_SECRET,
    process.env.GOOGLE_CALLBACK_URL
  );
  
  if (user.googleTokens) {
    oauth2Client.setCredentials({
      access_token: user.googleTokens.accessToken,
      refresh_token: user.googleTokens.refreshToken,
      expiry_date: user.googleTokens.expiryDate
    });
  }
  
  return oauth2Client;
};

// Get Google Calendar events
router.get('/calendar/events', async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    if (!user.googleIntegrations.calendar) {
      return res.status(403).json({ error: 'Calendar integration not enabled' });
    }
    
    const oauth2Client = getOAuth2Client(user);
    const calendar = google.calendar({ version: 'v3', auth: oauth2Client });
    
    const response = await calendar.events.list({
      calendarId: 'primary',
      timeMin: new Date().toISOString(),
      maxResults: 10,
      singleEvents: true,
      orderBy: 'startTime'
    });
    
    res.json({ events: response.data.items });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Create workout event in calendar
router.post('/calendar/workout-event', async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    if (!user.googleIntegrations.calendar) {
      return res.status(403).json({ error: 'Calendar integration not enabled' });
    }
    
    const { summary, description, startTime, duration } = req.body;
    
    const oauth2Client = getOAuth2Client(user);
    const calendar = google.calendar({ version: 'v3', auth: oauth2Client });
    
    const endTime = new Date(new Date(startTime).getTime() + duration * 60000);
    
    const event = {
      summary: summary || 'IronCore Workout',
      description,
      start: { dateTime: startTime, timeZone: 'America/Chicago' },
      end: { dateTime: endTime.toISOString(), timeZone: 'America/Chicago' },
      reminders: {
        useDefault: false,
        overrides: [
          { method: 'popup', minutes: 30 }
        ]
      }
    };
    
    const response = await calendar.events.insert({
      calendarId: 'primary',
      resource: event
    });
    
    res.json({ event: response.data });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get Google Fit data
router.get('/fit/data', async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    if (!user.googleIntegrations.fit) {
      return res.status(403).json({ error: 'Fit integration not enabled' });
    }
    
    const oauth2Client = getOAuth2Client(user);
    const fitness = google.fitness({ version: 'v1', auth: oauth2Client });
    
    const endTime = Date.now();
    const startTime = endTime - (7 * 24 * 60 * 60 * 1000); // 7 days ago
    
    const response = await fitness.users.dataset.aggregate({
      userId: 'me',
      requestBody: {
        aggregateBy: [{
          dataTypeName: 'com.google.step_count.delta'
        }],
        bucketByTime: { durationMillis: 86400000 }, // 1 day
        startTimeMillis: startTime,
        endTimeMillis: endTime
      }
    });
    
    res.json({ data: response.data });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Enable/disable Google integrations
router.post('/integrations/:service', async (req, res) => {
  try {
    const { service } = req.params;
    const { enabled } = req.body;
    
    const validServices = ['calendar', 'drive', 'fit', 'keep'];
    if (!validServices.includes(service)) {
      return res.status(400).json({ error: 'Invalid service' });
    }
    
    const user = await User.findById(req.user.id);
    user.googleIntegrations[service] = enabled;
    await user.save();
    
    res.json({
      message: `${service} integration ${enabled ? 'enabled' : 'disabled'}`,
      integrations: user.googleIntegrations
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

export default router;
