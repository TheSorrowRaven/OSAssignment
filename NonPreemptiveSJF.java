public class NonPreemptiveSJF extends SchedulingAlgorithm {


    private int waitForTime = 0;
    private Process processingProcess = null;


    public NonPreemptiveSJF(Scheduling scheduling) {
        super(scheduling);
    }
    public NonPreemptiveSJF(Scheduling scheduling, boolean step, boolean unitStep){
        super (scheduling, step, unitStep);
    }


    @Override
    protected boolean stepTo(int time) {
        
        if (processingProcess == null || time == waitForTime){
            Process shortest = getShortestTime();
            if (shortest == null){
                return queueEmpty();
            }
            processingProcess =  shortest;
            int burstTime = shortest.burstTime;

            waitForTime = burstTime + time;
            shortest.remainingTime = 0;
            shortest.finishTime = waitForTime;
            movePendingProcessToCompleted(shortest);

            setNextTime(waitForTime);

            processLogs.add(new ProcessLog(shortest, time, waitForTime));
        }

        return queueEmpty();
    }
    
    protected Process getProcess(){
        return getShortestTime();
    }

}
