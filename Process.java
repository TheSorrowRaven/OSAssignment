public class Process {
    
    public String name;
    public int burstTime;
    public int arrivalTime;
    
    public boolean hasPriority;
    public int priority;

    public int finishTime;
    public int turnAroundTime;
    public int waitingTime;

    public Process(String name, int burstTime, int arrivalTime, int priority){
        this.name = name;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        hasPriority = priority != Integer.MIN_VALUE;
    }

    public Process(String name, int burstTime, int arrivalTime){
        this(name, burstTime, arrivalTime, Integer.MIN_VALUE);
    }

    public void calcTTandWT(int finishTime){
        this.finishTime = finishTime;
        turnAroundTime = finishTime - arrivalTime;
        waitingTime = turnAroundTime - burstTime;
    }

}
