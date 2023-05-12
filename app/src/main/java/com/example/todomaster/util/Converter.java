package com.example.todomaster.util;

import androidx.room.TypeConverter;

import com.example.todomaster.model.Priority;

import java.util.Date;

public class Converter {

    @TypeConverter
    public static Date fromTimestamp(Long value){
        return value == null ? null : new Date(value);  // if value is null returns null if not returns a date
    }

    @TypeConverter
    public static Long datetoTimestamp(Date date){
        return date == null ? null : date.getTime();  // if value is null returns null if not returns a date
    }

    @TypeConverter
    public static String fromPriority(Priority priority){
        return priority == null ? null : priority.name();   //passing prority and converting it to string
    }

    @TypeConverter
    public static Priority toPriority(String priority){
        return priority == null ? null : Priority.valueOf(priority);
    }
}
