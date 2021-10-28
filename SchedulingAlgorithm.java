import java.util.ArrayList;
import java.util.PriorityQueue;

public abstract class SchedulingAlgorithm {
    
    protected Scheduling scheduling;
    protected boolean step;
    protected boolean unitStep;

    protected int lastTime = -1;
    protected int currentTime = -1;
    protected PriorityQueue<Integer> nextTime = new PriorityQueue<Integer>();

    protected ProcessLogs processLogs;
    protected ArrayList<Process> completedProcesses = new ArrayList<Process>();
    protected ArrayList<Process> pendingProcesses = new ArrayList<Process>();
    protected PriorityQueue<Process> unArrivedProcesses = new PriorityQueue<Process>(new PriorityQueue<Process>(5,(a,b) ->
    {
        if (a.arrivalTime == b.arrivalTime){

            return -1;   //Later come in (lower row in the table) means queue behind previous existing process (previous rows)
        }
        return a.arrivalTime - b.arrivalTime;
    }
    ));

    public SchedulingAlgorithm(Scheduling scheduling){
        this.scheduling = scheduling;
        processLogs = new ProcessLogs(scheduling.processes);
        step = false;
        unitStep = false;
        initialize();
    }
    public SchedulingAlgorithm(Scheduling scheduling, boolean step, boolean unitStep){
        this(scheduling);
        this.step = step;
        this.unitStep = unitStep;
    }

    protected Integer nextTime(){
        return nextTime.peek();
    }

    private void initialize(){
        for (Process p : scheduling.processes){
            unArrivedProcesses.add(p);
            setNextTime(p.arrivalTime);
        }
    }

    public void simulate(){
        while (true){

            boolean complete = step();

            if (step){
                CLI.println("");
                CLI.pressEnter(true);
                CLI.in.nextLine();


            }

            if (complete){
                break;
            }
        }
        finishSimulation();
    }

    private void finishSimulation(){
        for (Process process : scheduling.processes){
            process.calcTTandWT();
        }
    }

    private boolean step(){
        lastTime = currentTime;
        if (unitStep){
            currentTime++;
            return preStepOperations(currentTime);
        }
        Integer next = nextTime.poll();
        CLI.log("Next Time: " + next);
        if (next == null){
            return true;
        }
        boolean complete = preStepOperations(next);
        currentTime = next;
        return complete;
    }
    
    private boolean preStepOperations(int time){
        addArrivingProcessesToPendingProcesses(time);

        return stepTo(time);
    }

    //Returns if scheduling is complete
    protected abstract boolean stepTo(int time);

    protected boolean queueEmpty(){
        return nextTime.size() == 0 && pendingProcesses.size() == 0;
    }

    protected void setNextTime(int t){
        boolean alreadyExist = timeExists(t);
        if (alreadyExist){
            //CLI.log("Same Time Already Exist!");
            return;
        }
        nextTime.add(t);
    }

    private void addArrivingProcessesToPendingProcesses(int currentTime){
        while (true){
            if (unArrivedProcesses.size() == 0){
                return;
            }
            Process process = unArrivedProcesses.peek();
            if (process.arrivalTime <= currentTime){
                addArrivedProcess(process);
                continue;
            }
            else{
                return;
            }
        }
    }

    protected void addArrivedProcess(Process process){
        unArrivedProcesses.poll();
        pendingProcesses.add(process);
    }

    protected void movePendingProcessToCompleted(Process process){
        if (pendingProcesses.contains(process)){
            completedProcesses.add(process);
            pendingProcesses.remove(process);
        }
    }

    protected boolean timeExists(int t){
        for (Integer val : nextTime){
            if (val == t){
                return true;
            }
            if (val > t){
                break;
            }
        }
        return false;
    }

    protected Process getShortestTime(){
        CLI.printArr(pendingProcesses);
        if (pendingProcesses.size() == 0){
            return null;
        }
        Process shortestProcess = pendingProcesses.get(0);
        int shortestProcessVal = shortestProcess.remainingTime;
        for (int i = 1; i < pendingProcesses.size(); i++){
            Process p = pendingProcesses.get(i);
            if (p.remainingTime < shortestProcessVal){
                shortestProcess = p;
                shortestProcessVal = p.remainingTime;
            }
        }
        return shortestProcess;
    }
    
    protected Process getHighestPriority(){
        if (pendingProcesses.size() == 0){
            return null;
        }
        Process prioritizedProcess = pendingProcesses.get(0);
        int priorityVal = prioritizedProcess.priority;
        for (int i = 1; i < pendingProcesses.size(); i++){
            Process p = pendingProcesses.get(i);
            if (p.priority < priorityVal){
                prioritizedProcess = p;
                priorityVal = p.priority;
            }
        }
        return prioritizedProcess;
    }

}
