package babybot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * File: Time
 * Author: Goudbes
 * Created: 2021.12.23, 13.18
 */

public class Time {

    /**
     * Time and date
     * Example: ?time
     *
     * @return time and date
     */
    static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss zzzz");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

}
