package time;

import java.util.ArrayList;
import java.util.List;

public class TimeRange {
    private final int from, to;

    public TimeRange(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public boolean isAllDay() {
        return from == 0 && to == 24;
    }

    public List<Time> expand() {
        List<Time> times = new ArrayList<>();
        int h = from;
        do {
            h = (h + 1) % 24;
        } while (h != (to % 24));
        return times;
    }

    public static List<Time> allDay() {
        return new TimeRange(0, 24).expand();
    }

    public int totalHours() {
        if (from <= to) return to - from;
        return (24 - from) + to;
    }

    public static List<TimeRange> combine(TimeRange... ranges) {
        List<TimeRange> result = new ArrayList<>();
        for (TimeRange range : ranges) {
            result.add(range);
        }
        return result;
    }

    public boolean contains(Time time) {
        int hour = time.getHour();
        if (from <= to) {
            return hour >= from && hour < to;
        } else {
            return hour >= from || hour < to;
        }
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}