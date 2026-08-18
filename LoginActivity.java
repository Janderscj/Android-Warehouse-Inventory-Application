package com.zybooks.warehouse.ux.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zybooks.warehouse.R;
import com.zybooks.warehouse.data.database.InventoryDatabase;
import com.zybooks.warehouse.data.model.User;
import com.zybooks.warehouse.ux.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private Button newUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI references
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        newUserButton = findViewById(R.id.newUserButton);

        // Initial state
        updateLoginButtonState();

        // Listen for text changes to enable/disable button
        TextWatcher loginTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateLoginButtonState();
            }
        };

        usernameInput.addTextChangedListener(loginTextWatcher);
        passwordInput.addTextChangedListener(loginTextWatcher);

        // Login button handles authentication
        loginButton.setOnClickListener(v -> handleLogin());

        // New User button handles registration
        newUserButton.setOnClickListener(v -> handleRegister());
    }

    // Handle the login process
    private void handleLogin() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

        // Reset error messages
        usernameLayout.setError(null);
        passwordLayout.setError(null);

        // Basic validation for empty fields
        if (username.isEmpty()) {
            usernameLayout.setError("Enter a username");
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Enter a password");
            return;
        }

        // Access the database to find the user
        InventoryDatabase db = InventoryDatabase.getInstance(this);
        User user = db.userDao().findByUsername(username);

        // Verify credentials
        if (user != null && user.password.equals(password)) {
            // Navigate to the main inventory screen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Error message for security
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the registration process
    private void handleRegister() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

        // Reset error messages
        usernameLayout.setError(null);
        passwordLayout.setError(null);

        // Basic validation
        if (username.isEmpty()) {
            usernameLayout.setError("Username required for registration");
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password required for registration");
            return;
        }

        // Check if the username is already taken
        InventoryDatabase db = InventoryDatabase.getInstance(this);
        User existingUser = db.userDao().findByUsername(username);

        if (existingUser != null) {
            usernameLayout.setError("Username already exists");
        } else {
            // Save the new user to the database
            User newUser = new User(username, password);
            db.userDao().insert(newUser);

            Toast.makeText(this, "Account created", Toast.LENGTH_LONG).show();
            
            // Clear fields
            usernameInput.setText("");
            passwordInput.setText("");
        }
    }

    /**
     * Updates the enabled state of the login button based on input fields.
     */
    private void updateLoginButtonState() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
        loginButton.setEnabled(!username.isEmpty() && !password.isEmpty());
    }
}
