package com.westyorks.chargepoint.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.westyorks.chargepoint.R;
import com.westyorks.chargepoint.auth.UserPermissionHelper;
import com.westyorks.chargepoint.auth.UserRoleManager;

public class UserManagementActivity extends AppCompatActivity {
    private EditText etUserId;
    private Spinner spinnerRole;
    private Button btnAssignRole;
    private UserRoleManager roleManager;
    private UserPermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        roleManager = new UserRoleManager();
        permissionHelper = new UserPermissionHelper();

        // Initialize views
        etUserId = findViewById(R.id.etUserId);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnAssignRole = findViewById(R.id.btnAssignRole);

        // Setup role spinner
        String[] roles = {
            UserRoleManager.ROLE_VIEWER,
            UserRoleManager.ROLE_EDITOR,
            UserRoleManager.ROLE_ADMIN
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Check if current user is admin
        permissionHelper.checkEditPermission(hasEditPermission -> {
            if (!hasEditPermission) {
                Toast.makeText(this, "Admin access required", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            btnAssignRole.setEnabled(true);
        });

        btnAssignRole.setOnClickListener(v -> assignRole());
    }

    private void assignRole() {
        String userId = etUserId.getText().toString().trim();
        String selectedRole = spinnerRole.getSelectedItem().toString();

        if (userId.isEmpty()) {
            Toast.makeText(this, "Please enter a user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        roleManager.assignRoleToUser(userId, selectedRole, new UserRoleManager.RoleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserManagementActivity.this,
                    "Role assigned successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(UserManagementActivity.this,
                    "Failed to assign role: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
