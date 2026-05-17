import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  // Use the API Gateway endpoint
  static const String baseUrl = 'http://localhost:8080/api/v1';

  static Future<Map<String, dynamic>> getPensionBalance(String userId) async {
    try {
      final response = await http.get(Uri.parse('$baseUrl/contributions/user/$userId/vested-balance'),
          headers: {'Content-Type': 'application/json', 'Accept-Encoding': 'gzip'});
      
      if (response.statusCode == 200) {
        // The API returns a direct number (BigDecimal in Java). We parse it and wrap in Map for compatibility.
        final balance = jsonDecode(response.body);
        return {'total_balance': balance};
      } else {
        throw Exception('Failed to load balance');
      }
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  static Future<List<dynamic>> getTransactions(String userId) async {
    try {
      final response = await http.get(Uri.parse('$baseUrl/payments/user/$userId/transactions'),
          headers: {'Content-Type': 'application/json', 'Accept-Encoding': 'gzip'});
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('Failed to load transactions');
      }
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }
}
