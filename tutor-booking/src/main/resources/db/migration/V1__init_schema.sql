IF OBJECT_ID('dbo.user_accounts', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.user_accounts (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        email NVARCHAR(180) NOT NULL,
        password_hash NVARCHAR(255) NOT NULL,
        full_name NVARCHAR(120) NOT NULL,
        phone_number NVARCHAR(30) NULL,
        role NVARCHAR(20) NOT NULL,
        status NVARCHAR(30) NOT NULL,
        last_login_at DATETIMEOFFSET NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL
    );
    CREATE UNIQUE INDEX UX_user_accounts_email ON dbo.user_accounts(email);
END;

IF OBJECT_ID('dbo.parent_profiles', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.parent_profiles (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        user_account_id UNIQUEIDENTIFIER NOT NULL,
        display_name NVARCHAR(120) NULL,
        contact_phone NVARCHAR(30) NULL,
        preferred_contact_method NVARCHAR(50) NULL,
        notes NVARCHAR(500) NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_parent_profiles_user_accounts FOREIGN KEY (user_account_id)
            REFERENCES dbo.user_accounts(id)
            ON DELETE CASCADE,
        CONSTRAINT UX_parent_profiles_user UNIQUE (user_account_id)
    );
END;

IF OBJECT_ID('dbo.student_profiles', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.student_profiles (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        user_account_id UNIQUEIDENTIFIER NOT NULL,
        grade_level NVARCHAR(50) NULL,
        learning_goals NVARCHAR(500) NULL,
        preferred_learning_mode NVARCHAR(50) NULL,
        time_zone NVARCHAR(60) NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_student_profiles_user_accounts FOREIGN KEY (user_account_id)
            REFERENCES dbo.user_accounts(id)
            ON DELETE CASCADE,
        CONSTRAINT UX_student_profiles_user UNIQUE (user_account_id)
    );
END;

IF OBJECT_ID('dbo.parent_student_links', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.parent_student_links (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        parent_profile_id UNIQUEIDENTIFIER NOT NULL,
        student_profile_id UNIQUEIDENTIFIER NOT NULL,
        relationship NVARCHAR(60) NULL,
        is_primary BIT NOT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_parent_student_parent FOREIGN KEY (parent_profile_id)
            REFERENCES dbo.parent_profiles(id)
            ON DELETE CASCADE,
        CONSTRAINT FK_parent_student_student FOREIGN KEY (student_profile_id)
            REFERENCES dbo.student_profiles(id)
            ON DELETE NO ACTION,
        CONSTRAINT UX_parent_student UNIQUE (parent_profile_id, student_profile_id)
    );
END;

IF OBJECT_ID('dbo.subjects', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.subjects (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        code NVARCHAR(30) NOT NULL,
        name NVARCHAR(120) NOT NULL,
        description NVARCHAR(500) NULL,
        is_active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT UX_subjects_code UNIQUE (code),
        CONSTRAINT UX_subjects_name UNIQUE (name)
    );
END;

IF OBJECT_ID('dbo.tutor_profiles', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.tutor_profiles (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        user_account_id UNIQUEIDENTIFIER NOT NULL,
        headline NVARCHAR(150) NULL,
        bio NVARCHAR(MAX) NULL,
        years_experience INT NULL,
        hourly_rate DECIMAL(10,2) NULL,
        qualification_summary NVARCHAR(500) NULL,
        city NVARCHAR(120) NULL,
        country NVARCHAR(120) NULL,
        teaching_modes NVARCHAR(120) NULL,
        average_rating DECIMAL(3,2) NULL,
        total_reviews INT NULL,
        is_verified BIT NOT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_tutor_profiles_user_accounts FOREIGN KEY (user_account_id)
            REFERENCES dbo.user_accounts(id)
            ON DELETE CASCADE,
        CONSTRAINT UX_tutor_profiles_user UNIQUE (user_account_id)
    );
END;

IF OBJECT_ID('dbo.tutor_availabilities', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.tutor_availabilities (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        tutor_profile_id UNIQUEIDENTIFIER NOT NULL,
        day_of_week NVARCHAR(15) NOT NULL,
        start_time TIME NOT NULL,
        end_time TIME NOT NULL,
        is_recurring BIT NOT NULL,
        specific_date DATE NULL,
        time_zone NVARCHAR(60) NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_tutor_availabilities_tutor FOREIGN KEY (tutor_profile_id)
            REFERENCES dbo.tutor_profiles(id)
            ON DELETE CASCADE
    );
END;

IF OBJECT_ID('dbo.tutor_subjects', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.tutor_subjects (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        tutor_profile_id UNIQUEIDENTIFIER NOT NULL,
        subject_id UNIQUEIDENTIFIER NOT NULL,
        teaching_level NVARCHAR(30) NOT NULL,
        hourly_rate DECIMAL(10,2) NULL,
        currency NVARCHAR(10) NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_tutor_subjects_tutor FOREIGN KEY (tutor_profile_id)
            REFERENCES dbo.tutor_profiles(id)
            ON DELETE CASCADE,
        CONSTRAINT FK_tutor_subjects_subject FOREIGN KEY (subject_id)
            REFERENCES dbo.subjects(id)
            ON DELETE CASCADE,
        CONSTRAINT UX_tutor_subject UNIQUE (tutor_profile_id, subject_id, teaching_level)
    );
END;

IF OBJECT_ID('dbo.bookings', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.bookings (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        student_profile_id UNIQUEIDENTIFIER NOT NULL,
        tutor_profile_id UNIQUEIDENTIFIER NOT NULL,
        subject_id UNIQUEIDENTIFIER NOT NULL,
        start_time DATETIMEOFFSET NOT NULL,
        duration_minutes INT NOT NULL,
        total_fee DECIMAL(10,2) NOT NULL,
        status NVARCHAR(20) NOT NULL,
        meeting_link NVARCHAR(255) NULL,
        notes NVARCHAR(500) NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_bookings_student FOREIGN KEY (student_profile_id)
            REFERENCES dbo.student_profiles(id)
            ON DELETE NO ACTION,
        CONSTRAINT FK_bookings_tutor FOREIGN KEY (tutor_profile_id)
            REFERENCES dbo.tutor_profiles(id),
        CONSTRAINT FK_bookings_subject FOREIGN KEY (subject_id)
            REFERENCES dbo.subjects(id)
    );
END;

IF OBJECT_ID('dbo.payments', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.payments (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        booking_id UNIQUEIDENTIFIER NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        status NVARCHAR(20) NOT NULL,
        provider NVARCHAR(60) NULL,
        reference_code NVARCHAR(80) NULL,
        paid_at DATETIMEOFFSET NULL,
        failure_reason NVARCHAR(255) NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_payments_booking FOREIGN KEY (booking_id)
            REFERENCES dbo.bookings(id)
            ON DELETE CASCADE,
        CONSTRAINT UX_payments_booking UNIQUE (booking_id),
        CONSTRAINT UX_payments_reference UNIQUE (reference_code)
    );
END;

IF OBJECT_ID('dbo.reviews', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.reviews (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
        booking_id UNIQUEIDENTIFIER NOT NULL,
        tutor_profile_id UNIQUEIDENTIFIER NOT NULL,
        student_profile_id UNIQUEIDENTIFIER NOT NULL,
        rating INT NOT NULL,
        comment NVARCHAR(500) NULL,
        is_visible BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_reviews_booking FOREIGN KEY (booking_id)
            REFERENCES dbo.bookings(id)
            ON DELETE CASCADE,
        CONSTRAINT FK_reviews_tutor FOREIGN KEY (tutor_profile_id)
            REFERENCES dbo.tutor_profiles(id)
            ON DELETE CASCADE,
        CONSTRAINT FK_reviews_student FOREIGN KEY (student_profile_id)
            REFERENCES dbo.student_profiles(id)
            ON DELETE NO ACTION,
        CONSTRAINT UX_reviews_booking UNIQUE (booking_id)
    );
END;

