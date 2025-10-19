package com.example.waterrefilldraftv1.Customer.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utility class for Date and Time picking
 * Creates beautiful, easy-to-use date and time pickers
 */
public class DateTimePickerUtil {

    /**
     * Interface for date time selection callback
     */
    public interface OnDateTimeSelectedListener {
        void onDateTimeSelected(String dateTime, String displayText);
    }

    /**
     * Show Date and Time picker for pickup scheduling
     *
     * @param context Activity context
     * @param textView TextView to update with selected date/time (optional)
     * @param listener Callback for when date/time is selected
     */
    public static void showPickupDateTimePicker(Context context, TextView textView, OnDateTimeSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();

        // First show date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (dateView, year, month, dayOfMonth) -> {
                    // After date is selected, show time picker
                    showTimePicker(context, year, month, dayOfMonth, textView, listener);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today (can't pick past dates)
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        // Set maximum date to 30 days from now (optional constraint)
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    /**
     * Show time picker after date is selected
     */
    private static void showTimePicker(Context context, int year, int month, int dayOfMonth,
                                       TextView textView, OnDateTimeSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (timeView, hourOfDay, minute) -> {
                    // Format the selected date and time
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute);

                    // Format for display (user-friendly)
                    String displayText = String.format(Locale.getDefault(),
                            "%s %d, %d at %02d:%02d %s",
                            getMonthName(month),
                            dayOfMonth,
                            year,
                            hourOfDay == 0 ? 12 : (hourOfDay > 12 ? hourOfDay - 12 : hourOfDay),
                            minute,
                            hourOfDay >= 12 ? "PM" : "AM"
                    );

                    // Format for backend (ISO format)
                    String backendFormat = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d %02d:%02d:00",
                            year, month + 1, dayOfMonth, hourOfDay, minute
                    );

                    // Update TextView if provided
                    if (textView != null) {
                        textView.setText(displayText);
                        textView.setTextColor(context.getResources().getColor(android.R.color.black));
                    }

                    // Call listener if provided
                    if (listener != null) {
                        listener.onDateTimeSelected(backendFormat, displayText);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // 12-hour format
        );

        timePickerDialog.show();
    }

    /**
     * Get month name from month number
     */
    private static String getMonthName(int month) {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months[month];
    }

    /**
     * Simple date picker (without time)
     */
    public static void showDatePicker(Context context, TextView textView, OnDateSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (dateView, year, month, dayOfMonth) -> {
                    String displayText = String.format(Locale.getDefault(),
                            "%s %d, %d",
                            getMonthName(month),
                            dayOfMonth,
                            year
                    );

                    String backendFormat = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth
                    );

                    if (textView != null) {
                        textView.setText(displayText);
                        textView.setTextColor(context.getResources().getColor(android.R.color.black));
                    }

                    if (listener != null) {
                        listener.onDateSelected(backendFormat, displayText);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    /**
     * Interface for date only selection
     */
    public interface OnDateSelectedListener {
        void onDateSelected(String date, String displayText);
    }
}