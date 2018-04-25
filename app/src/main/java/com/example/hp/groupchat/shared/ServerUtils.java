package com.example.hp.groupchat.shared;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author hp
 */
public class ServerUtils {

    public static String dateLog() {
        return new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}

