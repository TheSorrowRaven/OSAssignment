import java.util.ArrayList;

public class ProcessLogs{

    protected ArrayList<ProcessLog> processLogs = new ArrayList<ProcessLog>();
    private ArrayList<Log> standardizedProcessLogs = new ArrayList<Log>();

    private Process[] allProcesses;

    public void add(ProcessLog processLog){
        processLogs.add(processLog);
    }
    
    public ProcessLogs(Process[] allProcesses){
        this.allProcesses = allProcesses;
    }

    public void standardize(){
        int lastEndTime = 0;
        ArrayList<Process> arrivingProcesses = new ArrayList<Process>();
        for (Process process : allProcesses){
            arrivingProcesses.add(process);
        }

        ProcessLog previous = null;
        for (int i = 0; i < processLogs.size(); i++){
            ProcessLog log = processLogs.get(i);
            if (i == 0){
                previous = log;
            }
            if (log.startTime != lastEndTime){
                EmptyProcessLog emptyLog = new EmptyProcessLog();
                emptyLog.startTime = lastEndTime;
                emptyLog.endTime = log.startTime;
                checkAddArrivingProcesses(emptyLog, arrivingProcesses);
                standardizedProcessLogs.add(emptyLog);
            }

            boolean skipAdd = false;
            if (i != 0){
                if (previous.compress){
                    if (previous.process == log.process){
                        previous.endTime = log.endTime;
                        previous.remainingTime = log.remainingTime;
                        lastEndTime = log.endTime;

                        log = previous;
                        skipAdd = true;
                    }
                }
            }

            checkAddArrivingProcesses(log, arrivingProcesses);
            if (!skipAdd){
                standardizedProcessLogs.add(log);
                lastEndTime = log.endTime;
                previous = log;
            }
        }
    }

    public ArrayList<Log> getStandardizedProcessLogs(){
        return standardizedProcessLogs;
    }

    private void checkAddArrivingProcesses(Log log, ArrayList<Process> arrivingProcesses){
        ArrayList<ArrivalProcessLog> arrivalProcessLog = new ArrayList<ArrivalProcessLog>();
        if (log.arrivalProcessLogs != null && log.arrivalProcessLogs.length > 0){
            for (ArrivalProcessLog apl : log.arrivalProcessLogs){
                arrivalProcessLog.add(apl);
            }
        }
        for (int i = 0; i < arrivingProcesses.size(); i++){
            Process process = arrivingProcesses.get(i);
            if (process.arrivalTime >= log.startTime && process.arrivalTime <= log.endTime){
                arrivalProcessLog.add(new ArrivalProcessLog(process, process.arrivalTime));
                arrivingProcesses.remove(i);
                i--;
            }
        }
        log.arrivalProcessLogs = arrivalProcessLog.toArray(new ArrivalProcessLog[0]);
    }

}


abstract class Log{

    public ArrivalProcessLog[] arrivalProcessLogs;  //Arriving processes within this start time and end time
    public int startTime;
    public int endTime;

    protected String arrivalText(){
        String text = "\n";
        for (ArrivalProcessLog log : arrivalProcessLogs){
            text += log + "\n";
        }
        return text;
    }

    public abstract String getName();
    public int getDiff(){
        return endTime - startTime;
    }

}

//Empty process (at this time nothing is running)
class EmptyProcessLog extends Log{

    @Override
    public String toString(){
        return "Empty: " + startTime + " -> " + endTime + arrivalText();
    }

    @Override
    public String getName() {
        return "";
    }

}

//Normal process
class ProcessLog extends Log{

    public Process process;
    public int remainingTime;

    public boolean compress = false;


    public ProcessLog(Process process, int startTime, int endTime){
        this.process = process;
        this.startTime = startTime;
        this.endTime = endTime;
        remainingTime = process.remainingTime;
    }
    public ProcessLog(Process process, int startTime, int endTime, boolean compress){
        this(process, startTime, endTime);
        this.compress = compress;
    }

    @Override
    public String toString(){
        return process.name + ": " + startTime + " -> " + endTime + "\t(R: " + remainingTime + ")" + arrivalText();
    }

    @Override
    public String getName() {
        return process.name;
    }


    public String getRemainingStr(){
        String text = process.name + "(" + remainingTime + ")";
        if (process.hasPriority){
            text += ""; //TODO
        }
        return text;
    }

    public String getRemainingStrPriority(){
        return getRemainingStr() + "";  //TODO
    }

}

class ArrivalProcessLog{

    public Process process;
    public int arrivalTime;

    public ArrivalProcessLog(Process process, int arrivalTime){
        this.process = process;
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString(){
        return process.name + " arrived at " + arrivalTime;
    }

    public String getArrivingString(){
        String text = process.name;// + "(" + arrivalTime + ")";
        if (process.hasPriority){
            text += ""; //TODO
        }
        return text;
    }

}