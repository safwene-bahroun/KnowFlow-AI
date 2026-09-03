package tn.knowflowai.backend.DTO.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tn.knowflowai.backend.Entity.Enum.EmployeProfile;
import tn.knowflowai.backend.Entity.Enum.Gender;

public class RegisterRequest {

    // =========================
    // NAME
    // =========================

    @NotBlank(message = "Name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Name must contain between 2 and 100 characters"
    )
    @Pattern(
        regexp = "^[\\p{L} .'-]+$",
        message = "Name contains invalid characters"
    )
    private String name;


    // =========================
    // FAMILY NAME
    // =========================

    @NotBlank(message = "Family name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Family name must contain between 2 and 100 characters"
    )
    @Pattern(
        regexp = "^[\\p{L} .'-]+$",
        message = "Family name contains invalid characters"
    )
    private String familyName;


    // =========================
    // EMAIL
    // =========================

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(
        max = 150,
        message = "Email must not exceed 150 characters"
    )
    private String email;


    // =========================
    // PASSWORD
    // =========================

    @NotBlank(message = "Password is required")
    @Size(
        min = 6,
        max = 100,
        message = "Password must contain between 6 and 100 characters"
    )
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
        message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;


    // =========================
    // CIN
    // =========================

    @NotBlank(message = "CIN is required")
    @Pattern(
        regexp = "^\\d{8}$",
        message = "CIN must contain exactly 8 digits"
    )
    private String cin;


    // =========================
    // PHONE NUMBER
    // =========================

    @Pattern(
        regexp = "^[+]?[0-9]{8,15}$",
        message = "Invalid phone number"
    )
    private String phoneNumber;


    // =========================
    // PROFILE IMAGE URL
    // =========================


    private String urlImage;


    // =========================
    // ADDRESS
    // =========================

    @Size(
        max = 255,
        message = "Address is too long"
    )
    private String address;


    // =========================
    // AGE
    // =========================

    @NotNull(message = "Age is required")
    @Min(
        value = 18,
        message = "User must be at least 18 years old"
    )
    @Max(
        value = 100,
        message = "Invalid age"
    )
    private Integer age;


    // =========================
    // GENDER
    // =========================

    @NotNull(message = "Gender is required")
    private Gender gender;


    // =========================
    // EMPLOYEE PROFILE
    // =========================

    @NotNull(message = "Employee profile is required")
    private EmployeProfile employeeProfile;


    // =========================
    // DEPARTMENT NAME
    // =========================

    @NotBlank(message = "Department is required")
    @Size(
        max = 100,
        message = "Department name is too long"
    )
    private String departmentName;


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }


    public EmployeProfile getEmployeeProfile() {
        return employeeProfile;
    }

    public void setEmployeeProfile(
            EmployeProfile employeeProfile
    ) {
        this.employeeProfile = employeeProfile;
    }


    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(
            String departmentName
    ) {
        this.departmentName = departmentName;
    }
}