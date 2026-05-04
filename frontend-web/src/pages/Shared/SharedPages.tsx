import { Box, Typography, Card, Stack, Avatar, Button } from '@mui/material';

export function ProfilePage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>My Profile</Typography>
      <Card sx={{ p: 4, maxWidth: 600 }}>
        <Stack spacing={4} sx={{ alignItems: 'center' }}>
          <Avatar sx={{ width: 120, height: 120, bgcolor: 'primary.main', fontSize: '3rem' }}>SJ</Avatar>
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="h5" sx={{ fontWeight: 800 }}>Sarah Jenkins</Typography>
            <Typography color="text.secondary">Software Engineer • Riyadh, KSA</Typography>
          </Box>
          <Button variant="outlined">Edit Profile</Button>
        </Stack>
      </Card>
    </Box>
  );
}

export function NotificationsPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Notifications</Typography>
      <Stack spacing={2}>
        <Card sx={{ p: 3 }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>System Maintenance</Typography>
          <Typography variant="body2" color="text.secondary">Scheduled maintenance on Oct 25, 2024.</Typography>
        </Card>
      </Stack>
    </Box>
  );
}

export function HelpPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Help & Support</Typography>
      <Typography variant="body1">Contact us at support@nsip.gov.sa</Typography>
    </Box>
  );
}
