public class Process {
    
    public String name;
    public int burstTime;
    public int arrivalTime;
    
    public boolean hasPriority;
    public int priority;

    public int finishTime;
    public int turnAroundTime;
    public int waitingTime;

    public int remainingTime;   //Dynamic

    public Process(String name, int burstTime, int arrivalTime, int priority){
        this.name = name;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        hasPriority = priority != Integer.MIN_VALUE;

        remainingTime = burstTime;  //Dynamic
    }

    public Process(String name, int burstTime, int arrivalTime){
        this(name, burstTime, arrivalTime, Integer.MIN_VALUE);
    }

    public void calcTTandWT(){
        turnAroundTime = finishTime - arrivalTime;
        waitingTime = turnAroundTime - burstTime;

        if (remainingTime != 0){
            if (remainingTime < 0){
                CLI.log(name + " - Remaining time is BELOW 0!!");
            }
            else{
                CLI.log(name + " - Still has leftover remaining time of " + remainingTime);
            }
        }
    }

    @Override
    public String toString(){
        return name + " " + arrivalTime;
    }

}
