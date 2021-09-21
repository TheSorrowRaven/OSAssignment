public class PreemptiveSJF extends SchedulingAlgorithm {

    

    private Process lastProcessingProcess = null;
    private int lastProcessStartTime = 0;

    public PreemptiveSJF(Scheduling scheduling) {
        super(scheduling);
    }
    public PreemptiveSJF(Scheduling scheduling, boolean step, boolean unitStep){
        super (scheduling, step, unitStep);
    }

    @Override
    protected boolean stepTo(int time) {
        
        if (lastProcessingProcess != null){
            lastProcessingProcess.remainingTime -= (time - lastProcessStartTime);
            processLogs.add(new ProcessLog(lastProcessingProcess, lastProcessStartTime, time, true));
            if (lastProcessingProcess.remainingTime == 0){
                lastProcessingProcess.finishTime = time;
                movePendingProcessToCompleted(lastProcessingProcess);
                lastProcessingProcess = null;
            }
        }

        Process shortest = getProcess();
        if (shortest != null){
            int timeLeft = shortest.remainingTime;
            int potentialFinishTime = time + timeLeft;
            setNextTime(potentialFinishTime);
            lastProcessingProcess = shortest;
            lastProcessStartTime = time;

            return false;
        }
        lastProcessingProcess = null;


        return queueEmpty();
    }

    protected Process getProcess(){
        return getShortestTime();
    }
    
}
