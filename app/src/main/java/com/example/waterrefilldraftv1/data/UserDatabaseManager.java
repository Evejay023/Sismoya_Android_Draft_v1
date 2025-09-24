package com.example.waterrefilldraftv1.data;


import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * UserDatabaseManager - Temporary database using SharedPreferences and Gson
 * This class handles user registration, login, and password reset functionality
 * Using JSON serialization to store user data locally until backend is implemented
 */
public class UserDatabaseManager {
    private static final String PREF_NAME = "user_database";
    private static final String USERS_KEY = "users_list";
    private static final String RESET_CODES_KEY = "reset_codes";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public UserDatabaseManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * User model class to represent user data
     */
    public static class User {
        public String firstName;
        public String lastName;
        public String email;
        public String contactNo;
        public String username;
        public String password;

        public User(String firstName, String lastName, String email, String contactNo, String username, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.contactNo = contactNo;
            this.username = username;
            this.password = password;
        }
    }

    /**
     * Reset Code model for password reset functionality
     */
    public static class ResetCode {
        public String email;
        public String code;
        public long timestamp;

        public ResetCode(String email, String code) {
            this.email = email;
            this.code = code;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Register a new user
     * @param user User object containing registration data
     * @return true if registration successful, false if user already exists
     */
    public boolean registerUser(User user) {
        List<User> users = getAllUsers();

        // Check if username or email already exists
        for (User existingUser : users) {
            if (existingUser.username.equals(user.username) || existingUser.email.equals(user.email)) {
                return false; // User already exists
            }
        }

        users.add(user);
        saveUsers(users);
        return true;
    }

    /**
     * Login user with username/email and password
     * @param usernameOrEmail Username or email
     * @param password Password
     * @return User object if login successful, null otherwise
     */
    public User loginUser(String usernameOrEmail, String password) {
        List<User> users = getAllUsers();

        for (User user : users) {
            if ((user.username.equals(usernameOrEmail) || user.email.equals(usernameOrEmail))
                    && user.password.equals(password)) {
                return user;
            }
        }
        return null; // Login failed
    }

    /**
     * Check if email exists in database
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.email.equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate and store reset code for password recovery
     * @param email User's email
     * @return Generated reset code (6 digits)
     */
    public String generateResetCode(String email) {
        // Generate 6-digit random code
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String resetCode = String.valueOf(code);

        // Store reset code
        List<ResetCode> resetCodes = getAllResetCodes();
        resetCodes.add(new ResetCode(email, resetCode));
        saveResetCodes(resetCodes);

        return resetCode;
    }

    /**
     * Verify reset code
     * @param email User's email
     * @param code Reset code to verify
     * @return true if code is valid and not expired, false otherwise
     */
    public boolean verifyResetCode(String email, String code) {
        List<ResetCode> resetCodes = getAllResetCodes();
        long currentTime = System.currentTimeMillis();

        for (ResetCode resetCode : resetCodes) {
            if (resetCode.email.equals(email) && resetCode.code.equals(code)) {
                // Check if code is not expired (valid for 10 minutes)
                if (currentTime - resetCode.timestamp < 10 * 60 * 1000) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Reset user password
     * @param email User's email
     * @param newPassword New password
     * @return true if password reset successful, false otherwise
     */
    public boolean resetPassword(String email, String newPassword) {
        List<User> users = getAllUsers();

        for (User user : users) {
            if (user.email.equals(email)) {
                user.password = newPassword;
                saveUsers(users);
                // Clear reset codes for this email
                clearResetCodes(email);
                return true;
            }
        }
        return false;
    }

    /**
     * Get all users from SharedPreferences
     */
    private List<User> getAllUsers() {
        String usersJson = sharedPreferences.getString(USERS_KEY, "[]");
        Type userListType = new TypeToken<List<User>>(){}.getType();
        return gson.fromJson(usersJson, userListType);
    }

    /**
     * Save users list to SharedPreferences
     */
    private void saveUsers(List<User> users) {
        String usersJson = gson.toJson(users);
        sharedPreferences.edit().putString(USERS_KEY, usersJson).apply();
    }

    /**
     * Get all reset codes from SharedPreferences
     */
    private List<ResetCode> getAllResetCodes() {
        String codesJson = sharedPreferences.getString(RESET_CODES_KEY, "[]");
        Type codeListType = new TypeToken<List<ResetCode>>(){}.getType();
        List<ResetCode> codes = gson.fromJson(codesJson, codeListType);
        return codes != null ? codes : new ArrayList<>();
    }

    /**
     * Save reset codes to SharedPreferences
     */
    private void saveResetCodes(List<ResetCode> resetCodes) {
        String codesJson = gson.toJson(resetCodes);
        sharedPreferences.edit().putString(RESET_CODES_KEY, codesJson).apply();
    }

    /**
     * Clear reset codes for specific email
     */
    private void clearResetCodes(String email) {
        List<ResetCode> resetCodes = getAllResetCodes();
        resetCodes.removeIf(code -> code.email.equals(email));
        saveResetCodes(resetCodes);
    }
}