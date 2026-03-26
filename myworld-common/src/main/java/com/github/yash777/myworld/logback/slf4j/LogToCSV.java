package com.github.yash777.myworld.logback.slf4j;
//String logFile = "C:\\Users\\ymerugu\\Downloads\\ES-DEV-CR\\commissionrun.log"; // Path to your log file
//String outputFile = "C:\\Users\\ymerugu\\Downloads\\ES-DEV-CR\\commissionrun_output.csv";   // CSV output

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class LogToCSV {

    static class TaskInfo {
        String threadName;
        String clientId;
        Date startTime;
    }

    static class OutputRow {
        String threadName;
        String clientId;
        String key;
        long durationInSeconds;

        @Override
        public String toString() {
            return String.format("%s,%s,%s,%d", threadName, clientId, key, durationInSeconds);
        }
    }

    public static void main(String[] args) throws Exception {
//        String logFile = "log.txt";         // Your input log file
//        String outputFile = "output.csv";   // CSV output
      String logFile = "C:\\Users\\ymerugu\\Downloads\\ES-DEV-CR\\commissionrun.log"; // Path to your log file
      String outputFile = "C:\\Users\\ymerugu\\Downloads\\ES-DEV-CR\\commissionrun_output.csv";   // CSV output

        
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        // Regex Patterns
        Pattern threadPattern = Pattern.compile("\\[(.*?)\\]");
        Pattern finishPattern = Pattern.compile("Finished CR Task .* for clientId (ET\\d+)");
        Pattern releasePattern = Pattern.compile("Released Lock for client (ET\\d+) with key:: (ET\\d+)");
        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

        // Active tasks by thread
        Map<String, TaskInfo> currentTaskByThread = new HashMap<>();
        List<OutputRow> outputRows = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            Matcher threadMatcher = threadPattern.matcher(line);
            Matcher dateMatcher = datePattern.matcher(line);

            if (!threadMatcher.find() || !dateMatcher.find()) continue;

            String threadName = threadMatcher.group(1);
            Date timestamp = sdf.parse(dateMatcher.group());

            Matcher finishMatcher = finishPattern.matcher(line);
            Matcher releaseMatcher = releasePattern.matcher(line);

            // Finished CR Task
            if (finishMatcher.find()) {
                String clientId = finishMatcher.group(1);
                TaskInfo task = new TaskInfo();
                task.threadName = threadName;
                task.clientId = clientId;
                task.startTime = timestamp;
                currentTaskByThread.put(threadName, task);
            }

            // Released Lock
            else if (releaseMatcher.find()) {
                String clientId = releaseMatcher.group(1);
                String key = releaseMatcher.group(2);

                TaskInfo task = currentTaskByThread.get(threadName);
                if (task != null && task.clientId.equals(clientId)) {
                    long duration = (timestamp.getTime() - task.startTime.getTime()) / 1000;

                    OutputRow row = new OutputRow();
                    row.threadName = threadName;
                    row.clientId = clientId;
                    row.key = key;
                    row.durationInSeconds = duration;
                    outputRows.add(row);
                }
            }
        }

        // Write header
        writer.write("Thread Name,Processed Client,Key,Time Taken (sec)");
        writer.newLine();

        // Write output rows
        for (OutputRow row : outputRows) {
            writer.write(row.toString());
            writer.newLine();
        }

        reader.close();
        writer.close();

        System.out.println("CSV written to: " + outputFile);
    }
}
