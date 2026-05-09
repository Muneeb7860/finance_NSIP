import React, { useState } from 'react';
import { 
  Box, 
  Fab, 
  Paper, 
  Typography, 
  IconButton, 
  CircularProgress,
  Fade,
  Tooltip
} from '@mui/material';
import { 
  SmartToy as RobotIcon, 
  Close as CloseIcon
} from '@mui/icons-material';
import {
  LiveKitRoom,
  RoomAudioRenderer,
  ControlBar,
  Chat,
} from '@livekit/components-react';
import '@livekit/components-styles';
import { api } from '../../api';

export const AIAssistant: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const serverUrl = import.meta.env.VITE_LIVEKIT_URL || 'wss://nsip-assistant.livekit.cloud';

  const handleToggle = async () => {
    if (!isOpen) {
      setLoading(true);
      try {
        const response = await api.getLiveKitToken();
        setToken(response.token);
        setIsOpen(true);
      } catch (err: any) {
        console.error('Failed to get LiveKit token:', err);
      } finally {
        setLoading(false);
      }
    } else {
      setIsOpen(false);
      setToken(null);
    }
  };

  return (
    <>
      {/* Floating Action Button */}
      <Box sx={{ position: 'fixed', bottom: 24, right: 24, zIndex: 1000 }}>
        <Tooltip title="Talk to NSIP Assistant" placement="left">
          <Fab 
            color="primary" 
            onClick={handleToggle}
            sx={{ 
              boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
              transition: 'transform 0.3s ease',
              '&:hover': { transform: 'scale(1.1)' }
            }}
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : <RobotIcon />}
          </Fab>
        </Tooltip>
      </Box>

      {/* Assistant Window */}
      <Fade in={isOpen}>
        <Paper
          elevation={24}
          sx={{
            position: 'fixed',
            bottom: 96,
            right: 24,
            width: 400,
            height: 600,
            borderRadius: 4,
            overflow: 'hidden',
            display: 'flex',
            flexDirection: 'column',
            zIndex: 1000,
            background: 'rgba(255, 255, 255, 0.95)',
            backdropFilter: 'blur(10px)',
            border: '1px solid rgba(255, 255, 255, 0.3)'
          }}
        >
          {/* Header */}
          <Box sx={{ 
            p: 2, 
            background: 'linear-gradient(135deg, #1976d2 0%, #1565c0 100%)', 
            color: 'white',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <RobotIcon />
              <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                Multimodal Assistant
              </Typography>
            </Box>
            <IconButton size="small" color="inherit" onClick={() => setIsOpen(false)}>
              <CloseIcon />
            </IconButton>
          </Box>

          {/* LiveKit Interface */}
          <Box sx={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
            {token ? (
              <LiveKitRoom
                video={false}
                audio={true}
                token={token}
                serverUrl={serverUrl}
                data-lk-theme="default"
                onDisconnected={() => setIsOpen(false)}
                style={{ height: '100%' }}
              >
                <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                  <Box sx={{ flex: 1, p: 2, overflowY: 'auto' }}>
                    <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 2 }}>
                      Connected to Gemini Flash via LiveKit
                    </Typography>
                    <Chat />
                  </Box>
                  <ControlBar variation="minimal" controls={{ camera: true, microphone: true, screenShare: false, leave: true }} />
                  <RoomAudioRenderer />
                </Box>
              </LiveKitRoom>
            ) : (
              <Box sx={{ height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <CircularProgress />
              </Box>
            )}
          </Box>
        </Paper>
      </Fade>
    </>
  );
};
