package tn.knowflowai.backend.DTO.Auth;

import java.util.List;

public class RegistrationOptionsResponse {

    private List<String> genders;

    private List<String> employeeProfiles;

    private List<DepartmentOption> departments;

    public RegistrationOptionsResponse(
            List<String> genders,
            List<String> employeeProfiles,
            List<DepartmentOption> departments
    ) {
        this.genders = genders;
        this.employeeProfiles = employeeProfiles;
        this.departments = departments;
    }

    public List<String> getGenders() {
        return genders;
    }

    public List<String> getEmployeeProfiles() {
        return employeeProfiles;
    }

    public List<DepartmentOption> getDepartments() {
        return departments;
    }
}