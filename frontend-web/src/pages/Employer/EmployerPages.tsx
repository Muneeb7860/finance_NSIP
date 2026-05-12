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

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>Payroll & Compliance</Typography>
        <Button variant="contained" component="label">
          {uploading ? 'Processing...' : 'Upload Payroll CSV'}
          <input type="file" hidden accept=".csv" onChange={handleUpload} />
        </Button>
      </Box>
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>Month</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Employees</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Total Contributions</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              <TableRow>
                <TableCell>October 2024</TableCell>
                <TableCell>145</TableCell>
                <TableCell>SAR 450,000</TableCell>
                <TableCell><Chip label="Paid" color="success" size="small" /></TableCell>
              </TableRow>
              <TableRow>
                <TableCell>September 2024</TableCell>
                <TableCell>142</TableCell>
                <TableCell>SAR 442,000</TableCell>
                <TableCell><Chip label="Paid" color="success" size="small" /></TableCell>
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

  useEffect(() => {
    fetchMyEvents();
  }, []);

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

