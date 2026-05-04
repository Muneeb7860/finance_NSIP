import { Box, Typography, Card, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, Stack, TextField } from '@mui/material';

export function PayrollPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Payroll & Compliance</Typography>
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Month</TableCell>
                <TableCell>Employees</TableCell>
                <TableCell>Total Contributions</TableCell>
                <TableCell>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              <TableRow>
                <TableCell>October 2024</TableCell>
                <TableCell>145</TableCell>
                <TableCell>SAR 450,000</TableCell>
                <TableCell><Chip label="Paid" color="success" size="small" /></TableCell>
              </TableRow>
              <TableRow>
                <TableCell>September 2024</TableCell>
                <TableCell>142</TableCell>
                <TableCell>SAR 442,000</TableCell>
                <TableCell><Chip label="Paid" color="success" size="small" /></TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}

export function EventProposalPage() {
  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 4 }}>Propose Event</Typography>
      <Card sx={{ p: 4, maxWidth: 600 }}>
        <Typography variant="h6" sx={{ mb: 2 }}>Event Details</Typography>
        <Stack spacing={3}>
          <TextField label="Event Name" fullWidth />
          <TextField label="Proposed Budget" fullWidth type="number" />
          <Button variant="contained">Submit Proposal</Button>
        </Stack>
      </Card>
    </Box>
  );
}

