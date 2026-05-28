import passport from 'passport';
import { Strategy as GoogleStrategy } from 'passport-google-oauth20';
import User from '../models/User.js';

passport.serializeUser((user, done) => {
  done(null, user.id);
});

passport.deserializeUser(async (id, done) => {
  try {
    const user = await User.findById(id);
    done(null, user);
  } catch (error) {
    done(error, null);
  }
});

// Google OAuth Strategy - only configure if credentials are provided
if (process.env.GOOGLE_CLIENT_ID && process.env.GOOGLE_CLIENT_SECRET) {
  passport.use(new GoogleStrategy({
      clientID: process.env.GOOGLE_CLIENT_ID,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET,
      callbackURL: process.env.GOOGLE_CALLBACK_URL || 'http://localhost:5000/auth/google/callback',
      scope: [
        'profile',
        'email',
        'https://www.googleapis.com/auth/calendar',
        'https://www.googleapis.com/auth/drive.file',
        'https://www.googleapis.com/auth/fitness.activity.read',
        'https://www.googleapis.com/auth/fitness.body.read',
        'https://www.googleapis.com/auth/keep.readonly'
      ]
    },
    async (accessToken, refreshToken, profile, done) => {
      try {
        // Check if user already exists
        let user = await User.findOne({ googleId: profile.id });
        
        if (user) {
          // Update tokens
          user.googleTokens = {
            accessToken,
            refreshToken: refreshToken || user.googleTokens.refreshToken,
            expiryDate: new Date(Date.now() + 3600000) // 1 hour
          };
          user.lastLogin = new Date();
          await user.save();
          return done(null, user);
        }
        
        // Check if user exists with same email
        user = await User.findOne({ email: profile.emails[0].value });
        
        if (user) {
          // Link Google account to existing user
          user.googleId = profile.id;
          user.googleTokens = {
            accessToken,
            refreshToken,
            expiryDate: new Date(Date.now() + 3600000)
          };
          user.profilePicture = profile.photos[0]?.value;
          user.lastLogin = new Date();
          await user.save();
          return done(null, user);
        }
        
        // Create new user
        user = await User.create({
          googleId: profile.id,
          email: profile.emails[0].value,
          name: profile.displayName,
          profilePicture: profile.photos[0]?.value,
          googleTokens: {
            accessToken,
            refreshToken,
            expiryDate: new Date(Date.now() + 3600000)
          },
          lastLogin: new Date()
        });
        
        done(null, user);
      } catch (error) {
        done(error, null);
      }
    }
  ));
  console.log('✅ Google OAuth configured');
} else {
  console.log('⚠️  Google OAuth not configured - using email/password authentication only');
}

export default passport;