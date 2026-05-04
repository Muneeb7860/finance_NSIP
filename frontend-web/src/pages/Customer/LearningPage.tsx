import { Box, Typography, Grid, Card, LinearProgress, Button, Chip, alpha, Paper } from '@mui/material';
import { 
  PlayCircle as PlayIcon, 
  CheckCircle as DoneIcon, EmojiEvents as TrophyIcon 
} from '@mui/icons-material';

export default function LearningPage() {
  const courses = [
    { title: 'Social Insurance 101', progress: 100, category: 'Compliance', status: 'Completed', xp: 500 },
    { title: 'Pension Optimization', progress: 45, category: 'Finance', status: 'In Progress', xp: 200 },
    { title: 'Disability Coverage', progress: 10, category: 'Policy', status: 'In Progress', xp: 50 },
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>Learning Management</Typography>
          <Typography variant="subtitle1" color="text.secondary">Master your benefits and earn XP rewards</Typography>
        </Box>
        <Paper sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 2, bgcolor: alpha('#f59e0b', 0.1), border: '1px solid #f59e0b' }}>
          <TrophyIcon sx={{ color: '#f59e0b' }} />
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 800, lineHeight: 1 }}>1,250 XP</Typography>
            <Typography variant="caption">Level 4 Advisor</Typography>
          </Box>
        </Paper>
      </Box>

      <Grid container spacing={3}>
        {courses.map((c, i) => (
          <Grid key={i} size={{ xs: 12, md: 4 }}>
            <Card sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column' }}>
              <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between' }}>
                <Chip label={c.category} size="small" variant="outlined" />
                <Typography variant="caption" color="text.secondary">{c.status}</Typography>
              </Box>
              <Typography variant="h6" sx={{ fontWeight: 800, mb: 2 }}>{c.title}</Typography>
              <Box sx={{ flexGrow: 1, mb: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="caption">Progress</Typography>
                  <Typography variant="caption" sx={{ fontWeight: 800 }}>{c.progress}%</Typography>
                </Box>
                <LinearProgress variant="determinate" value={c.progress} sx={{ height: 6, borderRadius: 3 }} />
              </Box>
              <Button fullWidth variant={c.progress === 100 ? "outlined" : "contained"} startIcon={c.progress === 100 ? <DoneIcon /> : <PlayIcon />}>
                {c.progress === 100 ? "Review Course" : "Continue Learning"}
              </Button>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

