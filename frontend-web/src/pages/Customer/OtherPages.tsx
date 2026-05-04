import { Box, Typography, Grid, Card, Button, alpha } from '@mui/material';
import { FitnessCenter as FitnessIcon, LocalHospital as HealthIcon } from '@mui/icons-material';

export function WellnessPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Wellness & Care</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Card sx={{ p: 4, bgcolor: alpha('#10b981', 0.05), border: '1px solid #10b981' }}>
            <FitnessIcon sx={{ fontSize: 50, color: 'success.main', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 800, mb: 1 }}>Preventative Care</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>Schedule your annual checkup and earn 500 XP rewards.</Typography>
            <Button variant="contained" color="success">Book Appointment</Button>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 6 }}>
          <Card sx={{ p: 4, bgcolor: alpha('#3b82f6', 0.05), border: '1px solid #3b82f6' }}>
            <HealthIcon sx={{ fontSize: 50, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 800, mb: 1 }}>Insurance Coverage</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>View and manage your health insurance beneficiaries.</Typography>
            <Button variant="contained">View Plan Details</Button>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

import { AccountBalance as BankIcon } from '@mui/icons-material';

export function PlanningPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Financial Planning</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ p: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 3 }}>Retirement Projection</Typography>
            <Box sx={{ height: 200, bgcolor: 'rgba(255,255,255,0.02)', borderRadius: 2, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Typography variant="body2" color="text.secondary">Interactive Chart Placeholder</Typography>
            </Box>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4, height: '100%' }}>
            <BankIcon sx={{ fontSize: 40, color: 'secondary.main', mb: 2 }} />
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>Advisor Booking</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>Chat with a certified financial advisor.</Typography>
            <Button fullWidth variant="contained" color="secondary">Start Chat</Button>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
