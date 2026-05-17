import React from 'react';
import { Box, Typography } from '@mui/material';
import {
  LiveKitRoom,
  RoomAudioRenderer,
  ControlBar,
  Chat,
} from '@livekit/components-react';
import '@livekit/components-styles';

interface AILiveKitInterfaceProps {
  token: string;
  serverUrl: string;
  onDisconnected: () => void;
}

export const AILiveKitInterface: React.FC<AILiveKitInterfaceProps> = ({ 
  token, 
  serverUrl, 
  onDisconnected 
}) => {
  return (
    <LiveKitRoom
      video={false}
      audio={true}
      token={token}
      serverUrl={serverUrl}
      data-lk-theme="default"
      onDisconnected={onDisconnected}
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
  );
};
