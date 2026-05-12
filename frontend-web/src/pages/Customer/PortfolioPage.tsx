import { useState, useEffect } from 'react';
import { 
  Box, Typography, Grid, Card, Divider, Stack, 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, alpha, Button, LinearProgress 
} from '@mui/material';
import { 
  Assessment as PortfolioIcon, FitnessCenter as FitnessIcon, 
  TrendingUp as PlanningIcon 
} from '@mui/icons-material';
import { api } from '../../api';

export default function PortfolioPage() {
  const [balance, setBalance] = useState({ total: 0, projected: 0, growth: 0 });
  const [transactions, setTransactions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);

  // Mock userId for demo
  const userId = '947458a5-6912-4b1e-b6db-e56cfbdc4bcc';

  useEffect(() => {
    fetchPortfolio();
  }, []);

  const fetchPortfolio = async () => {
    try {
      const [balData, txData] = await Promise.all([
        api.getPensionBalance(userId),
        api.getTransactions(userId)
      ]);
      setBalance({
        total: balData.balance || 345780.20,
        projected: (balData.balance || 345780.20) * 2.5,
        growth: 12.8
      });
      setTransactions(txData.length > 0 ? txData : [
        { type: 'Pension Contribution', date: 'Jan 15', source: 'Employer Fund', amount: '+$1,250.00', color: '#10b981', icon: <PortfolioIcon fontSize="small" /> },
        { type: 'Health Premium', date: 'Jan 10', source: 'State Health', amount: '-$450.00', color: '#ef4444', icon: <FitnessIcon fontSize="small" /> },
      ]);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApplyLoan = async () => {
    setApplying(true);
    try {
      await api.submitLoanRequest(userId, '103734', 'Personal loan for home renovation');
      alert('Loan application submitted! The Saga Orchestrator is now verifying your eligibility and locking funds.');
    } catch (err: any) {
      alert(err.message || 'Failed to submit loan request');
    } finally {
      setApplying(false);
    }
  };

  const handleDownloadCert = async () => {
    try {
      const data = await api.requestCertificate(userId);
      window.open(data.certificateUrl, '_blank');
      alert('Certificate generated successfully! Opening download link...');
    } catch (err) {
      alert('Failed to generate certificate');
    }
  };

  const handleRedeem = async () => {
    try {
      const data = await api.redeemPoints(userId, 'Fuel Discount Voucher', 500);
      alert(`Redemption successful! Your code is: ${data.voucherCode}`);
    } catch (err: any) {
      alert(err.message || 'Failed to redeem points');
    }
  };

  if (loading) return <LinearProgress />;

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 0.5, color: 'secondary.main' }}>Social Insurance Dashboard</Typography>
          <Typography variant="subtitle1" color="text.secondary">Welcome back to your national financial overview.</Typography>
        </Box>
        <Button variant="outlined" color="secondary" startIcon={<PortfolioIcon />} onClick={handleDownloadCert}>
          Download Certificate
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* Main Balance Hero */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ 
            p: 4, 
            height: '100%',
            background: 'linear-gradient(135deg, #1e1b4b 0%, #312e81 100%)',
            border: '1px solid rgba(139, 92, 246, 0.2)',
            position: 'relative',
            overflow: 'hidden'
          }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
              <Box>
                <Typography variant="subtitle2" sx={{ opacity: 0.7, fontWeight: 700 }}>Total Pension Balance</Typography>
                <Typography variant="h2" sx={{ fontWeight: 900, letterSpacing: -2 }}>SAR {balance.total.toLocaleString()}</Typography>
                <Typography variant="body2" sx={{ color: 'success.main', fontWeight: 800, mt: 1 }}>+{balance.growth}% YoY Growth</Typography>
              </Box>
              <Box sx={{ textAlign: 'right' }}>
                <Typography variant="subtitle2" sx={{ opacity: 0.7, fontWeight: 700 }}>Projected Retirement (Age 67)</Typography>
                <Typography variant="h4" sx={{ fontWeight: 800 }}>SAR {balance.projected.toLocaleString()}</Typography>
              </Box>
            </Box>
            <Divider sx={{ opacity: 0.1, my: 3 }} />
            <Stack direction="row" spacing={4}>
              <Box>
                <Typography variant="caption" sx={{ opacity: 0.6 }}>Portfolio Performance</Typography>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>18.5% YoY</Typography>
              </Box>
              <Box>
                <Typography variant="caption" sx={{ opacity: 0.6 }}>Next Vesting Event</Typography>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>Nov 2024</Typography>
              </Box>
            </Stack>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4, height: '100%', border: '1px solid #10b981', bgcolor: 'rgba(16, 185, 129, 0.05)' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
              <Box>
                <Typography variant="caption" sx={{ fontWeight: 800, color: 'success.main', display: 'block', mb: 0.5 }}>TAQDEER UTILIZATION</Typography>
                <Typography variant="h4" sx={{ fontWeight: 900 }}>37.31%</Typography>
              </Box>
              <PortfolioIcon sx={{ color: 'success.main', opacity: 0.5 }} />
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>Your loyalty tier has been upgraded to <b>Platinum</b> based on consistent contributions.</Typography>
            <Button fullWidth variant="contained" color="success" onClick={handleRedeem}>Redeem Perks</Button>
          </Card>
        </Grid>

        {/* Personal Loan Application */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ p: 4, height: '100%', border: '1px solid rgba(139, 92, 246, 0.3)', bgcolor: 'rgba(139, 92, 246, 0.02)' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>Apply for Personal Loan</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
              As a Platinum member, you are eligible for a personal loan capped at <b>30% of your vested savings</b>.
            </Typography>
            
            <Grid container spacing={4} sx={{ alignItems: 'center' }}>
              <Grid size={{ xs: 12, md: 6 }}>
                <Typography variant="caption" sx={{ fontWeight: 800, color: 'primary.main', display: 'block', mb: 1 }}>ELIBILITY LIMIT (SAR)</Typography>
                <Typography variant="h3" sx={{ fontWeight: 900 }}>SAR {(balance.total * 0.3).toLocaleString()}</Typography>
                <Typography variant="caption" color="text.secondary">Based on SAR {balance.total.toLocaleString()} balance</Typography>
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <Button 
                  fullWidth 
                  variant="contained" 
                  size="large" 
                  sx={{ py: 2 }}
                  onClick={handleApplyLoan}
                  disabled={applying}
                >
                  {applying ? 'Processing Saga...' : 'Start Application'}
                </Button>
                <Typography variant="caption" sx={{ display: 'block', mt: 1, textAlign: 'center', opacity: 0.6 }}>
                  Saga-orchestrated approval (~3s)
                </Typography>
              </Grid>
            </Grid>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 800, alignSelf: 'flex-start', mb: 3 }}>Investment Growth</Typography>
            <Box sx={{ position: 'relative', width: 140, height: 140 }}>
              <svg viewBox="0 0 36 36" style={{ width: '100%', height: '100%', transform: 'rotate(-90deg)' }}>
                <circle cx="18" cy="18" r="16" fill="transparent" stroke="rgba(255,255,255,0.05)" strokeWidth="4"></circle>
                <circle cx="18" cy="18" r="16" fill="transparent" stroke="#8b5cf6" strokeWidth="4" strokeDasharray="45 100"></circle>
                <circle cx="18" cy="18" r="16" fill="transparent" stroke="#06b6d4" strokeWidth="4" strokeDasharray="30 100" strokeDashoffset="-45"></circle>
                <circle cx="18" cy="18" r="16" fill="transparent" stroke="#ec4899" strokeWidth="4" strokeDasharray="25 100" strokeDashoffset="-75"></circle>
              </svg>
              <Box sx={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', textAlign: 'center' }}>
                <Typography variant="h6" sx={{ fontWeight: 900 }}>Diversified</Typography>
              </Box>
            </Box>
            <Stack direction="row" spacing={2} sx={{ mt: 3, width: '100%', justifyContent: 'center' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}><Box sx={{ width: 8, height: 8, bgcolor: '#8b5cf6', borderRadius: '50%' }} /><Typography variant="caption">Stocks</Typography></Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}><Box sx={{ width: 8, height: 8, bgcolor: '#06b6d4', borderRadius: '50%' }} /><Typography variant="caption">Mutual Funds</Typography></Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}><Box sx={{ width: 8, height: 8, bgcolor: '#ec4899', borderRadius: '50%' }} /><Typography variant="caption">ETFs</Typography></Box>
            </Stack>
          </Card>
        </Grid>

        {/* Recent Transactions */}
        <Grid size={{ xs: 12 }}>
          <Card sx={{ p: 0 }}>
            <Box sx={{ p: 4, pb: 2 }}>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>Recent Transactions</Typography>
            </Box>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                    <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>Type</TableCell>
                    <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>Date</TableCell>
                    <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>Source</TableCell>
                    <TableCell sx={{ fontWeight: 700, color: 'text.secondary', textAlign: 'right' }}>Amount</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {transactions.map((t, i) => (
                    <TableRow key={i} sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                      <TableCell>
                        <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
                          <Box sx={{ p: 1, bgcolor: alpha(t.color || '#3b82f6', 0.1), color: t.color || '#3b82f6', borderRadius: 2, display: 'flex' }}>
                            {t.icon || <PortfolioIcon fontSize="small" />}
                          </Box>
                          <Typography variant="body2" sx={{ fontWeight: 700 }}>{t.type || t.description}</Typography>
                        </Stack>
                      </TableCell>
                      <TableCell><Typography variant="body2" color="text.secondary">{t.date || new Date(t.createdAt).toLocaleDateString()}</Typography></TableCell>
                      <TableCell><Typography variant="body2" color="text.secondary">{t.source || 'National Fund'}</Typography></TableCell>
                      <TableCell sx={{ textAlign: 'right' }}>
                        <Typography variant="body2" sx={{ fontWeight: 900, color: (t.amount || '').includes('+') ? 'success.main' : 'error.main' }}>
                          {t.amount || `SAR ${t.value}`}
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
