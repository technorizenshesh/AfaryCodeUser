package com.my.afarycode.OnlineShopping;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.my.afarycode.OnlineShopping.chat.ChatAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends
        FirebaseMessagingService {

    private static final String TAG = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("remoteMessage>>", "" + remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload : " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            sendNotification(remoteMessage.getData().get("key")
                    , remoteMessage.getData().get("result"),remoteMessage.getData().get("message"),remoteMessage.getData().get("type"),remoteMessage);
        }


        if(remoteMessage.getData().get("key").equalsIgnoreCase("Order Accepted") || remoteMessage.getData().get("key").equalsIgnoreCase("Order Rejected")) {
            Intent intent1 = new Intent("check_status");
            Log.e("SendData=====", remoteMessage.getData().get("type"));
            intent1.putExtra("status", remoteMessage.getData().get("key"));
            intent1.putExtra("msg",remoteMessage.getData().get("message"));
            sendBroadcast(intent1);
        }



    }

    private void sendNotification(String title, String messageBody,String msg,String type,RemoteMessage remoteMessage) {

        Intent intent=null;

          if(type.equals("insert_chat")){
              intent  = new Intent(getApplicationContext(), ChatAct.class)
                      .putExtra("id",  PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.User_id,""))
                      .putExtra("name",  PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.User_name,""))
                      .putExtra("img",  PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.User_img,""))
                      .putExtra("UserId", remoteMessage.getData().get("userid") )
                      .putExtra("UserName", remoteMessage.getData().get("user_name") )
                      .putExtra("UserImage", remoteMessage.getData().get("userimage") );
          }
          else if(type.equals("Accepted") || type.equals("reject")){
              intent = new Intent(getApplicationContext(), HomeActivity.class)
                      .putExtra("status", type);
          }

          else if(msg.equalsIgnoreCase("Dear customer Your payment is successful, your order is sent to the seller for acceptance Please wait")
          || msg.equalsIgnoreCase("Dear customer,Your refund request has been successfully transmitted. Within 4 working hours, the refund will only be made on the method used to pay. Note that the refund will be made on the method used for payment.Thank you for your comprehension"))
          {
              intent = new Intent(getApplicationContext(), HomeActivity.class)
                      .putExtra("status", "openPaymentDialog")
                      .putExtra("msg",msg);

          }
          else {
              intent = new Intent(getApplicationContext(), HomeActivity.class)
                      .putExtra("status", "accept");
              if (messageBody != null) {
                  Bitmap bmp = null;
                  try {
                      InputStream in = new URL("http://placehold.it/120x120&text=image3").openStream();
                      bmp = BitmapFactory.decodeStream(in);
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          }
            final int not_nu = generateRandom();
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), not_nu, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, getString(R.string.channelId))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_well1))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setSmallIcon(R.drawable.logo_well1)
                    .setContentTitle(title)
                    //.setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(contentIntent)
                    // .setLargeIcon(bmp)
                    //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp))
                    .setVibrate(new long[]{1000, 1000});

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.channelId), "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                noBuilder.setChannelId(getString(R.string.channelId));
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(not_nu, noBuilder.build());
        }



    public int generateRandom() {
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}
