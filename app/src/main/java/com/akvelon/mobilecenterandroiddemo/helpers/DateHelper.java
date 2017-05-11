package com.akvelon.mobilecenterandroiddemo.helpers;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ruslan on 5/11/17.
 */

public class DateHelper {

    public static Date today() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }
}
