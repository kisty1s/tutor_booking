import 'package:dio/dio.dart';
import '../models/booking_models.dart';
import 'api_client.dart';

class BookingService {
  BookingService(this._client);

  final ApiClient _client;

  Future<BookingResponse> createBooking(BookingCreateRequest request) async {
    final response = await _client.dio.post('/api/v1/bookings', data: request.toJson());
    return BookingResponse.fromJson(response.data as Map<String, dynamic>);
  }

  Future<List<BookingResponse>> listTutorBookings(String tutorProfileId) async {
    final response = await _client.dio.get('/api/v1/bookings/tutor/$tutorProfileId');
    return (response.data as List)
        .map((item) => BookingResponse.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<List<BookingResponse>> listStudentBookings(String studentProfileId) async {
    final response = await _client.dio.get('/api/v1/bookings/student/$studentProfileId');
    return (response.data as List)
        .map((item) => BookingResponse.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<BookingResponse> confirm(String bookingId) => _transition(bookingId, 'confirm');

  Future<BookingResponse> cancel(String bookingId) => _transition(bookingId, 'cancel');

  Future<BookingResponse> complete(String bookingId) => _transition(bookingId, 'complete');

  Future<BookingResponse> _transition(String bookingId, String action) async {
    final Response response = await _client.dio.post('/api/v1/bookings/$bookingId/$action');
    return BookingResponse.fromJson(response.data as Map<String, dynamic>);
  }
}