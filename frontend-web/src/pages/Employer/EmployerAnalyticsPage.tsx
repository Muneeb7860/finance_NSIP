import { useState } from 'react';
import { 
  Box, Container, Typography, Grid, Paper, Stack, 
  LinearProgress, alpha, IconButton, Chip, AvatarGroup, Avatar, Button
} from '@mui/material';
import { 
  TrendingUp as TrendingIcon, 
  Group as PeopleIcon, 
  HealthAndSafety as WellnessIcon,
  EmojiEvents as TrophyIcon,
  Info as InfoIcon,
  Download as DownloadIcon
} from '@mui/icons-material';

export default function EmployerAnalyticsPage() {
  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ mb: 6, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 900, color: '#059669', mb: 1 }}>Workforce Financial Health</Typography>
          <Typography variant="h6" color="text.secondary">Strategic insights into your organization's social insurance and wellness compliance.</Typography>
        </Box>
        <Button variant="outlined" startIcon={<DownloadIcon />} sx={{ borderRadius: 2, borderColor: 'rgba(0,0,0,0.1)' }}>Export Report</Button>
      </Box>

      <Grid container spacing={3}>
        {/* KPI Section */}
        <Grid size={{ xs: 12, md: 3 }}>
          <Paper sx={{ p: 3, borderRadius: 4, bgcolor: 'rgba(5, 150, 105, 0.05)', border: '1px solid rgba(5, 150, 105, 0.1)' }}>
            <PeopleIcon sx={{ color: '#059669', mb: 2, fontSize: 32 }} />
            <Typography variant="h4" sx={{ fontWeight: 900 }}>1,240</Typography>
            <Typography variant="body2" color="text.secondary">ACTIVE EMPLOYEES</Typography>
            <Stack direction="row" sx={{ spacing: 1, alignItems: 'center', mt: 1 }}>
              <TrendingIcon sx={{ color: 'success.main', fontSize: '1rem' }} />
              <Typography variant="caption" color="success.main" sx={{ fontWeight: 700 }}>+4% this month</Typography>
            </Stack>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 3 }}>
          <Paper sx={{ p: 3, borderRadius: 4, bgcolor: 'rgba(212, 163, 29, 0.05)', border: '1px solid rgba(212, 163, 29, 0.1)' }}>
            <WellnessIcon sx={{ color: '#d4a31d', mb: 2, fontSize: 32 }} />
            <Typography variant="h4" sx={{ fontWeight: 900 }}>86%</Typography>
            <Typography variant="body2" color="text.secondary">WELLNESS ENROLLMENT</Typography>
            <Box sx={{ mt: 2 }}>
              <LinearProgress variant="determinate" value={86} sx={{ height: 6, borderRadius: 3, bgcolor: alpha('#d4a31d', 0.1), '& .MuiLinearProgress-bar': { bgcolor: '#d4a31d' } }} />
            </Box>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 3 }}>
          <Paper sx={{ p: 3, borderRadius: 4, bgcolor: 'rgba(5, 150, 105, 0.05)', border: '1px solid rgba(5, 150, 105, 0.1)' }}>
            <TrophyIcon sx={{ color: '#059669', mb: 2, fontSize: 32 }} />
            <Typography variant="h4" sx={{ fontWeight: 900 }}>45.2k</Typography>
            <Typography variant="body2" color="text.secondary">TOTAL IMPACT POINTS</Typography>
            <Typography variant="caption" color="text.secondary">Global Org Ranking: #12</Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 3 }}>
          <Paper sx={{ p: 3, borderRadius: 4, border: '1px solid rgba(0,0,0,0.05)' }}>
            <Typography variant="subtitle2" sx={{ fontWeight: 900, mb: 2 }}>COMPLIANCE STATUS</Typography>
            <Chip label="FULLY COMPLIANT" color="success" size="small" sx={{ fontWeight: 900, borderRadius: 1 }} />
            <Typography variant="caption" sx={{ display: 'block', mt: 2 }} color="text.secondary">Last Payroll Upload: 24 May 2026</Typography>
          </Paper>
        </Grid>

        {/* Analytics Breakdown */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Paper sx={{ p: 4, borderRadius: 4, height: 400, border: '1px solid rgba(0,0,0,0.05)', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
            <Typography variant="h6" sx={{ color: 'text.secondary', fontWeight: 300 }}>[ Interactive Analytics Chart: Contribution vs Wellness Trends ]</Typography>
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Paper sx={{ p: 4, borderRadius: 4, height: 400, border: '1px solid rgba(0,0,0,0.05)' }}>
            <Typography variant="h6" sx={{ fontWeight: 900, mb: 3 }}>Top Wellness Achievers</Typography>
            <Stack spacing={3}>
              {[1, 2, 3, 4].map((i) => (
                <Box key={i} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Stack direction="row" sx={{ spacing: 2, alignItems: 'center' }}>
                    <Avatar sx={{ bgcolor: alpha('#059669', 0.1), color: '#059669' }}>E{i}</Avatar>
                    <Box>
                      <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Employee #{1024 + i}</Typography>
                      <Typography variant="caption" color="text.secondary">Engineering Dept</Typography>
                    </Box>
                  </Stack>
                  <Typography variant="subtitle2" sx={{ fontWeight: 900, color: '#059669' }}>{500 - i * 50} pts</Typography>
                </Box>
              ))}
            </Stack>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}


