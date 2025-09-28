import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'api/api_client.dart';
import 'api/auth_service.dart';
import 'api/booking_service.dart';
import 'models/auth_models.dart';
import 'storage/jwt_storage.dart';

class TutorBookingApp extends StatefulWidget {
  const TutorBookingApp({super.key});

  @override
  State<TutorBookingApp> createState() => _TutorBookingAppState();
}

class _TutorBookingAppState extends State<TutorBookingApp> {
  late final Future<_AppDependencies> _deps;

  @override
  void initState() {
    super.initState();
    _deps = _loadDeps();
  }

  Future<_AppDependencies> _loadDeps() async {
    final apiClient = await ApiClient.create();
    final prefs = await SharedPreferences.getInstance();
    final storage = JwtStorage(prefs);
    return _AppDependencies(
      authService: AuthService(apiClient, storage),
      bookingService: BookingService(apiClient),
      storage: storage,
    );
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<_AppDependencies>(
      future: _deps,
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const MaterialApp(home: Scaffold(body: Center(child: CircularProgressIndicator())));
        }
        return _App(deps: snapshot.data!);
      },
    );
  }
}

class _AppDependencies {
  const _AppDependencies({required this.authService, required this.bookingService, required this.storage});

  final AuthService authService;
  final BookingService bookingService;
  final JwtStorage storage;
}

class _App extends StatefulWidget {
  const _App({required this.deps});

  final _AppDependencies deps;

  @override
  State<_App> createState() => _AppState();
}

class _AppState extends State<_App> {
  UserSummary? _user;

  @override
  void initState() {
    super.initState();
    _user = widget.deps.storage.user;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Tutor Booking',
      theme: ThemeData(colorSchemeSeed: Colors.blue, useMaterial3: true),
      home: _user == null
          ? LoginPage(
              authService: widget.deps.authService,
              onAuthenticated: (user) => setState(() => _user = user),
            )
          : DashboardPage(
              user: _user!,
              bookingService: widget.deps.bookingService,
              onLogout: () async {
                await widget.deps.storage.clear();
                setState(() => _user = null);
              },
            ),
    );
  }
}

class LoginPage extends StatefulWidget {
  const LoginPage({required this.authService, required this.onAuthenticated, super.key});

  final AuthService authService;
  final void Function(UserSummary user) onAuthenticated;

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _loading = false;
  String? _error;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Ðang nh?p')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextFormField(
                controller: _emailController,
                decoration: const InputDecoration(labelText: 'Email'),
                validator: (value) => value != null && value.contains('@') ? null : 'Email không h?p l?',
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _passwordController,
                decoration: const InputDecoration(labelText: 'M?t kh?u'),
                obscureText: true,
                validator: (value) => value != null && value.length >= 8 ? null : 'Ít nh?t 8 ký t?',
              ),
              const SizedBox(height: 16),
              if (_error != null) ...[
                Text(_error!, style: const TextStyle(color: Colors.red)),
                const SizedBox(height: 8),
              ],
              FilledButton(
                onPressed: _loading
                    ? null
                    : () async {
                        if (!_formKey.currentState!.validate()) return;
                        setState(() {
                          _loading = true;
                          _error = null;
                        });
                        try {
                          final auth = await widget.authService.login(
                            LoginRequest(email: _emailController.text.trim(), password: _passwordController.text),
                          );
                          widget.onAuthenticated(auth.user);
                        } on DioException catch (error) {
                          setState(() => _error = error.response?.data['message']?.toString() ?? 'Ðang nh?p th?t b?i');
                        } finally {
                          if (mounted) setState(() => _loading = false);
                        }
                      },
                child: _loading ? const CircularProgressIndicator() : const Text('Ðang nh?p'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class DashboardPage extends StatelessWidget {
  const DashboardPage({required this.user, required this.bookingService, required this.onLogout, super.key});

  final UserSummary user;
  final BookingService bookingService;
  final Future<void> Function() onLogout;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Xin chào ${user.fullName} (${user.role})'),
        actions: [
          IconButton(
            onPressed: () async {
              await onLogout();
            },
            icon: const Icon(Icons.logout),
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('Tích h?p danh sách l?ch d?y/h?c t?i dây'),
            const SizedBox(height: 12),
            Text('User ID: ${user.id}'),
          ],
        ),
      ),
    );
  }
}