import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class PrimaryCard extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry padding;
  final EdgeInsetsGeometry margin;

  const PrimaryCard({
    super.key,
    required this.child,
    this.padding = const EdgeInsets.all(20),
    this.margin = const EdgeInsets.only(bottom: 16),
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: margin,
      padding: padding,
      decoration: BoxDecoration(
        color: AppTheme.surface.withOpacity(0.8), // Slightly translucent
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: AppTheme.border.withOpacity(0.5), width: 1.5),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 20,
            offset: const Offset(0, 10),
          ),
          BoxShadow(
            color: AppTheme.primary.withOpacity(0.02),
            blurRadius: 30,
            spreadRadius: 5,
          ),
        ],
      ),
      child: child,
    );
  }
}
