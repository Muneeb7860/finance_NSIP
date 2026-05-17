import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class HelpCentreScreen extends StatefulWidget {
  const HelpCentreScreen({super.key});
  @override
  State<HelpCentreScreen> createState() => _HelpCentreScreenState();
}

class _HelpCentreScreenState extends State<HelpCentreScreen> {
  final Map<String, bool> _channels = {
    'WhatsApp Business': true,
    'Email Notification': true,
    'SMS Alerts': false,
    'X (Twitter) DM': false,
    'Instagram / Snapchat': false,
  };

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const Text('Help Centre', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w700)),
          const SizedBox(height: 8),
          const Text('Manage your notification preferences.', style: TextStyle(color: Colors.grey)),
          const SizedBox(height: 24),

          ..._channels.entries.map((entry) => Container(
            margin: const EdgeInsets.only(bottom: 1),
            padding: const EdgeInsets.symmetric(vertical: 8),
            decoration: BoxDecoration(border: Border(bottom: BorderSide(color: AppTheme.border, width: 0.5))),
            child: SwitchListTile(
              title: Text(entry.key, style: const TextStyle(fontWeight: FontWeight.w500)),
              value: entry.value,
              onChanged: (val) => setState(() => _channels[entry.key] = val),
              activeColor: AppTheme.primary,
            ),
          )),
        ],
      ),
    );
  }
}
