public class Timer {
    private long startTime, endTime;
    private int[] timeElapsed;
    private boolean hasStarted, hasEnded;

    public Timer() {
        this.hasStarted = false;
        this.hasEnded = false;
        this.timeElapsed = new int[4];
    }

    public Timer(Timer timer1, Timer timer2) {
        this.hasStarted = false;
        this.hasEnded = false;
        this.startTime = timer1.getStartTime() > timer2.getStartTime() ? timer2.getStartTime() : timer1.getStartTime();
        this.endTime = timer1.getEndTime() < timer2.getEndTime() ? timer2.getEndTime() : timer1.getEndTime();
        this.timeElapsed = timer1.combineTime(timer2);
    }

    public boolean start() {
        if(!this.hasStarted) {
            this.startTime = System.currentTimeMillis();
            this.hasStarted = true;
        }
        return this.hasStarted;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public boolean end() {
        if(!this.hasEnded) {
            this.endTime = System.currentTimeMillis();
            this.timeElapsed = timeConverter(endTime - startTime);
            this.hasEnded = true;
        }
        return this.hasEnded;
    }

    public long getEndTime() {
        return this.endTime;
    }

    /**
     * Clears the Timer object for reuse by user
     */
    public void clear() {
        this.startTime = 0;
        this.endTime = 0;
        this.hasStarted = false;
        this.hasEnded = false;
        this.timeElapsed = new int[4];
    }

    /**
     * Combines the time elapsed of two timer objects based on the earliest start time and latest end time
     * @param lhs first timer object
     * @param timer second timer object
     * @return the new combined time
     */
    public int[] combineTime(Timer timer) {
        long tempEndTime = this.getEndTime() < timer.getEndTime() ? timer.getEndTime() : this.getEndTime();
        long tempStartTime = this.getStartTime() > timer.getStartTime() ? timer.getStartTime() : this.getStartTime();
        return timeConverter(tempEndTime - tempStartTime);
    }

    /**
     * Combines the time elapse from two timers independent of when they started and ended
     * @param lhs first timer object
     * @param timer second timer object
     * @return the new flat-combined time
     */
    public int[] combineTimeFlat(Timer timer) {
        long combinedTime = this.getEndTime()-this.getStartTime() + timer.getEndTime()-timer.getStartTime();
        return timeConverter(combinedTime);
    }

    /**
     * Returns the difference between the two elapsed times
     * @peram lhs the first timer object
     * @param timer the second timer object
     * @return the difference
     */
    public int[] timeDifferenceFlat(Timer timer) {
        long timediffer = Math.abs((this.getEndTime()-this.getStartTime()) - (timer.getEndTime()-timer.getStartTime()));
        return timeConverter(timediffer);
    }

    public String getTimeFromStart() {
        return getTimeFromStart(this.endTime);
    }

    /**
     * Gets a string representing the time that has elapsed since the start time
     * @param endTime the ending time
     * @return
     */
    public String getTimeFromStart(long endTime) {
        return timeFormatter(endTime - this.startTime);
    }

    public int[] getTimeWithoutFormat() {
        return getTimeWithoutFormat(this.endTime);
    }

    /**
     * Gets the time elapsed in the format of {hours, minutes, seconds, milliseconds}
     * @param endTime the ending time
     * @return
     */
    public int[] getTimeWithoutFormat(long endTime) {
        return timeConverter(endTime - this.startTime);
    }

    /**
     * Formats the time elapsed in a nice way for outputting
     * @param milliseconds the time elapsed in milliseconds
     * @return
     */
    private static String timeFormatter(long milliseconds) {
        if(milliseconds < 0) return "Cannot have negative time";
        int[] timeValues = timeConverter(milliseconds);
        String time = String.format("%s hours, %s minutes, %s seconds, and %s milliseconds", timeValues[0], timeValues[1], timeValues[2], timeValues[3]);
        if(timeValues[0] == 0) {
            time = String.format("%s minutes, %s seconds, and %s milliseconds", timeValues[1], timeValues[2], timeValues[3]);
        }
        if(timeValues[0] == 0 && timeValues[1] == 0) {
            time = String.format("%s seconds, and %s milliseconds", timeValues[2], timeValues[3]);
        }
        if(timeValues[0] == 0 && timeValues[1] == 0 && timeValues[2] == 0) {
            time = String.format("%s milliseconds",  timeValues[3]);
        }
        return time;
    }

    /**
     * Converts time from milliseconds up to hours, minutes, seconds, and milliseconds
     * @param milliseconds the time elapsed in milliseconds
     * @return
     */
    private static int[] timeConverter(long milliseconds) {
        if(milliseconds <= 0) {
            return new int[] {0,0,0,0};
        }
        int seconds = millisToSecond(milliseconds);
        int minutes = secondsToMinute(seconds);
        int hours = minutesToHour(minutes);
        minutes = minutes - hours * 60;
        seconds = seconds - minutes * 60 - hours * 60 * 60;
        milliseconds = milliseconds - seconds * 1000 - minutes * 60 * 1000 - hours * 60 * 60 * 1000;
        return new int[]{hours, minutes, seconds, (int) milliseconds};
    }

    private static int millisToSecond(long millis) {
        return (int)(millis / 1000);
    }

    private static int secondsToMinute(int seconds) {
        return seconds / 60;
    }

    private static int minutesToHour(int minutes) {
        return minutes / 60;
    }
}
