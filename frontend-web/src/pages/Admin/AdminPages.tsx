import { useState, useEffect } from 'react';
import { Box, Typography, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Grid, Button, Stack, LinearProgress, alpha } from '@mui/material';
import { Assessment as PortfolioIcon } from '@mui/icons-material';
import { api } from '../../api';


export function AdminClaimsPage() {
  const [claims, setClaims] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchClaims();
  }, []);

  const fetchClaims = async () => {
    setLoading(true);
    try {
      const data = await api.getPendingClaims();
      setClaims(data);
    } catch (err) {
      // Fallback for demo
      setClaims([
        { id: 'CLM-882', userId: 'Muneeb A.', claimType: 'MEDICAL', amount: 'SAR 12,500', status: 'PENDING', aiScore: 98, aiRisk: 'Low' },
        { id: 'CLM-883', userId: 'Sarah J.', claimType: 'UNEMPLOYMENT', amount: 'SAR 8,200', status: 'PENDING', aiScore: 45, aiRisk: 'High' },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const metrics = [
    { title: 'Total Disbursed', value: 'SAR 4.2B', trend: '+15.2%', sub: 'Fiscal YTD', icon: <PortfolioIcon sx={{ color: '#059669' }} />, color: '#059669' },
    { title: 'Processing SLA', value: '1.2 Days', trend: '-20%', sub: 'Avg. Decision Time', icon: <PortfolioIcon sx={{ color: '#3b82f6' }} />, color: '#3b82f6' },
    { title: 'AI Accuracy', value: '99.8%', trend: 'Stable', sub: 'Fraud Detection Rate', icon: <PortfolioIcon sx={{ color: '#8b5cf6' }} />, color: '#8b5cf6' },
    { title: 'Active Citizens', value: '12.4M', trend: '+4.1%', sub: 'Platform Adoption', icon: <PortfolioIcon sx={{ color: '#f59e0b' }} />, color: '#f59e0b' },
  ];

  return (
    <Box sx={{ p: 4, bgcolor: 'transparent' }}>
      {/* Header Section */}
      <Box sx={{ mb: 6, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 900, letterSpacing: '-0.02em', mb: 1, background: 'linear-gradient(45deg, #fff 30%, rgba(255,255,255,0.5) 90%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            National Governance
          </Typography>
          <Stack direction="row" sx={{ spacing: 2, alignItems: 'center' }}>
            <Chip label="Real-time Monitoring" size="small" sx={{ bgcolor: 'rgba(5, 150, 105, 0.1)', color: '#059669', fontWeight: 700, border: '1px solid rgba(5, 150, 105, 0.2)' }} />
            <Typography variant="body2" color="text.secondary">System Status: <Box component="span" sx={{ color: '#059669', fontWeight: 800 }}>OPERATIONAL</Box></Typography>
          </Stack>
        </Box>
        <Stack direction="row" spacing={2}>
          <Button variant="outlined" sx={{ borderRadius: 3, px: 3, borderColor: 'rgba(255,255,255,0.1)' }}>Generate Audit Log</Button>
          <Button variant="contained" sx={{ borderRadius: 3, px: 4, bgcolor: '#059669', '&:hover': { bgcolor: '#047857' } }}>Policy Override</Button>
        </Stack>
      </Box>

      {/* Metric Grid */}
      <Grid container spacing={3} sx={{ mb: 6 }}>
        {metrics.map((m, i) => (
          <Grid key={i} size={{ xs: 12, md: 3 }}>
            <Card sx={{ 
              p: 3, 
              borderRadius: 5, 
              bgcolor: 'rgba(255,255,255,0.02)', 
              border: '1px solid rgba(255,255,255,0.05)',
              backdropFilter: 'blur(10px)',
              position: 'relative',
              overflow: 'hidden'
            }}>
              <Box sx={{ 
                position: 'absolute', 
                top: 0, 
                right: 0, 
                width: 100, 
                height: 100, 
                background: `radial-gradient(circle at top right, ${alpha(m.color, 0.1)}, transparent 70%)` 
              }} />
              <Typography variant="caption" sx={{ fontWeight: 800, color: 'text.secondary', letterSpacing: 1, textTransform: 'uppercase' }}>{m.title}</Typography>
              <Typography variant="h4" sx={{ fontWeight: 900, my: 1, letterSpacing: '-0.02em' }}>{m.value}</Typography>
              <Stack direction="row" sx={{ spacing: 1, alignItems: 'center' }}>
                <Typography variant="caption" sx={{ fontWeight: 900, color: m.trend.startsWith('+') ? '#059669' : '#3b82f6' }}>{m.trend}</Typography>
                <Typography variant="caption" color="text.secondary">vs Last Month</Typography>
              </Stack>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Claims Queue */}
      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>Pending National Claims</Typography>
        <Typography variant="body2" color="text.secondary">Showing {claims.length} urgent applications</Typography>
      </Box>

      <Card sx={{ borderRadius: 5, overflow: 'hidden', border: '1px solid rgba(255,255,255,0.05)', bgcolor: 'rgba(255,255,255,0.01)' }}>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'rgba(255,255,255,0.03)' }}>
                <TableCell sx={{ fontWeight: 800, color: 'text.secondary' }}>CITIZEN / ID</TableCell>
                <TableCell sx={{ fontWeight: 800, color: 'text.secondary' }}>BENEFIT TYPE</TableCell>
                <TableCell sx={{ fontWeight: 800, color: 'text.secondary' }}>VALUE</TableCell>
                <TableCell sx={{ fontWeight: 800, color: 'text.secondary' }}>AI RISK SCORE</TableCell>
                <TableCell sx={{ fontWeight: 800, color: 'text.secondary', textAlign: 'right' }}>ACTIONS</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {claims.map((row) => (
                <TableRow key={row.id} sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                  <TableCell>
                    <Typography variant="subtitle2" sx={{ fontWeight: 800 }}>{row.userId}</Typography>
                    <Typography variant="caption" color="text.secondary">{row.id}</Typography>
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={row.claimType} 
                      size="small" 
                      sx={{ fontWeight: 700, bgcolor: 'rgba(255,255,255,0.05)', borderRadius: 2 }} 
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 900, color: '#059669' }}>{row.amount}</Typography>
                  </TableCell>
                  <TableCell>
                    <Stack direction="row" sx={{ spacing: 2, alignItems: 'center' }}>
                      <Box sx={{ flexGrow: 1, minWidth: 80 }}>
                        <LinearProgress 
                          variant="determinate" 
                          value={row.aiScore} 
                          sx={{ 
                            height: 6, 
                            borderRadius: 3, 
                            bgcolor: 'rgba(255,255,255,0.05)',
                            '& .MuiLinearProgress-bar': {
                              bgcolor: row.aiRisk === 'Low' ? '#059669' : '#ef4444'
                            }
                          }} 
                        />
                      </Box>
                      <Typography variant="caption" sx={{ fontWeight: 900, color: row.aiRisk === 'Low' ? '#059669' : '#ef4444' }}>
                        {row.aiRisk} Risk
                      </Typography>
                    </Stack>
                  </TableCell>
                  <TableCell sx={{ textAlign: 'right' }}>
                    <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                      <Button variant="outlined" size="small" sx={{ borderRadius: 2, textTransform: 'none', fontWeight: 700 }}>Review Details</Button>
                      <Button variant="contained" size="small" sx={{ borderRadius: 2, textTransform: 'none', fontWeight: 700, bgcolor: '#059669' }}>Quick Approve</Button>
                    </Stack>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}

export function AdminEventsPage() {
  const [level, setLevel] = useState('L1_REVIEWER');
  const [pendingEvents, setPendingEvents] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPending();
  }, [level]);

  const fetchPending = async () => {
    setLoading(true);
    try {
      const data = await api.getEventsPendingAtLevel(level);
      setPendingEvents(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (id: string, action: 'Approve' | 'Reject') => {
    try {
      const approverId = '947458a5-6912-4b1e-b6db-e56cfbdc4bcc';
      const payload = {
        approverUserId: approverId,
        approverName: 'Admin System',
        level: level,
        comment: `${action}d via Admin Dashboard`,
        rejectionReason: action === 'Reject' ? 'Policy violation or insufficient budget' : undefined
      };

      if (action === 'Approve') {
        await api.approveEvent(id, payload);
      } else {
        await api.rejectEvent(id, payload);
      }
      
      alert(`Event ${id} ${action}d successfully.`);
      fetchPending();
    } catch (err) {
      alert('Action failed');
    }
  };

  if (loading) return <LinearProgress />;

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 0.5 }}>Event Approvals</Typography>
          <Typography variant="subtitle1" color="text.secondary">Manage the 3-layer governance pipeline for community events.</Typography>
        </Box>
        <Stack direction="row" sx={{ spacing: 1 }}>
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
                <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
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
                  <TableCell>{evt.organizationName || 'N/A'}</TableCell>
                  <TableCell>
                    <Chip label={evt.status} size="small" color="warning" variant="outlined" />
                  </TableCell>
                  <TableCell sx={{ textAlign: 'right' }}>
                    <Stack direction="row" sx={{ spacing: 1, justifyContent: 'flex-end' }}>
                      <Button variant="contained" color="success" size="small" onClick={() => handleAction(evt.id, 'Approve')}>Approve</Button>
                      <Button variant="outlined" color="error" size="small" onClick={() => handleAction(evt.id, 'Reject')}>Reject</Button>
                    </Stack>
                  </TableCell>
                </TableRow>
              )) : (
                <TableRow>
                  <TableCell colSpan={4} sx={{ textAlign: 'center', py: 4 }}>
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
                      <Stack direction="row" sx={{ spacing: 1, justifyContent: 'flex-end' }}>
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
