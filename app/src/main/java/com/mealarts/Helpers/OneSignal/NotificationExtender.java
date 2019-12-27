package com.mealarts.Helpers.OneSignal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mealarts.MapsActivity;
import com.mealarts.ProfileActivity;
import com.mealarts.R;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.squareup.picasso.Picasso;

public class NotificationExtender extends NotificationExtenderService {

      Bitmap largeIcon;
      PendingIntent pendingIntent;

      @Override
      protected boolean onNotificationProcessing(final OSNotificationReceivedResult receivedResult) {
            OverrideSettings overrideSettings = new OverrideSettings();
            overrideSettings.extender = new NotificationCompat.Extender() {
                  @Override
                  public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                        if (!receivedResult.restoring) {
                              // Only set custom channel if notification is not being restored
                              // Note: this would override any channels set through the OneSignal dashboard
                                 return builder.setChannelId("News");
                        }
                           return builder.setChannelId("News");
                     }
               };

               //picasso start

               try {
                     largeIcon= Picasso.get().load("http://mealarts.com/assets/img/logo.png").get();
               }
               catch(Exception e){
                     Log.d("onesignal error",e.toString());
               }

               //picasso end

               /* Do something with notification payload */
               //String title = receivedResult.payload.title;
               //String body = receivedResult.payload.body;
               String msg="", title="", orderid="", status="";
               try {
                     msg= receivedResult.payload.body;
                     title= receivedResult.payload.title;
                     orderid = receivedResult.payload.additionalData.getString("orderid");
                     status= receivedResult.payload.additionalData.getString("status");
                     //status= receivedResult.payload.additionalData.getString("status");
               }
               catch (Exception e){
                     Log.d("onesignal error",e.toString());
               }

               // Toast.makeText(this, ""+title+body, Toast.LENGTH_SHORT).show();
               Log.d("onesignal received data",title+"_"+status+"_"+msg+"_"+orderid);

               final NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
               final NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
               if(status.equals("paid_payment") || status.equals("Accepted")
                       || status.equals("Delivered") || status.equals("cancel")
                       || status.equals("not_delivered") || status.equals("rejected")){
                     Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
                     intent.setAction(ProfileActivity.NOTIFY_ACTIVITY_ACTION );
                     LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                     pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
               }else {
                  Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                  intent.putExtra("CheckoutId", orderid);
                  pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                  intent.setAction(MapsActivity.NOTIFY_ACTIVITY_ACTION );
                  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                  Log.e("checkoutid", intent.getStringExtra("CheckoutId"));
               }
               if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                     NotificationChannel nc=new NotificationChannel("1","channelnf",NotificationManager.IMPORTANCE_DEFAULT);
                     nc.setDescription("Description");
                     nc.enableVibration(true);
                     nm.createNotificationChannel(nc);

                     builder.setContentIntent(pendingIntent)
                             .setSmallIcon(R.mipmap.ic_launcher_round)
            //                    .setStyle(new NotificationCompat.BigPictureStyle().bigLargeIcon(largeIcon))
                             //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(largeIcon))
                             .setContentTitle(title)
                             .setContentText(msg)
                             .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                             .setSubText(status)
                             .setChannelId("1");

                     // nm.notify(1,builder.build());


               }
               else {
                     builder.setContentIntent(pendingIntent)
                             .setSmallIcon(R.mipmap.ic_launcher_round)
                             //.setLargeIcon(largeIcon)
                             .setContentTitle(title)
                             .setContentText(msg)
                             .setSubText(status);
                     // nm.notify(1,builder.build());
               }

               nm.notify(1,builder.build());
               return true;
         }
}