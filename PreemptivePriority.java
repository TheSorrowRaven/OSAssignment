public class PreemptivePriority extends PreemptiveSJF{

    public PreemptivePriority(Scheduling scheduling) {
        super(scheduling);
    }
    public PreemptivePriority(Scheduling scheduling, boolean step, boolean unitStep){
        super (scheduling, step, unitStep);
    }

    @Override
    protected Process getProcess(){
        return getHighestPriority();
    }
}
