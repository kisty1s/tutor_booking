import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

import '../models/auth_models.dart';

class JwtStorage {
  JwtStorage(this._prefs);

  final SharedPreferences _prefs;

  static const _tokenKey = 'auth.jwt';
  static const _userKey = 'auth.user';

  Future<void> saveToken(String token) => _prefs.setString(_tokenKey, token);

  Future<void> saveUser(UserSummary user) => _prefs.setString(_userKey, jsonEncode(user.toJson()));

  String? get token => _prefs.getString(_tokenKey);

  UserSummary? get user {
    final json = _prefs.getString(_userKey);
    if (json == null) return null;
    return UserSummary.fromJson(jsonDecode(json) as Map<String, dynamic>);
  }

  Future<void> clear() async {
    await _prefs.remove(_tokenKey);
    await _prefs.remove(_userKey);
  }
}