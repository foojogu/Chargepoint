package com.westyorks.chargepoint.auth;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class UserRoleManager {
    private static final String TAG = "UserRoleManager";
    private static final String USERS_PATH = "users";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_EDITOR = "editor";
    public static final String ROLE_VIEWER = "viewer";

    private final FirebaseDatabase database;
    private final FirebaseAuth auth;

    public interface RoleCallback {
        void onSuccess();
        void onError(String error);
    }

    public UserRoleManager() {
        this.database = FirebaseDatabase.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void assignRoleToCurrentUser(String role, RoleCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            String error = "No user logged in";
            Log.e(TAG, error);
            callback.onError(error);
            return;
        }

        Log.d(TAG, "Assigning role " + role + " to current user: " + currentUser.getUid());
        assignRoleToUser(currentUser.getUid(), role, callback);
    }

    public void assignRoleToUser(String uid, String role, RoleCallback callback) {
        if (!isValidRole(role)) {
            String error = "Invalid role: " + role;
            Log.e(TAG, error);
            callback.onError(error);
            return;
        }

        Log.d(TAG, "Assigning role " + role + " to user: " + uid);
        DatabaseReference userRef = database.getReference(USERS_PATH).child(uid);

        // Create a map for the user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("role", role);
        userData.put("createdAt", System.currentTimeMillis());
        
        // Use updateChildren instead of setValue for atomic updates
        userRef.updateChildren(userData)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Successfully assigned role " + role + " to user " + uid);
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                String error = "Failed to assign role: " + e.getMessage();
                Log.e(TAG, error);
                e.printStackTrace(); // Print full stack trace for debugging
                callback.onError(error);
            });
    }

    private boolean isValidRole(String role) {
        return ROLE_ADMIN.equals(role) || 
               ROLE_EDITOR.equals(role) || 
               ROLE_VIEWER.equals(role);
    }
}
