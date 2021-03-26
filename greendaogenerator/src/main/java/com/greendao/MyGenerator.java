package com.greendao;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MyGenerator {
    static final int DB_VERSION = 3;
    public static void main(String[] args) {
        Schema schema = new Schema(DB_VERSION, "in.co.eko.fundu.database.greendao"); // Your app package name and the (.db) is the folder where the DAO files will be generated into.
        schema.enableKeepSectionsByDefault();
        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema,"./app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        addUserEntities(schema);
        // addPhonesEntities(schema);
    }



    // This is use to describe the colums of your table
    private static Entity addUserEntities(final Schema schema) {
        Entity transxInfo = schema.addEntity("FunduTransaction");
        transxInfo.addIdProperty().primaryKey().autoincrement();
        transxInfo.addStringProperty("tid").unique();
        transxInfo.addStringProperty("seeker");
        transxInfo.addStringProperty("provider");
        transxInfo.addStringProperty("custid");
        transxInfo.addStringProperty("phoneNumber");
        transxInfo.addStringProperty("name");
        transxInfo.addStringProperty("image");
        transxInfo.addStringProperty("fee");
        transxInfo.addStringProperty("rating");
        transxInfo.addStringProperty("code");
        transxInfo.addStringProperty("providerCharge");
        transxInfo.addDoubleProperty("latitude");
        transxInfo.addDoubleProperty("longitude");
        transxInfo.addStringProperty("amount");
        transxInfo.addIntProperty("state");
        transxInfo.addStringProperty("status");
        transxInfo.addStringProperty("pairRequestId");
        transxInfo.addDoubleProperty("requestLatitude");
        transxInfo.addDoubleProperty("requestLongitude");
        return transxInfo;
    }


}