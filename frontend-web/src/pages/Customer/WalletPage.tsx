import { Box, Typography, Grid, Card, Button, Stack } from '@mui/material';
import { 
  AccountBalanceWallet as WalletIcon, Payment as PaymentIcon, 
  Send as SendIcon, NearMe as RedirectIcon 
} from '@mui/icons-material';

export default function WalletPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Digital Wallet</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 7 }}>
          <Card sx={{ 
            p: 4, 
            height: 250, 
            background: 'linear-gradient(135deg, #312e81 0%, #1e1b4b 100%)',
            border: '1px solid rgba(255,255,255,0.1)',
            position: 'relative',
            overflow: 'hidden',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}>
            <Box sx={{ position: 'absolute', top: -50, right: -50, width: 200, height: 200, background: 'rgba(255,255,255,0.03)', borderRadius: '50%' }} />
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <Typography variant="h6" sx={{ fontWeight: 800, color: 'primary.light' }}>NSIP PREMIUM PASS</Typography>
              <WalletIcon sx={{ fontSize: 40, opacity: 0.5 }} />
            </Box>
            <Box>
              <Typography variant="caption" sx={{ opacity: 0.6, letterSpacing: 2 }}>AVAILABLE BALANCE</Typography>
              <Typography variant="h2" sx={{ fontWeight: 900 }}>SAR 14,250.00</Typography>
            </Box>
            <Box sx={{ display: 'flex', gap: 3 }}>
              <Box><Typography variant="caption" sx={{ opacity: 0.5 }}>MEMBER SINCE</Typography><Typography variant="body2" sx={{ fontWeight: 700 }}>2021</Typography></Box>
              <Box><Typography variant="caption" sx={{ opacity: 0.5 }}>STATUS</Typography><Typography variant="body2" sx={{ fontWeight: 700, color: 'success.main' }}>ACTIVE</Typography></Box>
            </Box>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card sx={{ p: 3, height: '100%' }}>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: 3 }}>Quick Actions</Typography>
            <Stack spacing={2}>
              <Button fullWidth variant="contained" size="large" startIcon={<PaymentIcon />} sx={{ py: 2 }}>Withdraw to Bank</Button>
              <Button fullWidth variant="outlined" size="large" startIcon={<SendIcon />} sx={{ py: 2 }}>Transfer XP to Points</Button>
              <Button fullWidth variant="outlined" size="large" startIcon={<RedirectIcon />} sx={{ py: 2 }}>View Transaction History</Button>
            </Stack>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
