package com.github.yash777.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

//import org.springframework.batch.item.Chunk;
/*
<dependency>
    <groupId>org.springframework.batch</groupId>
    <artifactId>spring-batch-infrastructure</artifactId>
    <version>5.1.2</version>
</dependency>
 */
public class LocalDateTimeUtil {
	public static void main(String[] args) {
		// Example LocalDateTime
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		// Print the result
		System.out.println("LocalDateTime: " + currentDateTime);
		Date currentDate = getDateFromLocalDateTime(currentDateTime);
		System.out.println("Date: " + currentDate);
		System.out.println("LocalDateTime: " + getLocalDateTime( currentDate ));
		
		// Get time difference in milliseconds
		System.out.println("Date - Time: " + currentDate.getTime());
		LocalTime localTime = currentDateTime.toLocalTime();
		System.out.println("LocalDateTime - LocalTime Diff: " + localTime);
		
		
		// -----------
		try {
            Thread.sleep(2000); // Simulate some processing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		LocalDateTime startLocalDateTime = currentDateTime;
		 LocalDateTime endLocalDateTime = LocalDateTime.now();
		 Date startTime = currentDate;
	        Date endTime = new Date(); // Current time as end
	        
	        System.out.println("LocalDateTime: " + getLocalDateTimeDiffInMillis(startLocalDateTime, endLocalDateTime) );
	        
	        // Get time difference in milliseconds
	        long timeDiffMillis = endTime.getTime() - startTime.getTime();
	        System.out.println("Time difference in milliseconds: " + timeDiffMillis);
	        
	        // Convert milliseconds to seconds
	        long timeDiffSeconds = timeDiffMillis / 1000;
	        System.out.println("Time difference in seconds: " + timeDiffSeconds);
	        
//	        // Assuming Chunk<? extends Emp> is created somewhere in your Spring Batch process
//	        Chunk<? extends String> chunk = getChunk();  // Replace with actual Chunk initialization
//	        // Convert Chunk<? extends Emp> to List<? extends Emp>
//	        List<? extends String> list = chunk.getItems();  // This is already a List<? extends Emp>
//	        // Now you can use the list as needed
//	        System.out.println("List size: " + list.size());
	}
//    // Method for getting a sample Chunk (you can replace it with actual logic)
//    private static Chunk<? extends String> getChunk() {
//        // Create a Chunk with some sample items (replace with actual logic)
//        return new Chunk<String>(Arrays.asList("1", "2"));  // Sample initialization
//    }
	private static long getLocalDateTimeDiffInMillis(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
		// Calculate the time difference using Duration
		Duration duration = Duration.between(startLocalDateTime, endLocalDateTime);
        
        // Get the difference in seconds
        long timeDifferenceSeconds = duration.getSeconds();
        System.out.println("LocalDateTime difference in seconds: " + timeDifferenceSeconds);
        
        // Get the difference in milliseconds (optional)
        long timeDifferenceMillis = duration.toMillis();
        System.out.println("LocalDateTime difference in milliseconds: " + timeDifferenceMillis);
        return timeDifferenceMillis;
	}
	
	
	public static Date getDateFromLocalDateTime(LocalDateTime localDateTime) {
		// Convert LocalDateTime to Date
		Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		System.out.println("Date: " + date);
		return date;
	}
	public static LocalDateTime getLocalDateTime(Date date) {
		LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        System.out.println("LocalDateTime: " + localDateTime);
        return localDateTime;
	}
	
}
