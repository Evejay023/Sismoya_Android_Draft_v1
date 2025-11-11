package com.example.waterrefilldraftv1.Riders.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static String formatDateTimeForDisplay(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return "";

        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = serverFormat.parse(dateTimeString);

            if (date != null) {
                Calendar today = Calendar.getInstance();
                Calendar targetDate = Calendar.getInstance();
                targetDate.setTime(date);

                // Reset time portion for date comparison
                Calendar todayReset = (Calendar) today.clone();
                todayReset.set(Calendar.HOUR_OF_DAY, 0);
                todayReset.set(Calendar.MINUTE, 0);
                todayReset.set(Calendar.SECOND, 0);
                todayReset.set(Calendar.MILLISECOND, 0);

                Calendar targetReset = (Calendar) targetDate.clone();
                targetReset.set(Calendar.HOUR_OF_DAY, 0);
                targetReset.set(Calendar.MINUTE, 0);
                targetReset.set(Calendar.SECOND, 0);
                targetReset.set(Calendar.MILLISECOND, 0);

                long diff = targetReset.getTimeInMillis() - todayReset.getTimeInMillis();
                long daysDiff = TimeUnit.MILLISECONDS.toDays(diff);

                String dayPrefix;
                if (daysDiff == 0) {
                    dayPrefix = "Today";
                } else if (daysDiff == 1) {
                    dayPrefix = "Tomorrow";
                } else {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                    dayPrefix = dayFormat.format(date);
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                String timeString = timeFormat.format(date);

                return dayPrefix + ", " + timeString;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateTimeString;
    }

    public static boolean isPickupScheduledForToday(String pickupDatetime) {
        if (pickupDatetime == null || pickupDatetime.isEmpty()) return false;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date pickupDate = format.parse(pickupDatetime);

            Calendar pickupCal = Calendar.getInstance();
            pickupCal.setTime(pickupDate);

            Calendar today = Calendar.getInstance();

            return pickupCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    pickupCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    pickupCal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPickupScheduledForTomorrow(String pickupDatetime) {
        if (pickupDatetime == null || pickupDatetime.isEmpty()) return false;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date pickupDate = format.parse(pickupDatetime);

            Calendar pickupCal = Calendar.getInstance();
            pickupCal.setTime(pickupDate);

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);

            return pickupCal.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                    pickupCal.get(Calendar.MONTH) == tomorrow.get(Calendar.MONTH) &&
                    pickupCal.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ MODIFIED: Allow pickup for ANY today's order (remove time restriction)
    public static boolean isPickupInPast(String pickupDatetime) {
        if (pickupDatetime == null || pickupDatetime.isEmpty()) return false;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date pickupDate = format.parse(pickupDatetime);
            Date now = new Date();

            // For today's orders, we don't care if the time has passed
            // Only check if it's actually in the past (like yesterday)
            Calendar pickupCal = Calendar.getInstance();
            pickupCal.setTime(pickupDate);

            Calendar today = Calendar.getInstance();

            // If it's not today, then check if it's in the past
            if (pickupCal.get(Calendar.YEAR) != today.get(Calendar.YEAR) ||
                    pickupCal.get(Calendar.MONTH) != today.get(Calendar.MONTH) ||
                    pickupCal.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH)) {
                return pickupDate.before(now);
            }

            // If it's today, always return false (allow pickup regardless of time)
            return false;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NEW: Simple method to check if order is from yesterday or earlier
    public static boolean isHistoricalOrder(String pickupDatetime) {
        if (pickupDatetime == null || pickupDatetime.isEmpty()) return false;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date pickupDate = format.parse(pickupDatetime);
            Date now = new Date();

            Calendar pickupCal = Calendar.getInstance();
            pickupCal.setTime(pickupDate);

            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_MONTH, -1);

            // Return true if pickup date is before today
            return pickupCal.get(Calendar.YEAR) < today.get(Calendar.YEAR) ||
                    (pickupCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                            pickupCal.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR));

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPickupScheduledForTodayOrFuture(String pickupDatetime) {
        if (pickupDatetime == null || pickupDatetime.isEmpty()) return false;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date pickupDate = format.parse(pickupDatetime);
            Date now = new Date();

            return !pickupDate.before(now);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ ADD THIS MISSING METHOD
    public static String getRelativeTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = format.parse(dateTimeString);
            Date now = new Date();

            long diff = date.getTime() - now.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;

            if (hours > 0) {
                return String.format(Locale.getDefault(), "in %dh %dm", hours, minutes);
            } else if (minutes > 0) {
                return String.format(Locale.getDefault(), "in %dm", minutes);
            } else {
                return "now";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getRelativeTimeIfUrgent(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = format.parse(dateTimeString);
            Date now = new Date();

            long diff = date.getTime() - now.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;

            if (hours < 2 && hours >= 0) {
                if (hours > 0) {
                    return String.format(Locale.getDefault(), "in %dh %dm", hours, minutes);
                } else if (minutes > 0) {
                    return String.format(Locale.getDefault(), "in %dm", minutes);
                } else {
                    return "now";
                }
            }

            return "";

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date parseDateTimeForSorting(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return null;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return format.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}