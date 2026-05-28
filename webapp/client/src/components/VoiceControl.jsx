import { useState, useEffect } from 'react'
import { voiceCommands } from '../utils/voiceCommands'
import {
  MicrophoneIcon,
  SpeakerWaveIcon,
  SpeakerXMarkIcon,
} from '@heroicons/react/24/outline'
import toast from 'react-hot-toast'

export default function VoiceControl() {
  const [isListening, setIsListening] = useState(false)
  const [isAudioEnabled, setIsAudioEnabled] = useState(true)
  const [isSupported, setIsSupported] = useState(false)
  const [showCommands, setShowCommands] = useState(false)

  useEffect(() => {
    // Check if voice features are supported
    const supported = 
      ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) &&
      'speechSynthesis' in window;
    
    setIsSupported(supported);

    if (!supported) {
      console.log('[Voice] Voice features not supported in this browser');
    }

    // Listen for voice command events
    const handleVoiceCommand = (event) => {
      const { action } = event.detail;
      console.log('[Voice] Command action:', action);
      
      // Handle specific actions
      switch (action) {
        case 'start-workout':
          toast.success('Starting workout...');
          break;
        case 'finish-workout':
          toast.success('Finishing workout...');
          break;
        case 'next-set':
          toast.success('Next set!');
          break;
        case 'start-rest':
          toast.success('Rest timer started');
          break;
        case 'log-meal':
          toast.success('Opening meal log...');
          break;
        case 'show-stats':
          toast.success('Showing statistics...');
          break;
        default:
          console.log('[Voice] Unhandled action:', action);
      }
    };

    window.addEventListener('voice-command', handleVoiceCommand);

    return () => {
      window.removeEventListener('voice-command', handleVoiceCommand);
    };
  }, []);

  const toggleListening = async () => {
    if (!isSupported) {
      toast.error('Voice commands not supported in this browser');
      return;
    }

    if (!isListening) {
      // Request permission first
      const hasPermission = await requestMicrophonePermission();
      if (!hasPermission) {
        toast.error('Microphone permission denied');
        return;
      }

      const started = voiceCommands.startListening();
      if (started) {
        setIsListening(true);
        toast.success('Voice commands activated');
        voiceCommands.speak('Voice commands activated. Say help for available commands.');
      } else {
        toast.error('Failed to start voice commands');
      }
    } else {
      voiceCommands.stopListening();
      setIsListening(false);
      toast.success('Voice commands deactivated');
    }
  };

  const toggleAudio = () => {
    const newState = !isAudioEnabled;
    setIsAudioEnabled(newState);
    voiceCommands.audioCoachingEnabled = newState;
    
    if (newState) {
      toast.success('Audio coaching enabled');
      voiceCommands.speak('Audio coaching enabled');
    } else {
      toast.success('Audio coaching disabled');
    }
  };

  const requestMicrophonePermission = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      stream.getTracks().forEach(track => track.stop());
      return true;
    } catch (error) {
      console.error('[Voice] Microphone permission denied:', error);
      return false;
    }
  };

  const availableCommands = [
    { category: 'Navigation', commands: ['go to dashboard', 'open workouts', 'show metrics', 'open nutrition'] },
    { category: 'Workout', commands: ['start workout', 'finish workout', 'next set', 'rest'] },
    { category: 'Nutrition', commands: ['log meal', 'add meal'] },
    { category: 'Stats', commands: ['show stats', 'my progress'] },
    { category: 'Control', commands: ['help', 'mute', 'unmute', 'stop listening'] },
  ];

  if (!isSupported) {
    return null;
  }

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {/* Voice Control Buttons */}
      <div className="flex flex-col gap-3">
        {/* Microphone Button */}
        <button
          onClick={toggleListening}
          className={`p-4 rounded-full shadow-lg transition-all duration-200 ${
            isListening
              ? 'bg-red-500 hover:bg-red-600 animate-pulse'
              : 'bg-primary-500 hover:bg-primary-600'
          }`}
          title={isListening ? 'Stop listening' : 'Start voice commands'}
        >
          <MicrophoneIcon className="w-6 h-6 text-white" />
        </button>

        {/* Audio Toggle Button */}
        <button
          onClick={toggleAudio}
          className="p-4 rounded-full bg-gray-700 hover:bg-gray-600 shadow-lg transition-all duration-200"
          title={isAudioEnabled ? 'Mute audio coaching' : 'Enable audio coaching'}
        >
          {isAudioEnabled ? (
            <SpeakerWaveIcon className="w-6 h-6 text-white" />
          ) : (
            <SpeakerXMarkIcon className="w-6 h-6 text-white" />
          )}
        </button>

        {/* Help Button */}
        <button
          onClick={() => setShowCommands(!showCommands)}
          className="p-4 rounded-full bg-gray-700 hover:bg-gray-600 shadow-lg transition-all duration-200"
          title="Show available commands"
        >
          <span className="text-white font-bold text-lg">?</span>
        </button>
      </div>

      {/* Commands Modal */}
      {showCommands && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-40 flex items-center justify-center p-4">
          <div className="bg-ironcore-dark rounded-xl shadow-2xl border border-gray-800 max-w-2xl w-full max-h-[80vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-white">Voice Commands</h2>
                <button
                  onClick={() => setShowCommands(false)}
                  className="text-gray-400 hover:text-white"
                >
                  ✕
                </button>
              </div>

              <div className="space-y-6">
                {availableCommands.map((category) => (
                  <div key={category.category}>
                    <h3 className="text-lg font-semibold text-primary-400 mb-3">
                      {category.category}
                    </h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                      {category.commands.map((command) => (
                        <div
                          key={command}
                          className="bg-ironcore-darker rounded-lg p-3 border border-gray-700"
                        >
                          <code className="text-gray-300 text-sm">"{command}"</code>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>

              <div className="mt-6 p-4 bg-blue-500/10 border border-blue-500/20 rounded-lg">
                <p className="text-blue-300 text-sm">
                  <strong>Tip:</strong> Speak clearly and naturally. The system will understand
                  variations of these commands.
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Listening Indicator */}
      {isListening && (
        <div className="fixed top-6 right-6 bg-red-500 text-white px-4 py-2 rounded-full shadow-lg flex items-center gap-2 animate-pulse">
          <div className="w-2 h-2 bg-white rounded-full animate-ping"></div>
          <span className="font-semibold">Listening...</span>
        </div>
      )}
    </div>
  );
}
