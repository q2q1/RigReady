package com.example.rig;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private Button btnChooseAvatar;
    private TextView tvEmail;

    private View groupView;
    private View groupEdit;
    private View groupEditButtons;

    private TextView tvNameValue;
    private TextView tvAddressValue;
    private TextView tvAboutValue;

    private EditText etName;
    private EditText etAddress;
    private EditText etAbout;

    private Button btnEditProfile;
    private Button btnCancelEdit;
    private Button btnSaveProfile;
    private Button btnLogout;

    private Uri selectedAvatarUri;
    private ActivityResultLauncher<String> imagePicker;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) return;
            selectedAvatarUri = uri;
            if (ivAvatar != null) {
                Glide.with(requireContext()).load(uri).centerCrop().into(ivAvatar);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAvatar = view.findViewById(R.id.ivAvatar);
        btnChooseAvatar = view.findViewById(R.id.btnChooseAvatar);
        tvEmail = view.findViewById(R.id.tvEmail);

        groupView = view.findViewById(R.id.groupView);
        groupEdit = view.findViewById(R.id.groupEdit);
        groupEditButtons = view.findViewById(R.id.groupEditButtons);

        tvNameValue = view.findViewById(R.id.tvNameValue);
        tvAddressValue = view.findViewById(R.id.tvAddressValue);
        tvAboutValue = view.findViewById(R.id.tvAboutValue);

        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etAbout = view.findViewById(R.id.etAbout);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(requireActivity(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (uid == null) return;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            tvEmail.setText("Email: " + (email != null ? email : ""));
        }

        loadProfile(uid);

        btnChooseAvatar.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnEditProfile.setOnClickListener(v -> setEditing(true));
        btnCancelEdit.setOnClickListener(v -> setEditing(false));
        btnSaveProfile.setOnClickListener(v -> saveProfile(uid));

        // Default: view mode
        setEditing(false);
    }

    private void loadProfile(String uid) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this::onProfileLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void onProfileLoaded(DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) {
            tvNameValue.setText("-");
            tvAddressValue.setText("-");
            tvAboutValue.setText("-");
            etName.setText("");
            etAddress.setText("");
            etAbout.setText("");
            return;
        }

        String name = doc.getString("name");
        String address = doc.getString("address");
        String about = doc.getString("about");
        String photoUrl = doc.getString("photoUrl");

        String safeName = name != null ? name : "";
        String safeAddress = address != null ? address : "";
        String safeAbout = about != null ? about : "";

        tvNameValue.setText(!TextUtils.isEmpty(safeName) ? safeName : "-");
        tvAddressValue.setText(!TextUtils.isEmpty(safeAddress) ? safeAddress : "-");
        tvAboutValue.setText(!TextUtils.isEmpty(safeAbout) ? safeAbout : "-");

        etName.setText(safeName);
        etAddress.setText(safeAddress);
        etAbout.setText(safeAbout);

        if (!TextUtils.isEmpty(photoUrl)) {
            Glide.with(requireContext()).load(photoUrl).centerCrop().into(ivAvatar);
        }
    }

    private void saveProfile(String uid) {
        String name = getText(etName);
        String address = getText(etAddress);
        String about = getText(etAbout);

        // MVP：不依赖 Firebase Storage，所以头像只做本地预览，不上传云端
        if (selectedAvatarUri != null) {
            Toast.makeText(requireContext(), "Avatar is local preview only (MVP does not upload).", Toast.LENGTH_SHORT).show();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("address", address);
        data.put("about", about);

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Switch back immediately for MVP feel
        tvNameValue.setText(!TextUtils.isEmpty(name) ? name : "-");
        tvAddressValue.setText(!TextUtils.isEmpty(address) ? address : "-");
        tvAboutValue.setText(!TextUtils.isEmpty(about) ? about : "-");
        setEditing(false);
    }

    private String getText(EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setEditing(boolean editing) {
        groupView.setVisibility(editing ? View.GONE : View.VISIBLE);
        groupEdit.setVisibility(editing ? View.VISIBLE : View.GONE);
        groupEditButtons.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnEditProfile.setVisibility(editing ? View.GONE : View.VISIBLE);

        if (editing) {
            // Sync edit fields with current view text (in case user enters Edit multiple times)
            String curName = tvNameValue.getText() != null ? tvNameValue.getText().toString() : "";
            String curAddress = tvAddressValue.getText() != null ? tvAddressValue.getText().toString() : "";
            String curAbout = tvAboutValue.getText() != null ? tvAboutValue.getText().toString() : "";
            etName.setText("-".equals(curName) ? "" : curName);
            etAddress.setText("-".equals(curAddress) ? "" : curAddress);
            etAbout.setText("-".equals(curAbout) ? "" : curAbout);
        }
    }
}

