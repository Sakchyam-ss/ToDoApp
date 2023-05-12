package com.example.todomaster.adapter;

import com.example.todomaster.model.Task;

public interface OnTodoClickListener {
    void onTodoClick(Task task);
    void onTodoRadioButtonClick(Task task);

}
