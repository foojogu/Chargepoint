package com.westyorks.chargepoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.westyorks.chargepoint.auth.FirebaseAuthHelper;
import com.westyorks.chargepoint.auth.UserRoleManager;

public class RegisterActivity extends AppCompatActivity implements FirebaseAuthHelper.AuthCallback {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etAdminCode;
    private Button btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuthHelper authHelper;
    private UserRoleManager roleManager;

    // Admin code for creating admin accounts - in production, this should be stored securely
    private static final String ADMIN_REGISTRATION_CODE = "ADMIN123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authHelper = new FirebaseAuthHelper(this);
        roleManager = new UserRoleManager();

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etAdminCode = findViewById(R.id.etAdminCode);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String adminCode = etAdminCode.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);
        authHelper.registerUser(email, password);
        
        // Store if this should be an admin account
        getSharedPreferences("Auth", MODE_PRIVATE)
            .edit()
            .putBoolean("PendingAdminRegistration", ADMIN_REGISTRATION_CODE.equals(adminCode))
            .apply();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }

    @Override
    public void onSuccess() {
        // Check if this should be an admin account
        boolean isAdmin = getSharedPreferences("Auth", MODE_PRIVATE)
            .getBoolean("PendingAdminRegistration", false);
        
        // Clear the pending admin flag
        getSharedPreferences("Auth", MODE_PRIVATE)
            .edit()
            .remove("PendingAdminRegistration")
            .apply();

        // Assign appropriate role
        String role = isAdmin ? UserRoleManager.ROLE_ADMIN : UserRoleManager.ROLE_VIEWER;
        roleManager.assignRoleToCurrentUser(role, new UserRoleManager.RoleCallback() {
            @Override
            public void onSuccess() {
                showProgress(false);
                Toast.makeText(RegisterActivity.this, 
                    "Registration successful" + (isAdmin ? " (Admin)" : ""), 
                    Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                showProgress(false);
                Toast.makeText(RegisterActivity.this, 
                    "Registration successful but failed to set role: " + error, 
                    Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onError(String error) {
        showProgress(false);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
} 