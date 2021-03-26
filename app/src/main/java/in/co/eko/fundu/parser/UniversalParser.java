package in.co.eko.fundu.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Divyanshu jain on 11/3/2015.
 */
public class UniversalParser {

    private static UniversalParser universalParser = null;
    private Class modelClass;

    private UniversalParser() {

    }

    public static UniversalParser getInstance() {
        if (universalParser == null)
            universalParser = new UniversalParser();

        return universalParser;
    }


    public <T> ArrayList<T> parseJsonArrayWithJsonObject(JSONArray jsonArray, Class modelClass) {
        this.modelClass = modelClass;
        Object modelClassObject;
        ArrayList<T> data = new ArrayList<>();

        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                modelClassObject = modelClass.newInstance();
                if (modelClassObject instanceof String || modelClassObject instanceof Integer || modelClassObject instanceof Boolean) {
                    Object undefinedObj = jsonArray.opt(i);
                    if (undefinedObj != null && !undefinedObj.equals(null) && !undefinedObj.equals(""))
                        data.add((T) undefinedObj);
                } else {
                    for (Field f : modelClass.getDeclaredFields()) {
                        f.setAccessible(true);
                        Object undefinedObj = jsonArray.opt(i);
                        if (undefinedObj instanceof JSONObject) {
                            IterateForJsonObject(modelClassObject, f, (JSONObject) undefinedObj);
                        } else if (undefinedObj instanceof JSONArray)
                            getJsonArrayFromObject(modelClassObject, f, (JSONArray) undefinedObj);
                        else if (undefinedObj != null) {
                            f.set(modelClassObject, undefinedObj);
                        }
                    }
                    data.add((T) modelClassObject);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public <T> T parseJsonObject(JSONObject jsonObject, Class modelClass) {
        Object modelClassObject = null;
        T data = null;
        try {
            this.modelClass = modelClass;
            modelClassObject = modelClass.newInstance();
            for (Field f : modelClass.getDeclaredFields()) {
                f.setAccessible(true);
                Object undefinedInnerObj = jsonObject.opt(f.getName());
                if (undefinedInnerObj instanceof JSONArray) {
                    getJsonArrayFromObject(modelClassObject, f, (JSONArray) undefinedInnerObj);
                } else if (undefinedInnerObj instanceof JSONObject)
                    getJsonObjectFromObject(modelClassObject, (JSONObject) undefinedInnerObj);
                else if (undefinedInnerObj != null)
                    f.set(modelClassObject, jsonObject.opt(f.getName()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = ((T) modelClassObject);

        return data;
    }

    private <T> void getJsonArrayFromObject(Object obj, Field f, JSONArray undefinedInnerObj) throws IllegalAccessException {
        ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
        ArrayList<T> list = parseJsonArrayWithJsonObject(undefinedInnerObj, stringListClass);
        f.set(obj, list);
    }

    private <T> void IterateForJsonObject(Object modelClassObject, Field f, JSONObject undefinedObj) throws IllegalAccessException {
        String name = f.getName();
        Object undefinedInnerObj = undefinedObj.opt(name);
        if (undefinedInnerObj instanceof JSONArray) {
            getJsonArrayFromObject(modelClassObject, f, (JSONArray) undefinedInnerObj);
        } else if (undefinedInnerObj instanceof JSONObject) {
            getJsonObjectFromObject(modelClassObject, (JSONObject) undefinedInnerObj);
        } else if (undefinedInnerObj != null)
            f.set(modelClassObject, undefinedInnerObj);
    }

    private void getJsonObjectFromObject(Object modelClassObject, JSONObject undefinedInnerObj) throws IllegalAccessException {
        JSONObject json = undefinedInnerObj;
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            try {
                Field field = modelClass.getDeclaredField(keys.next());
                field.setAccessible(true);
                IterateForJsonObject(modelClassObject, field, undefinedInnerObj);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
}
