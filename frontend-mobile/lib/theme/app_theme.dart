import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

enum ThemeType {
  midnightSlate,
  emeraldShariah,
  neonCyberpunk
}

class AppTheme {
  // Active theme indicator
  static ThemeType currentThemeType = ThemeType.midnightSlate;
  
  // Theme notifier for reactivity
  static final ValueNotifier<ThemeType> themeNotifier = ValueNotifier<ThemeType>(ThemeType.midnightSlate);

  // Dynamic getters returning colors based on current theme type
  static Color get background {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFF0F172A); // Slate 900
      case ThemeType.emeraldShariah:
        return const Color(0xFF062F21); // Forest Deep
      case ThemeType.neonCyberpunk:
        return const Color(0xFF09090B); // Ultra Onyx
    }
  }

  static Color get surface {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFF1E293B); // Slate 800
      case ThemeType.emeraldShariah:
        return const Color(0xFF064E3B); // Forest Surface
      case ThemeType.neonCyberpunk:
        return const Color(0xFF18181B); // Rich Zinc
    }
  }

  static Color get border {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFF334155); // Slate 700
      case ThemeType.emeraldShariah:
        return const Color(0xFF047857); // Mint Border
      case ThemeType.neonCyberpunk:
        return const Color(0xFF27272A); // Zinc Border
    }
  }

  static Color get primary {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFF3B82F6); // Vibrant Blue
      case ThemeType.emeraldShariah:
        return const Color(0xFFD97706); // Golden Bronze
      case ThemeType.neonCyberpunk:
        return const Color(0xFFEC4899); // Neon Pink
    }
  }

  static Color get secondary {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFF8B5CF6); // Neon Violet
      case ThemeType.emeraldShariah:
        return const Color(0xFF10B981); // Mint Success
      case ThemeType.neonCyberpunk:
        return const Color(0xFF06B6D4); // Cyber Neon Cyan
    }
  }

  static Color get success {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFF10B981); // Emerald
      case ThemeType.emeraldShariah:
        return const Color(0xFF34D399); // Pastel Mint
      case ThemeType.neonCyberpunk:
        return const Color(0xFF10B981); // Neon Green
    }
  }

  static Color get warning {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFFF59E0B); // Amber
      case ThemeType.emeraldShariah:
        return const Color(0xFFFBBF24); // Gold Warning
      case ThemeType.neonCyberpunk:
        return const Color(0xFFF59E0B); // Orange
    }
  }

  static Color get error {
    switch (currentThemeType) {
      case ThemeType.midnightSlate:
        return const Color(0xFFF43F5E); // Rose
      case ThemeType.emeraldShariah:
        return const Color(0xFFF87171); // Pastel Red
      case ThemeType.neonCyberpunk:
        return const Color(0xFFEF4444); // Cyber Red
    }
  }

  // Generate Theme Data for Flutter context
  static ThemeData get darkTheme {
    return ThemeData(
      brightness: Brightness.dark,
      scaffoldBackgroundColor: background,
      primaryColor: primary,
      colorScheme: ColorScheme.dark(
        primary: primary,
        secondary: secondary,
        surface: surface,
        error: error,
      ),
      textTheme: GoogleFonts.outfitTextTheme(ThemeData.dark().textTheme),
      cardTheme: CardThemeData(
        color: surface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        elevation: 0,
      ),
      bottomNavigationBarTheme: BottomNavigationBarThemeData(
        backgroundColor: Colors.transparent,
        selectedItemColor: primary,
        unselectedItemColor: Colors.white60,
      ),
    );
  }

  // Change Active Theme
  static void switchTheme(ThemeType newTheme) {
    currentThemeType = newTheme;
    themeNotifier.value = newTheme;
  }
}
