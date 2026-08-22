package tn.knowflowai.backend.DTO.Auth;

import tn.knowflowai.backend.Entity.Enum.EmployeProfile;
import tn.knowflowai.backend.Entity.Enum.Gender;
import tn.knowflowai.backend.Entity.Enum.Role;
import tn.knowflowai.backend.Entity.User;

public class UserResponse {

    private Long id;
    private String name;
    private String familyName;
    private String email;
    private String urlImage;
    private String phoneNumber;
    private String address;
    private Integer age;
    private String cin;
    private Gender gender;
    private Role role;
    private EmployeProfile employeeProfile;
    private Long departmentId;
    private String departmentName;

    public static UserResponse fromUser(User user) {

        UserResponse response = new UserResponse();

        response.id = user.getId();
        response.name = user.getName();
        response.familyName = user.getFamilyName();
        response.email = user.getEmail();
        response.urlImage = user.getUrlImage();
        response.phoneNumber = user.getPhoneNumber();
        response.address = user.getAddress();
        response.age = user.getAge();
        response.cin = user.getCin();
        response.gender = user.getGender();
        response.role = user.getRole();
        response.employeeProfile = user.getEmployeeProfile();

        // ---------- Safe department handling ----------
        if (user.getDepartment() != null) {
            // Force initialization while the session is still open
            response.departmentId = user.getDepartment().getId();
            response.departmentName = user.getDepartment().getName();
        }
        // ----------------------------------------------

        return response;
    }

    // ===================== GETTERS =====================

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getFamilyName() { return familyName; }
    public String getEmail() { return email; }
    public String getUrlImage() { return urlImage; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public Integer getAge() { return age; }
    public String getCin() { return cin; }
    public Gender getGender() { return gender; }
    public Role getRole() { return role; }
    public EmployeProfile getEmployeeProfile() { return employeeProfile; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
}