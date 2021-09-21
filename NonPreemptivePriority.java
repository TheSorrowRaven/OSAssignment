public class NonPreemptivePriority extends NonPreemptiveSJF {

    public NonPreemptivePriority(Scheduling scheduling) {
        super(scheduling);
    }
    public NonPreemptivePriority(Scheduling scheduling, boolean step, boolean unitStep){
        super (scheduling, step, unitStep);
    }

    @Override
    protected Process getProcess(){
        return getHighestPriority();
    }
}
