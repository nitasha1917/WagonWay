package com.database.controllers;

import com.database.models.User;
import com.database.models.ApiResponse;
import com.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            // Remove password from response
            createdUser.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("User created successfully", createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Failed to create user: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("User not found"));
            }
            // Remove password from response
            user.get().setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user.get()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> user = userService.findByEmail(email);
            if (user.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("User not found"));
            }
            // Remove password from response
            user.get().setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user.get()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            // Remove passwords from response
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<User>>> getActiveUsers() {
        try {
            List<User> users = userService.getActiveUsers();
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(ApiResponse.success("Active users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve active users: " + e.getMessage()));
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(ApiResponse.success("Users by role retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve users by role: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            updatedUser.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @PathVariable String id,
            @RequestBody PasswordUpdateRequest request) {
        try {
            userService.updatePassword(id, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Failed to update password: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable String id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Failed to deactivate user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam String query) {
        try {
            List<User> users = userService.searchUsers(query);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStats>> getUserStats() {
        try {
            UserStats stats = new UserStats();
            stats.setTotalUsers(userService.getTotalUsers());
            stats.setActiveUsers(userService.getActiveUsersCount());
            stats.setAdmins(userService.getAdminsCount());

            return ResponseEntity.ok(ApiResponse.success("User statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve user statistics: " + e.getMessage()));
        }
    }

    // Inner classes for request/response
    public static class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class UserStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long admins;

        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }

        public Long getAdmins() { return admins; }
        public void setAdmins(Long admins) { this.admins = admins; }
    }
}
