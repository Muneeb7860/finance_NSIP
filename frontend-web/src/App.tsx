import { ThemeProvider, CssBaseline } from '@mui/material';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import theme from './theme';

// Layout
import Layout from './components/Layout';

// Pages
import LoginPage from './pages/Login/LoginPage';
import PortfolioPage from './pages/Customer/PortfolioPage';
import LearningPage from './pages/Customer/LearningPage';
import AdvisorsPage from './pages/Customer/AdvisorsPage';
import WalletPage from './pages/Customer/WalletPage';
import { WellnessPage, PlanningPage } from './pages/Customer/OtherPages';
import EventsPage from './pages/Customer/EventsPage';
import { PayrollPage, EventProposalPage } from './pages/Employer/EmployerPages';
import { AdminClaimsPage, AdminEventsPage, AdminSLAPage } from './pages/Admin/AdminPages';
import { ProfilePage, NotificationsPage, HelpPage } from './pages/Shared/SharedPages';

import MarketplacePage from './pages/MarketplacePage';
import EmployerAnalyticsPage from './pages/Employer/EmployerAnalyticsPage';
import HafidaAssistant from './components/HafidaAssistant';

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LoginPage />} />
          
          {/* Customer Routes */}
          <Route path="/customer/*" element={
            <Layout role="customer">
              <Routes>
                <Route path="portfolio" element={<PortfolioPage />} />
                <Route path="marketplace" element={<MarketplacePage />} />
                <Route path="learning" element={<LearningPage />} />
                <Route path="advisors" element={<AdvisorsPage />} />
                <Route path="wellness" element={<WellnessPage />} />
                <Route path="wallet" element={<WalletPage />} />
                <Route path="planning" element={<PlanningPage />} />
                <Route path="events" element={<EventsPage />} />
                <Route path="help" element={<HelpPage />} />
                <Route path="profile" element={<ProfilePage />} />
                <Route path="notifications" element={<NotificationsPage />} />
                <Route path="*" element={<Navigate to="portfolio" />} />
              </Routes>
            </Layout>
          } />

          {/* Employer Routes */}
          <Route path="/employer/*" element={
            <Layout role="employer">
              <Routes>
                <Route path="payroll" element={<PayrollPage />} />
                <Route path="analytics" element={<EmployerAnalyticsPage />} />
                <Route path="events" element={<EventProposalPage />} />
                <Route path="profile" element={<ProfilePage />} />
                <Route path="notifications" element={<NotificationsPage />} />
                <Route path="*" element={<Navigate to="payroll" />} />
              </Routes>
            </Layout>
          } />

          {/* Admin Routes */}
          <Route path="/admin/*" element={
            <Layout role="admin">
              <Routes>
                <Route path="claims" element={<AdminClaimsPage />} />
                <Route path="sla" element={<AdminSLAPage />} />
                <Route path="events" element={<AdminEventsPage />} />
                <Route path="profile" element={<ProfilePage />} />
                <Route path="notifications" element={<NotificationsPage />} />
                <Route path="*" element={<Navigate to="claims" />} />
              </Routes>
            </Layout>
          } />

          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
        <HafidaAssistant />
      </BrowserRouter>
    </ThemeProvider>
  );
}
