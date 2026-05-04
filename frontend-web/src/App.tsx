import { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { 
  ThemeProvider, CssBaseline, Box, Drawer, AppBar, Toolbar, Typography, List, 
  ListItem, ListItemButton, ListItemIcon, ListItemText, Container, Card, 
  CardContent, Button, Avatar, Chip, IconButton, Divider, Paper, LinearProgress,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField,
  Rating, Stack, MenuItem, Select, FormControl, InputLabel, Grid, Tab, Tabs,
  Dialog, DialogTitle, DialogContent, DialogActions, Alert, Fab, Badge,
  alpha
} from '@mui/material';
import { 
  School as SchoolIcon, AccountBalanceWallet as WalletIcon, 
  FitnessCenter as FitnessIcon, TrendingUp as PlanningIcon, 
  Menu as MenuIcon, Logout as LogoutIcon, Assessment as PortfolioIcon,
  Business as BusinessIcon, Security as AdminIcon, Add as AddIcon,
  WhatsApp as WhatsAppIcon, Email as EmailIcon, Chat as ChatIcon, Fingerprint as BioIcon,
  Smartphone as OtpIcon, Visibility as NafathIcon,
  Event as EventIcon, Notifications as NotificationIcon,
  Download as DownloadIcon, CheckCircle as CheckIcon,
  Public as PublicIcon, PlayCircle as PlayIcon,
  Calculate as CalcIcon,
  PersonSearch as ExpertIcon, ShowChart as ForecastIcon,
  CardMembership as CertificateIcon, Edit as EditIcon, Save as SaveIcon,
  EventNote as SessionIcon, Star as StarIcon, EventRepeat as RescheduleIcon,
  SmartToy as AiIcon, SupportAgent as TicketIcon, NearMe as RedirectIcon,
  Send as SendIcon, Search as SearchIcon
} from '@mui/icons-material';
import { api } from './api';
import theme from './theme';

// =============================================================================
// Layout Components
// =============================================================================
const DRAWER_WIDTH = 280;

function Layout({ children, role }: { children: React.ReactNode, role: 'customer' | 'employer' | 'admin' }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  const menuConfigs = {
    customer: [
      { text: 'Portfolio & Loans', icon: <PortfolioIcon />, path: '/customer/portfolio' },
      { text: 'Learning & LMS', icon: <SchoolIcon />, path: '/customer/learning' },
      { text: 'Digital Wallet', icon: <WalletIcon />, path: '/customer/wallet' },
      { text: 'Wellness & Care', icon: <FitnessIcon />, path: '/customer/wellness' },
      { text: 'Financial Planning', icon: <PlanningIcon />, path: '/customer/planning' },
      { text: 'Help & Notifications', icon: <ChatIcon />, path: '/customer/help' },
    ],
    employer: [
      { text: 'Payroll & Compliance', icon: <BusinessIcon />, path: '/employer/payroll' },
      { text: 'Propose Event', icon: <EventIcon />, path: '/employer/events' },
    ],
    admin: [
      { text: 'Claim Approvals', icon: <AdminIcon />, path: '/admin/claims' },
      { text: 'SLA & Forecasting', icon: <ForecastIcon />, path: '/admin/sla' },
      { text: 'Event Approvals', icon: <EventIcon />, path: '/admin/events' },
    ]
  };

  const menuItems = menuConfigs[role];

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
            <IconButton color="inherit" onClick={() => navigate(`/${role}/notifications`)}>
              <Badge badgeContent={3} color="error"><NotificationIcon /></Badge>
            </IconButton>
            <IconButton onClick={() => navigate(`/${role}/profile`)} sx={{ p: 0 }}>
              <Avatar sx={{ bgcolor: role === 'admin' ? 'error.main' : 'secondary.main', width: 35, height: 35, fontSize: '0.9rem' }}>JD</Avatar>
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

// =============================================================================
// Customer Pages
// =============================================================================

function PortfolioPage() {
  const transactions = [
    { type: 'Pension Contribution', date: 'Jan 15', source: 'Employer Fund', amount: '+$1,250.00', color: '#10b981', icon: <PortfolioIcon fontSize="small" /> },
    { type: 'Health Premium', date: 'Jan 10', source: 'State Health', amount: '-$450.00', color: '#ef4444', icon: <FitnessIcon fontSize="small" /> },
    { type: 'Dividend Payout', date: 'Jan 02', source: 'ETF Fund', amount: '+$850.50', color: '#8b5cf6', icon: <PlanningIcon fontSize="small" /> },
    { type: 'State Contribution', date: 'Dec 28', source: 'Govt. Offset', amount: '+$300.00', color: '#10b981', icon: <PortfolioIcon fontSize="small" /> },
  ];

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 0.5, color: 'secondary.main' }}>Social Insurance Dashboard</Typography>
        <Typography variant="subtitle1" color="text.secondary">Welcome back, Sarah Jenkins!</Typography>
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
                <Typography variant="h2" sx={{ fontWeight: 900, letterSpacing: -2 }}>$345,780.20</Typography>
                <Typography variant="body2" sx={{ color: 'success.main', fontWeight: 800, mt: 1 }}>+12.8% YoY Growth</Typography>
              </Box>
              <Box sx={{ textAlign: 'right' }}>
                <Typography variant="subtitle2" sx={{ opacity: 0.7, fontWeight: 700 }}>Projected Retirement (Age 67)</Typography>
                <Typography variant="h4" sx={{ fontWeight: 800 }}>$890,500.00</Typography>
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

        {/* Investment Growth Chart */}
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 4, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 800, alignSelf: 'flex-start', mb: 3 }}>Investment Growth</Typography>
            <Box sx={{ position: 'relative', width: 160, height: 160 }}>
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
                          <Box sx={{ p: 1, bgcolor: alpha(t.color, 0.1), color: t.color, borderRadius: 2, display: 'flex' }}>
                            {t.icon}
                          </Box>
                          <Typography variant="body2" sx={{ fontWeight: 700 }}>{t.type}</Typography>
                        </Stack>
                      </TableCell>
                      <TableCell><Typography variant="body2" color="text.secondary">{t.date}</Typography></TableCell>
                      <TableCell><Typography variant="body2" color="text.secondary">{t.source}</Typography></TableCell>
                      <TableCell sx={{ textAlign: 'right' }}>
                        <Typography variant="body2" sx={{ fontWeight: 900, color: t.amount.includes('+') ? 'success.main' : 'error.main' }}>
                          {t.amount}
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

function LearningPage() {
  const [tab, setTab] = useState(0);
  const courses = [
    { id: 1, title: 'Saudi Labor Law Basics', category: 'Compliance', points: 250, progress: 100, img: 'law.jpg' },
    { id: 2, title: 'Wealth Management 101', category: 'Finance', points: 500, progress: 45, img: 'finance.jpg' },
    { id: 3, title: 'Pension & Social Security', category: 'Planning', points: 150, progress: 10, img: 'pension.jpg' }
  ];

  const certificates = [
    { id: 'CERT-001', title: 'GOSI Fundamentals', issued: 'Oct 2024', status: 'Verified' },
    { id: 'CERT-002', title: 'Cybersecurity Awareness', issued: 'Sept 2024', status: 'Verified' }
  ];

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Learning & LMS</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 4 }}>
        <Tab label="Active Courses" />
        <Tab label="Certificates" />
        <Tab label="Leaderboard" />
      </Tabs>

      {tab === 0 && (
        <Grid container spacing={3}>
          {courses.map(c => (
            <Grid key={c.id} size={{ xs: 12, md: 4 }}>
              <Card>
                <Box sx={{ height: 140, bgcolor: 'rgba(255,255,255,0.02)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <PlayIcon sx={{ fontSize: 40, opacity: 0.3 }} />
                </Box>
                <CardContent>
                  <Typography variant="caption" color="secondary" sx={{ fontWeight: 700 }}>{c.category}</Typography>
                  <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>{c.title}</Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
                    <LinearProgress variant="determinate" value={c.progress} sx={{ flexGrow: 1 }} />
                    <Typography variant="caption">{c.progress}%</Typography>
                  </Box>
                  <Typography variant="caption" color="text.secondary">Earn {c.points} pts upon completion</Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {tab === 1 && (
        <Stack spacing={2}>
          {certificates.map(cert => (
            <Card key={cert.id} sx={{ p: 3, borderLeft: '5px solid', borderLeftColor: 'success.main' }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <CertificateIcon color="success" sx={{ fontSize: 30 }} />
                  <Box>
                    <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{cert.title}</Typography>
                    <Typography variant="caption" color="text.secondary">Issued: {cert.issued} • ID: {cert.id}</Typography>
                  </Box>
                </Box>
                <Button variant="outlined" size="small" startIcon={<DownloadIcon />}>Download PDF</Button>
              </Box>
            </Card>
          ))}
        </Stack>
      )}
    </Box>
  );
}

function WellnessPage() {
  const steps = 8420;
  const target = 10000;
  
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Wellness & Care</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ p: 4, mb: 3 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Daily Activity Tracking</Typography>
            <Box sx={{ position: 'relative', display: 'inline-flex', mb: 3 }}>
              <LinearProgress variant="determinate" value={(steps/target)*100} sx={{ height: 20, width: 300, borderRadius: 10 }} />
              <Typography variant="caption" sx={{ position: 'absolute', top: 22, left: 0 }}>{steps} / {target} steps today</Typography>
            </Box>
            <Typography variant="body2" sx={{ color: 'success.main', fontWeight: 600 }}>You're on a 5-day streak! +50 points bonus incoming.</Typography>
          </Card>
          
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>Upcoming Wellness Events</Typography>
          <Card sx={{ p: 2 }}>
            <List>
              <ListItem>
                <ListItemIcon><FitnessIcon color="primary" /></ListItemIcon>
                <ListItemText primary="Riyadh Marathon 2024" secondary="Nov 25, 2024 • 06:00 AM" />
                <Button variant="contained" size="small">Join</Button>
              </ListItem>
              <Divider sx={{ my: 1 }} />
              <ListItem>
                <ListItemIcon><FitnessIcon color="primary" /></ListItemIcon>
                <ListItemText primary="Mental Health Webinar" secondary="Nov 28, 2024 • 04:00 PM" />
                <Button variant="contained" size="small">Join</Button>
              </ListItem>
            </List>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, bgcolor: 'rgba(255,255,255,0.02)' }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>Health Integrations</Typography>
            <Stack spacing={2}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography variant="body2">Apple Health</Typography>
                <Chip label="Connected" size="small" color="success" />
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography variant="body2">Garmin Connect</Typography>
                <Button size="small">Connect</Button>
              </Box>
            </Stack>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

function WalletPage() {
  const transactions = [
    { id: 'TXN-9982', desc: 'October Salary Contribution', amount: '-2,450.00', date: 'Oct 31, 2024', status: 'Completed', type: 'deduction' },
    { id: 'TXN-9981', desc: 'Quiz Reward: Saudi Labor Law', amount: '+250.00', date: 'Oct 28, 2024', status: 'Completed', type: 'reward' },
    { id: 'TXN-9980', desc: 'Wallet Top-up (Apple Pay)', amount: '+1,000.00', date: 'Oct 25, 2024', status: 'Completed', type: 'topup' },
    { id: 'TXN-9979', desc: 'Advisor Session: Dr. Faisal', amount: '-1,000.00', date: 'Oct 22, 2024', status: 'Completed', type: 'deduction' },
  ];

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Digital Wallet & Points</Typography>
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, md: 5 }}>
          <Card sx={{ 
            background: 'linear-gradient(135deg, #8b5cf6 0%, #d946ef 100%)', 
            color: 'white', 
            minHeight: 220, 
            display: 'flex', 
            flexDirection: 'column', 
            justifyContent: 'space-between',
            p: 3,
            borderRadius: 6,
            position: 'relative',
            overflow: 'hidden',
            boxShadow: '0 20px 50px rgba(139, 92, 246, 0.4)',
            '&::before': {
              content: '""',
              position: 'absolute',
              top: -50,
              right: -50,
              width: 150,
              height: 150,
              borderRadius: '50%',
              background: 'rgba(255, 255, 255, 0.1)',
              filter: 'blur(40px)',
            }
          }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
              <Box>
                <Typography variant="h6" sx={{ opacity: 0.9, fontWeight: 700, mb: -0.5 }}>Digital Wallet</Typography>
                <Typography variant="caption" sx={{ opacity: 0.7 }}>Saudi National Bank (Linked)</Typography>
              </Box>
              <PublicIcon sx={{ opacity: 0.8, fontSize: '2rem' }} />
            </Box>
            <Box>
              <Typography variant="h3" sx={{ fontWeight: 800, letterSpacing: -1 }}>SAR 4,820.50</Typography>
              <Typography variant="caption" sx={{ opacity: 0.8 }}>Available Balance</Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'end' }}>
              <Typography variant="h6" sx={{ letterSpacing: 4, fontFamily: 'monospace', opacity: 0.9 }}>**** 8829</Typography>
              <Typography variant="subtitle2" sx={{ fontWeight: 800, bgcolor: 'rgba(255,255,255,0.2)', px: 1.5, py: 0.5, borderRadius: 2 }}>VISA PLATINUM</Typography>
            </Box>
          </Card>
          <Stack direction="row" spacing={2} sx={{ mt: 3 }}>
            <Button fullWidth variant="contained" startIcon={<AddIcon />} sx={{ borderRadius: 3, py: 1.5 }}>Top Up</Button>
            <Button fullWidth variant="outlined" startIcon={<DownloadIcon />} sx={{ borderRadius: 3, py: 1.5 }}>Withdraw</Button>
          </Stack>
        </Grid>
        
        <Grid size={{ xs: 12, md: 7 }}>
          <Card sx={{ p: 4, bgcolor: 'secondary.main', color: 'white' }}>
            <Typography variant="subtitle1" sx={{ opacity: 0.8 }}>Rewards Points</Typography>
            <Typography variant="h2" sx={{ fontWeight: 800, my: 1 }}>1,250 Pts</Typography>
            <Typography variant="body2" sx={{ mt: 3 }}>Next tier: **Gold Contributor** (500 pts left)</Typography>
            <LinearProgress variant="determinate" value={70} sx={{ mt: 1, bgcolor: 'rgba(255,255,255,0.1)', '& .MuiLinearProgress-bar': { bgcolor: 'white' } }} />
          </Card>
        </Grid>
      </Grid>

      <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>Transaction History</Typography>
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Amount</TableCell>
                <TableCell>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions.map(txn => (
                <TableRow key={txn.id}>
                  <TableCell>{txn.date}</TableCell>
                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>{txn.desc}</Typography>
                    <Typography variant="caption" color="text.secondary">{txn.id}</Typography>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 800, color: txn.amount.includes('+') ? 'success.main' : 'error.main' }}>
                      {txn.amount}
                    </Typography>
                  </TableCell>
                  <TableCell><Chip label={txn.status} size="small" variant="outlined" /></TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}


function PlanningPage() {
  const [tab, setTab] = useState(0);
  const [income, setIncome] = useState(15000);
  const [expenses, setExpenses] = useState(8000);
  const [expertDialog, setExpertDialog] = useState(false);
  const [rescheduleDialog, setRescheduleDialog] = useState(false);
  const [reviewDialog, setReviewDialog] = useState(false);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');

  const [sessions, setSessions] = useState([
    { id: 'SESS-001', advisor: 'Dr. Faisal Ahmed', type: 'Wealth Management', time: '2024-11-10 10:00 AM', status: 'SCHEDULED' },
    { id: 'SESS-002', advisor: 'Noura Al-Saud', type: 'Pension Review', time: '2024-10-25 02:00 PM', status: 'COMPLETED' },
  ]);

  const emergencyFund = expenses * 6; // Recommended 6 months
  const userPoints = 1250; // In real app, fetch from rewards-service

  const handleCancel = (id: string) => {
    setSessions(sessions.map(s => s.id === id ? { ...s, status: 'CANCELLED' } : s));
  };

  const handleReviewSubmit = () => {
    setReviewDialog(false);
    setRating(5);
    setComment('');
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>Financial Planning Center</Typography>
        <Chip label={`${userPoints} Points Available`} color="secondary" variant="outlined" sx={{ fontWeight: 700 }} />
      </Box>
      
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 4 }}>
        <Tab icon={<CalcIcon />} label="Budget Calc (BMS)" />
        <Tab icon={<CheckIcon />} label="Emergency Fund (EFC)" />
        <Tab icon={<ExpertIcon />} label="Experts" />
        <Tab icon={<SessionIcon />} label="My Sessions" />
      </Tabs>

      {tab === 0 && (
        <Card sx={{ p: 4 }}>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Budget Management Calculator (BMS)</Typography>
          <Grid container spacing={3}>
            <Grid size={{ xs: 12, md: 6 }}><TextField fullWidth label="Monthly Net Income" type="number" value={income} onChange={(e) => setIncome(Number(e.target.value))} /></Grid>
            <Grid size={{ xs: 12, md: 6 }}><TextField fullWidth label="Monthly Expenses" type="number" value={expenses} onChange={(e) => setExpenses(Number(e.target.value))} /></Grid>
          </Grid>
          <Box sx={{ mt: 4, p: 3, bgcolor: 'rgba(255,255,255,0.02)', borderRadius: 2 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>Analysis</Typography>
            <Typography variant="body2" color="text.secondary">Monthly Savings: SAR {income - expenses}</Typography>
            <Typography variant="body2" color="text.secondary">Savings Rate: {Math.round(((income - expenses) / income) * 100)}%</Typography>
            <LinearProgress variant="determinate" value={((income - expenses) / income) * 100} sx={{ mt: 2, height: 10, borderRadius: 5 }} />
          </Box>
        </Card>
      )}

      {tab === 1 && (
        <Card sx={{ p: 4 }}>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Emergency Fund Calculator (EFC)</Typography>
          <Typography variant="body2" sx={{ mb: 4 }}>Based on your expenses of SAR {expenses}/mo, we recommend a safety net of 6 months.</Typography>
          <Box sx={{ textAlign: 'center', p: 4, border: '2px dashed rgba(255,255,255,0.1)', borderRadius: 4 }}>
            <Typography variant="caption" color="text.secondary">Target Fund Amount</Typography>
            <Typography variant="h2" sx={{ fontWeight: 800, color: 'success.main' }}>SAR {emergencyFund}</Typography>
            <Button variant="contained" sx={{ mt: 3 }}>Create Goal</Button>
          </Box>
        </Card>
      )}
      {tab === 2 && (
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Book Session with Financial Expert</Typography>
          <Grid container spacing={3}>
            {[
              { name: 'Dr. Faisal Ahmed', title: 'Senior Wealth Manager', rating: 4.9 },
              { name: 'Noura Al-Saud', title: 'Pension Specialist', rating: 5.0 }
            ].map(expert => (
              <Grid key={expert.name} size={{ xs: 12, md: 6 }}>
                <Card sx={{ p: 3 }}>
                  <Stack direction="row" spacing={3} sx={{ alignItems: 'center' }}>
                    <Avatar sx={{ width: 60, height: 60 }}>{expert.name[0]}</Avatar>
                    <Box sx={{ flexGrow: 1 }}>
                      <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{expert.name}</Typography>
                      <Typography variant="caption" color="text.secondary">{expert.title}</Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}><Rating value={expert.rating} readOnly size="small" /><Typography variant="caption" sx={{ ml: 1 }}>({expert.rating})</Typography></Box>
                    </Box>
                    <Button variant="outlined" onClick={() => setExpertDialog(true)}>Book</Button>
                  </Stack>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}
      {tab === 3 && (
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>My Booked Sessions</Typography>
          <Stack spacing={2}>
            {sessions.map(sess => (
              <Card key={sess.id} sx={{ p: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Avatar sx={{ bgcolor: 'secondary.main' }}>{sess.advisor[0]}</Avatar>
                    <Box>
                      <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{sess.advisor}</Typography>
                      <Typography variant="caption" color="text.secondary">{sess.type} • {sess.time}</Typography>
                    </Box>
                  </Box>
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Chip label={sess.status} size="small" color={sess.status === 'SCHEDULED' ? 'primary' : (sess.status === 'COMPLETED' ? 'success' : 'default')} />
                    {sess.status === 'SCHEDULED' && (
                      <>
                        <Button size="small" variant="outlined" startIcon={<RescheduleIcon />} onClick={() => setRescheduleDialog(true)}>Reschedule</Button>
                        <Button size="small" variant="outlined" color="error" onClick={() => handleCancel(sess.id)}>Cancel</Button>
                      </>
                    )}
                    {sess.status === 'COMPLETED' && (
                      <Button size="small" variant="contained" startIcon={<StarIcon />} onClick={() => setReviewDialog(true)}>Rate</Button>
                    )}
                  </Box>
                </Box>
              </Card>
            ))}
          </Stack>
        </Box>
      )}

      <Dialog open={expertDialog} onClose={() => setExpertDialog(false)}>
        <DialogTitle>Confirm Booking</DialogTitle>
        <DialogContent>Schedule a 30-minute private consultation for 1,000 points?</DialogContent>
        <DialogActions>
          <Button onClick={() => setExpertDialog(false)}>Cancel</Button>
          <Button variant="contained" disabled={userPoints < 1000} onClick={() => {
            setSessions([...sessions, { id: `SESS-${Math.floor(Math.random()*1000)}`, advisor: 'Dr. Faisal Ahmed', type: 'Wealth Management', time: '2024-11-15 09:00 AM', status: 'SCHEDULED' }]);
            setExpertDialog(false);
          }}>Confirm Booking</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={rescheduleDialog} onClose={() => setRescheduleDialog(false)}>
        <DialogTitle>Reschedule Session</DialogTitle>
        <DialogContent>
          <Typography variant="body2" sx={{ mb: 2 }}>Select a new date and time for your consultation.</Typography>
          <TextField fullWidth type="datetime-local" defaultValue="2024-11-20T10:00" />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRescheduleDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => setRescheduleDialog(false)}>Update Time</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={reviewDialog} onClose={() => setReviewDialog(false)}>
        <DialogTitle>Rate Your Session</DialogTitle>
        <DialogContent>
          <Box sx={{ textAlign: 'center', my: 2 }}>
            <Rating value={rating} onChange={(_, v) => setRating(v || 5)} size="large" />
          </Box>
          <TextField fullWidth multiline rows={3} placeholder="Share your experience..." value={comment} onChange={(e) => setComment(e.target.value)} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReviewDialog(false)}>Skip</Button>
          <Button variant="contained" onClick={handleReviewSubmit}>Submit Review</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

function HelpPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Help & Support</Typography>
      
      <Card sx={{ mb: 4, bgcolor: 'primary.main', color: 'primary.contrastText', p: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
          <AiIcon sx={{ fontSize: 60 }} />
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h5" sx={{ fontWeight: 800 }}>Hafida Smart Assistant</Typography>
            <Typography variant="body1">I am Hafida, your personal social insurance assistant. How can I help you today?</Typography>
          </Box>
          <Button variant="contained" color="secondary" sx={{ fontWeight: 700 }}>Chat with Hafida</Button>
        </Box>
      </Card>

      <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>Omni-Channel Contact Methods</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}><Card sx={{ p: 3, textAlign: 'center' }}><WhatsAppIcon sx={{ fontSize: 40, mb: 1, color: '#25D366' }} /><Typography variant="h6">WhatsApp</Typography><Typography variant="caption">+966 50 000 0000</Typography></Card></Grid>
        <Grid size={{ xs: 12, md: 4 }}><Card sx={{ p: 3, textAlign: 'center' }}><EmailIcon sx={{ fontSize: 40, mb: 1, color: '#EA4335' }} /><Typography variant="h6">Email Support</Typography><Typography variant="caption">care@nsip.gov.sa</Typography></Card></Grid>
        <Grid size={{ xs: 12, md: 4 }}><Card sx={{ p: 3, textAlign: 'center' }}><ChatIcon sx={{ fontSize: 40, mb: 1, color: 'primary.main' }} /><Typography variant="h6">Live Chat</Typography><Typography variant="caption">Available 24/7</Typography></Card></Grid>
      </Grid>
    </Box>
  );
}

function Hafida() {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState([
    { text: 'Hello! I am Hafida, your NSIP AI Assistant. How can I help you today?', sender: 'ai' }
  ]);
  const navigate = useNavigate();

  const handleOption = (opt: string) => {
    setMessages([...messages, { text: opt, sender: 'user' }]);
    setTimeout(() => {
      let reply = "";
      if (opt.includes('Portfolio')) { reply = "Redirecting you to your Portfolio..."; navigate('/customer/portfolio'); }
      else if (opt.includes('Ticket')) reply = "I have created support ticket #TKT-992. An agent will contact you shortly.";
      else if (opt.includes('Certificate')) reply = "Generating your GOSI Compliance Certificate... Done! Check your downloads.";
      else reply = "I'm sorry, I didn't quite get that. Can you try again?";
      
      setMessages(prev => [...prev, { text: reply, sender: 'ai' }]);
    }, 1000);
  };

  return (
    <>
      <Fab 
        color="primary" 
        sx={{ 
          position: 'fixed', 
          bottom: 30, 
          right: 30, 
          width: 80, 
          height: 80,
          boxShadow: '0 15px 35px rgba(139, 92, 246, 0.4)',
          '&:hover': { transform: 'scale(1.1) rotate(5deg)' },
          transition: 'all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
          '&::after': {
            content: '""',
            position: 'absolute',
            width: '100%',
            height: '100%',
            borderRadius: '50%',
            border: '2px solid #8b5cf6',
            animation: 'pulse 2s infinite',
          }
        }} 
        onClick={() => setOpen(!open)}
      >
        <AiIcon sx={{ fontSize: 40 }} />
      </Fab>
      
      <Drawer 
        anchor="right" 
        open={open} 
        onClose={() => setOpen(false)} 
        slotProps={{ 
          paper: { 
            sx: { 
              width: { xs: '100%', md: 450 }, 
              bgcolor: '#030712', 
              p: 0,
              borderLeft: '1px solid rgba(255, 255, 255, 0.05)',
              boxShadow: '-20px 0 50px rgba(0,0,0,0.5)'
            } 
          } 
        }}
      >
        <Box sx={{ 
          p: 4, 
          background: 'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)', 
          color: 'white', 
          display: 'flex', 
          alignItems: 'center', 
          gap: 2,
          boxShadow: '0 10px 30px rgba(0,0,0,0.2)'
        }}>
          <Badge overlap="circular" anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }} variant="dot" color="success" sx={{ '& .MuiBadge-badge': { width: 12, height: 12, borderRadius: '50%', border: '2px solid white' } }}>
            <Avatar sx={{ width: 50, height: 50, bgcolor: 'rgba(255,255,255,0.2)' }}><AiIcon sx={{ fontSize: 30 }} /></Avatar>
          </Badge>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 800, mb: -0.5 }}>Hafida</Typography>
            <Typography variant="caption" sx={{ opacity: 0.8 }}>Virtual Assistant • Online</Typography>
          </Box>
        </Box>
        <Box sx={{ flexGrow: 1, p: 2, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 2 }}>
          {messages.map((m, i) => (
            <Box key={i} sx={{ alignSelf: m.sender === 'ai' ? 'flex-start' : 'flex-end', bgcolor: m.sender === 'ai' ? 'rgba(255,255,255,0.05)' : 'primary.main', p: 2, borderRadius: 2, maxWidth: '80%' }}>
              <Typography variant="body2">{m.text}</Typography>
            </Box>
          ))}
        </Box>
        <Box sx={{ p: 2, borderTop: '1px solid rgba(255,255,255,0.1)' }}>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>Quick Actions:</Typography>
          <Stack spacing={1}>
            <Button size="small" variant="outlined" startIcon={<RedirectIcon />} onClick={() => handleOption('Go to Portfolio')}>Go to Portfolio</Button>
            <Button size="small" variant="outlined" startIcon={<TicketIcon />} onClick={() => handleOption('Create Support Ticket')}>Create Support Ticket</Button>
            <Button size="small" variant="outlined" startIcon={<CertificateIcon />} onClick={() => handleOption('Generate Certificate')}>Generate Certificate</Button>
          </Stack>
          <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
            <TextField fullWidth size="small" placeholder="Ask Hafida anything..." />
            <IconButton color="primary"><SendIcon /></IconButton>
          </Box>
        </Box>
      </Drawer>
    </>
  );
}

// =============================================================================
// Common Shared Pages
// =============================================================================

function ProfilePage() {
  const [editing, setEditing] = useState(false);
  const [data, setData] = useState({
    name: 'John Doe',
    email: 'john.doe@example.com',
    phone: '+966 50 123 4567',
    nationalId: '1098765432',
    socialSecurity: 'SSN-998-22-1111',
    nafathId: 'NF-772-881'
  });

  const maskValue = (val: string) => {
    if (!val) return '';
    const visibleCount = 4;
    return '*'.repeat(val.length - visibleCount) + val.slice(-visibleCount);
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Personal Data & Identity</Typography>
      <Card sx={{ maxWidth: 600 }}>
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
            <Avatar sx={{ width: 80, height: 80, bgcolor: 'primary.main', fontSize: '2rem' }}>{data.name[0]}</Avatar>
            <Button 
              variant={editing ? "contained" : "outlined"} 
              startIcon={editing ? <SaveIcon /> : <EditIcon />}
              onClick={() => setEditing(!editing)}
            >
              {editing ? 'Save Profile' : 'Edit Data'}
            </Button>
          </Box>
          <Stack spacing={3}>
            <Typography variant="subtitle2" color="secondary" sx={{ fontWeight: 700, mb: -1 }}>Basic Information</Typography>
            <TextField fullWidth label="Full Name" value={data.name} disabled={!editing} onChange={(e) => setData({...data, name: e.target.value})} />
            <TextField fullWidth label="Email Address" value={data.email} disabled={!editing} onChange={(e) => setData({...data, email: e.target.value})} />
            <TextField fullWidth label="Phone Number" value={data.phone} disabled={!editing} onChange={(e) => setData({...data, phone: e.target.value})} />
            
            <Divider sx={{ my: 1 }} />
            <Typography variant="subtitle2" color="error" sx={{ fontWeight: 700, mb: -1 }}>Sensitive Identity Data (Read-only)</Typography>
            <TextField fullWidth label="National ID / Aadhar" value={editing ? maskValue(data.nationalId) : data.nationalId} disabled />
            <TextField fullWidth label="Social Security Number" value={editing ? maskValue(data.socialSecurity) : data.socialSecurity} disabled />
            <TextField fullWidth label="Nafath ID" value={editing ? maskValue(data.nafathId) : data.nafathId} disabled />
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}

function NotificationsPage() {
  const notifications = [
    { id: 1, title: 'Event Invitation', message: 'You are invited to the "Tech Innovation Summit" on Nov 15.', type: 'invite', date: '2 hours ago' },
    { id: 2, title: 'Contribution Received', message: 'Your October contribution of SAR 2,400 has been processed.', type: 'info', date: '1 day ago' },
    { id: 3, title: 'New Course Available', message: 'Unlock your potential with "Financial Mastery 101".', type: 'info', date: '2 days ago' },
  ];

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Notifications</Typography>
      <Stack spacing={2}>
        {notifications.map(notif => (
          <Card key={notif.id} sx={{ p: 2, borderLeft: '5px solid', borderLeftColor: notif.type === 'invite' ? 'secondary.main' : 'primary.main' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Box>
                <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{notif.title}</Typography>
                <Typography variant="body2">{notif.message}</Typography>
                <Typography variant="caption" color="text.secondary">{notif.date}</Typography>
              </Box>
              {notif.type === 'invite' && (
                <Stack direction="row" spacing={1}>
                  <Button size="small" variant="contained">RSVP</Button>
                  <Button size="small" variant="outlined">Decline</Button>
                </Stack>
              )}
            </Box>
          </Card>
        ))}
      </Stack>
    </Box>
  );
}

// =============================================================================
// Employer Pages
// =============================================================================

function PayrollPage() {
  const employees = [
    { id: '1', name: 'Muneeb Ahmed', idNum: '102928374', docs: ['ID', 'Contract', 'Insurance'], salary: '12,500', status: 'Active' },
    { id: '2', name: 'Sarah Malik', idNum: '109384726', docs: ['ID', 'Contract'], salary: '15,000', status: 'Incomplete' },
    { id: '3', name: 'Khalid Abdullah', idNum: '102948576', docs: ['ID'], salary: '10,200', status: 'Active' },
    { id: '4', name: 'Fatima Al-Harbi', idNum: '102948577', docs: ['ID', 'Contract'], salary: '18,400', status: 'Active' },
  ];

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>Payroll & Compliance</Typography>
        <Button variant="contained" startIcon={<AddIcon />}>Add Employee</Button>
      </Box>
      
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', bgcolor: 'rgba(255,255,255,0.02)' }}>
            <Typography variant="h4" sx={{ fontWeight: 800 }}>142</Typography>
            <Typography variant="caption" color="text.secondary">Total Employees</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', bgcolor: 'rgba(34,197,94,0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 800, color: 'success.main' }}>98%</Typography>
            <Typography variant="caption" color="text.secondary">Compliance Score</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', bgcolor: 'rgba(99,102,241,0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 800, color: 'primary.main' }}>SAR 1.2M</Typography>
            <Typography variant="caption" color="text.secondary">Monthly Payroll</Typography>
          </Card>
        </Grid>
      </Grid>

      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Employee Name</TableCell>
                <TableCell>National ID</TableCell>
                <TableCell>Salary (SAR)</TableCell>
                <TableCell>Mandatory Files</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {employees.map((emp) => (
                <TableRow key={emp.id}>
                  <TableCell sx={{ fontWeight: 600 }}>{emp.name}</TableCell>
                  <TableCell>{emp.idNum}</TableCell>
                  <TableCell>{emp.salary}</TableCell>
                  <TableCell>
                    <Stack direction="row" spacing={0.5}>
                      {['ID', 'Contract', 'Insurance'].map(doc => (
                        <Chip 
                          key={doc}
                          size="small" 
                          label={doc} 
                          color={emp.docs.includes(doc) ? 'success' : 'default'}
                          variant={emp.docs.includes(doc) ? 'filled' : 'outlined'}
                        />
                      ))}
                    </Stack>
                  </TableCell>
                  <TableCell><Chip label={emp.status} size="small" color={emp.status === 'Active' ? 'success' : 'warning'} /></TableCell>
                  <TableCell align="right">
                    <Button size="small">Manage</Button>
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


function EventProposalPage() {
  const [eventType, setEventType] = useState('PHYSICAL');
  const customizedEvents = [
    { title: 'Ramadan Charity Iftar', category: 'RAMADAN', icon: '🌙' },
    { title: 'Tech Innovation Summit', category: 'FINANCIAL', icon: '💻' },
    { title: 'Health & Wellness Run', category: 'WELLNESS', icon: '🏃' },
  ];

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Propose Corporate Event</Typography>
      
      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1.5fr 1fr' }, gap: 4 }}>
        <Card sx={{ p: 3 }}>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Event Details</Typography>
          <Stack spacing={3}>
            <TextField fullWidth label="Event Title" placeholder="e.g. Annual Success Meetup" />
            <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
              <FormControl fullWidth>
                <InputLabel>Type</InputLabel>
                <Select value={eventType} label="Type" onChange={(e) => setEventType(e.target.value)}>
                  <MenuItem value="PHYSICAL">Physical Location</MenuItem>
                  <MenuItem value="DIGITAL">Digital / Metaverse</MenuItem>
                </Select>
              </FormControl>
              <TextField fullWidth label="Max Capacity" type="number" />
            </Box>
            <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
              <TextField fullWidth label="Date & Time" type="datetime-local" slotProps={{ inputLabel: { shrink: true } }} />
              <TextField fullWidth label="Location / URL" placeholder={eventType === 'PHYSICAL' ? "Riyadh, Blvd City" : "Zoom/Teams Link"} />
            </Box>
            <TextField fullWidth label="Map Coordinates / Google Maps Link" placeholder="24.7136° N, 46.6753° E" />
            <TextField fullWidth label="Speakers" placeholder="Comma separated names" />
            <TextField fullWidth multiline rows={3} label="Features & Agenda" placeholder="e.g. WiFi, Catering, VIP Lounge" />
            <Button variant="contained" size="large">Submit for Approval</Button>
          </Stack>
        </Card>

        <Box>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>Customized Templates</Typography>
          <Stack spacing={2}>
            {customizedEvents.map(ev => (
              <Card key={ev.title} sx={{ cursor: 'pointer', '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Typography variant="h4">{ev.icon}</Typography>
                  <Box>
                    <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{ev.title}</Typography>
                    <Typography variant="caption" color="text.secondary">{ev.category} Template</Typography>
                  </Box>
                </CardContent>
              </Card>
            ))}
          </Stack>
          
          <Box sx={{ mt: 4, p: 3, bgcolor: 'background.paper', borderRadius: 2 }}>
            <Typography variant="subtitle2" color="secondary" sx={{ fontWeight: 700, mb: 1 }}>L1/L2 Approval Pipeline</Typography>
            <Typography variant="body2" color="text.secondary">All events require 3-layer director approval before becoming live on the portal.</Typography>
          </Box>
        </Box>
      </Box>
    </Box>
  );
}

// =============================================================================
// Admin Pages
// =============================================================================

function AdminClaimsPage() {
  const claims = [
    { id: 'CLM-001', user: 'Muneeb Ahmed', type: 'Personal Loan', status: 'Pending L2', sla: '2h 15m left', color: 'success' },
    { id: 'CLM-002', user: 'Sarah Malik', type: 'Marriage Loan', status: 'SLA Breach', sla: '-45m overdue', color: 'error' },
    { id: 'CLM-003', user: 'John Doe', type: 'Personal Loan', status: 'Awaiting L1', sla: '3h 40m left', color: 'warning' },
    { id: 'CLM-004', user: 'Fatima Al-Saud', type: 'Disbursement', status: 'Processing', sla: '15m left', color: 'info' },
  ];

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Admin Claims Queue</Typography>
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, bgcolor: 'rgba(255,255,255,0.02)' }}>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>Total Queue</Typography>
            <Typography variant="h3" sx={{ fontWeight: 800 }}>84</Typography>
            <Typography variant="caption" color="text.secondary">Across all microservices</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, bgcolor: 'rgba(239,68,68,0.05)', border: '1px solid rgba(239,68,68,0.2)' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'error.main' }}>SLA Breaches</Typography>
            <Typography variant="h3" sx={{ fontWeight: 800, color: 'error.main' }}>12</Typography>
            <Typography variant="caption">Requires immediate attention</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, bgcolor: 'rgba(34,197,94,0.05)', border: '1px solid rgba(34,197,94,0.2)' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'success.main' }}>Processed Today</Typography>
            <Typography variant="h3" sx={{ fontWeight: 800, color: 'success.main' }}>142</Typography>
            <Typography variant="caption">98.2% accuracy rate</Typography>
          </Card>
        </Grid>
      </Grid>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Claim ID</TableCell>
              <TableCell>User</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>SLA Progress</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Action</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {claims.map(cl => (
              <TableRow key={cl.id}>
                <TableCell sx={{ fontWeight: 700 }}>{cl.id}</TableCell>
                <TableCell>{cl.user}</TableCell>
                <TableCell>{cl.type}</TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <LinearProgress variant="determinate" value={cl.color === 'success' ? 60 : 95} color={cl.color as any} sx={{ width: 100 }} />
                    <Typography variant="caption">{cl.sla}</Typography>
                  </Box>
                </TableCell>
                <TableCell><Chip label={cl.status} color={cl.color as any} size="small" /></TableCell>
                <TableCell align="right">
                  <Button size="small" variant="contained">Review</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}

function AdminSLAPage() {
  const claims = [
    { id: 'CLM-001', user: 'Muneeb Ahmed', type: 'Personal Loan', sla: '2.5h / 4.0h', status: 'On Track', color: 'success' },
    { id: 'CLM-002', user: 'Sarah Malik', type: 'Emergency Relief', sla: '3.8h / 4.0h', status: 'Critical', color: 'error' },
  ];

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>SLA Monitoring & Forecasting</Typography>
      
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Workload Forecast (Next 7 Days)</Typography>
            <Box sx={{ height: 200, display: 'flex', alignItems: 'flex-end', gap: 2, p: 2 }}>
              {[40, 65, 30, 85, 45, 90, 70].map((h, i) => (
                <Box key={i} sx={{ flex: 1, bgcolor: 'primary.main', height: `${h}%`, borderRadius: '4px 4px 0 0', opacity: 0.8 }} />
              ))}
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
              {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map(d => <Typography key={d} variant="caption">{d}</Typography>)}
            </Box>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, bgcolor: 'rgba(239,68,68,0.05)', border: '1px solid rgba(239,68,68,0.2)' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'error.main' }}>SLA Alerts</Typography>
            <Stack spacing={2} sx={{ mt: 2 }}>
              <Alert severity="error">12 Claims exceeding 4h limit</Alert>
              <Alert severity="warning">System load at 85% capacity</Alert>
            </Stack>
          </Card>
        </Grid>
      </Grid>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Claim ID</TableCell>
              <TableCell>User</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>SLA Progress</TableCell>
              <TableCell>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {claims.map(cl => (
              <TableRow key={cl.id}>
                <TableCell sx={{ fontWeight: 700 }}>{cl.id}</TableCell>
                <TableCell>{cl.user}</TableCell>
                <TableCell>{cl.type}</TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <LinearProgress variant="determinate" value={cl.color === 'success' ? 60 : 95} color={cl.color as any} sx={{ width: 100 }} />
                    <Typography variant="caption">{cl.sla}</Typography>
                  </Box>
                </TableCell>
                <TableCell><Chip label={cl.status} color={cl.color as any} size="small" /></TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}

function AdminEventsPage() {
  const events = [
    { id: 'EVT-101', company: 'Saudi Aramco', title: 'Tech Innovation Summit', type: 'DIGITAL', status: 'Pending L1 Review' },
    { id: 'EVT-102', company: 'SABIC', title: 'Health & Wellness Run', type: 'PHYSICAL', status: 'Approved' },
    { id: 'EVT-103', company: 'STC', title: 'Cybersecurity Workshop', type: 'DIGITAL', status: 'Awaiting L3' },
    { id: 'EVT-104', company: 'NEOM', title: 'Sustainable Future Forum', type: 'PHYSICAL', status: 'Pending L2' },
  ];

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 4 }}>Event Governance Queue</Typography>
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Event ID</TableCell>
                <TableCell>Company</TableCell>
                <TableCell>Title</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Governance Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {events.map(ev => (
                <TableRow key={ev.id}>
                  <TableCell sx={{ fontWeight: 700 }}>{ev.id}</TableCell>
                  <TableCell>{ev.company}</TableCell>
                  <TableCell>{ev.title}</TableCell>
                  <TableCell><Chip label={ev.type} size="small" variant="outlined" /></TableCell>
                  <TableCell>
                    <Chip label={ev.status} color={ev.status === 'Approved' ? 'success' : 'warning'} size="small" />
                  </TableCell>
                  <TableCell align="right">
                    <Stack direction="row" spacing={1} sx={{ justifyContent: 'flex-end' }}>
                      <Button variant="contained" size="small" disabled={ev.status === 'Approved'}>Review</Button>
                      <Button variant="outlined" size="small">Log</Button>
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

// =============================================================================
// Main App Component & Routes
// =============================================================================

function LoginPage() {
  const [step, setStep] = useState(1);
  const [mfaType, setMfaType] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => { 
    e.preventDefault(); 
    setLoading(true);
    setError(null);
    try {
      // In a real test, use the demo buttons for speed, but this shows integration
      await api.login(credentials.email, credentials.password);
      setStep(2);
    } catch (err: any) {
      setError(err.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleMfaSelect = (type: string) => { setMfaType(type); setStep(3); };

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center', 
      background: 'radial-gradient(circle at top right, #1e1b4b 0%, #030712 100%)',
      p: 4,
      position: 'relative',
      overflow: 'hidden'
    }}>
      <Box sx={{ position: 'absolute', top: -100, left: -100, width: 400, height: 400, background: 'rgba(139, 92, 246, 0.05)', borderRadius: '50%', filter: 'blur(100px)' }} />
      <Box sx={{ position: 'absolute', bottom: -100, right: -100, width: 400, height: 400, background: 'rgba(6, 182, 212, 0.05)', borderRadius: '50%', filter: 'blur(100px)' }} />
      
      <Card sx={{ 
        maxWidth: 450, 
        width: '100%', 
        p: 3, 
        borderRadius: 8, 
        bgcolor: alpha('#111827', 0.4),
        backdropFilter: 'blur(20px)',
        border: '1px solid rgba(255, 255, 255, 0.1)',
        boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)'
      }}>
        <CardContent>
          <Box sx={{ textAlign: 'center', mb: 5 }}>
            <Typography variant="h3" sx={{ fontWeight: 900, color: 'primary.main', letterSpacing: -3, mb: 1 }}>NSIP</Typography>
            <Typography variant="subtitle1" sx={{ color: 'text.secondary', fontWeight: 500 }}>National Social Insurance Platform</Typography>
          </Box>
          
          {error && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{error}</Alert>}
          
          {step === 1 && (
            <form onSubmit={handleLogin}>
              <Stack spacing={2.5}>
                <TextField 
                  fullWidth label="Email / Username" variant="outlined" required 
                  slotProps={{ input: { sx: { borderRadius: 3 } } }}
                  value={credentials.email} onChange={(e) => setCredentials({...credentials, email: e.target.value})}
                />
                <TextField 
                  fullWidth label="Password" type="password" variant="outlined" required 
                  slotProps={{ input: { sx: { borderRadius: 3 } } }}
                  value={credentials.password} onChange={(e) => setCredentials({...credentials, password: e.target.value})}
                />
                <Button fullWidth variant="contained" type="submit" size="large" disabled={loading} sx={{ py: 2, borderRadius: 3, fontSize: '1.1rem' }}>
                  {loading ? 'Authenticating...' : 'Sign In'}
                </Button>
                
                <Divider sx={{ my: 2 }}><Typography variant="caption" color="text.secondary">QUICK DEMO ACCESS</Typography></Divider>
                
                <Stack direction="row" spacing={2}>
                  <Button fullWidth variant="outlined" onClick={() => navigate('/employer/payroll')} sx={{ borderRadius: 3 }}>Employer</Button>
                  <Button fullWidth variant="outlined" onClick={() => navigate('/admin/claims')} sx={{ borderRadius: 3 }}>Admin</Button>
                </Stack>
              </Stack>
            </form>
          )}
          {step === 2 && (
            <Box>
              <Typography variant="h6" sx={{ fontWeight: 800, mb: 3, textAlign: 'center' }}>Identity Verification</Typography>
              <Stack spacing={2}>
                <Button fullWidth variant="outlined" startIcon={<NafathIcon />} onClick={() => handleMfaSelect('Nafath')} sx={{ py: 2, borderRadius: 3, borderWidth: 2 }}>Sign in with Nafath</Button>
                <Button fullWidth variant="outlined" startIcon={<BioIcon />} onClick={() => handleMfaSelect('Biometric')} sx={{ py: 2, borderRadius: 3, borderWidth: 2 }}>Face ID / Touch ID</Button>
                <Button fullWidth variant="outlined" startIcon={<OtpIcon />} onClick={() => handleMfaSelect('OTP')} sx={{ py: 2, borderRadius: 3, borderWidth: 2 }}>SMS Verification</Button>
              </Stack>
            </Box>
          )}
          {step === 3 && (
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h6" sx={{ fontWeight: 800, mb: 3 }}>{mfaType} Verification</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>A verification code has been sent to your registered device.</Typography>
              <TextField fullWidth placeholder="0 0 0 0 0 0" slotProps={{ input: { sx: { textAlign: 'center', letterSpacing: 10, fontSize: '1.5rem', fontWeight: 800, borderRadius: 3 } } }} sx={{ mb: 4 }} />
              <Button fullWidth variant="contained" size="large" onClick={() => navigate('/customer/portfolio')} sx={{ py: 2, borderRadius: 3 }}>Verify & Enter</Button>
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LoginPage />} />
          <Route path="/customer/*" element={<Layout role="customer"><Routes>
            <Route path="portfolio" element={<PortfolioPage />} />
            <Route path="learning" element={<LearningPage />} />
            <Route path="wellness" element={<WellnessPage />} />
            <Route path="wallet" element={<WalletPage />} />
            <Route path="planning" element={<PlanningPage />} />
            <Route path="help" element={<HelpPage />} />
            <Route path="profile" element={<ProfilePage />} />
            <Route path="notifications" element={<NotificationsPage />} />
            <Route path="*" element={<Navigate to="portfolio" />} />
          </Routes></Layout>} />
          <Route path="/employer/*" element={<Layout role="employer"><Routes>
            <Route path="payroll" element={<PayrollPage />} />
            <Route path="events" element={<EventProposalPage />} />
            <Route path="profile" element={<ProfilePage />} />
            <Route path="notifications" element={<NotificationsPage />} />
            <Route path="*" element={<Navigate to="payroll" />} />
          </Routes></Layout>} />
          <Route path="/admin/*" element={<Layout role="admin"><Routes>
            <Route path="claims" element={<AdminClaimsPage />} />
            <Route path="sla" element={<AdminSLAPage />} />
            <Route path="events" element={<AdminEventsPage />} />
            <Route path="profile" element={<ProfilePage />} />
            <Route path="notifications" element={<NotificationsPage />} />
            <Route path="*" element={<Navigate to="claims" />} />
          </Routes></Layout>} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
        <Hafida />
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
