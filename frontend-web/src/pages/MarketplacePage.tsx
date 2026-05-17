import { useState, useEffect } from 'react';
import { 
  Box, Container, Typography, Grid, Card, CardContent, CardMedia, 
  Button, Chip, Stack, TextField, InputAdornment, alpha, Alert, Snackbar,
  Dialog, DialogTitle, DialogContent, DialogActions, Paper, Avatar
} from '@mui/material';
import { 
  Search as SearchIcon, 
  LocalOffer as OfferIcon, 
  CardGiftcard as GiftIcon,
  Star as StarIcon,
  FlightTakeoff as TravelIcon,
  HealthAndSafety as HealthIcon,
  ShoppingBag as ShoppingIcon
} from '@mui/icons-material';
import { api } from '../api';

const PARTNERS = [
  { id: 1, name: 'Saudia Airlines', category: 'Travel', points: 5000, desc: 'SAR 500 Flight Voucher for any domestic route.', icon: <TravelIcon />, image: 'https://images.unsplash.com/photo-1436491865332-7a61a109c0f3?w=800&q=80' },
  { id: 2, name: 'Nahdi Pharmacy', category: 'Health', points: 1200, desc: 'Free comprehensive wellness checkup and SAR 100 coupon.', icon: <HealthIcon />, image: 'https://images.unsplash.com/photo-1586015555751-63bb77f4322a?w=800&q=80' },
  { id: 3, name: 'Jarir Bookstore', category: 'Education', points: 2000, desc: 'SAR 200 Educational Materials discount.', icon: <GiftIcon />, image: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=800&q=80' },
  { id: 4, name: 'STC Pay', category: 'Financial', points: 3000, desc: 'SAR 300 Wallet Top-up for utility bills.', icon: <OfferIcon />, image: 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=800&q=80' },
  { id: 5, name: 'Panda Retail', category: 'Shopping', points: 1500, desc: 'SAR 150 Grocery Voucher.', icon: <ShoppingIcon />, image: 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&q=80' },
  { id: 6, name: 'Fitness Time', category: 'Wellness', points: 4000, desc: '1 Month Premium Gym Membership.', icon: <HealthIcon />, image: 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=800&q=80' },
];

export default function MarketplacePage() {
  const [points, setPoints] = useState(0);
  const [search, setSearch] = useState('');
  const [selectedPartner, setSelectedPartner] = useState<any>(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as any });

  const userId = '947458a5-6912-4b1e-b6db-e56cfbdc4bcc';

  const fetchPoints = async () => {
    try {
      const res = await api.getPointsBalance(userId);
      setPoints(res.balance || 0);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchPoints();
  }, []);

  const handleRedeem = async () => {
    if (!selectedPartner) return;
    try {
      await api.redeemPoints(userId, selectedPartner.name, selectedPartner.points);
      setSnackbar({ open: true, message: `Successfully redeemed ${selectedPartner.name} voucher!`, severity: 'success' });
      setSelectedPartner(null);
      fetchPoints();
    } catch (err: any) {
      setSnackbar({ open: true, message: err.message || 'Redemption failed', severity: 'error' });
    }
  };

  const filtered = PARTNERS.filter(p => 
    p.name.toLowerCase().includes(search.toLowerCase()) || 
    p.category.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      {/* Header Section */}
      <Box sx={{ mb: 6, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 900, color: '#059669', mb: 1 }}>Partner Marketplace</Typography>
          <Typography variant="h6" color="text.secondary">Redeem your National Loyalty Points for premium partner rewards.</Typography>
        </Box>
        <Paper sx={{ p: 2, borderRadius: 4, bgcolor: 'rgba(5, 150, 105, 0.1)', border: '1px solid rgba(5, 150, 105, 0.2)', display: 'flex', alignItems: 'center', gap: 2 }}>
          <StarIcon sx={{ color: '#059669', fontSize: 32 }} />
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#059669', lineHeight: 1 }}>{points.toLocaleString()}</Typography>
            <Typography variant="caption" sx={{ fontWeight: 700, color: '#059669' }}>LOYALTY POINTS AVAILABLE</Typography>
          </Box>
        </Paper>
      </Box>

      {/* Search & Filter */}
      <TextField 
        fullWidth 
        placeholder="Search partners, vouchers, or categories..." 
        variant="outlined" 
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        sx={{ mb: 4, '& .MuiOutlinedInput-root': { borderRadius: 4, bgcolor: 'background.paper' } }}
        slotProps={{
          input: {
            startAdornment: <InputAdornment position="start"><SearchIcon sx={{ color: 'text.secondary' }} /></InputAdornment>
          }
        }}
      />

      {/* Grid */}
      <Grid container spacing={3}>
        {filtered.map(partner => (
          <Grid size={{ xs: 12, sm: 6, md: 4 }} key={partner.id}>
            <Card sx={{ 
              height: '100%', 
              display: 'flex', 
              flexDirection: 'column', 
              borderRadius: 4, 
              overflow: 'hidden',
              transition: 'all 0.3s ease',
              '&:hover': { transform: 'translateY(-8px)', boxShadow: '0 20px 40px rgba(0,0,0,0.3)' }
            }}>
              <CardMedia 
                component="img" 
                height="200" 
                image={partner.image} 
                alt={partner.name} 
                sx={{ filter: 'brightness(0.8)' }}
              />
              <CardContent sx={{ flexGrow: 1, position: 'relative' }}>
                <Box sx={{ position: 'absolute', top: -30, left: 20 }}>
                  <Avatar sx={{ bgcolor: '#059669', width: 56, height: 56, boxShadow: '0 4px 20px rgba(0,0,0,0.3)' }}>
                    {partner.icon}
                  </Avatar>
                </Box>
                <Box sx={{ mt: 3, mb: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="h5" sx={{ fontWeight: 900 }}>{partner.name}</Typography>
                  <Chip label={partner.category} size="small" sx={{ bgcolor: alpha('#059669', 0.1), color: '#059669', fontWeight: 700 }} />
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>{partner.desc}</Typography>
                <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="h6" sx={{ fontWeight: 900, color: '#d4a31d' }}>{partner.points.toLocaleString()} pts</Typography>
                  <Button 
                    variant="contained" 
                    disabled={points < partner.points}
                    onClick={() => setSelectedPartner(partner)}
                    sx={{ borderRadius: 3, textTransform: 'none', px: 3, bgcolor: '#059669', '&:hover': { bgcolor: '#047857' } }}
                  >
                    Redeem
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Confirmation Dialog */}
      <Dialog open={!!selectedPartner} onClose={() => setSelectedPartner(null)} slotProps={{ paper: { sx: { borderRadius: 4, p: 2 } } }}>
        <DialogTitle sx={{ fontWeight: 900 }}>Confirm Redemption</DialogTitle>
        <DialogContent>
          <Typography variant="body1">
            Are you sure you want to redeem <strong>{selectedPartner?.points.toLocaleString()} points</strong> for a <strong>{selectedPartner?.name}</strong> voucher?
          </Typography>
          <Alert severity="info" sx={{ mt: 2, borderRadius: 2 }}>
            A digital voucher code will be sent to your registered email and mobile number immediately.
          </Alert>
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button onClick={() => setSelectedPartner(null)} sx={{ color: 'text.secondary' }}>Cancel</Button>
          <Button onClick={handleRedeem} variant="contained" sx={{ bgcolor: '#059669', borderRadius: 2, px: 4 }}>Confirm Redemption</Button>
        </DialogActions>
      </Dialog>

      <Snackbar 
        open={snackbar.open} 
        autoHideDuration={6000} 
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity} sx={{ width: '100%', borderRadius: 2 }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
}
