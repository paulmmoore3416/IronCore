// Voice Commands and Audio Coaching System
class VoiceCommandSystem {
  constructor() {
    this.recognition = null;
    this.synthesis = window.speechSynthesis;
    this.isListening = false;
    this.commands = new Map();
    this.audioCoachingEnabled = true;
    this.voiceCommandsEnabled = true;
    
    // Initialize Speech Recognition
    if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      this.recognition = new SpeechRecognition();
      this.recognition.continuous = true;
      this.recognition.interimResults = false;
      this.recognition.lang = 'en-US';
      
      this.setupRecognitionHandlers();
    }
    
    this.registerDefaultCommands();
  }

  setupRecognitionHandlers() {
    this.recognition.onstart = () => {
      console.log('[Voice] Recognition started');
      this.isListening = true;
    };

    this.recognition.onend = () => {
      console.log('[Voice] Recognition ended');
      this.isListening = false;
      
      // Auto-restart if enabled
      if (this.voiceCommandsEnabled) {
        setTimeout(() => this.startListening(), 1000);
      }
    };

    this.recognition.onresult = (event) => {
      const last = event.results.length - 1;
      const command = event.results[last][0].transcript.toLowerCase().trim();
      console.log('[Voice] Command received:', command);
      
      this.processCommand(command);
    };

    this.recognition.onerror = (event) => {
      console.error('[Voice] Recognition error:', event.error);
      
      if (event.error === 'no-speech') {
        console.log('[Voice] No speech detected, continuing...');
      }
    };
  }

  registerDefaultCommands() {
    // Navigation commands
    this.registerCommand(['go to dashboard', 'open dashboard', 'show dashboard'], () => {
      window.location.href = '/';
      this.speak('Opening dashboard');
    });

    this.registerCommand(['go to workouts', 'open workouts', 'show workouts'], () => {
      window.location.href = '/workouts';
      this.speak('Opening workouts');
    });

    this.registerCommand(['go to nutrition', 'open nutrition', 'show nutrition'], () => {
      window.location.href = '/nutrition';
      this.speak('Opening nutrition');
    });

    this.registerCommand(['go to metrics', 'open metrics', 'show metrics'], () => {
      window.location.href = '/metrics';
      this.speak('Opening metrics');
    });

    this.registerCommand(['go to progress', 'open progress', 'show progress'], () => {
      window.location.href = '/progress';
      this.speak('Opening progress');
    });

    // Workout commands
    this.registerCommand(['start workout', 'begin workout', 'log workout'], () => {
      this.speak('Starting workout logging');
      // Trigger workout modal or navigation
      window.dispatchEvent(new CustomEvent('voice-command', { 
        detail: { action: 'start-workout' } 
      }));
    });

    this.registerCommand(['finish workout', 'end workout', 'complete workout'], () => {
      this.speak('Finishing workout');
      window.dispatchEvent(new CustomEvent('voice-command', { 
        detail: { action: 'finish-workout' } 
      }));
    });

    this.registerCommand(['next set', 'start next set'], () => {
      this.speak('Starting next set');
      window.dispatchEvent(new CustomEvent('voice-command', { 
        detail: { action: 'next-set' } 
      }));
    });

    this.registerCommand(['rest', 'start rest', 'rest timer'], () => {
      this.speak('Starting rest timer');
      window.dispatchEvent(new CustomEvent('voice-command', { 
        detail: { action: 'start-rest' } 
      }));
    });

    // Nutrition commands
    this.registerCommand(['log meal', 'add meal', 'record meal'], () => {
      this.speak('Opening meal logging');
      window.dispatchEvent(new CustomEvent('voice-command', { 
        detail: { action: 'log-meal' } 
      }));
    });

    // Stats commands
    this.registerCommand(['show stats', 'my stats', 'view stats'], () => {
      this.speak('Showing your statistics');
      window.dispatchEvent(new CustomEvent('voice-command', { 
        detail: { action: 'show-stats' } 
      }));
    });

    this.registerCommand(['show progress', 'my progress'], () => {
      this.speak('Showing your progress');
      window.location.href = '/progress';
    });

    // Help command
    this.registerCommand(['help', 'what can you do', 'commands'], () => {
      this.speak('You can say commands like: start workout, log meal, show stats, go to dashboard, or ask for help');
    });

    // Control commands
    this.registerCommand(['stop listening', 'disable voice', 'turn off voice'], () => {
      this.speak('Voice commands disabled');
      this.stopListening();
      this.voiceCommandsEnabled = false;
    });

    this.registerCommand(['mute', 'silence', 'quiet'], () => {
      this.audioCoachingEnabled = false;
      this.speak('Audio coaching muted');
    });

    this.registerCommand(['unmute', 'speak', 'enable audio'], () => {
      this.audioCoachingEnabled = true;
      this.speak('Audio coaching enabled');
    });
  }

  registerCommand(phrases, callback) {
    if (Array.isArray(phrases)) {
      phrases.forEach(phrase => {
        this.commands.set(phrase.toLowerCase(), callback);
      });
    } else {
      this.commands.set(phrases.toLowerCase(), callback);
    }
  }

  processCommand(command) {
    // Try exact match first
    if (this.commands.has(command)) {
      this.commands.get(command)();
      return;
    }

    // Try partial match
    for (const [phrase, callback] of this.commands.entries()) {
      if (command.includes(phrase) || phrase.includes(command)) {
        callback();
        return;
      }
    }

    // No match found
    console.log('[Voice] Unknown command:', command);
    this.speak('Sorry, I didn\'t understand that command');
  }

  startListening() {
    if (!this.recognition) {
      console.error('[Voice] Speech recognition not supported');
      return false;
    }

    if (!this.isListening) {
      try {
        this.recognition.start();
        return true;
      } catch (error) {
        console.error('[Voice] Failed to start recognition:', error);
        return false;
      }
    }
    return true;
  }

  stopListening() {
    if (this.recognition && this.isListening) {
      this.recognition.stop();
    }
  }

  speak(text, options = {}) {
    if (!this.audioCoachingEnabled) {
      return;
    }

    if (!this.synthesis) {
      console.error('[Voice] Speech synthesis not supported');
      return;
    }

    // Cancel any ongoing speech
    this.synthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(text);
    utterance.rate = options.rate || 1.0;
    utterance.pitch = options.pitch || 1.0;
    utterance.volume = options.volume || 1.0;
    utterance.lang = options.lang || 'en-US';

    utterance.onend = () => {
      console.log('[Voice] Finished speaking:', text);
    };

    utterance.onerror = (event) => {
      console.error('[Voice] Speech error:', event);
    };

    this.synthesis.speak(utterance);
  }

  // Audio coaching for workouts
  coachWorkout(phase, data = {}) {
    const messages = {
      'workout-start': 'Workout started. Let\'s get it done!',
      'set-complete': `Set ${data.setNumber} complete. ${data.reps} reps at ${data.weight} pounds. Great job!`,
      'rest-start': `Rest for ${data.duration} seconds. Catch your breath.`,
      'rest-halfway': 'Halfway through your rest. Get ready for the next set.',
      'rest-end': 'Rest complete. Time for the next set!',
      'exercise-complete': `${data.exercise} complete. Moving to next exercise.`,
      'workout-complete': `Workout complete! You did ${data.totalSets} sets and burned approximately ${data.calories} calories. Excellent work!`,
      'pr-achieved': `Personal record! You just hit a new PR on ${data.exercise}!`,
      'form-reminder': 'Remember to maintain proper form. Quality over quantity.',
      'hydration-reminder': 'Don\'t forget to hydrate!',
      'motivation': [
        'You\'re doing great! Keep pushing!',
        'Strong work! Stay focused!',
        'Excellent form! Keep it up!',
        'You\'ve got this! One more rep!',
        'Beast mode activated!',
      ],
    };

    let message = messages[phase];
    
    if (Array.isArray(message)) {
      message = message[Math.floor(Math.random() * message.length)];
    }

    if (message) {
      this.speak(message);
    }
  }

  // Countdown timer with audio
  countdown(seconds, onComplete) {
    let remaining = seconds;
    
    const interval = setInterval(() => {
      if (remaining === 10 || remaining === 5 || remaining === 3 || remaining === 2 || remaining === 1) {
        this.speak(remaining.toString());
      }
      
      remaining--;
      
      if (remaining < 0) {
        clearInterval(interval);
        this.speak('Go!');
        if (onComplete) onComplete();
      }
    }, 1000);

    return interval;
  }

  // Interval timer for HIIT workouts
  intervalTimer(workSeconds, restSeconds, rounds, onWorkStart, onRestStart, onComplete) {
    let currentRound = 1;
    let isWorking = true;
    
    const runInterval = () => {
      if (currentRound > rounds) {
        this.speak(`Interval training complete! You finished ${rounds} rounds. Amazing work!`);
        if (onComplete) onComplete();
        return;
      }

      if (isWorking) {
        this.speak(`Round ${currentRound}. Work for ${workSeconds} seconds. Go!`);
        if (onWorkStart) onWorkStart(currentRound);
        
        setTimeout(() => {
          isWorking = false;
          runInterval();
        }, workSeconds * 1000);
      } else {
        this.speak(`Rest for ${restSeconds} seconds`);
        if (onRestStart) onRestStart(currentRound);
        
        setTimeout(() => {
          isWorking = true;
          currentRound++;
          runInterval();
        }, restSeconds * 1000);
      }
    };

    runInterval();
  }

  // Check if voice features are supported
  static isSupported() {
    return (
      ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) &&
      'speechSynthesis' in window
    );
  }

  // Request microphone permission
  static async requestPermission() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      stream.getTracks().forEach(track => track.stop());
      return true;
    } catch (error) {
      console.error('[Voice] Microphone permission denied:', error);
      return false;
    }
  }
}

// Export singleton instance
export const voiceCommands = new VoiceCommandSystem();

// Export class for custom instances
export default VoiceCommandSystem;
