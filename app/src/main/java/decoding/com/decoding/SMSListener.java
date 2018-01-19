package decoding.com.decoding;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Moses Gitau on 6/6/17.
 */

public class SMSListener extends BroadcastReceiver {

    private String phoneNumber = "0738954878";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            Log.d("SMSListener", intent.getAction().toString());
        }
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            String phoneNumbers = PreferenceManager.getDefaultSharedPreferences(
                    context).getString("phone_entries", "");
            StringTokenizer tokenizer = new StringTokenizer(phoneNumbers, ",");
            Set<String> phoneEnrties = new HashSet<String>();
            while (tokenizer.hasMoreTokens()) {
                phoneEnrties.add(tokenizer.nextToken().trim());
            }
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            String title = "";

            double lat = 0;
            double lng = 0;
            String speed = "0";
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                Pattern pattern = Pattern.compile("http://maps.google.com/maps\\?q=(\\w)(\\d+.\\d+),(\\w)(\\d+.\\d+)");
                Log.d("SMSListener", messages[i].getDisplayMessageBody().trim().split("\n")[0]);
                Matcher matcher = pattern.matcher(messages[i].getDisplayMessageBody().trim().split("\n")[0].trim());
                Log.d("SMSListener", "Matcher: " + matcher.matches());
                title += messages[i];
                if (matcher.matches()) {
                    Double latitude = matcher.group(1).equalsIgnoreCase("S") ? -Double.parseDouble(matcher.group(2)) : Double.parseDouble(matcher.group(2));
                    Double longitude = Double.parseDouble(matcher.group(4));
                    MainActivity.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                    MainActivity.marker.setPosition(new LatLng(latitude, longitude));
                    Log.d("SMSListener", "Lat: " + latitude + " Longitude: " + longitude);
                    lat = latitude;
                    lng = longitude;
                } else {
                    if (messages[i].getDisplayMessageBody().contains("Speed")) {
                        Pattern pattern1 = Pattern.compile(".*Speed:(\\d+.\\d+km/h).*");
                        Matcher matcher1 = pattern1.matcher(messages[i].getDisplayMessageBody().trim());
                        if (matcher1.matches()) {
                            if (MainActivity.speedTextView != null) {
                                MainActivity.speedTextView.setText(matcher1.group(1));
                                speed = matcher1.group(1);
                            }
                        }
                    }
                }
            }
            Mahali mahali = new Mahali();
            List<Double> list = new ArrayList<>();
            list.add(lat);
            list.add(lng);
            mahali.setPosition(list);

            Log.d("Moses", mahali.toString());
        }
        sendSMS(context, context.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE).getInt(MainActivity.PREFERRED_SIM, 0), phoneNumber, null, "smslink654321", null, null);
    }

    public static boolean sendSMS(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        String name;

        try {
            if (simID == 0) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 1) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, toNum, centerNum, smsText, sentIntent, deliveryIntent);
            } else {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, ctx.getPackageName(), toNum, centerNum, smsText, sentIntent, deliveryIntent);
            }

            return true;
        } catch (ClassNotFoundException e) {
            Log.e("apipas", "ClassNotFoundException:" + e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("apipas", "NoSuchMethodException:" + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("apipas", "InvocationTargetException:" + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("apipas", "IllegalAccessException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("apipas", "Exception:" + e.getMessage());
        }
        return false;
    }
}
