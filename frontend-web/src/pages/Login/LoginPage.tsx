import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Box, Card, CardContent, Typography, TextField, Button, 
  Alert, Stack, Divider, alpha 
} from '@mui/material';
import { 
  Security as NafathIcon, Fingerprint as BioIcon, 
  Sms as OtpIcon 
} from '@mui/icons-material';
import api from '../../services/api';

export default function LoginPage() {
  const [step, setStep] = useState(1);
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [mfaType, setMfaType] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
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
