import java.util.LinkedList;

public class RoundRobin extends SchedulingAlgorithm {

    protected Process toAddProcessNextStep; //add after addArrivedProcess/addProcessToProessingQueue
    protected LinkedList<Process> processingQueue = new LinkedList<Process>();
    protected int timeTillQFinish;
    protected boolean processing;

    public RoundRobin(Scheduling scheduling) {
        super(scheduling);
    }
    public RoundRobin(Scheduling scheduling, boolean step, boolean unitStep){
        super (scheduling, step, unitStep);
    }

    protected int quantum(){
        return scheduling.quantum;
    }

    @Override
    protected void addArrivedProcess(Process process){
        super.addArrivedProcess(process);
        addProcessToProcessingQueue(process);
    }

    protected void addProcessToProcessingQueue(Process process){
        processingQueue.add(process);
    }

    @Override
    protected boolean stepTo(int t) {
        if (processing && t != timeTillQFinish){
            return false;
        }

        if (toAddProcessNextStep != null){
            addProcessToProcessingQueue(toAddProcessNextStep);
            toAddProcessNextStep = null;
        }

        int q = quantum();
        Process nextProcess = processingQueue.poll();
        if (nextProcess == null){
            processing = false;
            return false;
        }
        int nextFinishTime;
        //Will finish after this quantum processing
        if (nextProcess.remainingTime <= q){
            nextFinishTime = nextProcess.remainingTime + t;
            setNextTime(nextFinishTime);
            
            timeTillQFinish = nextFinishTime;
            nextProcess.remainingTime = 0;
            nextProcess.finishTime = nextFinishTime;

            movePendingProcessToCompleted(nextProcess);    //Add
        }
        //Won't finish after qunatum units of processing
        else{
            nextFinishTime = t + q;
            setNextTime(nextFinishTime);
            
            nextProcess.remainingTime -= q;
            timeTillQFinish = nextFinishTime;

            toAddProcessNextStep = nextProcess; //Add
        }
        ProcessLog log = new ProcessLog(nextProcess, t, nextFinishTime);
        processLogs.add(log);
        processing = true;

        return queueEmpty();
    }
    
    @Override
    protected boolean queueEmpty(){
        return super.queueEmpty() && processingQueue.size() == 0;
    }
    

}
