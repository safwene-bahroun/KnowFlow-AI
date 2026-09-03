package tn.knowflowai.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;

import tn.knowflowai.backend.Entity.Enum.EmployeProfile;
import tn.knowflowai.backend.Entity.Enum.Gender;
import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.Entity.User;
import tn.knowflowai.backend.Repository.UserRepository;

@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

    @Bean
    CommandLineRunner createFirstAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // Check if an ADMIN already exists
            boolean adminExists =  userRepository.existsByRole(Role.ADMIN);

            if (adminExists) {

                System.out.println(
                    "ADMIN already exists. No admin account created."
                );

                return;
            }

            // Create the first ADMIN
            User admin = new User();

            admin.setName("Safwene");
            admin.setFamilyName("Bahroun");

            admin.setEmail("knowflowai.project@gmail.com");

            // NEVER store the password in plain text
            admin.setPassword(
                passwordEncoder.encode("Admin@123")
            );

            admin.setCin("11994068");

            admin.setPhoneNumber("+21692877186");

            admin.setAge(24);

            admin.setGender(Gender.MALE);

            admin.setRole(Role.ADMIN);

            admin.setEmployeeProfile(
                EmployeProfile.DEPARTMENT_MANAGER
            );

            admin.setUrlImage(null);

            admin.setAddress("KnowFlow AI");

            // No department for the system administrator
            admin.setDepartment(null);

            userRepository.save(admin);

            System.out.println(
                "=========================================="
            );

            System.out.println(
                "FIRST ADMIN ACCOUNT CREATED"
            );

            System.out.println(
                "Email: knowflowai.project@gmail.com"
            );


            System.out.println(
                "=========================================="
            );
        };
    }

    @Bean
    CommandLineRunner updateNotificationTypeConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("""
                    DO $$
                    BEGIN
                        IF EXISTS (
                            SELECT 1 FROM pg_constraint
                            WHERE conname = 'notifications_type_check'
                        ) THEN
                            ALTER TABLE notifications DROP CONSTRAINT notifications_type_check;
                        END IF;

                        ALTER TABLE notifications
                        ADD CONSTRAINT notifications_type_check
                        CHECK (type IN (
                            'DOCUMENT_UPLOADED', 'DOCUMENT_UPDATED',
                            'DOCUMENT_PROCESSED', 'DOCUMENT_REJECTED',
                            'FRAUD_DETECTED', 'NEW_MESSAGE',
                            'FEEDBACK_RECEIVED', 'SYSTEM'
                        ));
                    END $$;
                    """);
        };
    }
}