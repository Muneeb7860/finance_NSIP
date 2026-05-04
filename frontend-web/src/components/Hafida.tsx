import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Box, Drawer, Typography, Button, Avatar, IconButton, 
  TextField, Stack, Badge, Fab
} from '@mui/material';
import { 
  SmartToy as AiIcon, NearMe as RedirectIcon, 
  CardMembership as CertificateIcon,
  SupportAgent as TicketIcon, Send as SendIcon
} from '@mui/icons-material';

export default function Hafida() {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState([
    { text: 'Hello! I am Hafida, your NSIP AI Assistant. How can I help you today?', sender: 'ai' }
  ]);
  const navigate = useNavigate();

  const handleOption = (opt: string) => {
    setMessages([...messages, { text: opt, sender: 'user' }]);
    setTimeout(() => {
      let reply = "";
      if (opt.includes('Portfolio')) { reply = "Redirecting you to your Portfolio..."; navigate('/customer/portfolio'); }
      else if (opt.includes('Ticket')) reply = "I have created support ticket #TKT-992. An agent will contact you shortly.";
      else if (opt.includes('Certificate')) reply = "Generating your GOSI Compliance Certificate... Done! Check your downloads.";
      else reply = "I'm sorry, I didn't quite get that. Can you try again?";
      
      setMessages(prev => [...prev, { text: reply, sender: 'ai' }]);
    }, 1000);
  };

  return (
    <>
      <Fab 
        color="primary" 
        sx={{ 
          position: 'fixed', 
          bottom: 30, 
          right: 30, 
          width: 80, 
          height: 80,
          boxShadow: '0 15px 35px rgba(139, 92, 246, 0.4)',
          '&:hover': { transform: 'scale(1.1) rotate(5deg)' },
          transition: 'all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
          '&::after': {
            content: '""',
            position: 'absolute',
            width: '100%',
            height: '100%',
            borderRadius: '50%',
            border: '2px solid #8b5cf6',
            animation: 'pulse 2s infinite',
          }
        }} 
        onClick={() => setOpen(!open)}
      >
        <AiIcon sx={{ fontSize: 40 }} />
      </Fab>
      
      <Drawer 
        anchor="right" 
        open={open} 
        onClose={() => setOpen(false)} 
        slotProps={{ 
          paper: { 
            sx: { 
              width: { xs: '100%', md: 450 }, 
              bgcolor: '#030712', 
              p: 0,
              borderLeft: '1px solid rgba(255, 255, 255, 0.05)',
              boxShadow: '-20px 0 50px rgba(0,0,0,0.5)'
            } 
          } 
        }}
      >
        <Box sx={{ 
          p: 4, 
          background: 'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)', 
          color: 'white', 
          display: 'flex', 
          alignItems: 'center', 
          gap: 2,
          boxShadow: '0 10px 30px rgba(0,0,0,0.2)'
        }}>
          <Badge overlap="circular" anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }} variant="dot" color="success" sx={{ '& .MuiBadge-badge': { width: 12, height: 12, borderRadius: '50%', border: '2px solid white' } }}>
            <Avatar sx={{ width: 50, height: 50, bgcolor: 'rgba(255,255,255,0.2)' }}><AiIcon sx={{ fontSize: 30 }} /></Avatar>
          </Badge>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: -0.5 }}>Hafida</Typography>
            <Typography variant="caption" sx={{ opacity: 0.8 }}>Virtual Assistant • Online</Typography>
          </Box>
        </Box>
        <Box sx={{ flexGrow: 1, p: 2, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 2 }}>
          {messages.map((m, i) => (
            <Box key={i} sx={{ alignSelf: m.sender === 'ai' ? 'flex-start' : 'flex-end', bgcolor: m.sender === 'ai' ? 'rgba(255,255,255,0.05)' : 'primary.main', p: 2, borderRadius: 2, maxWidth: '80%' }}>
              <Typography variant="body2">{m.text}</Typography>
            </Box>
          ))}
        </Box>
        <Box sx={{ p: 2, borderTop: '1px solid rgba(255,255,255,0.1)' }}>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>Quick Actions:</Typography>
          <Stack spacing={1}>
            <Button size="small" variant="outlined" startIcon={<RedirectIcon />} onClick={() => handleOption('Go to Portfolio')}>Go to Portfolio</Button>
            <Button size="small" variant="outlined" startIcon={<TicketIcon />} onClick={() => handleOption('Create Support Ticket')}>Create Support Ticket</Button>
            <Button size="small" variant="outlined" startIcon={<CertificateIcon />} onClick={() => handleOption('Generate Certificate')}>Generate Certificate</Button>
          </Stack>
          <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
            <TextField fullWidth size="small" placeholder="Ask Hafida anything..." />
            <IconButton color="primary"><SendIcon /></IconButton>
          </Box>
        </Box>
      </Drawer>
    </>
  );
}
