package in.co.eko.fundu.models;
/*
 * Created by Bhuvnesh
 */

import android.content.Context;
import android.location.Location;

import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.utils.AppPreferences;

/**
 * Initialize this class in Application class
 */
public final class FunduUser {
    private static AppPreferences preferences;


    public static void initialize(Context context) {
        preferences = AppPreferences.getInstance(context);
    }

    public static AppPreferences getAppPreferences()
    {
        return preferences;
    }

    public static User getUser() {
        User user;
        user = new User();
        user.setContactId(preferences.getString(Constants.PrefKey.CONTACT_NUMBER));
        user.setName(preferences.getString(Constants.PrefKey.NAME));
        user.setVerified(preferences.getBoolean(Constants.PrefKey.IS_VERIFIED, false));
        user.setContactType(preferences.getString(Constants.PrefKey.CONTACT_TYPE));
        user.setContactIdType(preferences.getString(Constants.PrefKey.CONTACT_ID_TYPE));
        user.setAmount(preferences.getString(Constants.PrefKey.USER_AMOUNT));
        user.setLatitude(preferences.getDouble(Constants.PrefKey.LATITUDE, 0));
        user.setLongitude(preferences.getDouble(Constants.PrefKey.LONGITUDE, 0));
        user.setEmail(preferences.getString(Constants.PrefKey.EMAIL));
        user.setLoginType(preferences.getInt(Constants.PrefKey.LOGIN_TYPE,0));
        user.setProfileUrl(preferences.getString(Constants.PrefKey.USERIMAGE));
        user.setMobile(preferences.getString(Constants.PrefKey.CONTACT_NUMBER));
        user.setIi(preferences.getString(Constants.PrefKey.INVITATION_ID));

        return user;
    }




    public static Location getLocation(){
        Location location = new Location("");
        location.setLongitude(preferences.getDouble(Constants.PrefKey.LONGITUDE, 0));
        location.setLatitude(preferences.getDouble(Constants.PrefKey.LATITUDE, 0));
        return location;
    }

    public static void setLoggedIn(boolean value){
        preferences.putBoolean(Constants.IS_USER_LOGGED_IN,value);
    }

    public static boolean isUserLogin() {
        return preferences.getBoolean(Constants.PrefKey.IS_VERIFIED,false);
    }

    public static String getContactId() {
        /*if (preferences.getString(Constants.PrefKey.CONTACT_NUMBER).isEmpty()) {
            return null;
        } else {
            return preferences.getString(Constants.PrefKey.CONTACT_NUMBER);
        }*/
        return preferences.getString(Constants.PrefKey.CONTACT_NUMBER).isEmpty() ? null : preferences.getString(Constants.PrefKey.CONTACT_NUMBER);
    }
    public static void setContactId(String contactId){
         preferences.putString(Constants.PrefKey.CONTACT_NUMBER,contactId);
    }

    public static String getWalletAmount() {
        return preferences.getString(Constants.PrefKey.USER_AMOUNT);
    }

    public static void saveFullName(String name) {
        preferences.putString(Constants.PrefKey.NAME, name);
    }

    public static String getFullName() {
        return preferences.getString(Constants.PrefKey.NAME);
    }

    public static String getEmail() {
        return preferences.getString(Constants.PrefKey.EMAIL);
    }

    public static Double getLatitude() {
        return preferences.getDouble(Constants.PrefKey.LATITUDE,0);
    }

    public static Double getLongitude() {
        return preferences.getDouble(Constants.PrefKey.LONGITUDE,0);
    }

    public static String getCountryShortName(){ return preferences.getString(Constants.COUNTRY_SHORTCODE);}

    public static void setCountryShortName(String countryShortName){
        preferences.putString(Constants.COUNTRY_SHORTCODE, countryShortName);
    }

    public static String getChargesKen(){ return preferences.getString("ChargesKen");}

    public static void setChargesKen(String chargesKen){
        preferences.putString("ChargesKen", chargesKen);
    }

    public static String getCustomerId(){ return preferences.getString(Constants.CUSTOMERID);}

    public static void setCustomerId(String customerId){
        preferences.putString(Constants.CUSTOMERID, customerId);
    }

    public static void setAuthToken(String authToken){
        preferences.putString ( Constants.PrefKey.AUTH_TOKEN,authToken );
    }

    public static String getAuthToken(){
        return  preferences.getString(Constants.PrefKey.AUTH_TOKEN);
    }

    /*public static int getWalletAmount() {
        String amount = preferences.getString(Constants.PrefKey.USER_AMOUNT)
        return Long.parseLong(amount);
    }*/
    public static void setWalletAmount(String amount) {
        preferences.putString(Constants.PrefKey.USER_AMOUNT, amount);
    }

    public static boolean isUserMobileVerified() {
        return preferences.getBoolean(Constants.PrefKey.IS_VERIFIED, false);
    }

    public static void setUserMobileVerified(boolean isVerified) {
        preferences.putBoolean(Constants.PrefKey.IS_VERIFIED, isVerified);
    }

    public static boolean isUserLoginorRegister(){
        return preferences.getBoolean(Constants.PrefKey.IS_LOGIN_REG, false);
    }
    public static void setUserLoginOrRegister(boolean value){
        preferences.putBoolean(Constants.PrefKey.IS_LOGIN_REG, value);
    }
    public static void signOut() {
        preferences.clear();
    }

    public static void saveUser(User user) {
        preferences.putString(Constants.PrefKey.CONTACT_NUMBER, user.getContactId());
        preferences.putString(Constants.PrefKey.NAME, user.getName());
        preferences.putString(Constants.PrefKey.EMAIL,user.getEmail());
        preferences.putBoolean(Constants.PrefKey.IS_VERIFIED, user.isVerified());
        preferences.putString(Constants.PrefKey.CONTACT_TYPE, user.getContactType());
        preferences.putString(Constants.PrefKey.CONTACT_ID_TYPE, user.getContactIdType());
        preferences.putString(Constants.PrefKey.LATITUDE, "0");
        preferences.putString(Constants.PrefKey.LONGITUDE, "0");
        preferences.putString(Constants.PrefKey.USERIMAGE, user.getProfileUrl());
        preferences.putInt(Constants.PrefKey.LOGIN_TYPE,user.getLoginType());

    }

    public static String getProfilePic(){
        return preferences.getString(Constants.PrefKey.USERIMAGE);
    }

    public static void setUserMobile(String contactId) {
        preferences.putString(Constants.PrefKey.CONTACT_NUMBER, contactId);
    }


    public static void setLocation(Location mLastLocation) {
        preferences.putDouble(Constants.PrefKey.LATITUDE, mLastLocation.getLatitude());
        preferences.putDouble(Constants.PrefKey.LONGITUDE, mLastLocation.getLongitude());

    }

    public static String getContactIDType() {
        return preferences.getString(Constants.PrefKey.CONTACT_ID_TYPE);
    }

    public static void setContactIDType(String contactID) {
        preferences.putString(Constants.PrefKey.CONTACT_ID_TYPE, contactID);
    }

    public  static int getTotalIncentivesFromInvitations(){
        return preferences.getInt(Constants.PrefKey.INVITATION_INCENTIVE,0);
    }
    public static void setTotalIncentiveFromInvitaion(int value){
        preferences.putInt(Constants.PrefKey.INVITATION_INCENTIVE,value);
    }
    public  static String getVpa(){
        return preferences.getString(Constants.PrefKey.VPA);
    }
    public static void setVpa(String value){
        preferences.putString(Constants.PrefKey.VPA,value);
    }
    public  static String getIFSC(){
        return preferences.getString(Constants.PrefKey.IFSC_CODE);
    }
    public static void setIFSC(String value){
        preferences.putString(Constants.PrefKey.IFSC_CODE,value);
    }
    public  static String getAccountNo(){
        return preferences.getString(Constants.PrefKey.MASKED_ACCOUNT_NUMBER);
    }
    public static void setAccountNo(String value){
        preferences.putString(Constants.PrefKey.MASKED_ACCOUNT_NUMBER,value);
    }

    public static void setRecipientId(String recipientId){
            preferences.putString(Constants.PrefKey.RECIPIENT_ID,recipientId);
    }
    public static String getRecipientId(){
        return preferences.getString (Constants.PrefKey.RECIPIENT_ID);
    }

    public static String getBankName(){
        return preferences.getString ( Constants.PrefKey.BANK_NAME );
    }

    public static void setBankName(String bankName){
        preferences.putString ( Constants.PrefKey.BANK_NAME, bankName);
    }


    /**
     *
     * @param code like +91 for india, +254 for Kenya
     */
    public static void setCountryMobileCode(String code){
        preferences.putString(Constants.COUNTRY_MOBILE_CODE,code);
    }
    public static String getCountryMobileCode(){
       return preferences.getString(Constants.COUNTRY_MOBILE_CODE);
    }

    public static String getInvitationLink(){
       return preferences.getString(Constants.PrefKey.INVITATION_LINK);
    }

    public static void setInvitationLink(String link){
        preferences.putString(Constants.PrefKey.INVITATION_LINK,link);
    }

    public static void setInvitationId(String ii){
        preferences.putString(Constants.PrefKey.INVITATION_ID,ii);
    }
    public static void getInvitationId(){
        preferences.getString(Constants.PrefKey.INVITATION_ID);
    }
    public static void setRating(double rating){
        preferences.putDouble(Constants.PrefKey.RATING,rating);
    }
    public static double getRating(){
        double rating = preferences.getDouble(Constants.PrefKey.RATING,0);
        return rating;
    }

    public static String getInvitationMessage(){
        return preferences.getString(Constants.PrefKey.INVITATION_MESSAGE);
    }

    public static void setInvitationMessage(String message){
        preferences.putString(Constants.PrefKey.INVITATION_MESSAGE,message);
    }

}
