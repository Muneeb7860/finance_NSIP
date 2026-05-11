import { useState } from 'react';
import { Box, Typography, Card, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Stack, TextField, Grid } from '@mui/material';

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
  const [events, setEvents] = useState([
    { id: 'EVT-101', title: 'Ramadan 5K Run', budget: 50000, status: 'L2_APPROVED', color: 'info' },
    { id: 'EVT-102', title: 'Financial Literacy Workshop', budget: 15000, status: 'DRAFT', color: 'warning' },
    { id: 'EVT-103', title: 'Community Beach Cleanup', budget: 5000, status: 'LIVE', color: 'success' },
  ]);

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Event Proposals</Typography>
      
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4 }}>
            <Typography variant="h6" sx={{ mb: 3, fontWeight: 800 }}>New Proposal</Typography>
            <Stack spacing={3}>
              <TextField label="Event Name" fullWidth />
              <TextField label="Proposed Budget" fullWidth type="number" />
              <TextField label="Event Date" fullWidth type="date" InputLabelProps={{ shrink: true }} />
              <Button variant="contained">Submit for Review</Button>
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
                    <TableCell sx={{ fontWeight: 700 }}>ID</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Title</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Budget</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Approval Stage</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {events.map((evt) => (
                    <TableRow key={evt.id}>
                      <TableCell>{evt.id}</TableCell>
                      <TableCell sx={{ fontWeight: 700 }}>{evt.title}</TableCell>
                      <TableCell>SAR {evt.budget.toLocaleString()}</TableCell>
                      <TableCell>
                        <Stack direction="row" spacing={1} alignItems="center">
                          <Chip label={evt.status} color={evt.color as any} size="small" variant="outlined" />
                          {evt.status !== 'LIVE' && evt.status !== 'REJECTED' && (
                            <Typography variant="caption" color="text.secondary">
                              {evt.status === 'DRAFT' ? 'Awaiting L1' : evt.status === 'L1_APPROVED' ? 'Awaiting L2' : 'Awaiting L3'}
                            </Typography>
                          )}
                        </Stack>
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

