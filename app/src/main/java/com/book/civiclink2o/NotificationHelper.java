package com.book.civiclink2o;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "civiclink_reports_channel";
    private static final String CHANNEL_NAME = "Issue Reports";
    private static final String CHANNEL_DESC = "Notifications for submitted issue reports";

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        // Create the notification channel as soon as the helper is initialized
        createNotificationChannel();
    }

    // This method creates the notification channel (required for Android 8.0+)
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            // Set the channel to vibrate
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500}); // Vibrate pattern

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // This method builds and sends the actual notification
    public void sendReportSubmittedNotification() {

        // 1. Create the Intent that will open when the user taps the notification
        Intent intent = new Intent(context, HomeActivity.class);
        // This "extra" is the magic key. We tell HomeActivity to open the "My Reports" screen.
        intent.putExtra("NAVIGATE_TO", "MY_REPORTS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a unique request code for the PendingIntent.
        // This ensures that if the user has multiple notifications, each one works.
        int uniquePendingIntentRequestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, uniquePendingIntentRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 2. Build the notification itself
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check_circle) // We'll use our existing checkmark icon
                .setContentTitle("Report Submitted Successfully")
                .setContentText("Your civic issue report has been received. Tap to view your reports.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // This is the click action
                .setVibrate(new long[]{0, 500, 250, 500}) // This makes it vibrate
                .setAutoCancel(true); // The notification will disappear after it's tapped

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // 3. Send the notification (Permission check is included)
        // We must check for permission *before* calling notify().
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // If permission is not granted, we cannot send the notification.
                // The activity that called this (RaiseIssueActivity) is responsible for asking the user.
                return;
            }
        }

        // If we are on an older Android version OR if permission IS granted, send the notification.
        // We use a unique ID for every notification to ensure it always pops up and vibrates.
        int uniqueNotificationId = (int) System.currentTimeMillis();
        notificationManager.notify(uniqueNotificationId, builder.build());
    }
}

