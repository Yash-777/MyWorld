package com.github.yash777.myworld.api.http.config;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

//import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Convert the date using provided date formats. Date formats is empty or not able to convert with
 * custom date conversion, then use the default StdDeserializer
 * 
 */
public class CustomDateStdDeserializer extends StdDeserializer<Date> {
    private static final long serialVersionUID = 1L;
    private static CopyOnWriteArrayList<String> dateFormats = new CopyOnWriteArrayList<>();
    private static final Logger Logger = LoggerFactory.getLogger(CustomDateStdDeserializer.class);

    private static Pattern numberPattern = Pattern.compile("-?\\d+?");

    protected CustomDateStdDeserializer(Class<?> vc) {
        super(vc);
    }

    public CustomDateStdDeserializer() {
        super(Date.class);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        String dateStr = jsonParser.getText();
        if (StringUtils.isBlank(dateStr)) {
            // date string is null or blank, then return null
            return null;
        }
        // In case if the given input is a number, then convert to date based on the given time zone
        try {
            if (numberPattern.matcher(dateStr).matches()) {
                long dateInMillis = Long.valueOf(dateStr);
                return timeZoneConvert(context, dateInMillis);
            }
        } catch (Exception e) {
            Logger.info("deserialize: failed to convert value {} given time zone {}", dateStr, context.getTimeZone().getID());
        }
        //if (CollectionUtils.isEmpty(dateFormats)) {
        if (dateFormats != null && !dateFormats.isEmpty()) {
            // date formats is empty, then use default conversion
            Logger.debug("deserialize: Warning please add API date formats in Global Settings");
            return _parseDate(jsonParser, context);
        }
        for (String datePattern : CustomDateStdDeserializer.dateFormats) {
            if (!StringUtils.isBlank(datePattern)) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat(datePattern);
                    Date date = dateFormat.parse(dateStr);
                    return timeZoneConvert(context, date.getTime());
                } catch (Exception e) {
                    // Alert in logs not able to convert
                    Logger.info("deserialize: failed to convert value {} given date format {}", dateStr, datePattern);
                }
            }
        }
        // Final use default StdDeserializer parser to parse the given date
        return _parseDate(jsonParser, context);
    }

    private Date timeZoneConvert(DeserializationContext context, long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(context.getTimeZone());
        calendar.setTimeInMillis(dateInMillis + context.getTimeZone().getRawOffset());
        return calendar.getTime();
    }

    public static List<String> getDateFormats() {
        return dateFormats;
    }

    public static void addDateFormats(List<String> dateFormats) {
        if (dateFormats != null && !dateFormats.isEmpty()) {
            CustomDateStdDeserializer.dateFormats.addAllAbsent(dateFormats);
        }
    }

}
