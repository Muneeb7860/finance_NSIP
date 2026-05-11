import { useState } from 'react';
import { Box, Typography, Grid, Card, LinearProgress, Button, Chip, alpha, Paper, Stack, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material';
import { 
  PlayCircle as PlayIcon, 
  CheckCircle as DoneIcon, EmojiEvents as TrophyIcon 
} from '@mui/icons-material';

export default function LearningPage() {
  const [showQuiz, setShowQuiz] = useState(false);
  const [selectedCourse, setSelectedCourse] = useState<any>(null);

  const courses = [
    { id: '1', title: 'Social Insurance 101', progress: 100, category: 'Compliance', status: 'Completed', xp: 500 },
    { id: '2', title: 'Pension Optimization', progress: 45, category: 'Finance', status: 'In Progress', xp: 200 },
    { id: '3', title: 'Disability Coverage', progress: 10, category: 'Policy', status: 'In Progress', xp: 50 },
  ];

  const handleQuizSubmit = () => {
    alert(`Quiz for ${selectedCourse.title} submitted! You scored 95% and earned 150 points.`);
    setShowQuiz(false);
  };

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
        <Grid size={{ xs: 12, md: 8 }}>
          <Grid container spacing={3}>
            {courses.map((c, i) => (
              <Grid key={i} size={{ xs: 12, md: 6 }}>
                <Card sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column', border: '1px solid rgba(255,255,255,0.05)' }}>
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
                    <LinearProgress variant="determinate" value={c.progress} sx={{ height: 6, borderRadius: 3, bgcolor: 'rgba(255,255,255,0.05)' }} />
                  </Box>
                  <Stack direction="row" spacing={1}>
                    <Button fullWidth variant={c.progress === 100 ? "outlined" : "contained"} startIcon={c.progress === 100 ? <DoneIcon /> : <PlayIcon />}>
                      {c.progress === 100 ? "Review" : "Learn"}
                    </Button>
                    <Button 
                      fullWidth 
                      variant="contained" 
                      color="secondary"
                      onClick={() => { setSelectedCourse(c); setShowQuiz(true); }}
                    >
                      Quiz
                    </Button>
                  </Stack>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Stack spacing={3}>
            <Card sx={{ p: 3, bgcolor: 'rgba(255,255,255,0.02)' }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 3 }}>National Leaderboard</Typography>
              <Stack spacing={2}>
                {[
                  { name: 'Ahmad S.', xp: '4,250', rank: 1, color: '#f59e0b' },
                  { name: 'Sarah J. (You)', xp: '1,250', rank: 12, color: '#8b5cf6' },
                  { name: 'Khalid M.', xp: '950', rank: 45, color: 'text.secondary' },
                ].map((u, i) => (
                  <Box key={i} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', p: 1.5, borderRadius: 2, bgcolor: u.rank === 12 ? alpha('#8b5cf6', 0.1) : 'transparent' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      <Typography variant="body2" sx={{ fontWeight: 900, color: u.color }}>#{u.rank}</Typography>
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>{u.name}</Typography>
                    </Box>
                    <Typography variant="caption" sx={{ fontWeight: 800 }}>{u.xp} XP</Typography>
                  </Box>
                ))}
              </Stack>
            </Card>

            <Card sx={{ p: 3 }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 2 }}>Unlocked Badges</Typography>
              <Grid container spacing={1}>
                {[1, 2, 3, 4].map((b) => (
                  <Grid key={b} size={{ xs: 3 }}>
                    <Box sx={{ aspectRatio: '1/1', borderRadius: '50%', bgcolor: alpha('#10b981', 0.1), display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid rgba(16, 185, 129, 0.2)' }}>
                      <TrophyIcon sx={{ fontSize: '1.2rem', color: '#10b981' }} />
                    </Box>
                  </Grid>
                ))}
              </Grid>
            </Card>
          </Stack>
        </Grid>
      </Grid>

      {/* Quiz Dialog */}
      <Dialog open={showQuiz} onClose={() => setShowQuiz(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 800 }}>Quiz: {selectedCourse?.title}</DialogTitle>
        <DialogContent>
          <Typography variant="body1" sx={{ mb: 3, fontWeight: 600 }}>Question 1: What is the mandatory pension contribution rate for employees?</Typography>
          <Stack spacing={2}>
            <Button variant="outlined" sx={{ justifyContent: 'flex-start', textAlign: 'left' }}>A) 2%</Button>
            <Button variant="outlined" color="primary" sx={{ justifyContent: 'flex-start', textAlign: 'left', border: '2px solid' }}>B) 4% (Correct)</Button>
            <Button variant="outlined" sx={{ justifyContent: 'flex-start', textAlign: 'left' }}>C) 10%</Button>
          </Stack>
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button onClick={() => setShowQuiz(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleQuizSubmit}>Submit Quiz</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

