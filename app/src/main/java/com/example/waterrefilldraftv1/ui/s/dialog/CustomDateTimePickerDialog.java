package com.example.waterrefilldraftv1.ui.s.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;

import com.example.waterrefilldraftv1.R;

import java.util.Calendar;
import java.util.Locale;

public class CustomDateTimePickerDialog extends DialogFragment {

    private TextView tvMonthYear, tvSelectDay, tvSelectTime, tvSelectedTime;
    private ImageView ivClose, ivPrevMonth, ivNextMonth;
    private LinearLayout llSelectDay, llSelectTime;
    private GridLayout glCalendar;

    private Calendar currentCalendar;
    private Calendar selectedCalendar;
    private String selectedTimeString = "00:00 AM";
    private int selectedHour = 0;
    private int selectedMinute = 0;

    private OnDateTimeSelectedListener listener;
    private TextView selectedDateView; // Track selected date view

    public interface OnDateTimeSelectedListener {
        void onDateTimeSelected(String backendFormat, String displayFormat);
    }

    public static CustomDateTimePickerDialog newInstance(OnDateTimeSelectedListener listener) {
        CustomDateTimePickerDialog dialog = new CustomDateTimePickerDialog();
        dialog.listener = listener;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_datetime_picker, container, false);

        initViews(view);
        setupCalendar();
        setupClickListeners();
        updateCalendarView();

        return view;
    }

    private void initViews(View view) {
        tvMonthYear = view.findViewById(R.id.tv_month_year);
        tvSelectDay = view.findViewById(R.id.tv_select_day);
        tvSelectTime = view.findViewById(R.id.tv_select_time);
        tvSelectedTime = view.findViewById(R.id.tv_selected_time);
        ivClose = view.findViewById(R.id.iv_close);
        ivPrevMonth = view.findViewById(R.id.iv_prev_month);
        ivNextMonth = view.findViewById(R.id.iv_next_month);
        llSelectDay = view.findViewById(R.id.ll_select_day);
        llSelectTime = view.findViewById(R.id.ll_select_time);
        glCalendar = view.findViewById(R.id.gl_calendar);
    }

    private void setupCalendar() {
        currentCalendar = Calendar.getInstance();
        selectedCalendar = null;
    }

    private void setupClickListeners() {
        ivClose.setOnClickListener(v -> dismiss());

        ivPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarView();
        });

        ivNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarView();
        });

        llSelectTime.setOnClickListener(v -> showTimePicker());
    }

    private void updateCalendarView() {
        // Update month/year display
        String monthYear = String.format(Locale.getDefault(), "%s %d",
                getMonthName(currentCalendar.get(Calendar.MONTH)),
                currentCalendar.get(Calendar.YEAR));
        tvMonthYear.setText(monthYear);

        // Clear existing calendar views
        glCalendar.removeAllViews();

        // Create calendar grid
        createCalendarGrid();
    }

    private void createCalendarGrid() {
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar today = Calendar.getInstance();

        // Add empty views for days before the first day of month
        for (int i = 0; i < firstDayOfWeek; i++) {
            addEmptyDayView();
        }

        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            addDayView(day, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), today);
        }
    }

    private void addEmptyDayView() {
        TextView dayView = new TextView(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(40);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        dayView.setLayoutParams(params);
        glCalendar.addView(dayView);
    }

    private void addDayView(int day, int year, int month, Calendar today) {
        TextView dayView = new TextView(getContext());
        dayView.setText(String.valueOf(day));
        dayView.setTextSize(14);
        dayView.setGravity(android.view.Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(40);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        dayView.setLayoutParams(params);

        // Check if this day is in the past
        Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.set(year, month, day);

        boolean isPast = dayCalendar.before(today) && !isSameDay(dayCalendar, today);
        boolean isSelected = selectedCalendar != null &&
                selectedCalendar.get(Calendar.YEAR) == year &&
                selectedCalendar.get(Calendar.MONTH) == month &&
                selectedCalendar.get(Calendar.DAY_OF_MONTH) == day;

        if (isPast) {
            dayView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
            dayView.setEnabled(false);
        } else {
            dayView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            dayView.setEnabled(true);
            dayView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.calendar_day_background));

            if (isSelected) {
                dayView.setSelected(true);
                dayView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                selectedDateView = dayView;
            }

            dayView.setOnClickListener(v -> selectDate(dayView, day, year, month));
        }

        glCalendar.addView(dayView);
    }

    private void selectDate(TextView dayView, int day, int year, int month) {
        // Deselect previous selection
        if (selectedDateView != null) {
            selectedDateView.setSelected(false);
            selectedDateView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        }

        // Select new date
        dayView.setSelected(true);
        dayView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        selectedDateView = dayView;

        // Update selected calendar
        if (selectedCalendar == null) {
            selectedCalendar = Calendar.getInstance();
        }
        selectedCalendar.set(year, month, day);

        // Update select day text
        String dateText = String.format(Locale.getDefault(), "%s %d, %d",
                getMonthName(month), day, year);
        tvSelectDay.setText(dateText);

        checkIfComplete();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;

                    selectedTimeString = String.format(Locale.getDefault(), "%02d:%02d %s",
                            hourOfDay == 0 ? 12 : (hourOfDay > 12 ? hourOfDay - 12 : hourOfDay),
                            minute,
                            hourOfDay >= 12 ? "PM" : "AM");

                    tvSelectedTime.setText(selectedTimeString);
                    checkIfComplete();
                },
                selectedHour,
                selectedMinute,
                false // 12-hour format
        );

        timePickerDialog.show();
    }

    private void checkIfComplete() {
        if (selectedCalendar != null && !selectedTimeString.equals("00:00 AM")) {
            // Both date and time selected, notify listener
            String backendFormat = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d:00",
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH) + 1,
                    selectedCalendar.get(Calendar.DAY_OF_MONTH),
                    selectedHour,
                    selectedMinute);

            String displayFormat = String.format(Locale.getDefault(),
                    "%s %d, %d at %s",
                    getMonthName(selectedCalendar.get(Calendar.MONTH)),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH),
                    selectedCalendar.get(Calendar.YEAR),
                    selectedTimeString);

            if (listener != null) {
                listener.onDateTimeSelected(backendFormat, displayFormat);
            }

            dismiss();
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private String getMonthName(int month) {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months[month];
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}