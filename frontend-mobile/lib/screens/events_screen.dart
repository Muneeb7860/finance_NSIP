import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../widgets/primary_card.dart';
class EventsScreen extends StatelessWidget {
  const EventsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const Text('Upcoming Events', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w700)),
          const SizedBox(height: 8),
          const Text('Attend events to earn points and certificates.', style: TextStyle(color: Colors.grey)),
          const SizedBox(height: 24),
          _eventCard('Financial Literacy Webinar', 'DIGITAL', 'June 15, 2026 • 7:00 PM', 75, Icons.videocam_rounded, AppTheme.primary),
          _eventCard('Retirement Planning Workshop', 'PHYSICAL', 'June 22, 2026 • Riyadh Convention Center', 150, Icons.location_on_rounded, Colors.orange),
          _eventCard('Investment Masterclass', 'DIGITAL', 'July 3, 2026 • 8:00 PM', 100, Icons.videocam_rounded, AppTheme.secondary),
        ],
      ),
    );
  }

  static Widget _eventCard(String title, String type, String detail, int points, IconData icon, Color accent) {
    return PrimaryCard(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(children: [
            Icon(icon, color: accent, size: 20),
            const SizedBox(width: 8),
            Container(padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4), decoration: BoxDecoration(color: accent.withOpacity(0.15), borderRadius: BorderRadius.circular(8)), child: Text(type, style: TextStyle(color: accent, fontSize: 11, fontWeight: FontWeight.w700, letterSpacing: 0.5))),
            const Spacer(),
            Text('+$points pts', style: TextStyle(color: Colors.amber.shade600, fontWeight: FontWeight.w600)),
          ]),
          const SizedBox(height: 12),
          Text(title, style: const TextStyle(fontSize: 17, fontWeight: FontWeight.w600)),
          const SizedBox(height: 4),
          Text(detail, style: const TextStyle(color: Colors.grey, fontSize: 13)),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: OutlinedButton(
              style: OutlinedButton.styleFrom(side: BorderSide(color: accent), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)), padding: const EdgeInsets.symmetric(vertical: 14)),
              onPressed: () {},
              child: Text('RSVP Now', style: TextStyle(color: accent, fontWeight: FontWeight.w600)),
            ),
          ),
        ],
      ),
    );
  }
}
