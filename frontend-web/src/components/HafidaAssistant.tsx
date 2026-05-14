import { useState, useEffect, useRef } from 'react';
import { 
  Box, Paper, Typography, TextField, IconButton, Stack, 
  Avatar, Chip, Button, Collapse, CircularProgress,
  Tooltip, Badge
} from '@mui/material';
import { 
  Send as SendIcon, 
  Close as CloseIcon, 
  Lightbulb as TipIcon,
  ChevronRight as ActionIcon,
  Mic as MicIcon,
  GraphicEq as VoiceIcon
} from '@mui/icons-material';
import { api } from '../api';

export default function HafidaAssistant() {
  const [open, setOpen] = useState(false);
  const [isListening, setIsListening] = useState(false);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [messages, setMessages] = useState<any[]>([
    { role: 'hafida', text: 'Marhaba! I am Hafida, your National Support Advisor. How can I assist you today?' }
  ]);
  const [proactive, setProactive] = useState<any[]>([]);
  const scrollRef = useRef<HTMLDivElement>(null);
  const speechRef = useRef<any>(null);

  useEffect(() => {
    if (open) {
      fetchProactive();
    }
  }, [open]);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  useEffect(() => {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    if (SpeechRecognition) {
      speechRef.current = new SpeechRecognition();
      speechRef.current.continuous = false;
      speechRef.current.interimResults = false;
      speechRef.current.lang = 'en-US';

      speechRef.current.onresult = (event: any) => {
        const text = event.results[0][0].transcript;
        setQuery(text);
        setIsListening(false);
        setTimeout(() => handleSendWithText(text), 500);
      };

      speechRef.current.onerror = () => setIsListening(false);
      speechRef.current.onend = () => setIsListening(false);
    }
  }, []);

  async function fetchProactive() {
    try {
      const advice = await api.getProactiveAdvice('f00efc85-0ebf-41e2-82f4-f13cfcd8d22e')
        .catch(() => []);
      setProactive(advice);
    } catch (err) {
      console.error(err);
    }
  }

  function handleSend() {
    if (!query.trim()) return;
    handleSendWithText(query);
    setQuery('');
  }

  async function handleSendWithText(text: string) {
    if (!text.trim()) return;
    const userMsg = { role: 'user', text };
    setMessages(prev => [...prev, userMsg]);
    setLoading(true);

    try {
      const res = await api.consultHafida('f00efc85-0ebf-41e2-82f4-f13cfcd8d22e', text, { page: window.location.pathname });

      setMessages(prev => [...prev, { role: 'hafida', text: res.advice, actions: res.suggestedActions }]);
      
      const utterance = new SpeechSynthesisUtterance(res.advice);
      utterance.rate = 0.9;
      utterance.pitch = 1.1;
      window.speechSynthesis.speak(utterance);
    } catch (err) {
      setMessages(prev => [...prev, { role: 'hafida', text: 'Pardon me, I am having trouble connecting to the national grid.' }]);
    } finally {
      setLoading(false);
    }
  }

  function toggleMic() {
    if (isListening) {
      speechRef.current?.stop();
    } else {
      setIsListening(true);
      speechRef.current?.start();
    }
  }

  return (
    <Box sx={{ position: 'fixed', bottom: 32, right: 32, zIndex: 1000, display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
      <Collapse in={open} timeout={400}>
        <Paper sx={{ 
          width: 380, height: 550, mb: 2, display: 'flex', flexDirection: 'column', borderRadius: 4, overflow: 'hidden',
          boxShadow: '0 20px 50px rgba(0,0,0,0.5)', border: '1px solid rgba(255,255,255,0.08)',
          bgcolor: 'rgba(17, 24, 39, 0.98)', backdropFilter: 'blur(20px)', transition: 'height 0.3s ease'
        }}>
          <Box sx={{ p: 2, bgcolor: 'rgba(5, 150, 105, 0.1)', borderBottom: '1px solid rgba(255,255,255,0.05)', display: 'flex', alignItems: 'center', gap: 2 }}>
            <Avatar src="/logo.png" sx={{ width: 32, height: 32, bgcolor: 'transparent' }} />
            <Box sx={{ flexGrow: 1 }}>
              <Typography variant="subtitle2" sx={{ fontWeight: 900, color: '#059669', lineHeight: 1 }}>Hafida Smart AI</Typography>
              <Typography variant="caption" color="text.secondary">Multimodal National Advisor</Typography>
            </Box>
            <IconButton size="small" onClick={() => setOpen(false)} sx={{ color: 'text.secondary' }}>
              <CloseIcon fontSize="small" />
            </IconButton>
          </Box>

          <Box ref={scrollRef} sx={{ flexGrow: 1, overflowY: 'auto', p: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
            {messages.map((m, i) => (
              <Box key={i} sx={{ alignSelf: m.role === 'user' ? 'flex-end' : 'flex-start', maxWidth: '85%' }}>
                <Paper sx={{ 
                  p: 2, borderRadius: m.role === 'user' ? '20px 20px 4px 20px' : '20px 20px 20px 4px',
                  bgcolor: m.role === 'user' ? '#059669' : 'rgba(255,255,255,0.05)', color: m.role === 'user' ? 'white' : 'text.primary'
                }}>
                  <Typography variant="body2">{m.text}</Typography>
                </Paper>
                {m.actions && (
                  <Stack spacing={1} sx={{ mt: 1.5 }}>
                    {m.actions.map((act: string) => (
                      <Button key={act} size="small" variant="outlined" endIcon={<ActionIcon />}
                        sx={{ borderRadius: 2, textTransform: 'none', fontSize: '0.75rem', borderColor: 'rgba(5, 150, 105, 0.3)', color: '#059669' }}
                      >
                        {act}
                      </Button>
                    ))}
                  </Stack>
                )}
              </Box>
            ))}
            {loading && (
              <Box sx={{ display: 'flex', gap: 1, alignItems: 'center', ml: 1 }}>
                <CircularProgress size={16} thickness={6} sx={{ color: '#059669' }} />
                <Typography variant="caption" color="text.secondary">{isListening ? 'Listening...' : 'Hafida is analyzing...'}</Typography>
              </Box>
            )}
          </Box>

          <Box sx={{ p: 2, borderTop: '1px solid rgba(255,255,255,0.05)' }}>
            {proactive.length > 0 && (
              <Stack direction="row" spacing={1} sx={{ mb: 2, overflowX: 'auto', pb: 1 }}>
                {proactive.map(p => (
                  <Chip key={p.id} icon={<TipIcon sx={{ fontSize: '0.9rem !important' }} />} label={p.title} 
                    onClick={() => { setQuery(p.message); handleSendWithText(p.message); }}
                    size="small" sx={{ bgcolor: 'rgba(212, 163, 29, 0.1)', color: '#d4a31d', borderColor: 'rgba(212, 163, 29, 0.2)' }}
                    variant="outlined"
                  />
                ))}
              </Stack>
            )}
            <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
              <TextField fullWidth placeholder={isListening ? "Listening..." : "Ask Hafida..."} size="small" variant="outlined"
                value={query} onChange={(e) => setQuery(e.target.value)} onKeyPress={(e) => e.key === 'Enter' && handleSend()}
                disabled={isListening} sx={{ '& .MuiOutlinedInput-root': { borderRadius: 3, bgcolor: 'rgba(255,255,255,0.03)' } }}
              />
              <Stack direction="row" spacing={1}>
                <Tooltip title={isListening ? "Stop Listening" : "Voice Search"}>
                  <IconButton onClick={toggleMic} 
                    sx={{ 
                      bgcolor: isListening ? 'rgba(239, 68, 68, 0.1)' : 'rgba(5, 150, 105, 0.1)', 
                      color: isListening ? '#ef4444' : '#059669', 
                      border: `1px solid ${isListening ? 'rgba(239, 68, 68, 0.2)' : 'rgba(5, 150, 105, 0.2)'}`,
                      '&:hover': { bgcolor: isListening ? 'rgba(239, 68, 68, 0.2)' : 'rgba(5, 150, 105, 0.2)' } 
                    }}
                  >
                    {isListening ? <VoiceIcon fontSize="small" /> : <MicIcon fontSize="small" />}
                  </IconButton>
                </Tooltip>
                <IconButton onClick={handleSend} sx={{ bgcolor: '#059669', color: 'white', '&:hover': { bgcolor: '#047857' } }}>
                  <SendIcon fontSize="small" />
                </IconButton>
              </Stack>
            </Box>
          </Box>
        </Paper>
      </Collapse>

      <Badge badgeContent={proactive.length} color="warning" sx={{ '& .MuiBadge-badge': { top: 12, right: 12 } }}>
        <Paper onClick={() => setOpen(!open)}
          sx={{ 
            width: 64, height: 64, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
            bgcolor: '#059669', boxShadow: '0 8px 30px rgba(5, 150, 105, 0.4)', transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
            '&:hover': { transform: 'scale(1.1) rotate(5deg)', boxShadow: '0 12px 40px rgba(5, 150, 105, 0.5)' }
          }}
        >
          <Box component="img" src="/logo.png" sx={{ width: 40, height: 40, filter: 'brightness(0) invert(1)' }} />
        </Paper>
      </Badge>
    </Box>
  );
}
