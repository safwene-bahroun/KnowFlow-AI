package tn.knowflowai.backend.DTO;

public class ProfileRequest {
    private String name;
    private String familyName;
    private String email;
    private String phoneNumber;
    private String address;
    private String urlImage;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}