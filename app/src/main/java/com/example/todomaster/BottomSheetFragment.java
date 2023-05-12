package com.example.todomaster;

import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.todomaster.model.Priority;
import com.example.todomaster.model.SharedViewModel;
import com.example.todomaster.model.Task;
import com.example.todomaster.model.TaskViewModel;
import com.example.todomaster.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private EditText enterText;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;

    public BottomSheetFragment(){

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.bottom_sheet, container, false);
                calendarGroup = view.findViewById(R.id.calendar_group);
                calendarView = view.findViewById(R.id.calendar_view);
                calendarButton = view.findViewById(R.id.today_calendar_button);
                enterText = view.findViewById(R.id.enter_todo_et);
                saveButton = view.findViewById(R.id.save_todo_button);
                priorityButton = view.findViewById(R.id.priority_todo_button);
                priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

                Chip todayChip = view.findViewById(R.id.today_chip);
                todayChip.setOnClickListener(this);
                Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
                tomorrowChip.setOnClickListener(this);
                Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
                nextWeekChip.setOnClickListener(this);


                return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(sharedViewModel.getSelectedItem().getValue() != null){
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterText.setText(task.getTask());
            Log.d("MY", "onViewCreated: " + task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        calendarButton.setOnClickListener(view12 -> {
            calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            Utils.hideSoftKeyboard(view12);
        });

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
             calendar.clear();
             calendar.set(year, month, dayOfMonth);
             dueDate = calendar.getTime();

            //Log.d("Cal","onViewCreated: ===> " + (month +1)+ ", dayOfMonth " + dayOfMonth);
        });

        priorityButton.setOnClickListener(view13 -> {
            Utils.hideSoftKeyboard(view13);
            priorityRadioGroup.setVisibility(priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

            priorityRadioGroup.setOnCheckedChangeListener(((radioGroup, checkedId) -> {
                if(priorityRadioGroup.getVisibility() == View.VISIBLE){
                    selectedButtonId = checkedId;
                    selectedRadioButton = view.findViewById(selectedButtonId);
                    if(selectedRadioButton.getId() == R.id.radioButton_high){
                        priority = Priority.HIGH;
                    } else if (selectedRadioButton.getId() == R.id.radioButton_med) {
                        priority = Priority.MEDIUM;
                    } else if (selectedRadioButton.getId() == R.id.radioButton_low) {
                        priority = Priority.LOW;
                    }
                }else{
                    priority = Priority.LOW;
                }
            }));
        });


        saveButton.setOnClickListener(view1 -> {
            String task = enterText.getText().toString().trim();
            if(!TextUtils.isEmpty(task) && dueDate != null && priority != null) {
                Task myTask = new Task(task, priority, dueDate, Calendar.getInstance().getTime(), false);
                if (isEdit) {
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();
                    updateTask.setTask(task);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    updateTask.setDueDate(dueDate);
                    TaskViewModel.update(updateTask);
                    sharedViewModel.setIsEdit(false);
                } else {
                    TaskViewModel.insert(myTask);
                }
                enterText.setText("");
                if (this.isVisible()){  //hides bottom sheet after updating or adding task
                    this.dismiss();
                }
            }else {
                Snackbar.make(saveButton, R.string.empty_field,Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.today_chip){
            //Sets the date for today button
            calendar.add(Calendar.DAY_OF_YEAR,0);
            dueDate = calendar.getTime();
            Log.d("TIME","onClick: " + dueDate.toString());
        }else if (id == R.id.tomorrow_chip){
            //Sets the date for tomorrow button
            calendar.add(Calendar.DAY_OF_YEAR,1);
            dueDate = calendar.getTime();
            Log.d("TIME","onClick: " + dueDate.toString());
        } else if (id == R.id.next_week_chip) {
            //Sets the date for nextweek button
            calendar.add(Calendar.DAY_OF_YEAR,7);
            dueDate = calendar.getTime();
            Log.d("TIME","onClick: " + dueDate.toString());
        }
    }
}