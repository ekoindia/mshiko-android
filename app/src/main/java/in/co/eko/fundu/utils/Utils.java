package in.co.eko.fundu.utils;
/*
 * Created by Bhuvnesh
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import in.co.eko.fundu.BuildConfig;
import in.co.eko.fundu.FunduApplication;
import in.co.eko.fundu.R;
import in.co.eko.fundu.activities.Feedback;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.models.FunduUser;

public class Utils {
    private static String TAG = Utils.class.getName();
    private static AppPreferences pref;
    int digits = 10;
    // the char + is always at first.
    int plus_sign_pos = 0;

    public static int getDrawerWidth(Resources res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            if (res.getConfiguration().smallestScreenWidthDp >= 600 || res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // device is a tablet
                return (int) (320 * res.getDisplayMetrics().density);
            } else {
                return (int) (res.getDisplayMetrics().widthPixels - (56 * res.getDisplayMetrics().density));
            }
        } else { // for devices without smallestScreenWidthDp reference calculate if device screen is over 600 dp
            if ((res.getDisplayMetrics().widthPixels / res.getDisplayMetrics().density) >= 600 || res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                return (int) (320 * res.getDisplayMetrics().density);
            else
                return (int) (res.getDisplayMetrics().widthPixels - (56 * res.getDisplayMetrics().density));
        }
    }

    public static boolean isTablet(Resources res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return res.getConfiguration().smallestScreenWidthDp >= 600;
        } else { // for devices without smallestScreenWidthDp reference calculate if device screen is over 600
            return (res.getDisplayMetrics().widthPixels / res.getDisplayMetrics().density) >= 600;

        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected()))
            //showShortToast(context, "Sorry! Not connected to internet");
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getAddress(Context context, Double latitude, Double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        String addrs = "";
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String c = addresses.get(0).getAddressLine(1);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
                addrs = address + ", " + c + ", " + city + ", " + state + ", " + country + ".";
                if (address.contains(", null"))
                    addrs = addrs.replace(", null", "");
                else if (address.contains("null"))
                    addrs = addrs.replace("null", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
            addrs = "";
        }


        return addrs;

    }

    public static final String getCurrency(Context context) {
        String currencySymbol = context.getString(R.string.ruppee_symbol);
        if (FunduUser.getCountryShortName().equalsIgnoreCase("KEN")) {
            currencySymbol = "Shs.";
        }
        return currencySymbol;
    }

    public static String IMEI(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String  imei_number = null;
        //String number = tm.getLine1Number();
        try {
            imei_number = tm.getDeviceId();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (imei_number==null)
                imei_number = "352356709353798";
        }
        if (imei_number==null)
            imei_number = "352356709353798";
        return imei_number;
    }

    public static String Sim_number(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String sim_number = null;
        try {
            sim_number = tm.getSimSerialNumber();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (sim_number == null)
                sim_number = IMEI(context) + "00000";
//                sim_number = "89910430021308497698";
        }
        if (sim_number.length() == 19)
            sim_number += 1;
        if (sim_number == null)
            sim_number = IMEI(context) + "00000";
//            sim_number = "89910430021308497698";
        return sim_number;
    }

    public static int getScreenHeight(Activity act) {
        int height = 0;
        Display display = act.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        } else {
            height = display.getHeight();  // deprecated
        }
        return height;
    }

    public static Point getUserPhotoSize(Resources res) {
        int size = (int) (64 * res.getDisplayMetrics().density);

        return new Point(size, size);
    }

    public static Point getBackgroundSize(Resources res) {
        int width = getDrawerWidth(res);

        int height = (9 * width) / 16;

        return new Point(width, height);
    }

    public static Bitmap getCroppedBitmapDrawable(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static Bitmap resizeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);

    }

    public static int calculateSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void recycleDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmapDrawable.getBitmap().recycle();
        }
    }

    public static boolean isRTL() {
        Locale defLocale = Locale.getDefault();
        final int directionality = Character.getDirectionality(defLocale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static void setAlpha(View v, float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            v.setAlpha(alpha);
        } else {
            AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {

        }
    }

    public static void toggleSoftKeyboard(Activity activity) {
        try {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.
                        getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        } catch (NullPointerException e) {

        }
    }

    public static void printKeyHash(Context context) {
        try {
            PackageInfo INFO = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : INFO.signatures) {
                MessageDigest _md = MessageDigest.getInstance("SHA");
                _md.update(signature.toByteArray());
                Fog.d("KeyHash: =>", Base64.encodeToString(_md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String appendCountryCodeToNumber(Context context, String number) {
        number = formatNumber(context, number);
//        number = context.getString(R.string.country_code) + (number);
        pref= FunduUser.getAppPreferences();
        number = pref.getString("country_code_r")+number;
        return number;
    }

    public static String formatNumber(Context context, String number) {
        if(number == null){
            return null;
        }
        number = number.replace(" ", "");
        return checkAndRemoveCountryCode(context, number);
    }

    public static String checkAndRemoveCountryCode(Context context, String number) {

        if (number.startsWith(context.getString(R.string.country_code)) ||
                number.startsWith(context.getString(R.string.double_zero)))
            number = number.substring(3);

        else if (number.startsWith("0"))
            number = number.substring(1).trim();

        else if (number.startsWith(context.getString(R.string.country_code_ken)))
            number = number.substring(4);





        return number;

    }

    public static HashMap<String, String> phoneNumberParser(Context context, String number) {
        number = number.replace((char)160,'-');
        StringBuilder new_number= new StringBuilder();
        String special_char = "(),-#*+ ";
        for(int i=0;i<number.length();i++){

            char c = number.charAt(i);
            if(!special_char.contains(Character.toString(c))){
                new_number.append(number.charAt(i));
            }

        }
        String final_string = new_number.toString();
        HashMap<String,String> phnum_and_codes = new HashMap<>();

        if(final_string.length()==10&&(final_string.startsWith("9")||final_string.startsWith("7")||final_string.startsWith("8"))){

            phnum_and_codes.put(Constants.COUNTRYCODE,"+91");
            phnum_and_codes.put(Constants.PHONENUMBER,final_string);
        }

        else if((final_string.length()<7)||final_string.startsWith("1800")){

            phnum_and_codes.put(Constants.COUNTRYCODE,"****");
            phnum_and_codes.put(Constants.PHONENUMBER,"****");

        }

        else if(final_string.startsWith("0091")){

            //phnum_and_codes.put(Constants.COUNTRYCODE,final_string.substring(0,4));
            phnum_and_codes.put(Constants.COUNTRYCODE,"+91");
            phnum_and_codes.put(Constants.PHONENUMBER,final_string.substring(4));

        }

        else if(final_string.startsWith("254") &&  final_string.length()==12){

            phnum_and_codes.put(Constants.COUNTRYCODE,"+254");
            phnum_and_codes.put(Constants.PHONENUMBER,final_string.substring(3));

        }

        else if((final_string.length()==9)&&
                (final_string.startsWith("9")||final_string.startsWith("8")||final_string.startsWith("7"))){

            phnum_and_codes.put(Constants.COUNTRYCODE,"+254");
            phnum_and_codes.put(Constants.PHONENUMBER,final_string);

        }
        else if(final_string.startsWith("011")||final_string.startsWith("9111")||final_string.startsWith("027")
                ||final_string.startsWith("028")){

            phnum_and_codes.put(Constants.COUNTRYCODE,"****");
            phnum_and_codes.put(Constants.PHONENUMBER,"****");

        }
        else if(final_string.startsWith("91")||final_string.startsWith("00")){

            //phnum_and_codes.put(Constants.COUNTRYCODE,final_string.substring(0,2));
            phnum_and_codes.put(Constants.COUNTRYCODE,"+91");
            phnum_and_codes.put(Constants.PHONENUMBER,final_string.substring(2));

        }
        else if(final_string.startsWith("0")){
            if(final_string.length() == 11){
                //India Number
                phnum_and_codes.put(Constants.COUNTRYCODE,"+91");
                phnum_and_codes.put(Constants.PHONENUMBER,final_string.substring(1));
            }
            else if(final_string.length() == 10){
                phnum_and_codes.put(Constants.COUNTRYCODE,"+254");
                phnum_and_codes.put(Constants.PHONENUMBER,final_string.substring(1));
            }
            else{
                phnum_and_codes.put(Constants.COUNTRYCODE,"****");
                phnum_and_codes.put(Constants.PHONENUMBER,final_string);
            }

        }
        else{

            phnum_and_codes.put(Constants.COUNTRYCODE,"****");
            phnum_and_codes.put(Constants.PHONENUMBER,final_string);
        }




     /*   Fog.d("final_string","final_string"+phnum_and_codes.get(Constants.COUNTRYCODE));
        Fog.d("final_string","final_string"+phnum_and_codes.get(Constants.PHONENUMBER));
*/

        return phnum_and_codes;

    }

    public static void showLongToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }



/*created by pallavi "checkAndRemoveCountryCode" function not working properly so i have crete another one*/

    public static void showShortToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static VolleyError configureErrorMessage(VolleyError volleyError) {

        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            VolleyError error = null;
            String message = "";
            try {
                message = new JSONObject(new String(volleyError.networkResponse.data)).getString("message");
                error = new VolleyError(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            volleyError = error;
        }
        return volleyError;
    }

    public static void playRingtone(Context context) {

        Ringtone ringtone;
        Uri alert = Uri.parse("android.resource://in.co.eko.fundu/" + R.raw.notification_tone);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                // alert is null, using backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alert == null) {
                    // alert backup is null, using 2nd backup
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
        }
        ringtone = RingtoneManager.getRingtone(context, alert);
        ringtone.setStreamType(AudioManager.STREAM_RING);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int flag = AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
        if (volume == 0) {
            flag = AudioManager.FLAG_VIBRATE;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, flag);
        if (ringtone != null) {
            ringtone.play();
        }
    }

    public static boolean bothSimWorking(Context context){

        boolean bothSimWorking = false;
//        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);

//        String imeiSIM1 = telephonyInfo.getImsiSIM1();
//        String imeiSIM2 = telephonyInfo.getImsiSIM2();
//
//        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
//        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
//
//        boolean isDualSIM = telephonyInfo.isDualSIM();

        return bothSimWorking;
    }

    public static String getPhoneNumber(Context context){
        try {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();

            return mPhoneNumber;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getVersionCode(){
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        return String.valueOf(versionCode);
    }

    public static String getAppVersion(){
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        return versionName +"_ "+String.valueOf(versionCode);
    }

    public static boolean checkPermission(Context context,String permission){
        int result = ContextCompat.checkSelfPermission(context,permission);


        return result == PackageManager.PERMISSION_GRANTED;
    }

    private String removeCountryCode(String number) {
        if (hasCountryCode(number)) {
            int country_digits = number.length() - digits;
            number = number.substring(country_digits);
        }
        return number;
    }

    // Every country code starts with + right?
    private boolean hasCountryCode(String number) {
        return number.charAt(plus_sign_pos) == '+'; // Didn't String had contains() method?...
    }
    public static String getConnonicalizedUrl(String sUrl){
        String canonical = sUrl;
        try {

            URL url = new URL(sUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            canonical = uri.toString();

        }
        catch (URISyntaxException e){
            Fog.e(TAG,"Error while connonicalizing",e);
        }
        catch (MalformedURLException e){
            Fog.e(TAG,"Error while connonicalizing",e);
        }
        return canonical;
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("localhost", session);
            }
        };
    }


    public static boolean isDualSim(Context context){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
        Fog.d("SimDetails","isDualSim"+telephonyInfo.isDualSIM());
        return telephonyInfo.isDualSIM();
    }

    public static void getSimDetails(Context context){

        TelephonyManager manager = (TelephonyManager) FunduApplication.getAppContext()
                .getSystemService(Context.TELEPHONY_SERVICE);

        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);

        manager.getSimOperatorName();
        manager.getSimOperator();
        manager.getSimCountryIso();
        manager.getCellLocation();
        manager.getNetworkCountryIso();
       // manager.get
        Fog.d(" " + manager.getSimOperatorName() + " " + manager.getSimOperator(), "SimDetails");
        Fog.d(" getSimCountryIso" + manager.getSimCountryIso(), "SimDetails");
        Fog.d(" getCellLocation " + manager.getCellLocation() , "SimDetails");
        Fog.d(" getNetworkCountryIso " + manager.getNetworkCountryIso() , "SimDetails");
        Fog.d(" getDataActivity " + manager.getDataActivity() , "SimDetails");
        Fog.d(" getLine1Number " + manager.getLine1Number() , "SimDetails");
        Fog.d(" getPhoneType " + manager.getPhoneType() , "SimDetails");



    }



    public static String GetCountryZipCode(Context context){
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        String countryID= manager.getSimCountryIso().toUpperCase();
        Fog.d("COUNTRYID","COUNTRYID"+countryID);
        String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(countryID.trim())){
                Fog.d("COUNTRYID","CountryZipCode"+g[0]);
                Fog.d("COUNTRYID","COUNTRYID"+g[1]);
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }


    public static boolean isSimSupport(Context context)
   {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
               return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);

          }

     public static String getCountryID(){
         TelephonyManager manager = (TelephonyManager) FunduApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
         //getNetworkCountryIso
         String countryID= manager.getSimCountryIso().toUpperCase();
         return countryID;
     }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }



    public static final String spannableBoldString(String string) {

        SpannableString spanString = new SpannableString(string);
        //spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        //spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        return string;
    }
    public static View makeMeBlink(View view, int duration, int offset) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(duration);
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
        return view;
    }

    public static void takeFeedback(Bitmap bitmap,Context context){
        try {
            String fileName = Environment
                    .getExternalStorageDirectory().toString()+ "/FUNDU_SCREEN"
                    + System.currentTimeMillis() + ".jpg";
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Intent intent = new Intent(context,Feedback.class);
            intent.setAction("feedback");
            intent.putExtra("screen_shot_feedback",fileName);
            context.startActivity(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openMapIntent(Location source, Location destination, Context context){
        float distance = source.distanceTo(destination);// distance in meters
        String mode = "w";
        if(distance > 1000)
            mode = "d";

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + source.getLatitude() + "," + source.getLongitude() + "&daddr=" + destination.getLatitude() + "," + destination.getLongitude() + "&dirflg="+mode+"&sensor=true"));
        context.startActivity(intent);

    }

    public static void call(String number,Context context){
        if(TextUtils.isEmpty(number))
            return;
        number = Utils.appendCountryCodeToNumber(context,number);
        Uri call = Uri.parse("tel:" + number);
        Intent surf = new Intent(Intent.ACTION_DIAL, call);
        context.startActivity(surf);
    }

    public static String getErrorMessage(VolleyError volleyError){
        String error = null;
        try{
            String responseS = new String(volleyError.networkResponse.data);
            JSONObject responseJ = new JSONObject(responseS);
            return responseJ.optString("message");
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void vibratePhone(Activity activity){
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    public static String toCamelCase(String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }
}
