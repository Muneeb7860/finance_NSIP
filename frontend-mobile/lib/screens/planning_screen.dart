import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
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
          _resultCard('Remaining Savings', 'SAR ${savings.toStringAsFixed(0)}', savings >= 0 ? AppTheme.success : AppTheme.error),
          const SizedBox(height: 32),

          // Emergency Fund Calculator
          _sectionTitle('Emergency Fund Planner'),
          _inputField('Monthly Expenses (SAR)', (val) => setState(() => _emergencyExpense = double.tryParse(val) ?? 0)),
          _resultCard('6-Month Emergency Goal', 'SAR ${emergencyGoal.toStringAsFixed(0)}', AppTheme.error),

          // Personal Loan Calculator
          const SizedBox(height: 32),
          _sectionTitle('Personal Loan Eligibility'),
          _resultCard('Available (30% of SAR 150,000)', 'SAR 45,000', AppTheme.primary),
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
          fillColor: AppTheme.background,
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: BorderSide(color: AppTheme.border)),
          enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: BorderSide(color: AppTheme.border)),
          focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: BorderSide(color: AppTheme.primary)),
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
