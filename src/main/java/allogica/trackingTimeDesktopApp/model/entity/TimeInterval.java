package allogica.trackingTimeDesktopApp.model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeInterval {
    private LocalDateTime start;
    private LocalDateTime end;

    public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public TimeInterval(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
    
    public TimeInterval(LocalDateTime start) {
        this.start = start;
        this.end = start.with(LocalTime.MAX);
    }
    
    
    public Duration intervalDuration() {
    	Duration intervDur = Duration.between(start, end);
    	return intervDur;
    }
    
    public Boolean isLessThan(Duration intervDur) {
    	if (this.intervalDuration().compareTo(intervDur) < 0) {
    		return true;
    	}
    	else return false;
    }

    public boolean contains(LocalDateTime dateTime) {
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }
    
    public static List<TimeInterval> addToInterval(int intervalSize, List<LocalDateTime> beginingOfInterval, List<LocalDateTime> endingOfInterval){
    	List <TimeInterval> interval = new ArrayList<TimeInterval>();
    	for (int i = 0; i < intervalSize - 1; i++) {
			interval.add(new TimeInterval(beginingOfInterval.get(i), endingOfInterval.get(i+1)));
    }
    	return interval;
    }
    
    public static TimeInterval checksIfListContainsThatStart(List<TimeInterval> intervals, LocalDateTime startTime) {
    	for (TimeInterval interval : intervals) {
    		if (interval.contains(startTime)){
    			return interval;
    		}
    	}
    	return null;
    }
    
    public static TimeInterval checksIfListContainsThatInterval(List<TimeInterval> intervals, LocalDateTime startTime, LocalDateTime endTime) {
    	for (TimeInterval interval : intervals) {
    		if (interval.contains(startTime) && interval.contains(endTime)){
    			return interval;
    		}
    	}
    	return null;
    }
    
    public static void printTimeInterval(List<TimeInterval> intervals) {
    	long counter = 0;
    	for  (TimeInterval interval : intervals) {
    		System.out.println("The interval number: " + counter + " starts at: " + interval.getStart() + " and ends at " + interval.getEnd() + ";");
    		counter++;
    	}
    }
    
    public static List<TimeInterval> removeIntervalLessThan(List<TimeInterval> intervalList, Duration intervDur) {
//    	Remove in the opposite order to not mess the index
    	for (int i = intervalList.size() - 1; i > -1; i--) {
    		if (intervalList.get(i).isLessThan(intervDur)) {
    			intervalList.remove(i);
    		}
    	}
    	return intervalList;
    }
	
}
