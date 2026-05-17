import React from 'react';
import { Box, Typography, Paper, IconButton } from '@mui/material';
import { SmartToy as RobotIcon } from '@mui/icons-material';

export const AIDemoFallback: React.FC = () => {
  return (
    <Box sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column', gap: 2 }}>
      <Box sx={{ flex: 1, bgcolor: 'rgba(0,0,0,0.02)', borderRadius: 2, p: 2, overflowY: 'auto' }}>
        <Box sx={{ mb: 2, display: 'flex', gap: 1 }}>
          <Box sx={{ p: 1.5, bgcolor: 'primary.main', color: 'white', borderRadius: '15px 15px 15px 0', maxWidth: '80%' }}>
            <Typography variant="body2">Hello! I am Hafida, your NSIP Assistant. How can I help you today with your social insurance?</Typography>
          </Box>
        </Box>
        <Box sx={{ mb: 2, display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
          <Box sx={{ p: 1.5, bgcolor: 'grey.100', borderRadius: '15px 15px 0 15px', maxWidth: '80%' }}>
            <Typography variant="body2">What are my pension options?</Typography>
          </Box>
        </Box>
        <Box sx={{ mb: 2, display: 'flex', gap: 1 }}>
          <Box sx={{ p: 1.5, bgcolor: 'primary.main', color: 'white', borderRadius: '15px 15px 15px 0', maxWidth: '80%' }}>
            <Typography variant="body2">Based on your portfolio, you are eligible for the National Pension Scheme (NPS) and the Taqdeer Early Retirement plan. Would you like to see a projection?</Typography>
          </Box>
        </Box>
      </Box>
      <Paper sx={{ p: 1, display: 'flex', gap: 1, borderRadius: 3, border: '1px solid rgba(0,0,0,0.1)' }}>
        <Box component="input" sx={{ flex: 1, border: 'none', outline: 'none', px: 1, fontSize: '0.9rem' }} placeholder="Type or speak..." />
        <IconButton size="small" color="primary"><RobotIcon /></IconButton>
      </Paper>
      <Typography variant="caption" color="text.secondary" align="center">Demo Mode Active • Connectivity Offline</Typography>
    </Box>
  );
};
