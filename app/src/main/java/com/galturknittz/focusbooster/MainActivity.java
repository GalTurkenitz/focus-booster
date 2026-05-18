package com.galturknittz.focusbooster;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etTaskName;
    private LinearLayout tasksContainer;
    private ArrayList<String> taskList = new ArrayList<>();
    private ArrayList<Boolean> taskDone = new ArrayList<>();

    private static final int GO_TO_TASK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTaskName = findViewById(R.id.etTaskName);
        tasksContainer = findViewById(R.id.tasksContainer);

        findViewById(R.id.btnAddTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        findViewById(R.id.btnClearTasks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllTasks();
            }
        });

        loadTasks();
    }

    private void addTask() {
        String name = etTaskName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "נא להזין שם משימה", Toast.LENGTH_SHORT).show();
            return;
        }

        taskList.add(name);
        taskDone.add(false);

        SharedPreferences.Editor ed = getSharedPreferences("Tasks", MODE_PRIVATE).edit();
        ed.putString("lastTask", name);
        ed.apply();

        saveTasks();
        etTaskName.setText("");
        showTasks();
    }

    private void showTasks() {
        tasksContainer.removeAllViews();

        for (int i = 0; i < taskList.size(); i++) {
            final int pos = i;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 0, 0, 10);

            TextView tvName = new TextView(this);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f);
            tvName.setLayoutParams(tvParams);
            tvName.setText(taskList.get(i) + " - " + (taskDone.get(i) ? "הושלמה" : "בהמתנה"));
            tvName.setTextSize(15f);
            tvName.setPadding(12, 16, 12, 16);

            Button btnOpen = new Button(this);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            btnOpen.setLayoutParams(btnParams);
            btnOpen.setText("פתח");
            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTask(pos);
                }
            });

            row.addView(tvName);
            row.addView(btnOpen);
            tasksContainer.addView(row);
        }
    }

    private void openTask(int pos) {
        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        intent.putExtra("name", taskList.get(pos));
        intent.putExtra("pos", pos);
        intent.putExtra("done", taskDone.get(pos));
        startActivityForResult(intent, GO_TO_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GO_TO_TASK) {
                int pos = data.getIntExtra("pos", -1);
                boolean deleted = data.getBooleanExtra("deleted", false);
                boolean done = data.getBooleanExtra("done", false);

                if (pos >= 0 && pos < taskList.size()) {
                    if (deleted) {
                        taskList.remove(pos);
                        taskDone.remove(pos);
                    } else {
                        taskDone.set(pos, done);
                    }
                    saveTasks();
                    showTasks();
                }
            }
        }
    }

    private void clearAllTasks() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("נקה משימות");
        builder.setMessage("האם אתה בטוח שברצונך למחוק את כל המשימות?");
        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskList.clear();
                taskDone.clear();
                saveTasks();
                showTasks();
            }
        });
        builder.setNegativeButton("ביטול", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("אודות");
            builder.setMessage("Focus Booster\nפותח על ידי: גל טורקניץ\nאפליקציה לניהול משימות קצרות ושיפור הריכוז.");
            builder.setPositiveButton("סגור", null);
            builder.show();
            return true;
        }

        if (id == R.id.menu_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("עזרה");
            builder.setMessage("1. הזן שם משימה בשדה הטקסט\n2. לחץ 'הוסף משימה'\n3. לחץ על 'פתח' לביצוע המשימה\n4. סמן השלמה וצלם תמונה כראיה");
            builder.setPositiveButton("הבנתי", null);
            builder.show();
            return true;
        }

        if (id == R.id.menu_reset) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("איפוס נתונים");
            builder.setMessage("האם לאפס את כל הנתונים?");
            builder.setPositiveButton("אפס", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    taskList.clear();
                    taskDone.clear();
                    getSharedPreferences("Tasks", MODE_PRIVATE).edit().clear().apply();
                    showTasks();
                    Toast.makeText(MainActivity.this, "הנתונים אופסו", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("ביטול", null);
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveTasks() {
        StringBuilder names = new StringBuilder();
        StringBuilder done = new StringBuilder();

        for (int i = 0; i < taskList.size(); i++) {
            if (i > 0) {
                names.append("|||");
                done.append("|||");
            }
            names.append(taskList.get(i));
            done.append(taskDone.get(i));
        }

        SharedPreferences.Editor ed = getSharedPreferences("Tasks", MODE_PRIVATE).edit();
        ed.putString("taskList", names.toString());
        ed.putString("taskDone", done.toString());
        ed.apply();
    }

    private void loadTasks() {
        SharedPreferences sp = getSharedPreferences("Tasks", MODE_PRIVATE);
        String namesStr = sp.getString("taskList", "");
        String doneStr = sp.getString("taskDone", "");

        if (!namesStr.isEmpty()) {
            String[] names = namesStr.split("\\|\\|\\|");
            String[] doneArr = doneStr.split("\\|\\|\\|");

            for (int i = 0; i < names.length; i++) {
                taskList.add(names[i]);
                taskDone.add(i < doneArr.length && Boolean.parseBoolean(doneArr[i]));
            }
        }

        showTasks();
    }
}
