package server.ws;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserModel {
	
	@Id
	private String username;
	private String firstname;
	private String lastname;
	private String middlename;
	private String password;
	private String phone;
	private String email;
	
	public UserModel(){}
	public UserModel(String username, String firstname, String lastname, String middlename,String password, String phone,String email){
		
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.middlename = middlename;
		this.password = password;
		this.phone = phone;
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
	

}
