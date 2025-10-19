package com.example.waterrefilldraftv1.Customer.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("contact_no")
    private String contactNo;

    @SerializedName("username")
    private String username;

    @SerializedName("role")
    private String role;

    @SerializedName("password")
    private String password;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("status")
    private String status;

    @SerializedName("reset_code")
    private String resetCode;

    @SerializedName("reset_code_exp")
    private String resetCodeExp;

    @SerializedName("reset_verified")
    private int resetVerified;

    // =================== CONSTRUCTORS ===================

    // ✅ Constructor for registration (used in RegisterActivity)
    public User(String firstName, String lastName, String email, String contactNo, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNo = contactNo;

        // ✅ Auto-generate username (max 20 characters, no spaces)
        String baseUsername = (firstName + lastName).toLowerCase().replaceAll("\\s+", "");
        this.username = baseUsername.length() > 20 ? baseUsername.substring(0, 20) : baseUsername;

        this.password = password;
        this.role = "customer"; // Default role
    }

    // ✅ Full constructor for complete user info (API responses)
    public User(int userId, String firstName, String lastName, String email,
                String contactNo, String username, String role,
                String createdAt, String updatedAt, String status,
                String resetCode, String resetCodeExp, int resetVerified) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNo = contactNo;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.resetCode = resetCode;
        this.resetCodeExp = resetCodeExp;
        this.resetVerified = resetVerified;
    }

    // =================== GETTERS ===================
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getContactNo() { return contactNo; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getStatus() { return status; }
    public String getResetCode() { return resetCode; }
    public String getResetCodeExp() { return resetCodeExp; }
    public int getResetVerified() { return resetVerified; }
}
