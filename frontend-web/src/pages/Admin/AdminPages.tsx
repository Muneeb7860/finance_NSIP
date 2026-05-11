import { useState } from 'react';
import { Box, Typography, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Grid, Button, Stack } from '@mui/material';
import { Assessment as PortfolioIcon } from '@mui/icons-material';


export function AdminClaimsPage() {
  const metrics = [
    { title: 'Approved Loans', value: 'SAR 3.1B', trend: '+12%', sub: 'Taqdeer Loan Book', icon: <PortfolioIcon sx={{ color: '#10b981' }} /> },
    { title: 'App Rating', value: '4.8/5', trend: 'Excellent', sub: '2.94M Users', icon: <PortfolioIcon sx={{ color: '#f59e0b' }} /> },
    { title: 'Complaint Reduction', value: '14%', trend: '-2.5%', sub: 'Optimized Workflows', icon: <PortfolioIcon sx={{ color: '#ef4444' }} /> },
    { title: 'RPA Cost Savings', value: 'SAR 15M', trend: 'Annual', sub: 'Automated Processing', icon: <PortfolioIcon sx={{ color: '#8b5cf6' }} /> },
  ];

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 0.5 }}>National Impact Dashboard</Typography>
          <Typography variant="subtitle1" color="text.secondary">Real-time governance and service metrics</Typography>
        </Box>
        <Stack direction="row" spacing={2}>
          <Button variant="outlined" startIcon={<PortfolioIcon />}>Export Report</Button>
          <Button variant="contained">Global Settings</Button>
        </Stack>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        {metrics.map((m, i) => (
          <Grid key={i} size={{ xs: 12, md: 3 }}>
            <Card sx={{ p: 3, height: '100%', border: '1px solid rgba(255,255,255,0.05)', position: 'relative', overflow: 'hidden' }}>
              <Box sx={{ position: 'absolute', top: -10, right: -10, opacity: 0.05 }}>{m.icon}</Box>
              <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.secondary', display: 'block', mb: 1 }}>{m.title}</Typography>
              <Typography variant="h4" sx={{ fontWeight: 900, mb: 0.5 }}>{m.value}</Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Typography variant="caption" sx={{ fontWeight: 800, color: 'success.main' }}>{m.trend}</Typography>
                <Typography variant="caption" color="text.secondary">| {m.sub}</Typography>
              </Box>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Typography variant="h6" sx={{ fontWeight: 800, mb: 2 }}>Claim Approvals Queue</Typography>
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Claim ID</TableCell>
                <TableCell>User</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              <TableRow>
                <TableCell>CLM-882</TableCell>
                <TableCell>Muneeb A.</TableCell>
                <TableCell>Medical</TableCell>
                <TableCell><Chip label="Pending" color="warning" size="small" /></TableCell>
                <TableCell><Button size="small">Review</Button></TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}

export function AdminEventsPage() {
  const [level, setLevel] = useState('L1_REVIEWER');
  const [pendingEvents, setPendingEvents] = useState([
    { id: 'EVT-102', title: 'Financial Literacy Workshop', org: 'Standard Bank', budget: 15000, status: 'DRAFT' },
    { id: 'EVT-105', title: 'Startup Networking Gala', org: 'Tech Hub', budget: 75000, status: 'DRAFT' },
  ]);

  const handleAction = (id: string, action: 'Approve' | 'Reject') => {
    alert(`${action}d event ${id} at level ${level}`);
    setPendingEvents(prev => prev.filter(e => e.id !== id));
  };

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 0.5 }}>Event Approvals</Typography>
          <Typography variant="subtitle1" color="text.secondary">Manage the 3-layer governance pipeline for community events.</Typography>
        </Box>
        <Stack direction="row" spacing={1}>
          {['L1_REVIEWER', 'L2_MANAGER', 'L3_DIRECTOR'].map((lvl) => (
            <Button 
              key={lvl}
              variant={level === lvl ? "contained" : "outlined"}
              size="small"
              onClick={() => setLevel(lvl)}
            >
              {lvl.replace('_', ' ')}
            </Button>
          ))}
        </Stack>
      </Box>

      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                <TableCell sx={{ fontWeight: 700 }}>Event Details</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Organization</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Budget</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Current Stage</TableCell>
                <TableCell sx={{ fontWeight: 700, textAlign: 'right' }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {pendingEvents.length > 0 ? pendingEvents.map((evt) => (
                <TableRow key={evt.id}>
                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 800 }}>{evt.title}</Typography>
                    <Typography variant="caption" color="text.secondary">ID: {evt.id}</Typography>
                  </TableCell>
                  <TableCell>{evt.org}</TableCell>
                  <TableCell>SAR {evt.budget.toLocaleString()}</TableCell>
                  <TableCell>
                    <Chip label={evt.status} size="small" color="warning" variant="outlined" />
                  </TableCell>
                  <TableCell sx={{ textAlign: 'right' }}>
                    <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                      <Button variant="contained" color="success" size="small" onClick={() => handleAction(evt.id, 'Approve')}>Approve</Button>
                      <Button variant="outlined" color="error" size="small" onClick={() => handleAction(evt.id, 'Reject')}>Reject</Button>
                    </Stack>
                  </TableCell>
                </TableRow>
              )) : (
                <TableRow>
                  <TableCell colSpan={5} sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="body2" color="text.secondary">No pending events at {level.replace('_', ' ')} stage.</Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}

export function AdminSLAPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Security & Governance</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', border: '1px solid #10b981', bgcolor: 'rgba(16, 185, 129, 0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: 'success.main' }}>ENFORCED</Typography>
            <Typography variant="subtitle2" sx={{ mt: 1 }}>Account Isolation</Typography>
            <Typography variant="caption" color="text.secondary">Per-service DB credentials active</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', border: '1px solid #3b82f6', bgcolor: 'rgba(59, 130, 246, 0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: 'primary.main' }}>DAILY</Typography>
            <Typography variant="subtitle2" sx={{ mt: 1 }}>Immutable Backups</Typography>
            <Typography variant="caption" color="text.secondary">Next sync: 00:00 UTC</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', border: '1px solid #f59e0b', bgcolor: 'rgba(245, 158, 11, 0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: 'warning.main' }}>ACTIVE</Typography>
            <Typography variant="subtitle2" sx={{ mt: 1 }}>Approval Workflows</Typography>
            <Typography variant="caption" color="text.secondary">Multi-sig for data purges</Typography>
          </Card>
        </Grid>

        <Grid size={{ xs: 12 }}>
          <Card sx={{ p: 0 }}>
            <Box sx={{ p: 3, bgcolor: 'rgba(255,255,255,0.02)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>Pending Approval Queue</Typography>
              <Chip label="2 Actions Required" color="warning" />
            </Box>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 700 }}>Operation</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Requested By</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Risk Level</TableCell>
                    <TableCell sx={{ fontWeight: 700, textAlign: 'right' }}>Action</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  <TableRow>
                    <TableCell>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>Manual DB Schema Update</Typography>
                      <Typography variant="caption" color="text.secondary">Target: nsip_auth</Typography>
                    </TableCell>
                    <TableCell>Admin_User_99</TableCell>
                    <TableCell><Chip label="High" color="error" size="small" /></TableCell>
                    <TableCell sx={{ textAlign: 'right' }}>
                      <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                        <Button size="small" variant="contained" color="success">Approve</Button>
                        <Button size="small" variant="outlined" color="error">Reject</Button>
                      </Stack>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
