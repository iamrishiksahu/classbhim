package com.sepl.classbhim.classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sepl.classbhim.MainActivity;
import com.sepl.classbhim.R;
import com.sepl.classbhim.activities.NotificationsActivity;
import com.sepl.classbhim.classes.adapters.NotificationAdapter;
import com.sepl.classbhim.classes.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.sepl.classbhim.classes.Variables.IS_NEW_TOKEN_GENERATED;
import static com.sepl.classbhim.classes.Variables.fcmToken;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String body, title,  dataTitle, dataBody, stockName,  timeFrame, exchangeName;
    private String  isBannerUpdated="N";


    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification() != null){

            // data from normal notification
            body = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();

            //trigger Notification
            triggerNotification(MyFirebaseMessagingService.this,title,body, Variables.GENERAL_NOTIFICATION_CHANNEL_ID);

        }

        if (remoteMessage.getData() != null){

            if (remoteMessage.getData().containsKey("title")){
                dataTitle = remoteMessage.getData().get("title");
            }
            if (remoteMessage.getData().containsKey("body")){
                dataBody = remoteMessage.getData().get("body");
            }
            if (remoteMessage.getData().containsKey("isBannerUpdated")){
                isBannerUpdated = remoteMessage.getData().get("isBannerUpdated");
            }

            if (isBannerUpdated.equals("N")){
                //its a normal notification
                triggerNotification(MyFirebaseMessagingService.this,dataTitle,dataBody, Variables.GENERAL_NOTIFICATION_CHANNEL_ID);

            }else {
                // this is not a notification, it is notifying the app to update the banners.
                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".banners",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("isBannerUpdated", true);
                myEdit.commit();

            }


        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        IS_NEW_TOKEN_GENERATED = true;
        fcmToken = s;
    }

//    private void captureNotification(String title, String body, int classNum){
//        LocalDatabase db = LocalDatabase.getDbInstance(getApplicationContext());
//        NotificationModel model = new NotificationModel();
//        model.title = title;
//        model.body = body;
//        model.classNumber = classNum;
//        db.notificationDao().insertNotification(model);
//    }

    private void triggerNotification(Context context, String title, String body, String channelID){

        Random rand = new Random();
        int id = rand.nextInt(9999);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(this, MainActivity.class );
        notificationIntent.putExtra( "msg_title" , title);
        notificationIntent.putExtra( "msg_body" , body );
        notificationIntent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
        notificationIntent.setAction(Intent. ACTION_MAIN ) ;
        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;

        PendingIntent resultIntent = PendingIntent. getActivity (this, id, notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT) ;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        builder.setSmallIcon(R.drawable.logo_512);
        builder.setContentTitle(title);
        builder.setAutoCancel(true);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setChannelId(channelID);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(defaultSoundUri);
        builder.setContentIntent(resultIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id , builder.build());
    }
}
