import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  Box, Drawer, AppBar, Toolbar, Typography, List, 
  ListItem, ListItemButton, ListItemIcon, ListItemText, Container,
  IconButton, Divider, Paper, TextField, Stack, Badge, Avatar, alpha, Button, Chip,
  Menu, MenuItem
} from '@mui/material';
import { 
  School as SchoolIcon, AccountBalanceWallet as WalletIcon, 
  FitnessCenter as FitnessIcon, TrendingUp as PlanningIcon, 
  Menu as MenuIcon, Logout as LogoutIcon, Assessment as PortfolioIcon,
  Business as BusinessIcon, Security as AdminIcon,
  Notifications as NotificationIcon, Search as SearchIcon,
  Payment as FinancialIcon, Psychology as LmsIcon
} from '@mui/icons-material';

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

  const handleNotifOpen = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);
  const handleNotifClose = () => setAnchorEl(null);

  const menuConfigs = {
    customer: [
      { text: 'Dashboard', icon: <PortfolioIcon />, path: '/customer/portfolio' },
      { text: 'Learning & LMS', icon: <SchoolIcon />, path: '/customer/learning' },
      { text: 'Financial Advisors', icon: <LmsIcon />, path: '/customer/advisors' },
      { text: 'Digital Wallet', icon: <WalletIcon />, path: '/customer/wallet' },
      { text: 'Wellness & Care', icon: <FitnessIcon />, path: '/customer/wellness' },
      { text: 'Financial Planning', icon: <PlanningIcon />, path: '/customer/planning' },
    ],
    employer: [
      { text: 'Payroll & Compliance', icon: <BusinessIcon />, path: '/employer/payroll' },
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
      <Box sx={{ p: 3, mb: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
          <Avatar sx={{ width: 45, height: 45, bgcolor: 'primary.main', border: '2px solid rgba(255,255,255,0.1)' }}>SJ</Avatar>
          <Box>
            <Typography variant="subtitle1" sx={{ fontWeight: 800, lineHeight: 1 }}>Sarah Jenkins</Typography>
            <Typography variant="caption" color="text.secondary">sarah.j@email.com</Typography>
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
      <List sx={{ px: 2, py: 3 }}>
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
                  bgcolor: alpha('#8b5cf6', 0.1),
                  color: 'primary.main',
                  '&:hover': { bgcolor: alpha('#8b5cf6', 0.15) },
                  '& .MuiListItemIcon-root': { color: 'primary.main' }
                },
                '&:hover': {
                  bgcolor: 'rgba(255,255,255,0.03)',
                  transform: 'translateX(4px)'
                }
              }}
            >
              <ListItemIcon sx={{ color: location.pathname === item.path ? 'primary.main' : 'text.secondary', minWidth: 45 }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} sx={{ '& .MuiTypography-root': { fontWeight: 600, fontSize: '0.95rem' } }} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
      <Box sx={{ mt: 'auto', p: 3 }}>
        <Paper sx={{ p: 2, bgcolor: 'rgba(255,255,255,0.03)', borderRadius: 3, mb: 2 }}>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>System Status</Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 8, height: 8, bgcolor: 'success.main', borderRadius: '50%' }} />
            <Typography variant="caption" sx={{ fontWeight: 700 }}>All Systems Operational</Typography>
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
          
          <Box sx={{ flexGrow: 1, display: 'flex', justifyContent: 'center' }}>
            <Paper sx={{ 
              p: '4px 16px', 
              display: 'flex', 
              alignItems: 'center', 
              width: '100%',
              maxWidth: 600,
              bgcolor: 'rgba(255,255,255,0.03)', 
              borderRadius: 4,
              border: '1px solid rgba(255,255,255,0.08)',
              transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
              '&:focus-within': {
                bgcolor: 'rgba(255,255,255,0.06)',
                borderColor: 'primary.main',
                boxShadow: '0 0 0 4px rgba(139, 92, 246, 0.1)',
                transform: 'scale(1.02)'
              }
            }}>
              <SearchIcon sx={{ color: 'text.secondary', mr: 1.5, fontSize: '1.4rem' }} />
              <TextField 
                placeholder="Search resources or ask Hafida..." 
                fullWidth 
                variant="standard" 
                slotProps={{ input: { disableUnderline: true, sx: { fontSize: '1rem', fontWeight: 500 } } }}
              />
              <Chip label="⌘K" size="small" sx={{ height: 20, bgcolor: 'rgba(255,255,255,0.05)', color: 'text.secondary', fontSize: '0.6rem' }} />
            </Paper>
          </Box>

          <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
            <IconButton color="inherit" onClick={handleNotifOpen}>
              <Badge badgeContent={MOCK_NOTIFICATIONS.length} color="error" overlap="circular">
                <NotificationIcon />
              </Badge>
            </IconButton>
            
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleNotifClose}
              slotProps={{ 
                paper: { 
                  sx: { 
                    width: 380, 
                    mt: 1.5, 
                    bgcolor: '#111827', 
                    border: '1px solid rgba(255,255,255,0.05)',
                    boxShadow: '0 20px 40px rgba(0,0,0,0.4)',
                    borderRadius: 3
                  } 
                } 
              }}
              transformOrigin={{ horizontal: 'right', vertical: 'top' }}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
              <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>Notifications</Typography>
                <Button size="small" variant="text" sx={{ fontSize: '0.7rem' }}>Mark all read</Button>
              </Box>
              <Divider sx={{ opacity: 0.05 }} />
              {MOCK_NOTIFICATIONS.map((n) => (
                <MenuItem key={n.id} onClick={handleNotifClose} sx={{ py: 2, px: 2, display: 'flex', gap: 2, '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                  <Avatar sx={{ bgcolor: alpha(n.type === 'financial' ? '#10b981' : n.type === 'lms' ? '#8b5cf6' : '#ef4444', 0.1), color: n.type === 'financial' ? '#10b981' : n.type === 'lms' ? '#8b5cf6' : '#ef4444' }}>
                    {n.icon}
                  </Avatar>
                  <Box sx={{ flexGrow: 1 }}>
                    <Typography variant="body2" sx={{ fontWeight: 700 }}>{n.title}</Typography>
                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 0.5 }}>{n.desc}</Typography>
                    <Typography variant="caption" sx={{ fontSize: '0.65rem', opacity: 0.5 }}>{n.time}</Typography>
                  </Box>
                </MenuItem>
              ))}
              <Divider sx={{ opacity: 0.05 }} />
              <Box sx={{ p: 1.5 }}>
                <Button fullWidth variant="contained" size="small" onClick={() => navigate(`/${role}/notifications`)} sx={{ borderRadius: 2 }}>See All Activity</Button>
              </Box>
            </Menu>

            <IconButton onClick={() => navigate(`/${role}/profile`)} sx={{ p: 0 }}>
              <Avatar sx={{ bgcolor: role === 'admin' ? 'error.main' : 'secondary.main', width: 35, height: 35, fontSize: '0.9rem' }}>SJ</Avatar>
            </IconButton>
          </Stack>
        </Toolbar>
      </AppBar>

      <Box component="nav" sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}>
        <Drawer variant="temporary" open={mobileOpen} onClose={() => setMobileOpen(false)} ModalProps={{ keepMounted: true }} sx={{ display: { xs: 'block', md: 'none' }, '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH, bgcolor: 'background.paper' } }}>{drawerContent}</Drawer>
        <Drawer variant="permanent" sx={{ display: { xs: 'none', md: 'block' }, '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH, bgcolor: 'background.paper', borderRight: '1px solid rgba(255,255,255,0.05)' } }} open>{drawerContent}</Drawer>
      </Box>

      <Box component="main" sx={{ flexGrow: 1, p: 3, width: { md: `calc(100% - ${DRAWER_WIDTH}px)` }, mt: 8 }}>
        <Container maxWidth="xl">{children}</Container>
      </Box>
    </Box>
  );
}
