package com.thebridgestudio.amwayconference.daos;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.thebridgestudio.amwayconference.models.Message;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[] {
        Message.class,
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
