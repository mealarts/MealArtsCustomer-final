package com.mealarts.Helpers.OneSignal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mealarts.MapsActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
  // This fires when a notification is opened by tapping on it.

  private Context context;
  private String title;
  private String customKey;
  public OSNotificationOpenedHandler(Context context) {
    this.context = context;
  }

  @Override
  public void notificationOpened(OSNotificationOpenResult result) {
      OSNotificationAction.ActionType actionType = result.action.type;
      JSONObject data = result.notification.payload.additionalData;

      if (data != null) {
          customKey = data.optString("customkey", null);
          if (customKey != null)
              Log.i("OneSignalExample", "customkey set with value: " + customKey);
      }

      try {
          title = result.notification.payload.additionalData.getString("title");
      } catch (JSONException e) {
          e.printStackTrace();
      }

      if (actionType == OSNotificationAction.ActionType.ActionTaken)
          Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

      // The following can be used to open an Activity of your choice.
      // Replace - getApplicationContext() - with any Android Context.
      Intent intent = new Intent(context, MapsActivity.class);
      Pattern p = Pattern.compile("(?<=Order ID:=)\\d+");
      Matcher m = p.matcher(title);
      intent.putExtra("CheckoutId", m.group());
      intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);

       // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
       //   if you are calling startActivity above.
       /*
          <application ...>
            <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
          </application>
       */
  }
}