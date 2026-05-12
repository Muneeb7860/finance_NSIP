import { useState, useEffect } from 'react';
import { Box, Typography, Grid, Card, Button, Chip, Stack, alpha, LinearProgress } from '@mui/material';
import { Event as EventIcon, LocationOn as LocationIcon, AccessTime as TimeIcon, Stars as PointsIcon } from '@mui/icons-material';
import { api } from '../../api';

export default function EventsPage() {
  const [events, setEvents] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [rsvpLoading, setRsvpLoading] = useState<string | null>(null);

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    try {
      const data = await api.getLiveEvents();
      setEvents(data);
    } catch (err) {
      console.error('Failed to fetch events:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRSVP = async (eventId: string) => {
    setRsvpLoading(eventId);
    try {
      // Mock userId for demo
      const userId = '947458a5-6912-4b1e-b6db-e56cfbdc4bcc';
      await api.rsvpToEvent(eventId, userId);
      alert('RSVP Successful! You will earn points upon attendance.');
    } catch (err: any) {
      alert(err.message || 'Failed to RSVP');
    } finally {
      setRsvpLoading(null);
    }
  };

  if (loading) return <LinearProgress />;

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>National Impact Events</Typography>
        <Typography variant="subtitle1" color="text.secondary">Join community initiatives, gain experience, and earn reward points.</Typography>
      </Box>

      {events.length === 0 ? (
        <Card sx={{ p: 6, textAlign: 'center', bgcolor: alpha('#fff', 0.02) }}>
          <EventIcon sx={{ fontSize: 60, opacity: 0.1, mb: 2 }} />
          <Typography color="text.secondary">No live events available at the moment. Check back soon!</Typography>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {events.map((event) => (
            <Grid key={event.id} size={{ xs: 12, md: 6, lg: 4 }}>
              <Card sx={{ 
                height: '100%', 
                display: 'flex', 
                flexDirection: 'column',
                border: '1px solid rgba(255,255,255,0.05)',
                transition: 'transform 0.2s',
                '&:hover': { transform: 'translateY(-4px)', bgcolor: 'rgba(255,255,255,0.02)' }
              }}>
                <Box sx={{ p: 3, flexGrow: 1 }}>
                  <Stack direction="row" spacing={1} sx={{ mb: 2 }}>
                    <Chip label={event.type} size="small" color="primary" variant="outlined" />
                    <Chip label={event.category} size="small" variant="outlined" />
                  </Stack>
                  
                  <Typography variant="h6" sx={{ fontWeight: 800, mb: 2 }}>{event.title}</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 3, lineBreak: 'anywhere' }}>
                    {event.description || 'Join us for this impactful community event sponsored by national partners.'}
                  </Typography>

                  <Stack spacing={1.5}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <LocationIcon sx={{ fontSize: 18, color: 'primary.main' }} />
                      <Typography variant="body2">{event.location || 'Riyadh, Saudi Arabia'}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <TimeIcon sx={{ fontSize: 18, color: 'primary.main' }} />
                      <Typography variant="body2">{new Date(event.startTime).toLocaleDateString()} • {new Date(event.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                      <PointsIcon sx={{ fontSize: 18, color: 'warning.main' }} />
                      <Typography variant="body2" sx={{ fontWeight: 700, color: 'warning.main' }}>+{event.attendancePointsReward} Points</Typography>
                    </Box>
                  </Stack>
                </Box>
                
                <Box sx={{ p: 2, borderTop: '1px solid rgba(255,255,255,0.05)' }}>
                  <Button 
                    fullWidth 
                    variant="contained" 
                    onClick={() => handleRSVP(event.id)}
                    disabled={rsvpLoading === event.id}
                  >
                    {rsvpLoading === event.id ? 'Sending RSVP...' : 'RSVP Now'}
                  </Button>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
}
