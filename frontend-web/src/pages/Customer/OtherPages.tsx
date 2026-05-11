import { useState } from 'react';
import { Box, Typography, Grid, Card, Button, alpha, Slider, LinearProgress, Stack, Paper, Dialog, DialogTitle, DialogContent, DialogActions, TextField } from '@mui/material';
import { FitnessCenter as FitnessIcon, LocalHospital as HealthIcon } from '@mui/icons-material';

export function WellnessPage() {
  const [open, setOpen] = useState(false);
  const [regData, setRegData] = useState({ condition: '', assistance: 'Home Visit' });

  const handleRegister = () => {
    alert(`Enrolled in ${regData.condition} program. A health specialist will contact you for ${regData.assistance}.`);
    setOpen(false);
  };

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>Wellness & Chronic Care</Typography>
        <Typography variant="subtitle1" color="text.secondary">Enroll in government-sponsored care programs and earn rewards.</Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4, bgcolor: alpha('#10b981', 0.05), border: '1px solid #10b981', height: '100%' }}>
            <FitnessIcon sx={{ fontSize: 50, color: 'success.main', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 800, mb: 1 }}>Preventative Care</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>Master your health with annual checkups and earn 500 XP.</Typography>
            <Button variant="contained" color="success">Schedule Checkup</Button>
          </Card>
        </Grid>
        
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ p: 4, height: '100%' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 3 }}>Chronic Disease Assistance</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
              Register for chronic care programs to receive home assistance and teleconsults covered by your insurance plan.
            </Typography>
            
            <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
              <Paper sx={{ p: 2, flex: 1, bgcolor: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)' }}>
                <Typography variant="caption" sx={{ fontWeight: 800, color: 'primary.main' }}>ACTIVE PROGRAMS</Typography>
                <Typography variant="h4" sx={{ fontWeight: 900 }}>0</Typography>
              </Paper>
              <Paper sx={{ p: 2, flex: 1, bgcolor: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)' }}>
                <Typography variant="caption" sx={{ fontWeight: 800, color: 'secondary.main' }}>PENDING VISITS</Typography>
                <Typography variant="h4" sx={{ fontWeight: 900 }}>0</Typography>
              </Paper>
            </Stack>

            <Button variant="contained" size="large" onClick={() => setOpen(true)}>Register for New Program</Button>
          </Card>
        </Grid>
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle sx={{ fontWeight: 800 }}>Chronic Care Enrollment</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Stack spacing={3}>
              <TextField 
                label="Condition Name" 
                fullWidth 
                placeholder="e.g. Diabetes, Hypertension" 
                value={regData.condition}
                onChange={(e) => setRegData({...regData, condition: e.target.value})}
              />
              <TextField 
                select 
                label="Preferred Assistance" 
                fullWidth 
                value={regData.assistance}
                onChange={(e) => setRegData({...regData, assistance: e.target.value})}
                slotProps={{ select: { native: true } }}
              >
                <option value="Home Visit">Home Visit</option>
                <option value="Teleconsult">Teleconsult</option>
                <option value="Medication Delivery">Medication Delivery</option>
              </TextField>
            </Stack>
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleRegister} disabled={!regData.condition}>Enroll Now</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

import { AccountBalance as BankIcon } from '@mui/icons-material';

export function PlanningPage() {
  const [monthlyIncome, setMonthlyIncome] = useState(15000);
  const [monthlyExpenses, setMonthlyExpenses] = useState(10000);
  
  // EMF: Emergency Fund Calculator (Recommended 6 months of expenses)
  const targetEMF = monthlyExpenses * 6;
  
  // BMC: Budget Management (50/30/20 Rule)
  const needs = monthlyIncome * 0.5;
  const wants = monthlyIncome * 0.3;
  const savings = monthlyIncome * 0.2;

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>Financial Planning</Typography>
        <Typography variant="subtitle1" color="text.secondary">Use our smart tools to optimize your future savings.</Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Budget Inputs */}
        <Grid size={{ xs: 12 }}>
          <Card sx={{ p: 4, mb: 3 }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 3 }}>Your Financial Inputs</Typography>
            <Grid container spacing={4}>
              <Grid size={{ xs: 12, md: 6 }}>
                <Typography variant="caption" sx={{ fontWeight: 800, color: 'primary.main', mb: 1, display: 'block' }}>MONTHLY NET INCOME (SAR)</Typography>
                <Slider 
                  value={monthlyIncome} 
                  min={5000} max={100000} step={1000}
                  onChange={(_, val) => setMonthlyIncome(val as number)}
                  valueLabelDisplay="auto"
                />
                <Typography variant="h5" sx={{ fontWeight: 900 }}>SAR {monthlyIncome.toLocaleString()}</Typography>
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <Typography variant="caption" sx={{ fontWeight: 800, color: 'secondary.main', mb: 1, display: 'block' }}>MONTHLY EXPENSES (SAR)</Typography>
                <Slider 
                  value={monthlyExpenses} 
                  min={1000} max={50000} step={500}
                  onChange={(_, val) => setMonthlyExpenses(val as number)}
                  valueLabelDisplay="auto"
                />
                <Typography variant="h5" sx={{ fontWeight: 900 }}>SAR {monthlyExpenses.toLocaleString()}</Typography>
              </Grid>
            </Grid>
          </Card>
        </Grid>

        {/* 50/30/20 Budgeting Tool (BMC) */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ p: 4, height: '100%' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 3 }}>Budget Management (50/30/20 Rule)</Typography>
            <Stack spacing={4}>
              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>Needs (Housing, Utilities, Groceries)</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 900 }}>SAR {needs.toLocaleString()}</Typography>
                </Box>
                <LinearProgress variant="determinate" value={50} sx={{ height: 10, borderRadius: 5, bgcolor: alpha('#10b981', 0.1), '& .MuiLinearProgress-bar': { bgcolor: '#10b981' } }} />
              </Box>
              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>Wants (Dining, Hobbies, Travel)</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 900 }}>SAR {wants.toLocaleString()}</Typography>
                </Box>
                <LinearProgress variant="determinate" value={30} sx={{ height: 10, borderRadius: 5, bgcolor: alpha('#f59e0b', 0.1), '& .MuiLinearProgress-bar': { bgcolor: '#f59e0b' } }} />
              </Box>
              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>Savings & Debt Repayment</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 900 }}>SAR {savings.toLocaleString()}</Typography>
                </Box>
                <LinearProgress variant="determinate" value={20} sx={{ height: 10, borderRadius: 5, bgcolor: alpha('#8b5cf6', 0.1), '& .MuiLinearProgress-bar': { bgcolor: '#8b5cf6' } }} />
              </Box>
            </Stack>
            <Typography variant="caption" sx={{ display: 'block', mt: 4, opacity: 0.6 }}>
              *This rule helps you manage your spending by allocating 50% to needs, 30% to wants, and 20% to financial goals.
            </Typography>
          </Card>
        </Grid>

        {/* Emergency Fund (EMF) */}
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4, height: '100%', bgcolor: alpha('#3b82f6', 0.05), border: '1px solid #3b82f6' }}>
            <BankIcon sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>Emergency Fund Goal</Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Recommended: 6 months of living expenses.
            </Typography>
            <Typography variant="h3" sx={{ fontWeight: 900, color: 'primary.main', mb: 1 }}>SAR {targetEMF.toLocaleString()}</Typography>
            <Typography variant="caption" sx={{ display: 'block', mb: 4 }}>
              Financial safety net for unexpected events.
            </Typography>
            <Button fullWidth variant="contained">Set Goal in Wallet</Button>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
