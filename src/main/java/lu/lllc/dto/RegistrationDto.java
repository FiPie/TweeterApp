package lu.lllc.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegistrationDto {

	@Email(message="This doesn't seem like a correct email address")
	@NotBlank(message="This field cannot be empty or consist only of whitespace characters")
    private String email;
    @NotBlank(message="This field cannot be empty or consist only of whitespace characters")
    private String firstName;
    @NotBlank(message="This field cannot be empty or consist only of whitespace characters")
    private String lastName;
    @NotBlank(message="This field cannot be empty or consist only of whitespace characters")
    @Size(min = 1, max = 30, message = "Last name must be between 1 and 30 characters")
    private String password;
    @NotBlank(message="This field cannot be empty or consist only of whitespace characters")
    @Size(min = 1, max = 30, message = "Last name must be between 1 and 30 characters")
    
    private String confirmedPassword;


    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}