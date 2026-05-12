import { useEffect, useState } from 'react';
import { 
    Box, Typography, Grid, Card, Button, Avatar, Chip, Stack, Rating, 
  Dialog, DialogTitle, DialogContent, DialogActions, TextField, Tabs, Tab,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow
} from '@mui/material';
import { 
  EventAvailable as BookIcon,
  Cancel as CancelIcon,
  Update as RescheduleIcon,
  RateReview as ReviewIcon
} from '@mui/icons-material';
import { api } from '../../api';

interface Advisor {
  id: string;
  name: string;
  specialty: string;
  bio: string;
  averageRating: number;
  totalReviews: number;
  pointsCost: number;
}

interface Session {
  id: string;
  advisorName: string;
  customerName: string;
  scheduledAt: string;
  status: 'PENDING_APPROVAL' | 'APPROVED' | 'COMPLETED' | 'CANCELLED_BY_CUSTOMER' | 'CANCELLED_BY_ADVISOR' | 'REJECTED';
  pointsDeducted: number;
}

export default function AdvisorsPage() {
  const DEMO_CUSTOMER_ID = 'c1234567-89ab-cdef-0123-456789abcdef';

  const [tab, setTab] = useState(0);
  const [isAdvisorView, setIsAdvisorView] = useState(false);
  const [advisors, setAdvisors] = useState<Advisor[]>([]);
  const [sessions, setSessions] = useState<Session[]>([]);
  const [, setLoading] = useState(false);
  
  const [selectedAdvisor, setSelectedAdvisor] = useState<Advisor | null>(null);
  const [bookingDate, setBookingDate] = useState('');
  const [showReview, setShowReview] = useState(false);
  const [selectedSession, setSelectedSession] = useState<Session | null>(null);

  useEffect(() => {
    loadAdvisors();
  }, []);

  useEffect(() => {
    loadSessions();
  }, [isAdvisorView, tab]);

  const loadAdvisors = async () => {
    try {
      const data = await api.getAdvisors();
      setAdvisors(data);
    } catch (err) {
      console.error('Failed to load advisors', err);
    }
  };

  const loadSessions = async () => {
    setLoading(true);
    try {
      // In a real app, we'd use the logged-in user's ID
      const data = isAdvisorView 
        ? await api.getAdvisors().then(ads => ads[0]?.id ? api.request(`/api/v1/learning/advisors/${ads[0].id}/schedule`) : [])
        : await api.getCustomerSessions(DEMO_CUSTOMER_ID);
      
      // Map backend model to frontend interface
      const mapped = data.map((s: any) => ({
        id: s.id,
        advisorName: s.advisorName || 'Expert Advisor',
        customerName: s.customerName || 'Contributor',
        scheduledAt: s.scheduledAt,
        status: s.status,
        pointsDeducted: s.pointsCharged
      }));
      setSessions(mapped);
    } catch (err) {
      console.error('Failed to load sessions', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBook = async () => {
    if (!selectedAdvisor || !bookingDate) return;
    try {
      await api.bookSession({
        customerId: DEMO_CUSTOMER_ID,
        advisorId: selectedAdvisor.id,
        scheduledAt: bookingDate
      });
      alert('Request sent! Waiting for Advisor to accept/reject.');
      setSelectedAdvisor(null);
      loadSessions();
      setTab(1);
    } catch (err: any) {
      alert(`Booking failed: ${err.message}`);
    }
  };

  const handleAdvisorAction = async (id: string, action: 'APPROVE' | 'REJECT') => {
    try {
      if (action === 'APPROVE') {
        await api.approveSession(id);
      } else {
        const reason = window.prompt('Reason for rejection:');
        if (reason === null) return;
        await api.rejectSession(id, reason);
      }
      alert(`Session ${action === 'APPROVE' ? 'Approved' : 'Rejected'}.`);
      loadSessions();
    } catch (err: any) {
      alert(`Action failed: ${err.message}`);
    }
  };

  const handleCancel = async (id: string) => {
    if (window.confirm('Are you sure you want to cancel?')) {
      try {
        await api.cancelSession(id, 'User cancelled');
        alert('Session cancelled.');
        loadSessions();
      } catch (err: any) {
        alert(`Cancellation failed: ${err.message}`);
      }
    }
  };

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>Financial Advisors</Typography>
          <Typography variant="subtitle1" color="text.secondary">2-Layer Verified Consultation Pipeline.</Typography>
        </Box>
        <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
          <Button 
            variant="outlined" 
            size="small" 
            color={isAdvisorView ? "secondary" : "inherit"}
            onClick={() => setIsAdvisorView(!isAdvisorView)}
            sx={{ fontWeight: 800 }}
          >
            Switch to {isAdvisorView ? "Customer" : "Advisor"} View
          </Button>
          <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
            <Tab label="Marketplace" sx={{ fontWeight: 700 }} disabled={isAdvisorView} />
            <Tab label={isAdvisorView ? "Inbox" : "My Sessions"} sx={{ fontWeight: 700 }} />
          </Tabs>
        </Stack>
      </Box>

      {tab === 0 && !isAdvisorView ? (
        <Grid container spacing={3}>
          {advisors.map((advisor) => (
            <Grid key={advisor.id} size={{ xs: 12, md: 6, lg: 4 }}>
              <Card sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column', border: '1px solid rgba(255,255,255,0.05)' }}>
                <Box sx={{ display: 'flex', gap: 2, mb: 2, alignItems: 'center' }}>
                  <Avatar sx={{ width: 64, height: 64, bgcolor: '#8b5cf6', fontSize: '1.5rem', fontWeight: 800 }}>{advisor.name[0]}</Avatar>
                  <Box>
                    <Typography variant="h6" sx={{ fontWeight: 800 }}>{advisor.name}</Typography>
                    <Chip label={advisor.specialty} size="small" color="primary" variant="outlined" />
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                  <Rating value={advisor.averageRating} readOnly precision={0.5} size="small" />
                  <Typography variant="caption" color="text.secondary">({advisor.totalReviews} reviews)</Typography>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ flexGrow: 1, mb: 3 }}>{advisor.bio}</Typography>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="h6" sx={{ fontWeight: 900, color: 'secondary.main' }}>{advisor.pointsCost} <Typography variant="caption">PTS</Typography></Typography>
                  <Button variant="contained" startIcon={<BookIcon />} onClick={() => setSelectedAdvisor(advisor)}>Request Session</Button>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>
      ) : (
        <Card sx={{ p: 0 }}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                  <TableCell sx={{ fontWeight: 700 }}>{isAdvisorView ? "Customer" : "Advisor"}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Date & Time</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                  <TableCell sx={{ fontWeight: 700, textAlign: 'right' }}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {sessions.map((s) => (
                  <TableRow key={s.id}>
                    <TableCell sx={{ fontWeight: 700 }}>{isAdvisorView ? s.customerName : s.advisorName}</TableCell>
                    <TableCell>{new Date(s.scheduledAt).toLocaleString()}</TableCell>
                    <TableCell>
                      <Chip 
                        label={s.status.replace(/_/g, ' ')} 
                        size="small" 
                        color={s.status === 'APPROVED' ? 'success' : s.status === 'PENDING_APPROVAL' ? 'warning' : s.status === 'REJECTED' ? 'error' : 'default'} 
                        variant={s.status === 'PENDING_APPROVAL' ? 'outlined' : 'filled'}
                      />
                    </TableCell>
                    <TableCell sx={{ textAlign: 'right' }}>
                      <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                        {isAdvisorView ? (
                          s.status === 'PENDING_APPROVAL' ? (
                            <>
                              <Button size="small" variant="contained" color="success" onClick={() => handleAdvisorAction(s.id, 'APPROVE')}>Accept</Button>
                              <Button size="small" variant="outlined" color="error" onClick={() => handleAdvisorAction(s.id, 'REJECT')}>Reject</Button>
                            </>
                          ) : (
                            <Typography variant="caption" color="text.secondary">No Actions</Typography>
                          )
                        ) : (
                          <>
                            {s.status === 'PENDING_APPROVAL' && (
                              <Button size="small" variant="outlined" color="error" onClick={() => handleCancel(s.id)}>Cancel Request</Button>
                            )}
                            {s.status === 'APPROVED' && (
                              <>
                                <Button size="small" variant="outlined" startIcon={<RescheduleIcon />}>Reschedule</Button>
                                <Button size="small" variant="outlined" color="error" startIcon={<CancelIcon />} onClick={() => handleCancel(s.id)}>Cancel</Button>
                              </>
                            )}
                            {s.status === 'COMPLETED' && (
                              <Button size="small" variant="contained" color="secondary" startIcon={<ReviewIcon />} onClick={() => { setSelectedSession(s); setShowReview(true); }}>Leave Review</Button>
                            )}
                          </>
                        )}
                      </Stack>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Card>
      )}

      {/* Booking Dialog */}
      <Dialog open={!!selectedAdvisor} onClose={() => setSelectedAdvisor(null)}>
        <DialogTitle sx={{ fontWeight: 800 }}>Book Session with {selectedAdvisor?.name}</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Typography variant="body2" sx={{ mb: 3 }}>This session will deduct <b>{selectedAdvisor?.pointsCost} points</b>. Refunds available if cancelled 24h in advance.</Typography>
            <TextField label="Select Date & Time" type="datetime-local" fullWidth slotProps={{ inputLabel: { shrink: true } }} value={bookingDate} onChange={(e) => setBookingDate(e.target.value)} />
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button onClick={() => setSelectedAdvisor(null)}>Cancel</Button>
          <Button variant="contained" onClick={handleBook} disabled={!bookingDate}>Confirm Booking</Button>
        </DialogActions>
      </Dialog>

      {/* Review Dialog */}
      <Dialog open={showReview} onClose={() => setShowReview(false)}>
        <DialogTitle sx={{ fontWeight: 800 }}>Review Session with {selectedSession?.advisorName}</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2, textAlign: 'center' }}>
            <Typography variant="body2" sx={{ mb: 2 }}>How was your experience?</Typography>
            <Rating size="large" sx={{ mb: 3 }} />
            <TextField label="Your Feedback" fullWidth multiline rows={4} placeholder="What did you learn? Was the advisor helpful?" />
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button onClick={() => setShowReview(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => { alert('Review submitted!'); setShowReview(false); }}>Submit Review</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
