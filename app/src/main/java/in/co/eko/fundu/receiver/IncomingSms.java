package in.co.eko.fundu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.greenrobot.eventbus.EventBus;

import in.co.eko.fundu.activities.PairContactFoundActivity;
import in.co.eko.fundu.constants.Constants;
import in.co.eko.fundu.event.OtpReceiverEvent;
import in.co.eko.fundu.utils.Fog;

/**
 * Created by user on 12/12/16.
 */

public class IncomingSms extends BroadcastReceiver
{



    @Override
    public void onReceive(Context context, Intent intent)
    {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null)
            {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj .length; i++)
                {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])                                                                                                    pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber ;
                    String message = currentMessage .getDisplayMessageBody();

                    try
                    {

                        String valued = message;
                        String otp =null;

                        if(senderNum.contains("EKOIND") && senderNum.contains("-")){
                            String[] split = senderNum.split("-");
                            senderNum = split[1];
                            Fog.d("Sender number extracted : ",senderNum);
                        }


                        switch (senderNum.toUpperCase()){

                            case Constants.EKOIND:
                                String temp[] = valued.split("\n");
                                notifyOtp(temp[0].replaceAll("[^0-9]", ""));
                                break;

                            case Constants.KENSWITCH:

                                otp = valued.replaceAll("[^0-9]", "");
                                notifyOtp(otp);
                                notifyTotp(otp);
                                break;
                            case Constants.FundU:

                                otp = valued.replaceAll("[^0-9]", "");
                                notifyOtp(otp);
                                notifyTotp(otp);
                                break;
                            case Constants.IMWAYSMS:

                                otp = valued.replaceAll("[^0-9]", "");
                                notifyOtp(otp);
                                notifyTotp(otp);
                                break;
                        }

                        /*if (senderNum.contains("EKOIND") || senderNum.equals("FundU")
                                || senderNum.equalsIgnoreCase("KENSWITCH") || senderNum.equalsIgnoreCase("IM-WAYSMS"))
                        {
                            // String valued = message;
                            String intValue = valued.replaceAll("[^0-9]", "");
                            //Fog.e("OTP ", intValue);
                            try {
                                *//*VerifyCodeFragment Sms = new VerifyCodeFragment();
                                Sms.recivedSms(intValue);*//*
                                EventBus.getDefault().post(new OtpReceiverEvent(intValue));

                            }
                            catch (Exception e){
                              e.printStackTrace();
                            }
                            try {
                            PairContactFoundActivity pc = new PairContactFoundActivity();
                            pc.receivedTOTP(intValue);
                        }
                          catch (Exception e){

                    }*/
//


                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }

        } catch (Exception e)
        {

        }
    }


    public void notifyOtp(String intValue){
        try {
            EventBus.getDefault().post(new OtpReceiverEvent(intValue));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void notifyTotp(String intValue){
        try {
            PairContactFoundActivity pc = new PairContactFoundActivity();
            pc.receivedTOTP(intValue);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}