package tn.knowflowai.backend.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import tn.knowflowai.backend.Entity.Enum.EmployeProfile;
import tn.knowflowai.backend.Entity.Enum.Gender;
import tn.knowflowai.backend.Entity.Enum.Role;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_user_cin", columnNames = "cin")
    }
)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer age;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(name = "family_name", nullable = false, length = 100)
    private String familyName;

    @Email
    @NotBlank
    @Column(nullable = false, length = 150)
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String cin;

    @Column(name = "url_image", length = 500)
    private String urlImage;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_profile", length = 50)
    private EmployeProfile employeeProfile;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ChatConversation> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "recipient")
    @JsonIgnore
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<DocumentAccess> documentAccesses = new ArrayList<>();

    public User() {
    }

    public User(
            String name,
            String familyName,
            String email,
            String password,
            String cin,
            String urlImage,
            String phoneNumber,
            String address,
            Role role,
            EmployeProfile employeeProfile,
            Integer age,
            Gender gender,
            Department department
    ) {
        this.name = name;
        this.familyName = familyName;
        this.email = email;
        this.password = password;
        this.cin = cin;
        this.urlImage = urlImage;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.employeeProfile = employeeProfile;
        this.age = age;
        this.gender = gender;
        this.department = department;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public EmployeProfile getEmployeeProfile() { return employeeProfile; }
    public void setEmployeeProfile(EmployeProfile employeeProfile) { this.employeeProfile = employeeProfile; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public List<ChatConversation> getConversations() { return conversations; }
    public void setConversations(List<ChatConversation> conversations) { this.conversations = conversations; }

    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }

    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }

    public List<DocumentAccess> getDocumentAccesses() { return documentAccesses; }
    public void setDocumentAccesses(List<DocumentAccess> documentAccesses) { this.documentAccesses = documentAccesses; }
}