package com.galturknittz.focusbooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageView picture;
    private EditText etUser;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        picture = findViewById(R.id.ivProfile);
        etUser = findViewById(R.id.etUserName);

        SharedPreferences sp = getSharedPreferences("Data", MODE_PRIVATE);
        etUser.setText(sp.getString("user", ""));

findViewById(R.id.btnPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 202);
            }
        });

        findViewById(R.id.btnSaveSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = getSharedPreferences("Data", MODE_PRIVATE).edit();
                ed.putString("user", etUser.getText().toString());
                ed.apply();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("image_bitmap", bitmap);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 202 && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            picture.setImageBitmap(bitmap);
        }
    }
}
