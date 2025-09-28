import 'package:json_annotation/json_annotation.dart';

part 'auth_models.g.dart';

@JsonSerializable()
class LoginRequest {
  const LoginRequest({required this.email, required this.password});

  factory LoginRequest.fromJson(Map<String, dynamic> json) => _$LoginRequestFromJson(json);

  Map<String, dynamic> toJson() => _$LoginRequestToJson(this);

  final String email;
  final String password;
}

@JsonSerializable()
class RegisterRequest {
  const RegisterRequest({
    required this.email,
    required this.password,
    required this.fullName,
    this.phoneNumber,
    this.role = 'PARENT',
  });

  factory RegisterRequest.fromJson(Map<String, dynamic> json) => _$RegisterRequestFromJson(json);

  Map<String, dynamic> toJson() => _$RegisterRequestToJson(this);

  final String email;
  final String password;
  final String fullName;
  final String? phoneNumber;
  final String role;
}

@JsonSerializable()
class UserSummary {
  const UserSummary({
    required this.id,
    required this.email,
    required this.fullName,
    this.phoneNumber,
    required this.role,
    required this.status,
  });

  factory UserSummary.fromJson(Map<String, dynamic> json) => _$UserSummaryFromJson(json);

  Map<String, dynamic> toJson() => _$UserSummaryToJson(this);

  final String id;
  final String email;
  final String fullName;
  final String? phoneNumber;
  final String role;
  final String status;
}

@JsonSerializable()
class AuthResponse {
  const AuthResponse({
    required this.tokenType,
    required this.accessToken,
    required this.expiresAt,
    required this.user,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) => _$AuthResponseFromJson(json);

  Map<String, dynamic> toJson() => _$AuthResponseToJson(this);

  final String tokenType;
  final String accessToken;
  final DateTime expiresAt;
  final UserSummary user;
}