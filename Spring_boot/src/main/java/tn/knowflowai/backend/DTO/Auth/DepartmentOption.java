package tn.knowflowai.backend.DTO.Auth;

public class DepartmentOption {

    private Long id;

    private String name;

    public DepartmentOption(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}