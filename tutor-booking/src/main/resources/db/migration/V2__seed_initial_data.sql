SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM dbo.subjects WHERE code = 'MATH')
BEGIN
    INSERT INTO dbo.subjects (id, code, name, description, is_active, created_at, updated_at)
    VALUES (NEWID(), 'MATH', N'Toán học', N'Chương trình toán học tổng quát', 1, SYSUTCDATETIME(), SYSUTCDATETIME());
END;

IF NOT EXISTS (SELECT 1 FROM dbo.subjects WHERE code = 'ENG')
BEGIN
    INSERT INTO dbo.subjects (id, code, name, description, is_active, created_at, updated_at)
    VALUES (NEWID(), 'ENG', N'Ngữ văn', N'Ngữ văn và luyện viết', 1, SYSUTCDATETIME(), SYSUTCDATETIME());
END;

IF NOT EXISTS (SELECT 1 FROM dbo.subjects WHERE code = 'SCI')
BEGIN
    INSERT INTO dbo.subjects (id, code, name, description, is_active, created_at, updated_at)
    VALUES (NEWID(), 'SCI', N'Khoa học', N'Khoa học tự nhiên tích hợp', 1, SYSUTCDATETIME(), SYSUTCDATETIME());
END;

IF NOT EXISTS (SELECT 1 FROM dbo.user_accounts WHERE email = 'admin@tutorbooking.vn')
BEGIN
    INSERT INTO dbo.user_accounts (id, email, password_hash, full_name, phone_number, role, status, last_login_at, created_at, updated_at)
    VALUES (
        NEWID(),
        'admin@tutorbooking.vn',
        '$2a$12$q5P0hB1j7rwhhtjfiPgkfuZKuXzeuL1NVr7DiIP9N6pN1Nsx3Rp/m', -- bcrypt hash for Admin123!
        N'Platform Administrator',
        NULL,
        'ADMIN',
        'ACTIVE',
        NULL,
        SYSUTCDATETIME(),
        SYSUTCDATETIME()
    );
END;
