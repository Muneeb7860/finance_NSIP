import 'package:flutter/material.dart';
import 'theme/app_theme.dart';
import 'screens/portfolio_screen.dart';
import 'screens/learning_screen.dart';
import 'screens/events_screen.dart';
import 'screens/planning_screen.dart';
import 'screens/help_centre_screen.dart';
import 'widgets/hafida_assistant_bottom_sheet.dart';

void main() {
  runApp(const NSIPMobileApp());
}

class NSIPMobileApp extends StatelessWidget {
  const NSIPMobileApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<ThemeType>(
      valueListenable: AppTheme.themeNotifier,
      builder: (context, themeType, child) {
        return MaterialApp(
          title: 'NSIP Wealth',
          debugShowCheckedModeBanner: false,
          theme: AppTheme.darkTheme,
          home: const MainNavigation(),
        );
      },
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

class _MainNavigationState extends State<MainNavigation> with SingleTickerProviderStateMixin {
  int _currentIndex = 0;
  late AnimationController _fabController;

  final List<Widget> _pages = const [
    PortfolioScreen(),
    LearningScreen(),
    EventsScreen(),
    PlanningScreen(),
    HelpCentreScreen(),
  ];

  @override
  void initState() {
    super.initState();
    // Repeating animation controller for the FAB pulse glow effect
    _fabController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat(reverse: true);
  }

  @override
  void dispose() {
    _fabController.dispose();
    super.dispose();
  }

  void _openHafidaAssistant() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      barrierColor: Colors.black.withOpacity(0.5),
      builder: (context) => const HafidaAssistantBottomSheet(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],
      floatingActionButton: AnimatedBuilder(
        animation: _fabController,
        builder: (context, child) {
          return Container(
            margin: const EdgeInsets.only(bottom: 12, right: 4),
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              boxShadow: [
                BoxShadow(
                  color: AppTheme.success.withOpacity(0.2 + (_fabController.value * 0.3)),
                  blurRadius: 10 + (_fabController.value * 12),
                  spreadRadius: 2 + (_fabController.value * 4),
                )
              ],
            ),
            child: FloatingActionButton(
              onPressed: _openHafidaAssistant,
              backgroundColor: AppTheme.success,
              elevation: 4,
              child: const Icon(
                Icons.mic_rounded,
                color: Colors.white,
                size: 28,
              ),
            ),
          );
        },
      ),
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: AppTheme.surface.withOpacity(0.8),
          border: Border(
            top: BorderSide(
              color: Colors.white.withOpacity(0.06),
              width: 0.8,
            ),
          ),
        ),
        child: BottomNavigationBar(
          currentIndex: _currentIndex,
          onTap: (index) => setState(() => _currentIndex = index),
          type: BottomNavigationBarType.fixed,
          backgroundColor: Colors.transparent,
          elevation: 0,
          selectedItemColor: AppTheme.primary,
          unselectedItemColor: Colors.white60,
          selectedLabelStyle: const TextStyle(fontWeight: FontWeight.bold, fontSize: 11),
          unselectedLabelStyle: const TextStyle(fontWeight: FontWeight.w500, fontSize: 11),
          selectedFontSize: 11,
          unselectedFontSize: 11,
          items: const [
            BottomNavigationBarItem(
              icon: Padding(
                padding: EdgeInsets.only(bottom: 4),
                child: Icon(Icons.pie_chart_rounded),
              ),
              label: 'Portfolio',
            ),
            BottomNavigationBarItem(
              icon: Padding(
                padding: EdgeInsets.only(bottom: 4),
                child: Icon(Icons.school_rounded),
              ),
              label: 'Learning',
            ),
            BottomNavigationBarItem(
              icon: Padding(
                padding: EdgeInsets.only(bottom: 4),
                child: Icon(Icons.event_rounded),
              ),
              label: 'Events',
            ),
            BottomNavigationBarItem(
              icon: Padding(
                padding: EdgeInsets.only(bottom: 4),
                child: Icon(Icons.bar_chart_rounded),
              ),
              label: 'Planning',
            ),
            BottomNavigationBarItem(
              icon: Padding(
                padding: EdgeInsets.only(bottom: 4),
                child: Icon(Icons.help_center_rounded),
              ),
              label: 'Help',
            ),
          ],
        ),
      ),
    );
  }
}
