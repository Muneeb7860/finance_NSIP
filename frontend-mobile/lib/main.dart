import 'package:flutter/material.dart';

void main() {
  runApp(const NSIPMobileApp());
}

class NSIPMobileApp extends StatelessWidget {
  const NSIPMobileApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'NSIP Wealth',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF0A0A0A),
        primaryColor: const Color(0xFF3B82F6),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF3B82F6),
          secondary: Color(0xFF6366F1),
          surface: Color(0xFF171717),
          error: Color(0xFFEF4444),
        ),
        fontFamily: 'Roboto',
        cardTheme: CardThemeData(
          color: const Color(0xFF171717),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
          elevation: 0,
        ),
      ),
      home: const MainNavigation(),
    );
  }
}

// =============================================================================
// Bottom Navigation Shell
// =============================================================================
class MainNavigation extends StatefulWidget {
  const MainNavigation({super.key});
  @override
  State<MainNavigation> createState() => _MainNavigationState();
}

class _MainNavigationState extends State<MainNavigation> {
  int _currentIndex = 0;
  final List<Widget> _pages = const [
    PortfolioScreen(),
    LearningScreen(),
    EventsScreen(),
    PlanningScreen(),
    HelpCentreScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],
      bottomNavigationBar: Container(
        decoration: const BoxDecoration(
          border: Border(top: BorderSide(color: Color(0xFF333333), width: 0.5)),
        ),
        child: BottomNavigationBar(
          currentIndex: _currentIndex,
          onTap: (index) => setState(() => _currentIndex = index),
          type: BottomNavigationBarType.fixed,
          backgroundColor: const Color(0xFF171717),
          selectedItemColor: const Color(0xFF3B82F6),
          unselectedItemColor: Colors.grey,
          selectedFontSize: 12,
          items: const [
            BottomNavigationBarItem(icon: Icon(Icons.pie_chart_rounded), label: 'Portfolio'),
            BottomNavigationBarItem(icon: Icon(Icons.school_rounded), label: 'Learning'),
            BottomNavigationBarItem(icon: Icon(Icons.event_rounded), label: 'Events'),
            BottomNavigationBarItem(icon: Icon(Icons.bar_chart_rounded), label: 'Planning'),
            BottomNavigationBarItem(icon: Icon(Icons.help_center_rounded), label: 'Help'),
          ],
        ),
      ),
    );
  }
}

// =============================================================================
// 1. Portfolio Screen (Loans, Payments, Contributions)
// =============================================================================
class PortfolioScreen extends StatelessWidget {
  const PortfolioScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const Text('Total Portfolio Balance', style: TextStyle(color: Colors.grey, fontSize: 14, fontWeight: FontWeight.w500)),
          const SizedBox(height: 8),
          Row(
            crossAxisAlignment: CrossAxisAlignment.baseline,
            textBaseline: TextBaseline.alphabetic,
            children: [
              const Text('SAR 150,000', style: TextStyle(fontSize: 36, fontWeight: FontWeight.w700, color: Colors.white)),
              Text('.00', style: TextStyle(fontSize: 20, color: Colors.grey.shade600)),
            ],
          ),
          const SizedBox(height: 12),
          _badge('Vested (+3 Years)', Colors.green),
          const SizedBox(height: 32),

          // Loan Credit Cards
          Row(
            children: [
              Expanded(child: _creditCard('Personal (30%)', 'SAR 45,000', const Color(0xFF3B82F6), 'Apply')),
              const SizedBox(width: 12),
              Expanded(child: _creditCard('Emergency (70%)', 'SAR 105,000', const Color(0xFFEF4444), 'Request')),
            ],
          ),
          const SizedBox(height: 32),

          // Contribution History
          const Text('Recent Contributions', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 16),
          _contributionTile('April 2026', 'TechCorp LLC', 'SAR 480', true),
          _contributionTile('March 2026', 'TechCorp LLC', 'SAR 480', true),
          _contributionTile('February 2026', 'PreviousCo Inc.', 'SAR 400', true),
          const SizedBox(height: 24),

          // Points Balance
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              gradient: const LinearGradient(colors: [Color(0xFF6366F1), Color(0xFF3B82F6)]),
              borderRadius: BorderRadius.circular(20),
            ),
            child: const Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('Reward Points', style: TextStyle(color: Colors.white70, fontSize: 14)),
                    SizedBox(height: 4),
                    Text('850 pts', style: TextStyle(color: Colors.white, fontSize: 28, fontWeight: FontWeight.bold)),
                  ],
                ),
                Icon(Icons.stars_rounded, color: Colors.white70, size: 40),
              ],
            ),
          ),
          const SizedBox(height: 24),

          // Payment Gateways
          const Text('Payment Methods', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 16),
          _gatewayTile('Stripe', 'Visa/Mastercard', Icons.credit_card, const Color(0xFF6366F1)),
          _gatewayTile('Local Wallet', 'PhonePe / STC Pay', Icons.account_balance_wallet, Colors.green),
        ],
      ),
    );
  }

  static Widget _badge(String text, Color color) {
    return Align(
      alignment: Alignment.centerLeft,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(color: color.withOpacity(0.1), borderRadius: BorderRadius.circular(100)),
        child: Text(text, style: TextStyle(color: color, fontWeight: FontWeight.w600, fontSize: 13)),
      ),
    );
  }

  static Widget _creditCard(String title, String value, Color accent, String btnText) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(color: const Color(0xFF171717), borderRadius: BorderRadius.circular(20), border: Border.all(color: const Color(0xFF333333))),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title, style: const TextStyle(color: Colors.grey, fontSize: 11, fontWeight: FontWeight.w600, letterSpacing: 0.5)),
          const SizedBox(height: 8),
          Text(value, style: TextStyle(color: accent, fontSize: 18, fontWeight: FontWeight.w700)),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(backgroundColor: accent, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)), padding: const EdgeInsets.symmetric(vertical: 12)),
              onPressed: () {},
              child: Text(btnText, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w600)),
            ),
          ),
        ],
      ),
    );
  }

  static Widget _contributionTile(String month, String employer, String amount, bool paid) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(color: const Color(0xFF171717), borderRadius: BorderRadius.circular(16), border: Border.all(color: const Color(0xFF333333))),
      child: Row(
        children: [
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(month, style: const TextStyle(fontWeight: FontWeight.w600)), Text(employer, style: const TextStyle(color: Colors.grey, fontSize: 13))])),
          Text(amount, style: const TextStyle(fontWeight: FontWeight.w600)),
          const SizedBox(width: 12),
          Icon(paid ? Icons.check_circle : Icons.pending, color: paid ? Colors.green : Colors.amber, size: 20),
        ],
      ),
    );
  }

  static Widget _gatewayTile(String title, String sub, IconData icon, Color color) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(color: const Color(0xFF171717), borderRadius: BorderRadius.circular(16), border: Border.all(color: const Color(0xFF333333))),
      child: Row(children: [
        Icon(icon, size: 28, color: color),
        const SizedBox(width: 16),
        Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(title, style: const TextStyle(fontWeight: FontWeight.w600)), Text(sub, style: const TextStyle(color: Colors.grey, fontSize: 13))])),
        const Icon(Icons.chevron_right, color: Colors.grey),
      ]),
    );
  }
}

// =============================================================================
// 2. Learning Screen (LMS + Gamification Courses)
// =============================================================================
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
          _courseCard('How to Save Wisely', 'Save', 'Learn budgeting and emergency fund basics.', 50, Colors.green),
          _courseCard('Investing for Beginners', 'Invest', 'Mutual funds, stocks, and compound interest.', 80, const Color(0xFF3B82F6)),
          _courseCard('Multiply Your Wealth', 'Multiply', 'Advanced portfolio diversification strategies.', 100, const Color(0xFF6366F1)),
        ],
      ),
    );
  }

  static Widget _courseCard(String title, String category, String desc, int points, Color accent) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(color: const Color(0xFF171717), borderRadius: BorderRadius.circular(20), border: Border.all(color: const Color(0xFF333333))),
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

// =============================================================================
// 3. Events Screen (Digital + Physical)
// =============================================================================
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
          _eventCard('Financial Literacy Webinar', 'DIGITAL', 'June 15, 2026 • 7:00 PM', 75, Icons.videocam_rounded, const Color(0xFF3B82F6)),
          _eventCard('Retirement Planning Workshop', 'PHYSICAL', 'June 22, 2026 • Riyadh Convention Center', 150, Icons.location_on_rounded, Colors.orange),
          _eventCard('Investment Masterclass', 'DIGITAL', 'July 3, 2026 • 8:00 PM', 100, Icons.videocam_rounded, const Color(0xFF6366F1)),
        ],
      ),
    );
  }

  static Widget _eventCard(String title, String type, String detail, int points, IconData icon, Color accent) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(color: const Color(0xFF171717), borderRadius: BorderRadius.circular(20), border: Border.all(color: const Color(0xFF333333))),
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

// =============================================================================
// 4. Planning Screen (Expense + Emergency Calculators)
// =============================================================================
class PlanningScreen extends StatefulWidget {
  const PlanningScreen({super.key});
  @override
  State<PlanningScreen> createState() => _PlanningScreenState();
}

class _PlanningScreenState extends State<PlanningScreen> {
  double _income = 0, _expenses = 0, _emergencyExpense = 0;

  @override
  Widget build(BuildContext context) {
    double savings = _income - _expenses;
    double emergencyGoal = _emergencyExpense * 6;

    return SafeArea(
      child: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const Text('Financial Planning', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w700)),
          const SizedBox(height: 8),
          const Text('Plan your budget and emergency fund.', style: TextStyle(color: Colors.grey)),
          const SizedBox(height: 24),

          // Expense Calculator
          _sectionTitle('Expense Calculator'),
          _inputField('Monthly Income (SAR)', (val) => setState(() => _income = double.tryParse(val) ?? 0)),
          _inputField('Monthly Expenses (SAR)', (val) => setState(() => _expenses = double.tryParse(val) ?? 0)),
          _resultCard('Remaining Savings', 'SAR ${savings.toStringAsFixed(0)}', savings >= 0 ? Colors.green : const Color(0xFFEF4444)),
          const SizedBox(height: 32),

          // Emergency Fund Calculator
          _sectionTitle('Emergency Fund Planner'),
          _inputField('Monthly Expenses (SAR)', (val) => setState(() => _emergencyExpense = double.tryParse(val) ?? 0)),
          _resultCard('6-Month Emergency Goal', 'SAR ${emergencyGoal.toStringAsFixed(0)}', const Color(0xFFEF4444)),

          // Personal Loan Calculator
          const SizedBox(height: 32),
          _sectionTitle('Personal Loan Eligibility'),
          _resultCard('Available (30% of SAR 150,000)', 'SAR 45,000', const Color(0xFF3B82F6)),
        ],
      ),
    );
  }

  Widget _sectionTitle(String text) => Padding(padding: const EdgeInsets.only(bottom: 16), child: Text(text, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w600)));

  Widget _inputField(String hint, Function(String) onChanged) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextField(
        onChanged: onChanged,
        keyboardType: TextInputType.number,
        style: const TextStyle(color: Colors.white),
        decoration: InputDecoration(
          hintText: hint,
          hintStyle: const TextStyle(color: Colors.grey),
          filled: true,
          fillColor: const Color(0xFF0A0A0A),
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFF333333))),
          enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFF333333))),
          focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFF3B82F6))),
        ),
      ),
    );
  }

  Widget _resultCard(String label, String value, Color color) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(color: color.withOpacity(0.08), borderRadius: BorderRadius.circular(16)),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(color: Colors.grey, fontSize: 14)),
          Text(value, style: TextStyle(color: color, fontSize: 22, fontWeight: FontWeight.w700)),
        ],
      ),
    );
  }
}

// =============================================================================
// 5. Help Centre Screen (Omnichannel Settings)
// =============================================================================
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
            decoration: const BoxDecoration(border: Border(bottom: BorderSide(color: Color(0xFF333333), width: 0.5))),
            child: SwitchListTile(
              title: Text(entry.key, style: const TextStyle(fontWeight: FontWeight.w500)),
              value: entry.value,
              onChanged: (val) => setState(() => _channels[entry.key] = val),
              activeColor: const Color(0xFF3B82F6),
            ),
          )),
        ],
      ),
    );
  }
}
