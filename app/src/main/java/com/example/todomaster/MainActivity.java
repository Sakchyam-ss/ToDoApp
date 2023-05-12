package com.example.todomaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.todomaster.adapter.OnTodoClickListener;
import com.example.todomaster.adapter.RecyclerViewAdapter;
import com.example.todomaster.data.Repository;
import com.example.todomaster.model.Priority;
import com.example.todomaster.model.SharedViewModel;
import com.example.todomaster.model.Task;
import com.example.todomaster.model.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements OnTodoClickListener {

    public static final String TAG = "ITEM";
    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private  int counter;
    BottomSheetFragment bottomSheetFragment;
    private SharedViewModel sharedViewModel;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        repository = new Repository(getApplication());
        counter = 0;


        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        taskViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication()).create(TaskViewModel.class);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        taskViewModel.getAllTasks().observe(this, tasks -> {
            recyclerViewAdapter = new RecyclerViewAdapter(tasks, this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
//            Task task = new Task("Test  " + counter++, Priority.MEDIUM, Calendar.getInstance().getTime(),Calendar.getInstance().getTime(),false);   //sample todo for testing insert
//
//            TaskViewModel.insert(task);

            showBottomSheetDialog();
        });
    }

    private void showBottomSheetDialog() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_delete_all) {
            // Handle "Delete All Tasks" option
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete All Tasks")
                    .setMessage("Are you sure you want to delete all tasks?")
                    .setPositiveButton("Yes", (dialog, which) -> repository.deleteAllTasks())
                    .setNegativeButton("No", null);

            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTodoClick(Task task) {
        sharedViewModel.selectedItem(task);
        sharedViewModel.setIsEdit(true);
        //Log.d("Click","onTodoClick: " + task.getTask());
        showBottomSheetDialog();
    }

//    @Override
//    public void onTodoRadioButtonClick(Task task) {
//        Log.d("Click","onRadioButton: " + task.getTask());
//        TaskViewModel.delete(task);
//        recyclerViewAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onTodoRadioButtonClick(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TaskViewModel.delete(task);
                recyclerViewAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}

