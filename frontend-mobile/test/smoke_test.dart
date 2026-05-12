import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:nsip_mobile/main.dart';

void main() {
  testWidgets('NSIP Mobile App Smoke Test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const NSIPMobileApp());

    // Verify that we start on the Portfolio screen
    expect(find.text('Total Portfolio Balance'), findsOneWidget);
    expect(find.text('SAR 150,000'), findsOneWidget);

    // Tap on the Learning tab
    await tester.tap(find.byIcon(Icons.school_rounded));
    await tester.pumpAndSettle();

    // Verify that we are on the Learning screen
    expect(find.text('Learning Center'), findsOneWidget);
    expect(find.text('How to Save Wisely'), findsOneWidget);

    // Tap on the Help tab
    await tester.tap(find.byIcon(Icons.help_center_rounded));
    await tester.pumpAndSettle();

    // Verify that we are on the Help Centre screen
    expect(find.text('Help Centre'), findsOneWidget);
    expect(find.text('WhatsApp Business'), findsOneWidget);
  });
}
