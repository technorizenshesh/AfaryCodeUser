package com.my.afarycode.OnlineShopping;

import static android.util.Log.println;

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
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.my.afarycode.OnlineShopping.chat.ChatAct;
import com.my.afarycode.OnlineShopping.constant.PreferenceConnector;
import com.my.afarycode.OnlineShopping.deeplink.PaymentByAnotherAct;
import com.my.afarycode.R;
import com.my.afarycode.Splash;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends
        FirebaseMessagingService {

    private static final String TAG = "";
    private static final String CHANNEL_ID = "my_channel_id";
    JSONObject notificationObj;
    String result="",key="",message="",type="";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
        Log.e("remoteMessage>>", "" + remoteMessage);

        if (!remoteMessage.getData().isEmpty()) {
            Log.e(TAG, "Message data payload : " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();



                notificationObj = new JSONObject(data.get("data"));
                result = notificationObj.getString("result");
                key = notificationObj.getString("key");
                type = notificationObj.getString("type");
                message = notificationObj.getString("message");
                Log.d("FCMService", "data====: " + notificationObj.toString());

                // Process the data as needed
                Log.d("FCMService", "Result: " + result);
                Log.d("FCMService", "Key: " + key);
                Log.d("FCMService", "Message: " + message);
                Log.d("FCMService", "Type: " + type);



                //  Log.e("check notification=====",remoteMessage.getData().toString());
        //  Log.e("check notification type=====",remoteMessage.getData().get("type"));

               sendNotification(key
                   , result,message,type,notificationObj);
        }


        if(key.equalsIgnoreCase("Order Accepted") || key.equalsIgnoreCase("Order Rejected")
        || key.equals("You have been logged out because you have logged in on another device")) {
            Intent intent1 = new Intent("check_status");
            Log.e("SendData=====", type);
            intent1.putExtra("status", key);
            intent1.putExtra("msg",message);
            sendBroadcast(intent1);
        }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendNotification(String title, String messageBody,String msg,String type,JSONObject remoteMessage) {
           try {
               Intent intent = null;

               if (type.equals("insert_chat")) {
                   intent = new Intent(getApplicationContext(), ChatAct.class)
                           .putExtra("id", PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.User_id, ""))
                           .putExtra("name", PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.User_name, ""))
                           .putExtra("img", PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.User_img, ""))
                           .putExtra("UserId", remoteMessage.getString("userid"))
                           .putExtra("UserName", remoteMessage.getString("user_name"))
                           .putExtra("UserImage", remoteMessage.getString("userimage"));
               } else if (type.equals("Accepted") || type.equals("reject")) {
                   intent = new Intent(getApplicationContext(), HomeActivity.class)
                           .putExtra("status", type);
               }
               else if (type.equals("InvoiceToOtherUser")) {
                   intent = new Intent(getApplicationContext(), PaymentByAnotherAct.class)
                           .putExtra("paymentInsertId", remoteMessage.getString("invoice_id"));
               }

               else if (type.equals("InvoiceToUser")) {
                   intent = new Intent(getApplicationContext(), HomeActivity.class)
                           .putExtra("status", "")
                           .putExtra("msg", "");
               }

               else if (msg.equalsIgnoreCase("Dear customer Your payment is successful, your order is sent to the seller for acceptance Please wait")
                       || msg.equalsIgnoreCase("Dear customer,Your refund request has been successfully transmitted. Within 4 working hours, the refund will only be made on the method used to pay. Note that the refund will be made on the method used for payment.Thank you for your comprehension")
                       || msg.equalsIgnoreCase("Your request has been sent to your correspondent")
                      /* || msg.contains("product now available")*/
                       || msg.contains("product out of stock")
                       || msg.contains("Dear customer, Your order has not been accepted by the seller.Reason: However, your Wallet has been credited with the amount of the order. You can place another order at any time and pay with your wallet.  For more information on using the wallet,")
               ) {

                   intent = new Intent(getApplicationContext(), HomeActivity.class)
                           .putExtra("status", "openPaymentDialog")
                           .putExtra("msg", msg);

               } else if (msg.equalsIgnoreCase("Dear Customer your order has just been delivered we hope to see you again soon on afarycode You can consult all of our services on this page   www……..thank you for choosing AfaryCode")) {
                   intent = new Intent(getApplicationContext(), HomeActivity.class)
                           .putExtra("status", "orderCompleteDialog")
                           .putExtra("order_id", remoteMessage.getString("order_id"))
                           .putExtra("msg", msg);

               } else if (msg.contains("product now available")) {
                   intent = new Intent(getApplicationContext(), ProductDetailAct.class)
                           .putExtra("product_id",  remoteMessage.getString("product_id"))
                           .putExtra("restaurant_id",  remoteMessage.getString("shop_id"))
                           .putExtra("productPrice",  remoteMessage.getString("product_price"));
               }
               else if (msg.contains("You have been logged out because you have logged in on another device")){
                   PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.LoginStatus, "false");
                   intent = new Intent(getApplicationContext(), Splash.class);
               }

               else if (type.contains("Statuschange")){
                   // PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.LoginStatus, "false");
                   intent = new Intent(getApplicationContext(), Splash.class)
                           .putExtra("title",title)
                           .putExtra("msg",msg)
                           .putExtra("from","notification");
               }

               else if (type.contains("Registration")){
                   // PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.LoginStatus, "false");
                   intent = new Intent(getApplicationContext(), Splash.class);
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

       /*     NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
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
                NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id), "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                noBuilder.setChannelId(getString(R.string.default_notification_channel_id));
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(not_nu, noBuilder.build());*/

               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               //  PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
               PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), not_nu, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
               Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
               NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                       .setSmallIcon(R.drawable.logo_well1)
                       .setContentTitle(title)
                       // .setContentText(msg)
                       .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                       .setAutoCancel(true)
                       .setContentIntent(contentIntent)
                       .setPriority(NotificationCompat.PRIORITY_HIGH);

               NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel name", NotificationManager.IMPORTANCE_HIGH);
                   notificationManager.createNotificationChannel(channel);
               }

               notificationManager.notify(0, notificationBuilder.build());
           }catch (Exception exception){
               exception.printStackTrace();
           }




        }



    public int generateRandom() {
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}
