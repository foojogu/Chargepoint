package com.westyorks.chargepoint.auth;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseAuthHelper {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AuthCallback callback;

    public interface AuthCallback {
        void onSuccess();
        void onError(String error);
    }

    public FirebaseAuthHelper(AuthCallback callback) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.callback = callback;
    }

    public void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create user role entry
                        FirebaseUser user = mAuth.getCurrentUser();
                        createUserRole(user.getUid(), false);
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    private void createUserRole(String userId, boolean isAdmin) {
        UserRole userRole = new UserRole(isAdmin);
        mDatabase.child("users").child(userId).setValue(userRole);
    }

    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void logoutUser() {
        mAuth.signOut();
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void checkUserRole(String userId, RoleCallback roleCallback) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserRole userRole = snapshot.getValue(UserRole.class);
                if (userRole != null) {
                    roleCallback.onRoleChecked(userRole.isAdmin());
                } else {
                    roleCallback.onError("User role not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                roleCallback.onError(error.getMessage());
            }
        });
    }

    public interface RoleCallback {
        void onRoleChecked(boolean isAdmin);
        void onError(String error);
    }

    public static class UserRole {
        private boolean admin;

        public UserRole() {
            // Required empty constructor for Firebase
        }

        public UserRole(boolean admin) {
            this.admin = admin;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }
    }
}