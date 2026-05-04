import { Box, Typography, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Grid, Button, Stack, Paper } from '@mui/material';

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
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Security & Governance</Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', border: '1px solid #10b981', bgcolor: 'rgba(16, 185, 129, 0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: 'success.main' }}>ENFORCED</Typography>
            <Typography variant="subtitle2" sx={{ mt: 1 }}>Account Isolation</Typography>
            <Typography variant="caption" color="text.secondary">Per-service DB credentials active</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', border: '1px solid #3b82f6', bgcolor: 'rgba(59, 130, 246, 0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: 'primary.main' }}>DAILY</Typography>
            <Typography variant="subtitle2" sx={{ mt: 1 }}>Immutable Backups</Typography>
            <Typography variant="caption" color="text.secondary">Next sync: 00:00 UTC</Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <Card sx={{ p: 3, textAlign: 'center', border: '1px solid #f59e0b', bgcolor: 'rgba(245, 158, 11, 0.05)' }}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: 'warning.main' }}>ACTIVE</Typography>
            <Typography variant="subtitle2" sx={{ mt: 1 }}>Approval Workflows</Typography>
            <Typography variant="caption" color="text.secondary">Multi-sig for data purges</Typography>
          </Card>
        </Grid>

        <Grid size={{ xs: 12 }}>
          <Card sx={{ p: 0 }}>
            <Box sx={{ p: 3, bgcolor: 'rgba(255,255,255,0.02)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>Pending Approval Queue</Typography>
              <Chip label="2 Actions Required" color="warning" />
            </Box>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 700 }}>Operation</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Requested By</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Risk Level</TableCell>
                    <TableCell sx={{ fontWeight: 700, textAlign: 'right' }}>Action</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  <TableRow>
                    <TableCell>
                      <Typography variant="body2" sx={{ fontWeight: 700 }}>Manual DB Schema Update</Typography>
                      <Typography variant="caption" color="text.secondary">Target: nsip_auth</Typography>
                    </TableCell>
                    <TableCell>Admin_User_99</TableCell>
                    <TableCell><Chip label="High" color="error" size="small" /></TableCell>
                    <TableCell sx={{ textAlign: 'right' }}>
                      <Stack direction="row" spacing={1} justifyContent="flex-end">
                        <Button size="small" variant="contained" color="success">Approve</Button>
                        <Button size="small" variant="outlined" color="error">Reject</Button>
                      </Stack>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
