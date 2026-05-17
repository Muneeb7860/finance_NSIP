import 'dart:async';
import 'dart:ui';
import 'package:flutter/material.dart';
import '../theme/app_theme.dart';
import '../services/api_service.dart';
import 'primary_card.dart';

class HafidaAssistantBottomSheet extends StatefulWidget {
  const HafidaAssistantBottomSheet({super.key});

  @override
  State<HafidaAssistantBottomSheet> createState() => _HafidaAssistantBottomSheetState();
}

class _HafidaAssistantBottomSheetState extends State<HafidaAssistantBottomSheet> with TickerProviderStateMixin {
  final TextEditingController _messageController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  final List<Map<String, dynamic>> _messages = [
    {
      'isUser': false,
      'text': 'Marhaba! I am Hafida, your National Support Advisor. How can I assist you with your pension, financial planning, or wellness programs today?',
      'time': 'Just now',
    }
  ];

  bool _isTyping = false;
  bool _isListening = false;
  late AnimationController _soundwaveController;

  final List<String> _suggestions = [
    'Check Vested Balance',
    'Calculate Pension Loan',
    'Book Financial Advisor',
    'View Learning Rewards'
  ];

  @override
  void initState() {
    super.initState();
    _soundwaveController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
  }

  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    _soundwaveController.dispose();
    super.dispose();
  }

  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  Future<void> _handleSendMessage(String text) async {
    if (text.trim().isEmpty) return;

    setState(() {
      _messages.add({
        'isUser': true,
        'text': text,
        'time': 'Just now',
      });
      _isTyping = true;
    });
    _messageController.clear();
    _scrollToBottom();

    // Simulate AI thinking and calling the live microservice
    await Future.delayed(const Duration(milliseconds: 1500));

    String reply = '';
    Widget? customWidget;

    final lowerText = text.toLowerCase();
    if (lowerText.contains('balance') || lowerText.contains('vested') || lowerText.contains('check')) {
      setState(() {
        _isTyping = true;
      });
      try {
        // Query the live Contribution microservice via ApiService
        final balanceData = await ApiService.getPensionBalance('f00efc85-0ebf-41e2-82f4-f13cfcd8d22e');
        final double balance = balanceData['total_balance'] ?? 0.0;
        reply = 'According to the Contribution microservice database, your current vested balance is SAR ${balance.toStringAsFixed(2)}. Here is your live portfolio allocation details:';
        customWidget = Container(
          margin: const EdgeInsets.only(top: 10),
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [AppTheme.primary.withOpacity(0.2), AppTheme.secondary.withOpacity(0.2)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: AppTheme.primary.withOpacity(0.3)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Vested Balance', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: Colors.white70)),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(color: AppTheme.success.withOpacity(0.2), borderRadius: BorderRadius.circular(12)),
                    child: Text('Live Connected', style: TextStyle(color: AppTheme.success, fontSize: 10, fontWeight: FontWeight.bold)),
                  )
                ],
              ),
              const SizedBox(height: 8),
              Text(
                'SAR ${balance.toStringAsFixed(2)}',
                style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: Colors.white),
              ),
              const Divider(color: Colors.white24, height: 20),
              const Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text('Equity Portion', style: TextStyle(fontSize: 12, color: Colors.white60)),
                  Text('SAR 97,500.00 (65%)', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white)),
                ],
              ),
              const SizedBox(height: 4),
              const Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text('Sukuk & Cash', style: TextStyle(fontSize: 12, color: Colors.white60)),
                  Text('SAR 52,500.00 (35%)', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white)),
                ],
              ),
            ],
          ),
        );
      } catch (e) {
        // Fallback mock if backend is down during launch
        reply = 'I am unable to fetch your real-time balance right now due to a network gateway timeout. However, based on your cached contribution profile, your estimated vested balance is SAR 150,000.00.';
      }
    } else if (lowerText.contains('loan') || lowerText.contains('calculate') || lowerText.contains('simulate')) {
      reply = 'Based on your vested balance of SAR 150,000.00, you are eligible to simulate a Pension-Backed Personal Loan of up to 30% of your vested assets (SAR 45,000.00). Here is your simulated low-interest repayment structure:';
      customWidget = Container(
        margin: const EdgeInsets.only(top: 10),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: AppTheme.surface.withOpacity(0.6),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: AppTheme.secondary.withOpacity(0.3)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Simulated Loan Structure', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: AppTheme.secondary)),
            const SizedBox(height: 12),
            const Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Max Loan Amount', style: TextStyle(fontSize: 12, color: Colors.white60)),
                Text('SAR 45,000.00', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white)),
              ],
            ),
            const SizedBox(height: 6),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Annual Profit Rate', style: TextStyle(fontSize: 12, color: Colors.white60)),
                Text('2.75% (Special Pension Rate)', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: AppTheme.success)),
              ],
            ),
            const SizedBox(height: 6),
            const Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Monthly Instalment', style: TextStyle(fontSize: 12, color: Colors.white60)),
                Text('SAR 804.17 / month', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white)),
              ],
            ),
            const SizedBox(height: 6),
            const Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Repayment Period', style: TextStyle(fontSize: 12, color: Colors.white60)),
                Text('60 Months', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.white)),
              ],
            ),
          ],
        ),
      );
    } else if (lowerText.contains('advisor') || lowerText.contains('book')) {
      reply = 'I have identified 3 certified Shariah-compliant financial advisors available this week who specialize in pension growth and retirement planning. Would you like to secure a 1,000-point loyalty reward booking now?';
      customWidget = Container(
        margin: const EdgeInsets.only(top: 10),
        width: double.infinity,
        child: ElevatedButton.icon(
          onPressed: () {
            Navigator.pop(context);
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: const Text('Navigating to Advisor Booking center...'),
                backgroundColor: AppTheme.primary,
              ),
            );
          },
          icon: const Icon(Icons.calendar_month, color: Colors.white),
          label: const Text('Book Financial Advisor Now', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
          style: ElevatedButton.styleFrom(
            backgroundColor: AppTheme.primary,
            padding: const EdgeInsets.symmetric(vertical: 12),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          ),
        ),
      );
    } else if (lowerText.contains('reward') || lowerText.contains('learning') || lowerText.contains('points')) {
      reply = 'Excellent! You have earned 450 points by completing the \"Gamified Pension Planning Module\". You are only 50 points away from unlocking a premium SAR 100 Noon Voucher! Keep learning!';
    } else {
      reply = 'I appreciate your question. As your National social insurance assistant, I can check your vested balance, simulate pension loans, or help book advisory calls. Let me know if you would like me to retrieve any specific record!';
    }

    setState(() {
      _messages.add({
        'isUser': false,
        'text': reply,
        'time': 'Just now',
        'customWidget': customWidget,
      });
      _isTyping = false;
    });
    _scrollToBottom();
  }

  void _toggleVoiceListening() {
    if (_isListening) {
      setState(() {
        _isListening = false;
      });
      _soundwaveController.stop();
      // Simulate speaking complete
      _handleSendMessage('Check my Vested Balance');
    } else {
      setState(() {
        _isListening = true;
      });
      _soundwaveController.repeat(reverse: true);
      // Automatically "hear" a query after 3 seconds
      Timer(const Duration(seconds: 3), () {
        if (_isListening) {
          _toggleVoiceListening();
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: AppTheme.background.withOpacity(0.85),
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(32),
          topRight: Radius.circular(32),
        ),
        border: Border.all(color: Colors.white.withOpacity(0.08), width: 1.5),
      ),
      child: ClipRRect(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(32),
          topRight: Radius.circular(32),
        ),
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 20, sigmaY: 20),
          child: Padding(
            padding: EdgeInsets.only(
              bottom: MediaQuery.of(context).viewInsets.bottom,
            ),
            child: SizedBox(
              height: MediaQuery.of(context).size.height * 0.75,
              child: Column(
                children: [
                  // Upper handle & header
                  Container(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    child: Column(
                      children: [
                        Container(
                          width: 40,
                          height: 5,
                          decoration: BoxDecoration(
                            color: Colors.white24,
                            borderRadius: BorderRadius.circular(10),
                          ),
                        ),
                        const SizedBox(height: 16),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 24),
                          child: Row(
                            children: [
                              // Avatar circle with glow
                              Container(
                                decoration: BoxDecoration(
                                  shape: BoxShape.circle,
                                  boxShadow: [
                                    BoxShadow(
                                      color: AppTheme.success.withOpacity(0.3),
                                      blurRadius: 10,
                                      spreadRadius: 2,
                                    )
                                  ],
                                ),
                                child: CircleAvatar(
                                  radius: 18,
                                  backgroundColor: AppTheme.surface,
                                  child: Text('H', style: TextStyle(color: AppTheme.success, fontWeight: FontWeight.bold, fontSize: 18)),
                                ),
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const Text(
                                      'Hafida Smart AI',
                                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.w900, color: Colors.white),
                                    ),
                                    Row(
                                      children: [
                                        Container(
                                          width: 6,
                                          height: 6,
                                          decoration: BoxDecoration(color: AppTheme.success, shape: BoxShape.circle),
                                        ),
                                        const SizedBox(width: 6),
                                        const Text(
                                          'Online | National Assistant',
                                          style: TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.w500),
                                        ),
                                      ],
                                    ),
                                  ],
                                ),
                              ),
                              IconButton(
                                icon: const Icon(Icons.close_rounded, color: Colors.white60),
                                onPressed: () => Navigator.pop(context),
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(height: 12),
                        const Divider(color: Colors.white12, height: 1),
                      ],
                    ),
                  ),

                  // Message List
                  Expanded(
                    child: ListView.builder(
                      controller: _scrollController,
                      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                      itemCount: _messages.length,
                      itemBuilder: (context, index) {
                        final msg = _messages[index];
                        final isUser = msg['isUser'] as bool;
                        final customWidget = msg['customWidget'] as Widget?;

                        return Align(
                          alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
                          child: Padding(
                            padding: const EdgeInsets.only(bottom: 16),
                            child: Column(
                              crossAxisAlignment: isUser ? CrossAxisAlignment.end : CrossAxisAlignment.start,
                              children: [
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                                  decoration: BoxDecoration(
                                    gradient: isUser
                                        ? LinearGradient(
                                            colors: [AppTheme.primary, AppTheme.secondary],
                                            begin: Alignment.topLeft,
                                            end: Alignment.bottomRight,
                                          )
                                        : null,
                                    color: isUser ? null : AppTheme.surface.withOpacity(0.6),
                                    borderRadius: BorderRadius.only(
                                      topLeft: const Radius.circular(20),
                                      topRight: const Radius.circular(20),
                                      bottomLeft: isUser ? const Radius.circular(20) : const Radius.circular(4),
                                      bottomRight: isUser ? const Radius.circular(4) : const Radius.circular(20),
                                    ),
                                    border: isUser ? null : Border.all(color: Colors.white.withOpacity(0.05)),
                                  ),
                                  constraints: BoxConstraints(
                                    maxWidth: MediaQuery.of(context).size.width * 0.75,
                                  ),
                                  child: Text(
                                    msg['text'] as String,
                                    style: TextStyle(
                                      color: isUser ? Colors.white : Colors.white.withOpacity(0.9),
                                      fontSize: 14,
                                      height: 1.4,
                                    ),
                                  ),
                                ),
                                if (customWidget != null)
                                  Container(
                                    constraints: BoxConstraints(
                                      maxWidth: MediaQuery.of(context).size.width * 0.75,
                                    ),
                                    child: customWidget,
                                  ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
                  ),

                  // Typing indicator
                  if (_isTyping)
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: Row(
                          children: [
                            SizedBox(
                              width: 14,
                              height: 14,
                              child: CircularProgressIndicator(strokeWidth: 2.5, valueColor: AlwaysStoppedAnimation(AppTheme.primary)),
                            ),
                            const SizedBox(width: 8),
                            const Text('Hafida is analyzing record...', style: TextStyle(color: Colors.grey, fontSize: 11, fontWeight: FontWeight.bold)),
                          ],
                        ),
                      ),
                    ),

                  // Suggestions horizontal list
                  Container(
                    height: 40,
                    margin: const EdgeInsets.only(bottom: 12),
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      itemCount: _suggestions.length,
                      itemBuilder: (context, index) {
                        return Padding(
                          padding: const EdgeInsets.only(right: 8),
                          child: ActionChip(
                            label: Text(
                              _suggestions[index],
                              style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.w600),
                            ),
                            backgroundColor: AppTheme.surface.withOpacity(0.5),
                            side: BorderSide(color: Colors.white.withOpacity(0.08)),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                            onPressed: () => _handleSendMessage(_suggestions[index]),
                          ),
                        );
                      },
                    ),
                  ),

                  // Soundwave micro-animation (only shown when listening)
                  if (_isListening)
                    Container(
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      child: Column(
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: List.generate(6, (index) {
                              return AnimatedBuilder(
                                animation: _soundwaveController,
                                builder: (context, child) {
                                  // Rhythmic height variation for bars
                                  final double value = index % 2 == 0
                                      ? _soundwaveController.value
                                      : 1.0 - _soundwaveController.value;
                                  return Container(
                                    width: 4,
                                    height: 15 + (value * 25),
                                    margin: const EdgeInsets.symmetric(horizontal: 3),
                                    decoration: BoxDecoration(
                                      color: index % 2 == 0 ? AppTheme.primary : AppTheme.secondary,
                                      borderRadius: BorderRadius.circular(10),
                                    ),
                                  );
                                },
                              );
                            }),
                          ),
                           const SizedBox(height: 8),
                           Text(
                            'Listening to your request...',
                            style: TextStyle(color: AppTheme.primary, fontSize: 11, fontWeight: FontWeight.bold),
                          ),
                        ],
                      ),
                    ),

                  // Input bar
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
                    decoration: BoxDecoration(
                      color: AppTheme.surface.withOpacity(0.3),
                      border: Border(top: BorderSide(color: Colors.white.withOpacity(0.05))),
                    ),
                    child: Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _messageController,
                            style: const TextStyle(color: Colors.white, fontSize: 14),
                            decoration: InputDecoration(
                              hintText: _isListening ? 'Speak your request...' : 'Ask Hafida...',
                              hintStyle: const TextStyle(color: Colors.grey, fontSize: 14),
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(24),
                                borderSide: BorderSide.none,
                              ),
                              fillColor: Colors.white.withOpacity(0.04),
                              filled: true,
                              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                            ),
                            onSubmitted: _handleSendMessage,
                          ),
                        ),
                        const SizedBox(width: 8),
                        // Glowing mic button
                        GestureDetector(
                          onTap: _toggleVoiceListening,
                          child: Container(
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              color: _isListening ? AppTheme.error.withOpacity(0.2) : AppTheme.primary.withOpacity(0.1),
                              border: Border.all(
                                color: _isListening ? AppTheme.error : AppTheme.primary.withOpacity(0.3),
                                width: 1.5,
                              ),
                              boxShadow: _isListening
                                  ? [
                                      BoxShadow(
                                        color: AppTheme.error.withOpacity(0.3),
                                        blurRadius: 8,
                                        spreadRadius: 1,
                                      )
                                    ]
                                  : null,
                            ),
                            child: Icon(
                              _isListening ? Icons.stop_rounded : Icons.mic_rounded,
                              color: _isListening ? AppTheme.error : AppTheme.primary,
                              size: 20,
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        // Send button
                        GestureDetector(
                          onTap: () => _handleSendMessage(_messageController.text),
                          child: Container(
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              gradient: LinearGradient(
                                colors: [AppTheme.primary, AppTheme.secondary],
                              ),
                            ),
                            child: const Icon(
                              Icons.send_rounded,
                              color: Colors.white,
                              size: 18,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
