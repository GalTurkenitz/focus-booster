package com.galturknittz.focusbooster;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TaskActivity extends AppCompatActivity {

    private TextView tvTaskName;
    private Switch switchDone;
    private ImageView ivPhoto;

    private String taskName;
    private int taskPos;
    private boolean isDone;

    private static final int CAMERA_REQUEST = 100;
    private static final int CAMERA_PERMISSION_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        tvTaskName = findViewById(R.id.tvTaskName);
        switchDone = findViewById(R.id.switchCompleted);
        ivPhoto = findViewById(R.id.ivTaskPhoto);

        taskName = getIntent().getStringExtra("name");
        taskPos = getIntent().getIntExtra("pos", -1);
        isDone = getIntent().getBooleanExtra("done", false);

        tvTaskName.setText(taskName);
        switchDone.setChecked(isDone);

        switchDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDone = isChecked;
            }
        });

        findViewById(R.id.btnFinishTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinishDialog();
            }
        });

        findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        findViewById(R.id.btnDeleteTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(false);
            }
        });
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "נדרשת הרשאת מצלמה", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFinishDialog() {
        if (!switchDone.isChecked()) {
            Toast.makeText(this, "סמן את המשימה כהושלמה תחילה", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("כל הכבוד!");
        builder.setMessage("סיימת את המשימה:\n\"" + taskName + "\"\n\nעבודה מצוינת!");
        builder.setPositiveButton("תודה", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goBack(false);
            }
        });
        builder.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("מחיקת משימה");
        builder.setMessage("האם אתה בטוח שברצונך למחוק את המשימה \"" + taskName + "\"?");
        builder.setPositiveButton("מחק", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goBack(true);
            }
        });
        builder.setNegativeButton("ביטול", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ivPhoto.setVisibility(View.VISIBLE);
            ivPhoto.setImageBitmap(bitmap);
        }
    }

    private void goBack(boolean deleted) {
        Intent result = new Intent();
        result.putExtra("pos", taskPos);
        result.putExtra("deleted", deleted);
        result.putExtra("done", isDone);
        setResult(RESULT_OK, result);
        finish();
    }
}
