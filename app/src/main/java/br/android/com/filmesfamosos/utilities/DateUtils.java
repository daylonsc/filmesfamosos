/*
 * Created on 01/11/2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package br.android.com.filmesfamosos.utilities;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    private static final int START_FIELD = 0;
    private static final int END_FIELD = 1;

    private static final SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getInstance();
    private static String defaultDatePattern = "MM/dd/yyyy";

    /**
     * Field number indicating the year.
     */
    public static final int YEAR = Calendar.YEAR;

    /**
     * Field number indicating the month.
     * The first month of the year is JANUARY which is 1.
     */
    public static final int MONTH = Calendar.MONTH;

    /**
     * Field number indicating the day of the month.
     * The first day of the month has value 1.
     */
    public static final int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;

    /**
     * Field number indicating the hour of the day.
     * <code>HOUR_OF_DAY</code> is used for the 24-hour clock.
     * E.g., at 10:04:15.250 PM the <code>HOUR_OF_DAY</code> is 22.
     */
    public static final int HOUR_OF_DAY = Calendar.HOUR_OF_DAY;

    /**
     * Field number indicating the minute within the hour.
     * E.g., at 10:04:15.250 PM the <code>MINUTE</code> is 4.
     */
    public static final int MINUTE = Calendar.MINUTE;

    /**
     * Field number indicating the second within the minute.
     * E.g., at 10:04:15.250 PM the <code>SECOND</code> is 15.
     */
    public static final int SECOND = Calendar.SECOND;

    /**
     * Field number indicating the millisecond within the second.
     * E.g., at 10:04:15.250 PM the <code>MILLISECOND</code> is 250.
     */
    public static final int MILLISECOND = Calendar.MILLISECOND;

    static {
        sdf.setLenient(false);
    }

    private DateUtils() {
        // Empty
    }

    /**
     * Returns if the given field is valid or not.
     * @param field the given field.
     * @return if the given field is valid or not.
     */
    private static boolean isValid(int field) {
        return field == YEAR || field == MONTH || field == DAY_OF_MONTH || field == HOUR_OF_DAY
                || field == MINUTE || field == SECOND || field == MILLISECOND;
    }

    /**
     * Returns the length of the given field.
     * @param field the given field.
     * @return the value for the given field.
     */
    private static int getLength(int field) {
        if (field == YEAR)
            return 4;
        else if (field == MILLISECOND)
            return 3;
        else if (field == MONTH || field == DAY_OF_MONTH || field == HOUR_OF_DAY
                || field == MINUTE || field == SECOND)
            return 2;
        else
            throw new IllegalArgumentException("Invalid field: " + field);
    }

    /**
     * Formats a date with according to the given pattern. It uses a
     * SimpleDateFormat internally to accomplish this task.
     * @param date date to be formated.
     * @param pattern pattern to be applied to the given date.
     * For example: dd/MM/yyyy HH:mm:ss SSS
     * @see SimpleDateFormat
     */
    public static synchronized String format(Date date, String pattern) {
        sdf.applyPattern(pattern == null ? defaultDatePattern : pattern);
        return sdf.format(date);
    }

    /**
     * Formata uma string de data do tipo 'yyyymmdd' de acordo com
     * o formato desejado.
     * @param date Data no formato 'yyyymmdd'.
     * @param pattern Especifica o formato desejado (ex: 'ddMMyyyy').
     */
    public static String format(final String date, final String pattern){
        try {
            if (date.length() != 4
                    && date.length() != 6
                    && date.length() != 8) {
                throw new IllegalArgumentException("Date with invalid length");
            }

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, new Integer(date.substring(0, 4)));

            if (date.length() >= 6) {
                cal.set(Calendar.MONTH, new Integer(date.substring(4, 6)) - 1);
            }

            if (date.length() == 8) {
                cal.set(Calendar.DAY_OF_MONTH, new Integer(date.substring(6, 8)) - 1);
            }

            return format(cal.getTime(), pattern);
        } catch (Exception ex){
            return "";
        }
    }

    /**
     * Formats a date with according to the given pattern. It uses a
     * SimpleDateFormat internally to accomplish this task.
     * @param date date to be formated.
     * @see SimpleDateFormat
     */
    public static synchronized String format(Date date) {
        return format(date, null);
    }

    /**
     * Parses a date with according to the given pattern. It uses a
     * SimpleDateFormat internally to accomplish this task.
     * @param date
     * @param pattern pattern to be applied to the given date.
     * For example: dd/MM/yyyy HH:mm:ss SSS
     * @see SimpleDateFormat
     * @return
     * @throws ParseException
     */
    public static synchronized Date parse(String date, String pattern) throws ParseException {
        sdf.applyPattern(pattern == null ? defaultDatePattern : pattern);
        return sdf.parse(date);
    }

    /**
     * Parses a date with according to the given pattern. It uses a
     * SimpleDateFormat internally to accomplish this task.
     * @param date
     * @see SimpleDateFormat
     * @return
     * @throws ParseException
     */
    public static synchronized Date parse(String date) throws ParseException {
        return parse(date, null);
    }

    /**
     * Returns the value of the given field.
     * @param date
     * @param field the given field.
     * @return the value for the given field.
     */
    public static int getFieldValue(Date date, int field) {
        int result;
        if (isValid(field)) {
            Calendar gc  = getCalendar(date);
            if (field == MONTH)
                result = gc.get(field) + 1;
            else
                result = gc.get(field);
        } else
            throw new IllegalArgumentException("Invalid field: " + field);
        return result;
    }

    /**
     * Sets the given field to the given value.
     * @param date
     * @param field the given field.
     * @param value the value to be set for the given field.
     */
    public static void setFieldValue(Date date, int field, int value) {
        if (isValid(field)) {
            Calendar gc  = getCalendar(date);
            gc.setTime(date);
            if (field == MONTH)
                gc.set(field, value - 1);
            else
                gc.set(field, value);
            date.setTime(gc.getTimeInMillis());
        }
    }

    /**
     * Returns if date is between date1 and date2
     * @param date Base date.
     * @param date1 First date of interval.
     * @param date2 Last date of interval
     * @return true if date is between date1 and date2;
     */
    public static boolean between(Date date, Date date1, Date date2) {
        return compare(date, date1) >= 0 && compare(date, date2) <=0;
    }


    /**
     * Compares two Dates for ordering. Using year until day.
     * @param date1 First date to be compared.
     * @param date2 Second date to be compared.
     * @return  the value <code>0</code> if the argument date1 is equal to
     *          date2; a value <code>-1</code> if date1 is before the date2;
     *          and a value <code>1</code> if date1 is after the date2 argument.
     */
    public static int compare(Date date1, Date date2) {
        return compare(date1, date2, YEAR, DAY_OF_MONTH);
    }

    /**
     * Compares two Dates for ordering.
     * @param date1 First date to be compared.
     * @param date2 Second date to be compared.
     * @param startField
     * @param endField
     * @return  the value <code>0</code> if the argument date1 is equal to
     *          date2; a value <code>-1</code> if date1 is before the date2;
     *          and a value <code>1</code> if date1 is after the date2 argument.
     */
    public static int compare(Date date1, Date date2, int startField, int endField) {
        if (startField > endField) {
            int swap = endField;
            endField = startField;
            startField = swap;
        }
        for (int i=startField; i <= endField; i++) {
            if (isValid(i)) {
                int f1 = getFieldValue(date1, i);
                int f2 = getFieldValue(date2, i);
                if (f1 > f2)
                    return 1;
                else if (f1 < f2)
                    return -1;
            }
        }
        return 0;
    }

    public static Date getDate(Date date, int startField, int endField) {
        if (startField > endField) {
            int swap = endField;
            endField = startField;
            startField = swap;
        }
        if (startField == YEAR && endField == MILLISECOND) {
            return date;
        } else {
            // Somente usar Calendar se for necessario zerar algum campo.
            Calendar gc = getCalendar(date);
            switch (startField) {
                case MILLISECOND:
                    gc.set(Calendar.SECOND, 0);
                case SECOND:
                    gc.set(Calendar.MINUTE, 0);
                case MINUTE:
                    gc.set(Calendar.HOUR_OF_DAY, 0);
                case HOUR_OF_DAY:
                    gc.set(Calendar.DAY_OF_MONTH, 1);
                case DAY_OF_MONTH:
                    gc.set(Calendar.MONTH, Calendar.JANUARY);
                case MONTH:
                    gc.set(Calendar.YEAR, 1);
            }
            switch (endField) {
                case YEAR:
                    gc.set(Calendar.MONTH, Calendar.JANUARY);
                case MONTH:
                    gc.set(Calendar.DAY_OF_MONTH, 1);
                case DAY_OF_MONTH:
                    gc.set(Calendar.HOUR_OF_DAY, 0);
                case HOUR_OF_DAY:
                    gc.set(Calendar.MINUTE, 0);
                case MINUTE:
                    gc.set(Calendar.SECOND, 0);
                case SECOND:
                    gc.set(Calendar.MILLISECOND, 0);
            }
            return gc.getTime();
        }
    }

    /**
     * Creates a date with the defined precision.
     * @param date
     * @param precision
     * @return date with the defined precision.
     */
    public static Date getDate(Date date, int precision) {
        return getDate(date, YEAR, precision);
    }

    public static Time getTime(int hour, int minute, int second) {
        return new Time(getTimeMilles(70, 0, 1, hour, minute, second, 0));
    }

    /**
     * Creates a Date with the given fields.
     * @param year the value used to set the <code>YEAR</code> field.
     * @param month the value used to set the <code>MONTH</code> field.
     * Month value is 1-based. e.g., 1 for January.
     * @param day OfMonth the value used to set the <code>DAY_OF_MONTH</code> field.
     * @return a Date with the given fields.
     */
    public static Date getDate(int year, int month, int day) {
        return getDate(year, month, day, 0, 0);
    }

    /**
     * Creates a Date with the given fields.
     * @param year the value used to set the <code>YEAR</code> field.
     * @param month the value used to set the <code>MONTH</code> field.
     * Month value is 1-based. e.g., 1 for January.
     * @param day OfMonth the value used to set the <code>DAY_OF_MONTH</code> field.
     * @param hour OfDay the value used to set the <code>HOUR_OF_DAY</code> field.
     * @param minute the value used to set the <code>MINUTE</code> field.
     * @return a Date with the given fields.
     */
    public static Date getDate(int year, int month, int day, int hour, int minute) {
        return getDate(year, month, day, hour, minute, 0);
    }

    /**
     * Creates a Date with the given fields.
     * @param year the value used to set the <code>YEAR</code> field.
     * @param month the value used to set the <code>MONTH</code> field.
     * Month value is 1-based. e.g., 1 for January.
     * @param day OfMonth the value used to set the <code>DAY_OF_MONTH</code> field.
     * @param hour OfDay the value used to set the <code>HOUR_OF_DAY</code> field.
     * @param minute the value used to set the <code>MINUTE</code> field.
     * @param second the value used to set the <code>SECOND</code> field.
     * @return a Date with the given fields.
     */
    public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        return getDate(year, month, day, hour, minute, second, 0);
    }

    /**
     * Creates a Date with the given fields.
     * @param year the value used to set the <code>YEAR</code> field.
     * @param month the value used to set the <code>MONTH</code> field.
     * Month value is 1-based. e.g., 1 for January.
     * @param day Of Month the value used to set the <code>DAY_OF_MONTH</code> field.
     * @param hour OfDay the value used to set the <code>HOUR_OF_DAY</code> field.
     * @param minute the value used to set the <code>MINUTE</code> field.
     * @param second the value used to set the <code>SECOND</code> field.
     * @param millisecond the value used to set the <code>MILLISECOND</code> field.
     * @return a Date with the given fields.
     */
    public static Date getDate(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        return new Date(getTimeMilles(year, month, day, hour, minute, second, millisecond));
    }

    private static long getTimeMilles(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        GregorianCalendar gc = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        if (millisecond != 0)
            gc.set(Calendar.MILLISECOND, millisecond);
        return gc.getTimeInMillis();
    }

    /**
     * Gets start and end fields of a date pattern.
     * @param datePattern
     * @return start and end fields of a date pattern.
     */
    private static int[] getFieldRange(String datePattern) {
        int patternLength = datePattern.length();
        int startField = MILLISECOND;
        int endField = YEAR;
        for (int i=0; i < patternLength; i++) {
            char c = datePattern.charAt(i);
            switch (c) {
                case 'y':
                    startField = YEAR;
                    break;
                case 'Y':
                    startField = YEAR;
                    break;
                case 'M':
                    if (startField > MONTH)
                        startField = MONTH;
                    if (endField < MONTH)
                        endField = MONTH;
                    break;
                case 'd':
                    if (startField > DAY_OF_MONTH)
                        startField = DAY_OF_MONTH;
                    if (endField < DAY_OF_MONTH)
                        endField = DAY_OF_MONTH;
                    break;
                case 'H':
                    if (startField > HOUR_OF_DAY)
                        startField = HOUR_OF_DAY;
                    if (endField < HOUR_OF_DAY)
                        endField = HOUR_OF_DAY;
                    break;
                case 'h':
                    if (startField > HOUR_OF_DAY)
                        startField = HOUR_OF_DAY;
                    if (endField < HOUR_OF_DAY)
                        endField = HOUR_OF_DAY;
                    break;
                case 'm':
                    if (startField > MINUTE)
                        startField = MINUTE;
                    if (endField < MINUTE)
                        endField = MINUTE;
                    break;
                case 's':
                    if (startField > SECOND)
                        startField = SECOND;
                    if (endField < SECOND)
                        endField = SECOND;
                    break;
                case 'S':
                    endField = MILLISECOND;
                    break;
            }
        }
        int[] fieldRange = new int[2];
        fieldRange[START_FIELD] = startField;
        fieldRange[END_FIELD] = endField;
        return fieldRange;
    }

    /**
     * Converts a String in the form yyyyMMddHHmmssSSS if startField=YEAR and
     * endField=MILLISECOND to a date considering start and end fields got from
     * a date pattern. The date pattern is only used to get start and end fields
     * and not the pattern used in the given string date.
     * @param date
     * @param datePattern
     * @return
     */
    public static Date stringToDate(String date, String datePattern) {
        if ( datePattern == null )
            throw new IllegalArgumentException("date parameter does not match datePatern parameter.");

        int[] fieldRange = getFieldRange(datePattern);
        int startField = fieldRange[START_FIELD];
        int endField = fieldRange[END_FIELD];
        return stringToDate(date, startField, endField);
    }

    /**
     * Converts a String in the form yyyyMMddHHmmssSSS if startField=YEAR and
     * endField=MILLISECOND.
     * @param date
     * @param startField
     * @param endField
     * @return
     */
    public static Date stringToDate(String date, int startField, int endField) {
        if ( date == null )
            return null;

        Date result = getDate(1, 1, 1);
        int pos = 0;
        for (int i = startField; i <= endField; i++) {
            if ( isValid(i) )
                if ( date.length() >= pos + getLength(i) ) {
                    setFieldValue(result, i, Integer.parseInt(date.substring(pos, pos + getLength(i))));
                    pos = pos + getLength(i);
                } else
                    throw new IllegalArgumentException("date parameter does not match datePatern parameter.");
        }
        return result;
    }

    /**
     * Converts a date to a String in the form yyyyMMddHHmmssSSS.
     * The pattern is only used to get start and end fields and not
     * to format the date.
     * @param date
     * @param datePattern pattern used to get start and end fields.
     * @return String in the form: yyyyMMddHHmmssSSS.
     */
    public static String dateToString(Date date, String datePattern) {
        int[] fieldRange = getFieldRange(datePattern);
        return dateToString(date, fieldRange[START_FIELD], fieldRange[END_FIELD]);
    }

    /**
     * Converts a date to a String in the form yyyyMMddHHmmssSSS
     * @param date
     * @return String in the form: yyyyMMddHHmmssSSS
     */
    public static String dateToString(Date date) {
        return dateToString(date, YEAR, MILLISECOND);
    }

    /**
     * Converts a date to a String in the form yyyyMMddHHmmssSSS when
     * startField=YEAR and endField=MILLISECOND.
     * @param date
     * @param startField
     * @param endField
     * @return long in the form: yyyyMMddHHmmssSSS
     */
    public static String dateToString(Date date, int startField, int endField) {
        StringBuilder sb = new StringBuilder(17);
        int fv;
        for (int i = startField; i <= endField; i++) {
            if (isValid(i)) {
                fv = getFieldValue(date, i);

                // Adiciona zeros à esquerda se necessário
                int length = getLength(i);// - NumberUtils.numberOfDigits(fv);
                for (int j = 0; j < length; j++)
                    sb.append(0);

                sb.append(fv);
            }
        }
        return sb.toString();
    }

    /**
     * Calculates time between two dates in a given unit.
     * Unit fractions are not considered.
     * @param date1
     * @param date2
     * @param unit
     * @return
     */
    public static long diff(Date date1, Date date2, int unit) {
        return diff(date1, date2, unit, false);
    }

    /**
     * Calculates time between two dates in endField unit.
     * endField unit fractions are not considered.
     * @param date1
     * @param date2
     * @param startField
     * @param endField
     * @return
     */
    public static long diff(Date date1, Date date2, int startField, int endField) {
        return diff(date1, date2, startField, endField, false);
    }

    /**
     * Calculates time between two dates in a given unit.
     * Unit fractions can be considered or not.
     * @param date1
     * @param date2
     * @param unit
     * @param useUnitFraction
     * @return
     */
    public static long diff(Date date1, Date date2, int unit, boolean useUnitFraction) {
        return diff(date1, date2, YEAR, unit, useUnitFraction);
    }

    /**
     * Calculates time between two dates in endField unit.
     * endField unit fractions can be considered or not.
     * @param date1
     * @param date2
     * @param startField
     * @param endField
     * @return
     */
    public static long diff(Date date1, Date date2, int startField, int endField, boolean useUnitFraction) {
        if (useUnitFraction) {
            date1 = getDate(date1, startField, MILLISECOND);
            date2 = getDate(date2, startField, MILLISECOND);
        } else {
            date1 = getDate(date1, startField, endField);
            date2 = getDate(date2, startField, endField);
        }

        long diff = 0;
        if (endField == YEAR || endField == MONTH) {
            Calendar gc1 = getCalendar(date1);
            Calendar gc2 = getCalendar(date2);

            if (gc1.compareTo(gc2) < 0) {
                if (useUnitFraction)
                    gc1.add(endField, 1);
                while (gc1.compareTo(gc2) < 0) {
                    gc1.add(endField, 1);
                    diff++;
                }
            } else if (gc1.compareTo(gc2) > 0) {
                if (useUnitFraction)
                    gc2.add(endField, 1);
                while (gc1.compareTo(gc2) > 0) {
                    gc2.add(endField, 1);
                    diff--;
                }
            }
        } else {
            diff = date2.getTime() - date1.getTime();
            switch (endField) {
                case DAY_OF_MONTH:
                    diff = diff / 24;
                case HOUR_OF_DAY:
                    diff = diff / 60;
                case MINUTE:
                    diff = diff / 60;
                case SECOND:
                    diff = diff / 1000;
            }
        }
        return diff;
    }

    /**
     * Adds the specified amount of time to the given field.
     * For example, to subtract 5 days from a date, you can achieve it by calling:
     * <p><code>DateUtils.add(date, Calendar.DAY_OF_MONTH, -5)</code>.
     * @param field the calendar field.
     * @param amount the amount of date or time to be added to the field.
     * @param date original date to be added to the field.
     * @param field the field used to add.
     * @param amount the amount of date or time to be added to the field.
     * @return date resulted from adding the specified amount of time.
     */
    public static Date sum(Date date, int field, int amount) {
        if (isValid(field)) {
            Calendar c = getCalendar(date);
            c.add(field, amount);
            return new Date(c.getTimeInMillis());
        } else
            throw new IllegalArgumentException("Invalid field: " + field);
    }

    /**
     * Adds the specified amount of time to the given field.
     * For example, to subtract 5 days from a date, you can achieve it by calling:
     * <p><code>DateUtils.add(date, Calendar.DAY_OF_MONTH, -5)</code>.
     * @param field the calendar field.
     * @param amount the amount of date or time to be added to the field.
     * @param date original date to be added to the field.
     * @param field the field used to add.
     * @param amount the amount of date or time to be added to the field.
     */
    public static void add(Date date, int field, int amount) {
        if (isValid(field)) {
            Calendar c = getCalendar(date);
            c.add(field, amount);
            date.setTime(c.getTimeInMillis());
        } else
            throw new IllegalArgumentException("Invalid field: " + field);
    }


    /**
     * Gets a Calendar from a given date.
     * @param date
     * @return
     */
    public static Calendar getCalendar(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        return c;
    }

}
