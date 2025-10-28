package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {

    EditText contactNameInput, contactNumberInput;
    Button btnSave, btnCancel;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        contactNameInput = findViewById(R.id.contactNameInput);
        contactNumberInput = findViewById(R.id.contactNumberInput);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSave.setOnClickListener(v -> saveContact());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveContact() {
        String name = contactNameInput.getText().toString().trim();
        String number = contactNumberInput.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid(); // each user's unique ID

        Map<String, Object> contact = new HashMap<>();
        contact.put("name", name);
        contact.put("number", number);

        db.collection("users")
                .document(userId)
                .collection("contacts")
                .add(contact)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Contact saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
