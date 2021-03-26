package in.co.eko.fundu.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.FunduApplication;


/**
 * Created by pallavi on 25/9/17.
 */

public class SimSuscriptionManager {

    Context context;
    private SubscriptionManager subManager ;
    private TelephonyManager manager;
    private ArrayList<String> arrayListNumber = new ArrayList<>();
    private ArrayList<String> arrayListSimNames = new ArrayList<>();
    private List<SubscriptionInfo> subInfoList = null;
    private List subscriptionInfo;

    public SimSuscriptionManager(Context context) {
        this.context = context;
        subManager   = (SubscriptionManager) FunduApplication.getAppContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        manager      = (TelephonyManager) FunduApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
    }


    public ArrayList<String> getSuscriptionSimNumbers(){


        if(!Utils.isDualSim(context)){

            arrayListNumber.add(manager.getLine1Number());


        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                subInfoList = subManager.getActiveSubscriptionInfoList();

                if (subInfoList.size() != 0 /*&& subInfoList.size() ==2*/) {

                    for(int i =0;i<subInfoList.size();i++){
                        Fog.d("subInfoList","subInfoList"+subInfoList.get(i));
                        if(subInfoList.get(i).getNumber()!=null&&!subInfoList.get(i).getNumber().isEmpty())
                            arrayListNumber.add(subInfoList.get(i).getNumber());
                        else {

                            if(i==0){
                                TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                                String operator = manager.getSimOperator();
                                String  line1Number  = manager.getLine1Number();
                                List<CellInfo> list = manager.getAllCellInfo();

                                if(!line1Number.equalsIgnoreCase(""))
                                {
                                    Fog.d("Test", "Current list = " + line1Number);
                                    arrayListNumber.add(manager.getLine1Number());
                                }
                                else{
                                    if (Utils.getCountryID().equalsIgnoreCase("254")) {

                                        arrayListNumber.add("xxxxxxxxx");
                                    } else {
                                        arrayListNumber.add("xxxxxxxxxx");
                                    }
                                }
                            }

                            else{
                                if (Utils.getCountryID().equalsIgnoreCase("254")) {

                                    arrayListNumber.add("xxxxxxxxx");
                                } else {
                                    arrayListNumber.add("xxxxxxxxxx");
                                }
                            }


                        }
                    }

                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                    if (subInfoList.size() ==1&&(subInfoList.get(0).getNumber().equalsIgnoreCase("")||
                            subInfoList.get(0).getNumber().equalsIgnoreCase("null")
                            ||subInfoList.get(0).getNumber().isEmpty())){
                        Fog.d("SimNum******",""+manager.getLine1Number());

                        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                        String operator = manager.getSimOperator();
                        String  line1Number  = manager.getLine1Number();
                        List<CellInfo> list = manager.getAllCellInfo();

                        if(!line1Number.equalsIgnoreCase(""))
                        {
                            Fog.d("Test", "Current list = " + line1Number);
                            arrayListNumber.add(manager.getLine1Number());
                        }


                    }

                    else {

                        if(!manager.getLine1Number().equalsIgnoreCase(""))
                        {
                            Fog.d("Test", "Current list = " + manager.getLine1Number());
                            arrayListNumber.add(manager.getLine1Number());
                        }

            }



            }
        }





        return arrayListNumber;
    }



    public  ArrayList<String>  getSimNames(){


        if(Utils.isDualSim(context)){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                List subscriptionInfo = SubscriptionManager.from(FunduApplication.getAppContext()).
                        getActiveSubscriptionInfoList();

                for (int i = 0; i < subscriptionInfo.size(); i++) {
                    String str = subscriptionInfo.get(i).toString();
                    String strName[] = str.substring(str.indexOf("displayName")
                            , str.indexOf("carrierName")).split("=");
                    Fog.d("strName","strName"+strName[1]);
                    int j = i + 1;
                    arrayListSimNames.add(strName[1]);
                }


            }

            else {

                TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
                boolean isDualSIM = telephonyInfo.isDualSIM();
                TelephonyManager manager = (TelephonyManager) FunduApplication.getAppContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    if(isDualSIM){
                        String optName1 = getOutput(FunduApplication.getAppContext(), "SimOperatorName", 1);
                        String optName2 = getOutput(FunduApplication.getAppContext(), "SimOperatorName", 2);
                        arrayListSimNames.add(optName1);
                        arrayListSimNames.add(optName2);
                    }
                    else {
                        String optName1 = manager.getSimOperatorName();
                        arrayListSimNames.add(optName1);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else {
            String optName1 = manager.getSimOperatorName();
            arrayListSimNames.add(optName1);
        }

        return arrayListSimNames;
    }

    private static String getOutput(Context context, String methodName, int slotId) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        String reflectionMethod = null;
        String output = null;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            for (Method method : telephonyClass.getMethods()) {
                String name = method.getName();
                if (name.contains(methodName)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && params[0].getName().equals("int")) {
                        reflectionMethod = name;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephony, reflectionMethod, slotId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    private static String getOpByReflection(TelephonyManager telephony, String predictedMethodName, int slotID, boolean isPrivate) {

        Fog.i("Reflection", "Method: " + predictedMethodName+" "+slotID);
        String result = null;

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID;
            if (slotID != -1) {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName, parameter);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                }
            } else {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName);
                }
            }

            Object ob_phone;
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            if (getSimID != null) {
                if (slotID != -1) {
                    ob_phone = getSimID.invoke(telephony, obParameter);
                } else {
                    ob_phone = getSimID.invoke(telephony);
                }

                if (ob_phone != null) {
                    result = ob_phone.toString();

                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        Fog.i("Reflection", "Result: " + result);
        return result;
    }



}
