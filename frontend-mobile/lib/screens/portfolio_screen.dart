import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../widgets/primary_card.dart';
import '../services/api_service.dart';

class PortfolioScreen extends StatefulWidget {
  const PortfolioScreen({super.key});

  @override
  State<PortfolioScreen> createState() => _PortfolioScreenState();
}

class _PortfolioScreenState extends State<PortfolioScreen> {
  bool _isLoading = true;
  String _balance = '150,000';
  List<Map<String, dynamic>> _transactions = [];

  @override
  void initState() {
    super.initState();
    _fetchData();
  }

  Future<void> _fetchData() async {
    try {
      final balanceData = await ApiService.getPensionBalance('user123');
      final txData = await ApiService.getTransactions('user123');
      
      setState(() {
        _balance = balanceData['total_balance']?.toString() ?? '150,000';
        _transactions = List<Map<String, dynamic>>.from(txData);
        _isLoading = false;
      });
    } catch (e) {
      // Fallback to static data if API is not reachable
      setState(() {
        _balance = '150,000';
        _transactions = [
          {'month': 'April 2026', 'employer': 'TechCorp LLC', 'amount': 'SAR 480', 'paid': true},
          {'month': 'March 2026', 'employer': 'TechCorp LLC', 'amount': 'SAR 480', 'paid': true},
          {'month': 'February 2026', 'employer': 'PreviousCo Inc.', 'amount': 'SAR 400', 'paid': true},
        ];
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Center(child: CircularProgressIndicator(color: AppTheme.primary));
    }

    return SafeArea(
      child: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          // Dynamic Overhaul Theme Selector (Slate / Shariah Gold / Neon Cyber)
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'NSIP Wealth',
                style: TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.w900,
                  letterSpacing: -0.5,
                ),
              ),
              Row(
                children: [
                  _themeIndicator(ThemeType.midnightSlate, 'Slate', const Color(0xFF3B82F6)),
                  const SizedBox(width: 6),
                  _themeIndicator(ThemeType.emeraldShariah, 'Shariah', const Color(0xFFD97706)),
                  const SizedBox(width: 6),
                  _themeIndicator(ThemeType.neonCyberpunk, 'Neon', const Color(0xFFEC4899)),
                ],
              ),
            ],
          ),
          const SizedBox(height: 24),
          const Text('Total Portfolio Balance', style: TextStyle(color: Colors.grey, fontSize: 14, fontWeight: FontWeight.w500)),
          const SizedBox(height: 8),
          Row(
            crossAxisAlignment: CrossAxisAlignment.baseline,
            textBaseline: TextBaseline.alphabetic,
            children: [
              Text('SAR $_balance', style: const TextStyle(fontSize: 36, fontWeight: FontWeight.w700, color: Colors.white)),
              Text('.00', style: TextStyle(fontSize: 20, color: Colors.grey.shade600)),
            ],
          ),
          const SizedBox(height: 12),
          _badge('Vested (+3 Years)', Colors.green),
          const SizedBox(height: 32),

          Row(
            children: [
              Expanded(child: _creditCard('Personal (30%)', 'SAR 45,000', AppTheme.primary, 'Apply')),
              const SizedBox(width: 12),
              Expanded(child: _creditCard('Emergency (70%)', 'SAR 105,000', AppTheme.error, 'Request')),
            ],
          ),
          const SizedBox(height: 32),

          const Text('Recent Contributions', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 16),
          ..._transactions.map((tx) => _contributionTile(
            tx['month'] ?? 'Unknown',
            tx['employer'] ?? 'Unknown',
            tx['amount']?.toString() ?? 'SAR 0',
            tx['paid'] ?? true,
          )),
          const SizedBox(height: 24),

          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              gradient: LinearGradient(colors: [AppTheme.secondary, AppTheme.primary]),
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

          const Text('Payment Methods', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
          const SizedBox(height: 16),
          _gatewayTile('Stripe', 'Visa/Mastercard', Icons.credit_card, AppTheme.secondary),
          _gatewayTile('Local Wallet', 'PhonePe / STC Pay', Icons.account_balance_wallet, AppTheme.success),
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
    return PrimaryCard(
      padding: const EdgeInsets.all(16),
      margin: EdgeInsets.zero,
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
    return PrimaryCard(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
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
    return PrimaryCard(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(20),
      child: Row(children: [
        Icon(icon, size: 28, color: color),
        const SizedBox(width: 16),
        Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(title, style: const TextStyle(fontWeight: FontWeight.w600)), Text(sub, style: const TextStyle(color: Colors.grey, fontSize: 13))])),
        const Icon(Icons.chevron_right, color: Colors.grey),
      ]),
    );
  }

  Widget _themeIndicator(ThemeType type, String label, Color color) {
    final isSelected = AppTheme.currentThemeType == type;
    return GestureDetector(
      onTap: () {
        setState(() {
          AppTheme.switchTheme(type);
        });
      },
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
        decoration: BoxDecoration(
          color: isSelected ? color.withOpacity(0.15) : Colors.white.withOpacity(0.04),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isSelected ? color : Colors.white.withOpacity(0.08),
            width: 1.5,
          ),
        ),
        child: Text(
          label,
          style: TextStyle(
            color: isSelected ? color : Colors.white60,
            fontSize: 11,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }
}
