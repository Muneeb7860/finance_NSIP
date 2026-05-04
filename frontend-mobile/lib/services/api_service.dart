import 'dart:convert';
import 'package:http/http.dart' as http;

/// Centralized API Service for the Flutter mobile app.
/// FLAW #27 FIX: Replaces hardcoded data with real HTTP calls.
class ApiService {
  static const String _baseUrl = 'http://localhost:8080'; // Hardened API Gateway
  static String? _authToken;

  /// Set the JWT token after login
  static void setToken(String token) => _authToken = token;

  /// Common headers with JWT auth
  static Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    if (_authToken != null) 'Authorization': 'Bearer $_authToken',
  };

  // --- Auth ---

  static Future<Map<String, dynamic>> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );
    final data = jsonDecode(response.body);
    if (response.statusCode == 200) {
      _authToken = data['token'];
    }
    return data;
  }

  static Future<Map<String, dynamic>> register(Map<String, String> userData) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/auth/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(userData),
    );
    return jsonDecode(response.body);
  }

  // --- Claims ---

  static Future<Map<String, dynamic>> submitClaim(String userId, String claimType, String amount, String description) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/claims'),
      headers: _headers,
      body: jsonEncode({'userId': userId, 'claimType': claimType, 'amount': amount, 'description': description}),
    );
    return jsonDecode(response.body);
  }

  static Future<List<dynamic>> getUserClaims(String userId) async {
    final response = await http.get(Uri.parse('$_baseUrl/api/v1/claims/user/$userId'), headers: _headers);
    return jsonDecode(response.body);
  }

  // --- Rewards ---

  static Future<Map<String, dynamic>> getPointsBalance(String userId) async {
    final response = await http.get(Uri.parse('$_baseUrl/api/v1/rewards/balance/$userId'), headers: _headers);
    return jsonDecode(response.body);
  }

  // --- Learning ---

  static Future<List<dynamic>> getCourses() async {
    final response = await http.get(Uri.parse('$_baseUrl/api/v1/learning/courses'), headers: _headers);
    return jsonDecode(response.body);
  }

  static Future<Map<String, dynamic>> completeCourse(String videoId, String userId, String courseId, int score) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/learning/videos/$videoId/complete?userId=$userId&courseId=$courseId&interactiveQuizScore=$score'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  // --- Events ---

  static Future<List<dynamic>> getEvents() async {
    final response = await http.get(Uri.parse('$_baseUrl/api/v1/events'), headers: _headers);
    return jsonDecode(response.body);
  }

  static Future<Map<String, dynamic>> rsvpToEvent(String eventId, String userId) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/events/$eventId/rsvp?userId=$userId'),
      headers: _headers,
    );
    return jsonDecode(response.body);
  }

  // --- Reviews ---

  static Future<Map<String, dynamic>> submitReview(String userId, String featureName, int rating, String comment) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/v1/reviews'),
      headers: _headers,
      body: jsonEncode({'userId': userId, 'featureName': featureName, 'rating': rating.toString(), 'comment': comment}),
    );
    return jsonDecode(response.body);
  }
}
