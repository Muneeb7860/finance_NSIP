import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Paper, 
  Typography, 
  IconButton, 
  Fade
} from '@mui/material';
import { 
  SmartToy as RobotIcon, 
  Close as CloseIcon
} from '@mui/icons-material';
import { api } from '../../api';

import { AIFloatingButton } from './AIFloatingButton';
import { AILiveKitInterface } from './AILiveKitInterface';
import { AIDemoFallback } from './AIDemoFallback';

export const AIAssistant: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const serverUrl = import.meta.env.VITE_LIVEKIT_URL || 'wss://nsip-assistant.livekit.cloud';

  // Proactive AI Intervention: Open assistant after 15 seconds if untouched
  useEffect(() => {
    const timer = setTimeout(() => {
      if (!isOpen) {
        setIsOpen(true);
      }
    }, 15000);
    return () => clearTimeout(timer);
  }, [isOpen]);

  const handleToggle = async () => {
    if (!isOpen) {
      setLoading(true);
      try {
        const response = await api.getLiveKitToken('user123');
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
      <AIFloatingButton loading={loading} onClick={handleToggle} />

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

          {/* LiveKit Interface / Demo Fallback */}
          <Box sx={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
            {token ? (
              <AILiveKitInterface 
                token={token} 
                serverUrl={serverUrl} 
                onDisconnected={() => setIsOpen(false)} 
              />
            ) : (
              <AIDemoFallback />
            )}
          </Box>
        </Paper>
      </Fade>
    </>
  );
};
