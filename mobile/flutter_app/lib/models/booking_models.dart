import 'package:json_annotation/json_annotation.dart';

part 'booking_models.g.dart';

@JsonSerializable()
class BookingCreateRequest {
  const BookingCreateRequest({
    required this.studentProfileId,
    required this.tutorProfileId,
    required this.subjectId,
    this.parentProfileId,
    required this.startTime,
    required this.durationMinutes,
    required this.totalFee,
    this.meetingLink,
    this.notes,
  });

  factory BookingCreateRequest.fromJson(Map<String, dynamic> json) => _$BookingCreateRequestFromJson(json);

  Map<String, dynamic> toJson() => _$BookingCreateRequestToJson(this);

  final String studentProfileId;
  final String tutorProfileId;
  final String subjectId;
  final String? parentProfileId;
  final DateTime startTime;
  final int durationMinutes;
  final double totalFee;
  final String? meetingLink;
  final String? notes;
}

@JsonSerializable()
class BookingResponse {
  const BookingResponse({
    required this.id,
    required this.studentProfileId,
    required this.tutorProfileId,
    required this.subjectId,
    required this.startTime,
    required this.durationMinutes,
    required this.totalFee,
    required this.status,
    this.meetingLink,
    this.notes,
  });

  factory BookingResponse.fromJson(Map<String, dynamic> json) => _$BookingResponseFromJson(json);

  Map<String, dynamic> toJson() => _$BookingResponseToJson(this);

  final String id;
  final String studentProfileId;
  final String tutorProfileId;
  final String subjectId;
  final DateTime startTime;
  final int durationMinutes;
  final double totalFee;
  final String status;
  final String? meetingLink;
  final String? notes;
}