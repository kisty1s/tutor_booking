import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';

const _baseUrl = 'http://localhost:8080';

class ApiClient {
  ApiClient._(this._dio);

  final Dio _dio;

  static Future<ApiClient> create() async {
    final dio = Dio(BaseOptions(
      baseUrl: _baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
    ));

    final prefs = await SharedPreferences.getInstance();

    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) {
        final token = prefs.getString('auth.jwt');
        if (token != null && token.isNotEmpty) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
      onError: (error, handler) {
        if (error.response?.statusCode == 401) {
          prefs.remove('auth.jwt');
        }
        return handler.next(error);
      },
    ));

    return ApiClient._(dio);
  }

  Dio get dio => _dio;
}