import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  Box, Drawer, AppBar, Toolbar, Typography, List, 
  ListItem, ListItemButton, ListItemIcon, ListItemText, Container,
  IconButton, Divider, Paper, TextField, Stack, Badge, Avatar, alpha, Button, Chip,
  Menu, MenuItem, Tooltip, Fab
} from '@mui/material';
import { 
  School as SchoolIcon, AccountBalanceWallet as WalletIcon, 
  FitnessCenter as FitnessIcon, TrendingUp as PlanningIcon, 
  TrendingUp as TrendingIcon,
  Menu as MenuIcon, Logout as LogoutIcon, Assessment as PortfolioIcon,
  Business as BusinessIcon, Security as AdminIcon,
  Notifications as NotificationIcon, Search as SearchIcon,
  Payment as FinancialIcon, Psychology as LmsIcon,
  CardGiftcard as GiftIcon,
  Feedback as FeedbackIcon, Star as StarIcon
} from '@mui/icons-material';
import { api } from '../api';
import HafidaAssistant from './HafidaAssistant';

const DRAWER_WIDTH = 280;

const MOCK_NOTIFICATIONS = [
  { id: 1, title: 'Approved Loan Funds', desc: 'SAR 25,000 has been credited to your wallet.', type: 'financial', time: '2m ago', icon: <FinancialIcon /> },
  { id: 2, title: 'New Course Available', desc: 'Sustainable Investing 101 is now open.', type: 'lms', time: '1h ago', icon: <LmsIcon /> },
  { id: 3, title: 'Security Alert', desc: 'Login detected from a new Azure region.', type: 'security', time: '3h ago', icon: <AdminIcon /> },
];

interface LayoutProps {
  children: React.ReactNode;
  role: 'customer' | 'employer' | 'admin';
}

export default function Layout({ children, role }: LayoutProps) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const location = useLocation();
  const navigate = useNavigate();
  const [feedbackOpen, setFeedbackOpen] = useState(false);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [highAccessibility, setHighAccessibility] = useState(false);

  useEffect(() => {
    if (highAccessibility) {
      document.body.classList.add('high-accessibility');
    } else {
      document.body.classList.remove('high-accessibility');
    }
  }, [highAccessibility]);

  const handleFeedbackSubmit = async () => {
    setSubmitting(true);
    try {
      const userId = '947458a5-6912-4b1e-b6db-e56cfbdc4bcc'; 
      await api.submitFeatureReview(userId, 'Overall App Experience', rating, comment);
      setFeedbackOpen(false);
      setComment('');
      alert('Thank you for your feedback! This helps us build a better NSIP.');
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleNotifOpen = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);
  const handleNotifClose = () => setAnchorEl(null);

  const menuConfigs = {
    customer: [
      { text: 'Dashboard', icon: <PortfolioIcon />, path: '/customer/portfolio' },
      { text: 'National Marketplace', icon: <GiftIcon />, path: '/customer/marketplace' },
      { text: 'Learning & LMS', icon: <SchoolIcon />, path: '/customer/learning' },
      { text: 'Financial Advisors', icon: <LmsIcon />, path: '/customer/advisors' },
      { text: 'Impact Events', icon: <MenuIcon />, path: '/customer/events' },
      { text: 'Digital Wallet', icon: <WalletIcon />, path: '/customer/wallet' },
      { text: 'Wellness & Care', icon: <FitnessIcon />, path: '/customer/wellness' },
      { text: 'Financial Planning', icon: <PlanningIcon />, path: '/customer/planning' },
    ],
    employer: [
      { text: 'Payroll & Compliance', icon: <BusinessIcon />, path: '/employer/payroll' },
      { text: 'Workforce Insights', icon: <TrendingIcon />, path: '/employer/analytics' },
      { text: 'Propose Event', icon: <PortfolioIcon />, path: '/employer/events' },
    ],
    admin: [
      { text: 'Claim Approvals', icon: <AdminIcon />, path: '/admin/claims' },
      { text: 'Event Approvals', icon: <PortfolioIcon />, path: '/admin/events' },
    ]
  };

  const menuItems = menuConfigs[role] || [];

  const drawerContent = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ p: 3, mb: 1 }}>
        <Box 
          component="img" 
          src="/logo.png" 
          sx={{ 
            height: 70, 
            mb: 3, 
            cursor: 'pointer'
          }} 
          onClick={() => navigate('/')}
        />
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <Avatar sx={{ width: 40, height: 40, bgcolor: '#059669', border: '1px solid rgba(255,255,255,0.1)' }}>SJ</Avatar>
          <Box>
            <Typography variant="subtitle2" sx={{ fontWeight: 800, lineHeight: 1 }}>Sultan Jameel</Typography>
            <Typography variant="caption" color="text.secondary">National ID: ****4201</Typography>
          </Box>
        </Box>
        <Paper sx={{ 
          p: '4px 12px', 
          display: 'flex', 
          alignItems: 'center', 
          bgcolor: 'rgba(255,255,255,0.03)', 
          borderRadius: 2,
          border: '1px solid rgba(255,255,255,0.05)'
        }}>
          <SearchIcon sx={{ color: 'text.secondary', mr: 1, fontSize: '1rem' }} />
          <TextField 
            placeholder="Search..." 
            variant="standard" 
            slotProps={{ input: { disableUnderline: true, sx: { fontSize: '0.8rem' } } }}
          />
        </Paper>
      </Box>
      <Divider sx={{ opacity: 0.05 }} />
      <List sx={{ px: 2, py: 3, flexGrow: 1 }}>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding sx={{ mb: 1.5 }}>
            <ListItemButton 
              onClick={() => { navigate(item.path); setMobileOpen(false); }}
              selected={location.pathname === item.path}
              sx={{ 
                borderRadius: 3,
                py: 1.5,
                transition: 'all 0.2s ease',
                '&.Mui-selected': { 
                  bgcolor: alpha('#059669', 0.1),
                  color: '#059669',
                  '&:hover': { bgcolor: alpha('#059669', 0.15) },
                  '& .MuiListItemIcon-root': { color: '#059669' }
                },
                '&:hover': {
                  bgcolor: 'rgba(5, 150, 105, 0.03)',
                  transform: 'translateX(4px)'
                }
              }}
            >
              <ListItemIcon sx={{ color: location.pathname === item.path ? '#059669' : 'text.secondary', minWidth: 45 }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} sx={{ '& .MuiTypography-root': { fontWeight: 600, fontSize: '0.95rem' } }} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
      <Box sx={{ p: 3, borderTop: '1px solid rgba(255,255,255,0.05)' }}>
        <Stack direction="row" sx={{ alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="caption" sx={{ fontWeight: 700, color: 'text.secondary' }}>ACCESSIBILITY MODE</Typography>
          <Button 
            size="small" 
            variant={highAccessibility ? "contained" : "outlined"}
            onClick={() => setHighAccessibility(!highAccessibility)}
            sx={{ borderRadius: 2, fontSize: '0.65rem', minWidth: 60, bgcolor: highAccessibility ? '#059669' : 'transparent', color: highAccessibility ? 'white' : 'text.secondary' }}
          >
            {highAccessibility ? "ON" : "OFF"}
          </Button>
        </Stack>
        <Paper sx={{ p: 2, bgcolor: 'rgba(255,255,255,0.03)', borderRadius: 3, mb: 2 }}>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>National Safety Net</Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 8, height: 8, bgcolor: 'success.main', borderRadius: '50%' }} />
            <Typography variant="caption" sx={{ fontWeight: 700 }}>Your Future is Protected</Typography>
          </Box>
        </Paper>
        <Button fullWidth variant="text" startIcon={<LogoutIcon />} onClick={() => navigate('/')} color="inherit" sx={{ opacity: 0.5, borderRadius: 2 }}>Log Out</Button>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="fixed" sx={{ 
        width: { md: `calc(100% - ${DRAWER_WIDTH}px)` }, 
        ml: { md: `${DRAWER_WIDTH}px` },
        bgcolor: 'background.default',
        boxShadow: 'none',
        borderBottom: '1px solid rgba(255,255,255,0.05)',
        backdropFilter: 'blur(10px)'
      }}>
        <Toolbar sx={{ display: 'flex', gap: 2 }}>
          <IconButton color="inherit" edge="start" onClick={() => setMobileOpen(!mobileOpen)} sx={{ display: { md: 'none' } }}><MenuIcon /></IconButton>
          
          <Box 
            component="img" 
            src="/logo.png" 
            sx={{ 
              height: 35, 
              display: { xs: 'block', md: 'none' },
              mr: 1
            }} 
          />
          <Box sx={{ display: { xs: 'none', lg: 'flex' }, alignItems: 'center', gap: 1, mr: 2 }}>
            <Chip 
              icon={<StarIcon sx={{ color: '#059669', fontSize: '1rem !important' }} />}
              label="NATIONAL SUPPORT & INTERVENTION" 
              variant="outlined" 
              sx={{ 
                borderColor: 'rgba(5, 150, 105, 0.2)',
                color: '#059669',
                fontWeight: 700,
                fontSize: '0.65rem'
              }} 
            />
          </Box>
          <Box sx={{ flexGrow: 1, display: 'flex', justifyContent: 'center' }}>
            <Paper sx={{ 
               p: '4px 16px', 
               display: 'flex', 
               alignItems: 'center', 
               width: '100%',
               maxWidth: 600,
               bgcolor: 'rgba(0,0,0,0.02)', 
               borderRadius: 2,
               border: '1px solid rgba(0,0,0,0.08)',
               transition: 'all 0.2s ease',
               '&:focus-within': {
                 bgcolor: 'rgba(0,0,0,0.04)',
                 borderColor: '#1e1b4b',
               }
             }}>
               <SearchIcon sx={{ color: 'text.secondary', mr: 1.5, fontSize: '1.2rem' }} />
               <TextField 
                 placeholder="Search resources..." 
                 fullWidth 
                 variant="standard" 
                 slotProps={{ input: { disableUnderline: true, sx: { fontSize: '0.9rem' } } }}
               />
               <Chip label="⌘K" size="small" sx={{ height: 20, bgcolor: 'rgba(0,0,0,0.05)', color: 'text.secondary', fontSize: '0.6rem' }} />
             </Paper>
           </Box>

          <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
            <IconButton color="inherit" onClick={handleNotifOpen}>
              <Badge badgeContent={MOCK_NOTIFICATIONS.length} color="error" overlap="circular">
                <NotificationIcon />
              </Badge>
            </IconButton>

            <Button 
              variant="outlined" 
              size="small" 
              sx={{ 
                borderRadius: 2, 
                px: 2, 
                borderColor: 'rgba(255,255,255,0.1)',
                color: 'text.secondary',
                fontSize: '0.75rem',
                fontWeight: 700
              }}
              onClick={() => setFeedbackOpen(true)}
            >
              FEEDBACK
            </Button>
          </Stack>
        </Toolbar>

        <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleNotifClose} slotProps={{ paper: { sx: { width: 320, borderRadius: 3, mt: 1.5, border: '1px solid rgba(255,255,255,0.05)', bgcolor: 'background.paper' } } }}>
          <Box sx={{ p: 2, borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
            <Typography variant="subtitle2" sx={{ fontWeight: 900 }}>Notifications</Typography>
          </Box>
          {MOCK_NOTIFICATIONS.map((n) => (
            <MenuItem key={n.id} onClick={handleNotifClose} sx={{ py: 2, borderBottom: '1px solid rgba(255,255,255,0.03)' }}>
              <ListItemIcon sx={{ color: 'primary.main', minWidth: 40 }}>{n.icon}</ListItemIcon>
              <Box>
                <Typography variant="body2" sx={{ fontWeight: 700 }}>{n.title}</Typography>
                <Typography variant="caption" color="text.secondary">{n.desc}</Typography>
                <Typography variant="caption" sx={{ display: 'block', mt: 0.5, color: 'text.disabled', fontSize: '0.65rem' }}>{n.time}</Typography>
              </Box>
            </MenuItem>
          ))}
          <Box sx={{ p: 1.5, textAlign: 'center' }}>
            <Button size="small" fullWidth sx={{ textTransform: 'none', color: 'primary.main', fontWeight: 700 }}>View All</Button>
          </Box>
        </Menu>
      </AppBar>

      <Box component="nav" sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}>
        <Drawer 
          variant="temporary" 
          open={mobileOpen} 
          onClose={() => setMobileOpen(false)} 
          ModalProps={{ keepMounted: true }} 
          sx={{ 
            display: { xs: 'block', md: 'none' }, 
            '& .MuiDrawer-paper': { 
              boxSizing: 'border-box', 
              width: DRAWER_WIDTH, 
              bgcolor: 'background.paper',
              borderRight: '1px solid rgba(0,0,0,0.08)'
            } 
          }}
        >
          {drawerContent}
        </Drawer>
        <Drawer 
          variant="permanent" 
          sx={{ 
            display: { xs: 'none', md: 'block' }, 
            '& .MuiDrawer-paper': { 
              boxSizing: 'border-box', 
              width: DRAWER_WIDTH, 
              bgcolor: 'background.paper',
              borderRight: '1px solid rgba(0,0,0,0.08)',
              boxShadow: 'none'
            } 
          }} 
          open
        >
          {drawerContent}
        </Drawer>
      </Box>

      <Box component="main" sx={{ flexGrow: 1, p: 3, width: { md: `calc(100% - ${DRAWER_WIDTH}px)` }, mt: 8 }}>
        <Container maxWidth="xl">{children}</Container>
      </Box>

      {/* Feedback Dialog */}
      <Menu
        open={feedbackOpen}
        onClose={() => setFeedbackOpen(false)}
        anchorOrigin={{ vertical: 'center', horizontal: 'center' }}
        transformOrigin={{ vertical: 'center', horizontal: 'center' }}
        slotProps={{ paper: { sx: { width: 400, p: 3, borderRadius: 4, bgcolor: 'background.paper' } } }}
      >
        <Typography variant="h6" sx={{ fontWeight: 900, mb: 1 }}>How was your experience?</Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>Your feedback helps us pull more citizens up.</Typography>
        
        <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 700 }}>Rating</Typography>
        <Stack direction="row" spacing={1} sx={{ mb: 3 }}>
          {[1, 2, 3, 4, 5].map((val) => (
            <Fab 
              key={val} 
              size="small" 
              onClick={() => setRating(val)}
              color={rating === val ? "primary" : "default"}
              sx={{ boxShadow: 'none' }}
            >
              {val}
            </Fab>
          ))}
        </Stack>

        <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 700 }}>Comment</Typography>
        <TextField 
          fullWidth 
          multiline 
          rows={4} 
          placeholder="Share your thoughts..." 
          variant="outlined"
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          sx={{ mb: 3, '& .MuiOutlinedInput-root': { borderRadius: 3 } }}
        />

        <Button 
          fullWidth 
          variant="contained" 
          onClick={handleFeedbackSubmit} 
          disabled={submitting}
          sx={{ borderRadius: 2, py: 1 }}
        >
          {submitting ? 'Sending...' : 'Submit Feedback'}
        </Button>
      </Menu>
      <HafidaAssistant />
    </Box>
  );
}
