package com.westyorks.chargepoint.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class UserPermissionHelper {
    private static final String USERS_PATH = "users";
    private static final String ROLE_KEY = "role";
    private static final String ADMIN_ROLE = "admin";
    private static final String EDITOR_ROLE = "editor";

    private final FirebaseDatabase database;
    private final FirebaseAuth auth;

    public interface PermissionCallback {
        void onPermissionResult(boolean hasEditPermission);
    }

    public UserPermissionHelper() {
        this.database = FirebaseDatabase.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void checkEditPermission(PermissionCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onPermissionResult(false);
            return;
        }

        DatabaseReference userRef = database.getReference(USERS_PATH)
            .child(currentUser.getUid())
            .child(ROLE_KEY);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                boolean hasEditPermission = ADMIN_ROLE.equals(role) || EDITOR_ROLE.equals(role);
                callback.onPermissionResult(hasEditPermission);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onPermissionResult(false);
            }
        });
    }
}
