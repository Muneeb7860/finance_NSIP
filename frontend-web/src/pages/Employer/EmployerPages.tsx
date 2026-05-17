import { useState, useEffect } from 'react';
import { Box, Typography, Card, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Stack, TextField, Grid, LinearProgress } from '@mui/material';
import { api } from '../../api';

export function PayrollPage() {
  const [uploading, setUploading] = useState(false);

  const handleUpload = () => {
    setUploading(true);
    setTimeout(() => {
      setUploading(false);
      alert('Payroll CSV processed: 145 employees recognized, SAR 450,000 total deduction calculated.');
    }, 2000);
  };

  const payrollStats = [
    { title: 'Compliance Score', value: '100%', trend: 'Perfect', color: '#059669' },
    { title: 'Next Submission', value: '12 Days', trend: 'Nov 25', color: '#3b82f6' },
    { title: 'Total Employees', value: '145', trend: 'Active', color: '#f59e0b' },
  ];

  return (
    <Box sx={{ p: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 6 }}>
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 900, mb: 1 }}>Employer Portal</Typography>
          <Typography variant="body1" color="text.secondary">Manage your organization's contributions and compliance.</Typography>
        </Box>
        <Button 
          variant="contained" 
          component="label"
          sx={{ 
            borderRadius: 4, 
            px: 4, 
            py: 1.5, 
            bgcolor: '#059669', 
            fontWeight: 800,
            boxShadow: '0 10px 30px rgba(5, 150, 105, 0.3)',
            '&:hover': { bgcolor: '#047857' }
          }}
        >
          {uploading ? 'Processing Architecture...' : 'Upload Payroll Dataset'}
          <input type="file" hidden accept=".csv" onChange={handleUpload} />
        </Button>
      </Box>

      {/* Quick Stats */}
      <Grid container spacing={3} sx={{ mb: 6 }}>
        {payrollStats.map((s, i) => (
          <Grid key={i} size={{ xs: 12, md: 4 }}>
            <Card sx={{ p: 3, borderRadius: 5, bgcolor: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)' }}>
              <Typography variant="caption" sx={{ fontWeight: 800, color: 'text.secondary', letterSpacing: 1 }}>{s.title}</Typography>
              <Typography variant="h4" sx={{ fontWeight: 900, my: 1, color: s.color }}>{s.value}</Typography>
              <Typography variant="caption" sx={{ fontWeight: 700, opacity: 0.6 }}>Current Status: {s.trend}</Typography>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>Submission History</Typography>
        <Chip label="2024 Audit Clear" sx={{ bgcolor: 'rgba(5, 150, 105, 0.1)', color: '#059669', fontWeight: 700 }} />
      </Box>

      <Card sx={{ borderRadius: 5, border: '1px solid rgba(255,255,255,0.05)', overflow: 'hidden' }}>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'rgba(255,255,255,0.03)' }}>
                <TableCell sx={{ fontWeight: 800 }}>PERIOD</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>EMPLOYEE COUNT</TableCell>
                <TableCell sx={{ fontWeight: 800 }}>TOTAL CONTRIBUTION</TableCell>
                <TableCell sx={{ fontWeight: 800, textAlign: 'right' }}>STATUS</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              <TableRow sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                <TableCell sx={{ fontWeight: 700 }}>October 2024</TableCell>
                <TableCell>145 Employees</TableCell>
                <TableCell sx={{ fontWeight: 900, color: '#059669' }}>SAR 450,000</TableCell>
                <TableCell sx={{ textAlign: 'right' }}><Chip label="VERIFIED" sx={{ bgcolor: 'rgba(5, 150, 105, 0.1)', color: '#059669', fontWeight: 800, borderRadius: 2 }} /></TableCell>
              </TableRow>
              <TableRow sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                <TableCell sx={{ fontWeight: 700 }}>September 2024</TableCell>
                <TableCell>142 Employees</TableCell>
                <TableCell sx={{ fontWeight: 900, color: '#059669' }}>SAR 442,000</TableCell>
                <TableCell sx={{ textAlign: 'right' }}><Chip label="VERIFIED" sx={{ bgcolor: 'rgba(5, 150, 105, 0.1)', color: '#059669', fontWeight: 800, borderRadius: 2 }} /></TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}

export function EventProposalPage() {
  const [events, setEvents] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ title: '', budget: '', date: '', description: '' });

  const fetchMyEvents = async () => {
    try {
      // Mock userId for demo
      const userId = '947458a5-6912-4b1e-b6db-e56cfbdc4bcc'; 
      const data = await api.getMyEvents(userId);
      setEvents(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMyEvents();
  }, []);

  const handleSubmit = async () => {
    try {
      const userId = '947458a5-6912-4b1e-b6db-e56cfbdc4bcc';
      await api.submitEventProposal({
        title: form.title,
        description: form.description,
        createdByUserId: userId,
        organizationName: 'TechCorp LLC',
        pointsReward: '100',
        startTime: `${form.date}T09:00:00`
      });
      alert('Event proposal submitted successfully!');
      setForm({ title: '', budget: '', date: '', description: '' });
      fetchMyEvents();
    } catch (err) {
      console.error(err);
      alert('Failed to submit proposal');
    }
  };

  if (loading) return <LinearProgress />;

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Event Proposals</Typography>
      
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4 }}>
            <Typography variant="h6" sx={{ mb: 3, fontWeight: 800 }}>New Proposal</Typography>
            <Stack spacing={3}>
              <TextField label="Event Name" fullWidth value={form.title} onChange={(e) => setForm({...form, title: e.target.value})} />
              <TextField label="Proposed Budget" fullWidth type="number" value={form.budget} onChange={(e) => setForm({...form, budget: e.target.value})} />
              <TextField label="Event Date" fullWidth type="date" value={form.date} onChange={(e) => setForm({...form, date: e.target.value})} slotProps={{ inputLabel: { shrink: true } }} />
              <TextField label="Description" fullWidth multiline rows={2} value={form.description} onChange={(e) => setForm({...form, description: e.target.value})} />
              <Button variant="contained" onClick={handleSubmit}>Submit for Review</Button>
            </Stack>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ p: 0 }}>
            <Box sx={{ p: 3, bgcolor: 'rgba(255,255,255,0.02)' }}>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>My Proposals Status</Typography>
            </Box>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 700 }}>Title</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Stage</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {events.length === 0 ? (
                    <TableRow><TableCell colSpan={3} sx={{ textAlign: 'center', py: 4 }}>No proposals yet.</TableCell></TableRow>
                  ) : events.map((evt) => (
                    <TableRow key={evt.id}>
                      <TableCell sx={{ fontWeight: 700 }}>{evt.title}</TableCell>
                      <TableCell><Chip label={evt.status} color={evt.status === 'LIVE' ? 'success' : 'warning'} size="small" variant="outlined" /></TableCell>
                      <TableCell>
                        <Typography variant="caption" color="text.secondary">
                          {evt.status === 'DRAFT' ? 'L1 Review' : evt.status === 'L1_APPROVED' ? 'L2 Review' : evt.status === 'L2_APPROVED' ? 'L3 Final' : 'Completed'}
                        </Typography>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

