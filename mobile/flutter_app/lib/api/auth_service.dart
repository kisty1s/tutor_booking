import 'package:dio/dio.dart';
import '../models/auth_models.dart';
import 'api_client.dart';
import '../storage/jwt_storage.dart';

class AuthService {
  AuthService(this._client, this._jwtStorage);

  final ApiClient _client;
  final JwtStorage _jwtStorage;

  Future<AuthResponse> login(LoginRequest request) async {
    final response = await _client.dio.post('/api/v1/auth/login', data: request.toJson());
    final auth = AuthResponse.fromJson(response.data as Map<String, dynamic>);
    await _jwtStorage.saveToken(auth.accessToken);
    await _jwtStorage.saveUser(auth.user);
    return auth;
  }

  Future<AuthResponse> register(RegisterRequest request) async {
    final response = await _client.dio.post('/api/v1/auth/register', data: request.toJson());
    final auth = AuthResponse.fromJson(response.data as Map<String, dynamic>);
    await _jwtStorage.saveToken(auth.accessToken);
    await _jwtStorage.saveUser(auth.user);
    return auth;
  }

  Future<void> logout() async {
    await _jwtStorage.clear();
  }
}