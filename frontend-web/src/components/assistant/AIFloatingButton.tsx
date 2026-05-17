import React from 'react';
import { Box, Fab, Tooltip, CircularProgress } from '@mui/material';
import { SmartToy as RobotIcon } from '@mui/icons-material';

interface AIFloatingButtonProps {
  loading: boolean;
  onClick: () => void;
}

export const AIFloatingButton: React.FC<AIFloatingButtonProps> = ({ loading, onClick }) => {
  return (
    <Box sx={{ position: 'fixed', bottom: 24, right: 24, zIndex: 1000 }}>
      <Tooltip title="Talk to NSIP Assistant" placement="left">
        <Fab 
          color="primary" 
          onClick={onClick}
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
  );
};
