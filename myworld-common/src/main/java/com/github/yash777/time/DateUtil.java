package com.github.yash777.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/*
    <!-- -->
    <dependency>
        <groupId>commons-codec</groupId>
        <artifactId>commons-codec</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
 */
//@lombok.extern.slf4j.Slf4j
public class DateUtil {
    private static final String DATE_FORMAT = "yyyyMMdd";
    public static final String YEAT_MONTH_DATE_FORMAT = "yyyy-MM-dd";
    public static final String YEAT_MONTH_DATE_SLASH_FORMAT = "yyyy/MM/dd";
    public static final String DATE_FORMAT_YEAR_MONTH = "yyyyMM";
    private static final String MM_DD = "MM-dd";
    public static final String DD_MM = "dd/MM";
    private static final String INFINITY_DATE = "1/1/2200";
    private static final String PERIOD_FORMAT = "yyyy/MM";
    private static final String YYYY_MM_DD_TZ_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String UTC = "UTC";
    public static final String FORMAT_DATE = "dd.MM.yyyy";

    public static final String SPACE = " ";
    
    private DateUtil() {}

    public static Date getMonthDate(final String monthName, Date currentPeriodEndDate) {
        Integer monthNumber = getMonthNumberByName(monthName);
        if (monthNumber == null) {
            return null;
        }
        int yearNumber = getYearFromDate(currentPeriodEndDate);
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthNumber.intValue());
        cal.set(Calendar.YEAR, yearNumber);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
    
    public static boolean validateDate(String orderDate) {
        boolean dateFlag = false;
        String regExp =
                "(^(((0[1-9]|1[0-9]|2[0-8])[.](0[1-9]|1[012]))|((29|30|31)[.](0[13578]|1[02]))|((29|30)[.](0[4,6,9]|11)))[.](19|[2-9][0-9])\\d\\d$)|(^29[.]02[.](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(orderDate);
        if (matcher.matches()) {
            dateFlag = true;
        }
        return dateFlag;
    }

    public static int getYearFromDate(Date currentDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return Integer.parseInt(df.format(currentDate));
    }

    public static int getMMFromDate(Date currentDate) {
        SimpleDateFormat df = new SimpleDateFormat("MM");
        return Integer.parseInt(df.format(currentDate));
    }

    public static int getDDFromDate(Date currentDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return Integer.parseInt(df.format(currentDate));
    }

    public static Integer getMonthNumberByName(final String monthName) {
        Integer monthNumber;
        switch (monthName.toUpperCase()) {
            case "JANUARY":
                monthNumber = Calendar.JANUARY;
                break;
            case "FEBRUARY":
                monthNumber = Calendar.FEBRUARY;
                break;
            case "MARCH":
                monthNumber = Calendar.MARCH;
                break;
            case "APRIL":
                monthNumber = Calendar.APRIL;
                break;
            case "MAY":
                monthNumber = Calendar.MAY;
                break;
            case "JUNE":
                monthNumber = Calendar.JUNE;
                break;
            case "JULY":
                monthNumber = Calendar.JULY;
                break;
            case "AUGUST":
                monthNumber = Calendar.AUGUST;
                break;
            case "SEPTEMBER":
                monthNumber = Calendar.SEPTEMBER;
                break;
            case "OCTOBER":
                monthNumber = Calendar.OCTOBER;
                break;
            case "NOVEMBER":
                monthNumber = Calendar.NOVEMBER;
                break;
            case "DECEMBER":
                monthNumber = Calendar.DECEMBER;
                break;
            default:
                monthNumber = null;
        }
        return monthNumber;
    }

    public static Date getDateWithStartPeriod(Date startPeriod, Date currentPeriodEndDate) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(currentPeriodEndDate);
        // cal.set(Calendar.MONTH, startPeriod.getMonth());
        cal.set(Calendar.HOUR_OF_DAY, startPeriod.getHours());
        cal.set(Calendar.MINUTE, startPeriod.getMinutes());
        cal.set(Calendar.SECOND, startPeriod.getSeconds());
        return cal.getTime();
    }

    /**
     * Method which return month number according to the input date
     *
     * @param startDate
     * @return
     */
    public static int getMonthNumber(final Date startDate) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        return cal.get(Calendar.MONTH);
    }

    public static boolean isOldDate(final Date currentDate, final Date compareWith) {
        if (currentDate == null || compareWith == null) {
            return false;
        }
        if (currentDate.before(compareWith)) {
            return true;
        } else if (currentDate.after(compareWith)) {
            return false;
        } else {
            return true;
        }
    }

    public static Date getMonthDateFormat(String date) {
        if (StringUtils.isNotEmpty(date)) {
            try {
                SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
                return sf.parse(date);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Date getDate(String date) {
        if (StringUtils.isNotEmpty(date)) {
            try {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                return sf.parse(date);
            } catch (Exception e) {
            }
        }
        return null;
    }
    
    public static Date convertDateToUTC(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));      
    	// Format the Date object to UTC time
    	String utcTime = sdf.format(date);
    	Date startDate = DateUtil.getDate(utcTime);
    	return startDate;
    }
    
    public static Date getDateWithDayFormatInputString(String dateString)
    {
        //E MMM dd HH:mm:ss z yyyy
        //String dateString = "Sun Sep 11 00:00:00 IST 2022";
        if (StringUtils.isNotEmpty(dateString)) {
            try {
                SimpleDateFormat sf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                return sf.parse(dateString);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static String getDateString(String date, String format) {
        if (StringUtils.isNotEmpty(date)) {
            try {
                SimpleDateFormat inputSf = new SimpleDateFormat(format);
                SimpleDateFormat outputSf = new SimpleDateFormat("yyyy/MM/dd");
                Date formatedDate = inputSf.parse(date);
                return outputSf.format(formatedDate);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static String getDateConversion(String date, String format) {
        if (StringUtils.isNotEmpty(date)) {
            try {
                SimpleDateFormat inSDF = new SimpleDateFormat(format);
                SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSS'Z'");
                Date formatedDate = inSDF.parse(date);
                return outSDF.format(formatedDate);
            } catch (ParseException ex) {
            }
        }
        return null;
    }

    public static String getMonthDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(MM_DD);
        return sdf.format(date);
    }

    public static Date getInfinityDate() {
        return getMonthDateFormat(INFINITY_DATE);
    }

    public static String getYearMonthDateFormat(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    public static String getYearMonthDateHyphenFormat(Date date) {
        return new SimpleDateFormat(YEAT_MONTH_DATE_FORMAT).format(date);
    }

    public static String getYearMonthDateSlashFormat(Date date) {
        return new SimpleDateFormat(YEAT_MONTH_DATE_SLASH_FORMAT).format(date);
    }

    public static String getYearMonthDateSlashFormat(Date date, String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(YEAT_MONTH_DATE_SLASH_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZone)));
        return sdf.format(date);
    }

    public static String getYearMonthFormat(Date date) {
        return new SimpleDateFormat(DATE_FORMAT_YEAR_MONTH).format(date);
    }

    public static String getPeriodName(String periodName) {
        String year = periodName.substring(0, 4);
        int month = Integer.parseInt(periodName.substring(4));
        String monthName = Month.of(month).name().substring(0, 1).toUpperCase() + Month.of(month).name().substring(1).toLowerCase();
        return monthName + SPACE + year;
    }

    public static String getMonthYearName(String periodName) {
        String year = periodName.substring(0, 4);
        int month = Integer.parseInt(periodName.substring(4));
        String monthName = Month.of(month).name().substring(0, 1).toUpperCase() + Month.of(month).name().substring(1, 3).toLowerCase();
        return monthName + SPACE + year;
    }

    public static String getShortedPeriodName(String periodName) {
        String year = periodName.substring(2, 4);
        int month = Integer.parseInt(periodName.substring(4));
        String monthName = Month.of(month).name().substring(0, 1).toUpperCase() + Month.of(month).name().substring(1, 3).toLowerCase();
        return monthName + SPACE + year;
    }

    public static String getPeriodNameByLocale(final String periodName, final Locale locale, String inputFormat, String outputFormat) {
        String formattedDate = null;
        try {
            Date inputDate = new SimpleDateFormat(inputFormat).parse(periodName);
            if (null != inputDate) {
                return new SimpleDateFormat(outputFormat, locale).format(inputDate);
            }
            return null;
        } catch (Exception e) {
        }
        return formattedDate;
    }

    public static Date getFutureDate(Date currentDate, int monthYearDate, int incrementTo) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(monthYearDate, incrementTo);
        return cal.getTime();
    }

    public static Date getFutureDateWithLastDateOfMonth(Date currentDate, int monthYearDate, int incrementTo) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(monthYearDate, incrementTo);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getFirstDayOfMonthInclusiveCurrentMonth(Date inputDate, int monthYearDate, int incrementTo) {
        Date currentDate = getStartDateOfMonth(inputDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        // Inclusive current month
        cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) - 1));
        cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) + incrementTo));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getStartDateOfMonth(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getOneyearOldDate(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    /**
     * Method which return year and month number according to the input date
     *
     * @param date
     * @return
     */
    public static String getYearMonth(final String date) {
        String[] str = date.split("/");
        return str[2].concat(str[0]);
    }

    public static boolean isValidStartDate(Date startDate, Date periodStartDate) {
        periodStartDate = getZeroTimeDate(periodStartDate);
        startDate = getZeroTimeDate(startDate);
        if (startDate.compareTo(periodStartDate) <= 0) {
            return true;
        }
        return false;
    }

    public static boolean isInvalidOrderDate(Date startDate, Date periodStartDate) {
        boolean isOrderInvalid = false;
        periodStartDate = getZeroTimeDate(periodStartDate);
        startDate = getZeroTimeDate(startDate);
        if (startDate.compareTo(periodStartDate) < 0 || startDate.compareTo(periodStartDate) == 0) {

        } else {
            isOrderInvalid = true;
        }
        return isOrderInvalid;
    }


//    public static int getMonthsCount(Date fromDate, Date toDate) {
//        org.joda.time.LocalDate date = new org.joda.time.LocalDate(fromDate);
//        org.joda.time.LocalDate endDate = new org.joda.time.LocalDate(toDate);
//        return org.joda.time.Months.monthsBetween(date, endDate).getMonths();
//    }
//
//    public static Date addDaysAndMonth(Date fromDate) {
//        org.joda.time.LocalDate date = new org.joda.time.LocalDate(fromDate);
//        date = date.plusMonths(1);
//        date = date.plusDays(14);
//        return date.toDate();
//    }

    public static boolean isValidEndDate(Date endDate, Date periodEndDate) {
        periodEndDate = getZeroTimeDate(periodEndDate);
        endDate = getZeroTimeDate(endDate);
        if (endDate.compareTo(periodEndDate) >= 0) {
            return true;
        }
        return false;
    }

    public static boolean dateCompare(Date endDate, Date periodEndDate) {
        if(endDate!=null && periodEndDate!=null) {
            periodEndDate = getZeroTimeDate(periodEndDate);
            endDate = getZeroTimeDate(endDate);
            if (endDate.compareTo(periodEndDate) <= 0) {
                return true;
            }
        }     
        return false;
    }

    public static Date getZeroTimeDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }

    /**
     * Adds the required months to the given date and adjust the days exactly to that month end date
     * 
     * @param date the date to be updates
     * @param months the months to be added to the date
     * @return the updated date and adjusted to the exact end day of that month
     */
    public static Date addMonths(Date date, int months) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeWithAddedMonths = localDateTime.plusMonths(months);
        LocalDateTime localDateTimeWithEndDate =
                localDateTimeWithAddedMonths.withDayOfMonth(localDateTimeWithAddedMonths.getMonth().length(isLeapYear(localDateTimeWithAddedMonths)));
        return Date.from(localDateTimeWithEndDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Check if the year is leap year or not
     * 
     * @param localDateTime the localDateTime to check
     * @return true if year is leap otherwise false
     */
    public static boolean isLeapYear(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return false;
        }
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate.isLeapYear();
    }

    /**
     * Date comparison with just the date not with the time
     * 
     * @param date1 is the first date to compare
     * @param date2 is the second date to comapare
     * @return the value 0 if date1 is equal to date2; a value less than 0 if date1 is less than the
     *         date2; and a value greater than 0 if date1 is greater than the date2.
     */
    public static int compareTo(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).compareTo(sdf.format(date2));
    }

    public static int is(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).compareTo(sdf.format(date2));
    }

    public static boolean isBeforeInclusive(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).compareTo(sdf.format(date2)) <= 0;
    }

    public static boolean isAfterInclusive(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).compareTo(sdf.format(date2)) >= 0;
    }

    public static boolean isAfter(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).compareTo(sdf.format(date2)) > 0;
    }

    public static boolean isBefore(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).compareTo(sdf.format(date2)) < 0;
    }

    public static boolean isBetweenInclusive(Date startDate, Date endDate, Date reqDate) {
        boolean status = false;
        if (null != startDate && null != endDate && null != reqDate) {
            return (compareTo(startDate, reqDate) <= 0 && compareTo(reqDate, endDate) <= 0);
        }
        return status;
    }

    public static boolean isDateBetweenInclusive(Date startDate, Date endDate, Date reqDate) {
        boolean status = false;
        if (null == startDate || null == endDate || null == reqDate) {
            return status;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (sdf.format(startDate).compareTo(sdf.format(reqDate)) <= 0 && sdf.format(reqDate).compareTo(sdf.format(endDate)) <= 0) {
            status = true;
        }
        return status;
    }

    public static boolean isDateBetweenInclusive(Date startDate, Date endDate, Date effStartDate, Date effEndDate) {
        if (startDate == null || endDate == null || effStartDate == null || effEndDate == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!(sdf.format(startDate).compareTo(sdf.format(effStartDate)) >= 0 && sdf.format(startDate).compareTo(sdf.format(effEndDate)) <= 0)) {
            return false;
        }
        if (!(sdf.format(endDate).compareTo(sdf.format(effStartDate)) >= 0 && sdf.format(endDate).compareTo(sdf.format(effEndDate)) <= 0)) {
            return false;
        }
        return true;
    }

    public static Date addMonths(int noOfMonths, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, noOfMonths);
        return cal.getTime();
    }

    public static int getDiffBetweenDatesInMonths(Date startDate, Date endDate) {
        Calendar sDate = Calendar.getInstance();
        Calendar eDate = Calendar.getInstance();
        sDate.setTime(startDate);
        eDate.setTime(endDate);
        return (eDate.get(Calendar.YEAR) - sDate.get(Calendar.YEAR)) * 12 + eDate.get(Calendar.MONTH) - sDate.get(Calendar.MONTH);
    }

    public static long getDiffBetweenDatesInDays(Date startDate, Date endDate) {
        long diff = startDate.getTime() - endDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static Date updateDateWithEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date updateDateWithStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        return calendar.getTime();
    }

    public static Date getNextOneYearDate(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // minus number would decrement the days
        return cal.getTime();
    }

    public static Date addDaysAndSetWithStartTime(Date date, int days) {
        if (date == null) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // minus number would decrement the days
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        return cal.getTime();
    }


    public static Date addDaysAndSetWithEndTime(Date date, int days) {
        if (date == null) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // minus number would decrement the days
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public static int getHour(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static Boolean isValidResponsorDate(final Date responsorDate) {
        Boolean responsorDateFlag = Boolean.FALSE;
        if (responsorDate == null) {
            return responsorDateFlag;
        }
        Date currentStartDate = getZeroTimeDate(getCurrentDate());
        Date responsorStartDate = getZeroTimeDate(responsorDate);
        if (currentStartDate.compareTo(responsorStartDate) > 0) {
            //LOG.info("Validation failed Responsor Date: {}, Responsor Start Date: {}, Current Start Date: {}" + responsorDate, responsorStartDate, currentStartDate);
            responsorDateFlag = Boolean.FALSE;
        } else {
            responsorDateFlag = Boolean.TRUE;
        }
        return responsorDateFlag;
    }

    public static String getBonusPeriodFormat(Date date) {
        if (date != null) {
            Date currentDate = addMonths(date, 1);
            Date futureDate = addMonths(currentDate, 8);
            SimpleDateFormat sdf = new SimpleDateFormat(PERIOD_FORMAT);
            return sdf.format(currentDate) + " - " + sdf.format(futureDate);
        }
        return null;
    }

    public static String getDateString(Date enrollmentDate) {
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        String dateAsString = df.format(enrollmentDate);
        return dateAsString;
    }

    public static String getDateString(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static Boolean isLeapYearManual(Date date) {
        if (date == null) {
            return Boolean.FALSE;
        }
        int yyyy = getYearFromDate(date);
        if ((yyyy % 400 == 0) || ((yyyy % 4 == 0) && (yyyy % 100 != 0))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Date adjustFebruaryDate(Date date) {
        // For ex: 29-02-2020 then it will return the date as 28-02-2020
        if (date == null) {
            return null;
        }
        int mm = getMMFromDate(date);
        int dd = getDDFromDate(date);
        if (isLeapYearManual(date) && mm == 2 && dd == 29) {
            date = addDays(date, -1);
        }
        return date;
    }

    public static boolean isValidDate(String dateString) {
        if (dateString == null || !dateString.matches("\\d{4}/[01]\\d/[0-3]\\d"))
            return false;
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        df.setLenient(false);
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    public static boolean isValidDate_MonthDateYearSlashFormat(String dateString) {
        if (dateString == null || !dateString.matches("[01]\\d/[0-3]\\d/\\d{4}"))
            return false;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        df.setLenient(false);
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    /**
     * Convert to given specific time zone from a given date
     * 
     * @param date
     * @param timeZoneName
     * @return
     * @throws ParseException
     */
    public static Date convertToSpecificTimezone(Date date, String timeZoneName) throws ParseException {
        if (date == null) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_TZ_FMT);
        if (!StringUtils.isBlank(timeZoneName) && ZoneId.of(timeZoneName) != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZoneName)));
        } else {
            //LOG.info("convertToSpecificTimezone: invalid time zone with name:{}", timeZoneName);
        }
        return dateFormat.parse(dateFormat.format(date));
    }

    public static Date updateDateWithEndTime(long date, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZone)));
        if (!StringUtils.equals(TimeZone.getDefault().getID(), TimeZone.getTimeZone(ZoneId.of(timeZone)).getID())) {
            calendar.setTimeInMillis(date + TimeZone.getTimeZone(ZoneId.of(timeZone)).getRawOffset());
        } else {
            calendar.setTimeInMillis(date);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date updateDateWithStartTime(long date, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZone)));
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        return calendar.getTime();
    }

    public static Date convertToSpecificTimezone(long date, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZone)));
        calendar.setTimeInMillis(date + TimeZone.getTimeZone(ZoneId.of(timeZone)).getRawOffset());
        return calendar.getTime();
    }
    
    public static long getHoursDifferenceBTWGivenDateAndCurrentDate(Date date) {
    	Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - date.getTime();
        return diffInMillis / (1000 * 60 * 60);  
    }
    

    public static Date addDaysAndSetWithEndTime(Date date, int days, String timeZone) {
    	//timeZone = ""
        //LOG.info("Actual Date System Time Zone:" + date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZone)));
        calendar.setTimeInMillis(date.getTime() + TimeZone.getTimeZone(ZoneId.of(timeZone)).getRawOffset());
        //LOG.info("Actual Date With Time Zone:" + calendar.getTime());
        calendar.add(Calendar.DATE, days - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        //LOG.info("Updated Date With End Of Time:" + calendar.getTime());
        return calendar.getTime();
    }

    public static Date addYears(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    public static String getValue(Date date, String format) {
        if (date != null) {
            try {
                SimpleDateFormat sdf = null;
                if (StringUtils.isBlank(format)) {
                    sdf = new SimpleDateFormat(YEAT_MONTH_DATE_FORMAT);
                } else {
                    sdf = new SimpleDateFormat(format);
                }
                return sdf.format(date);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Validate give string date value is in the correct date format Strict use only if your date is in
     * this pattern yyyy/MM/dd (or) yyyy-MM-dd
     * 
     * @param value, contains date as string
     * @param sdf, contains SimpleDateFormat
     * @return true if the given string date value is in the correct date format, otherwise false
     */
    public static boolean validateDateFormat(String value, SimpleDateFormat sdf) {
        try {
            if (sdf == null || StringUtils.isBlank(value) || sdf.toPattern().length() != value.length()) {
                return Boolean.FALSE;
            }
            Integer.parseInt(String.valueOf(value.charAt(value.length() - 1)));
            Date date = sdf.parse(value);
            if (date != null) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static boolean validateSalesOrderDateFormat(String value, SimpleDateFormat sdf) {
        try {
            Date date = sdf.parse(value);
            if (date != null) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Return the give date with specified format, Returns null in case date or format is null or blank
     * 
     * @param date, contains date
     * @param format, contains date format
     * @return String
     */
    public static String formatDate(Date date, String format) {
        String strDate = null;
        if (date == null || StringUtils.isBlank(format)) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            strDate = sdf.format(date);
        } catch (Exception ex) {
        }
        return strDate;
    }
    
    /**
     * Return the formatted Date for the given Date, Date format and TimeZone. 
     * Returns null for date/format/timeZone invalid/null/blank values 
     * 
     * @param date
     * @param format
     * @param timeZone
     * @return
     */
    public static String formatDate(Date date, String format, String timeZone) {
        if (StringUtils.isNotBlank(timeZone) && null != date) {
            if (StringUtils.isBlank(format)) {
                format = YYYY_MM_DD_TZ_FMT;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
                return sdf.format(date) + " " + sdf.getTimeZone().getID();
            } catch (Exception ex) {
            }

        }
        return null;
    }

    public static boolean isAfterInclusive(String date1, String date2, String format) {
        try {
            if (null == date1 || null == date2) {
                return false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date1).compareTo(sdf.parse(date2)) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isEqual(String date1, String date2, String format) {
        try {
            if(null == date1 || null == date2) {
                return false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date1).compareTo(sdf.parse(date2)) == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static int getMMFromDD_MMFormat(String date) {
        return Integer.parseInt(date.substring(3));
    }
    
    public static int getDDFromDD_MMFormat(String date) {
        return Integer.parseInt(date.substring(0, 2));
    }

	public static Date getZeroMilliTimeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		return date;
	}
	
    /**
     * This method will convert the given date in the E MMM dd HH:mm:ss z yyyy format'
     * Example input parameter = 2023-11-29 17:31:34 return = Wed Nov 29 17:31:34 IST 2023
     * @param Date
     * @return String
     */
    public static String getStringWithDayFormatInputDate(Date date) {
        if (date != null) {
            try {
                SimpleDateFormat sf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                return sf.format(date);
            } catch (Exception e) {
                //LOG.error(MessageFormat.format("Error while formating the date {0} , {1} ", date, e));
            }
        }
        return null;
    }
}
