import { Box, Typography, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Grid, Button } from '@mui/material';

export function AdminClaimsPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Claim Approvals</Typography>
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Claim ID</TableCell>
                <TableCell>User</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              <TableRow>
                <TableCell>CLM-882</TableCell>
                <TableCell>Muneeb A.</TableCell>
                <TableCell>Medical</TableCell>
                <TableCell><Chip label="Pending" color="warning" size="small" /></TableCell>
                <TableCell><Button size="small">Review</Button></TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}

export function AdminEventsPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Event Approvals</Typography>
      <Card sx={{ p: 3 }}>
        <Typography variant="body2" color="text.secondary">No pending event proposals.</Typography>
      </Card>
    </Box>
  );
}

export function AdminSLAPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>SLA Monitoring</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center' }}>
            <Typography variant="h3" sx={{ fontWeight: 800, color: 'success.main' }}>99.9%</Typography>
            <Typography variant="subtitle2">System Uptime</Typography>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
