import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../widgets/primary_card.dart';
class LearningScreen extends StatelessWidget {
  const LearningScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const Text('Learning Center', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w700)),
          const SizedBox(height: 8),
          const Text('Watch, learn, and earn reward points.', style: TextStyle(color: Colors.grey)),
          const SizedBox(height: 24),
          _courseCard('How to Save Wisely', 'Save', 'Learn budgeting and emergency fund basics.', 50, AppTheme.success),
          _courseCard('Investing for Beginners', 'Invest', 'Mutual funds, stocks, and compound interest.', 80, AppTheme.primary),
          _courseCard('Multiply Your Wealth', 'Multiply', 'Advanced portfolio diversification strategies.', 100, AppTheme.secondary),
        ],
      ),
    );
  }

  static Widget _courseCard(String title, String category, String desc, int points, Color accent) {
    return PrimaryCard(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(children: [
            Container(padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4), decoration: BoxDecoration(color: accent.withOpacity(0.15), borderRadius: BorderRadius.circular(8)), child: Text(category, style: TextStyle(color: accent, fontSize: 12, fontWeight: FontWeight.w600))),
            const Spacer(),
            Icon(Icons.stars, color: Colors.amber.shade600, size: 18),
            const SizedBox(width: 4),
            Text('$points pts', style: TextStyle(color: Colors.amber.shade600, fontWeight: FontWeight.w600)),
          ]),
          const SizedBox(height: 12),
          Text(title, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 4),
          Text(desc, style: const TextStyle(color: Colors.grey, fontSize: 14)),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(backgroundColor: accent, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)), padding: const EdgeInsets.symmetric(vertical: 14)),
              onPressed: () {},
              child: const Text('Watch & Take Quiz', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
            ),
          ),
        ],
      ),
    );
  }
}
