import java.util.ArrayList;
import java.util.HashMap;

public class GanttChart extends Table {

    private SchedulingAlgorithm algorithm;
    private Scheduling scheduling;
    private ProcessLogs processLogs;

    private int maxProcessCharacters;

    private static final char fillInChar = '.';
    private HashMap<Integer, Integer> arrivalTimes = new HashMap<Integer, Integer>();
    private ArrayList<Integer> mainTimes = new ArrayList<Integer>();
    private int mainTimesIndex = 0;

    public GanttChart(SchedulingAlgorithm algorithm){
        super();
        this.algorithm = algorithm;
        scheduling = algorithm.scheduling;
        processLogs = algorithm.processLogs;
        
        initialize();

        ArrayList<Log> logs = processLogs.getStandardizedProcessLogs();
        columns = logs.size();

        if (logs.size() == 0){
            return;
        }

        String[] row = new String[logs.size()];
        Log log = null;
        for (int i = 0; i < row.length; i++){
            log = logs.get(i);
            String name = log.getName();
            //TODO if log.geDiff() is 0 aka 0 burst time
            int units = log.getDiff();
            row[i] = fillStringToMaxLength(name, units);
            mainTimes.add(log.startTime);
        }
        mainTimes.add(log.endTime);
        String[][] text2d = new String[][]{ row };

        drawTable(text2d);
        // for (Log p : logs){
        //     CLI.println(p);
        // }
    }

    private void initialize(){
        padding = false;
        processLogs.standardize();
        findProcessMaxCharacters();
    }

    private String fillStringToMaxLength(String str, int units){
        int current = str.length();
        int actualMax = units * maxProcessCharacters;
        int remaining = actualMax - current;
        remaining = Math.max(remaining, 0);
        return str + repeat(remaining, fillInChar);
    }

    private void putArrivalTimeAt(int pos, int time){
        arrivalTimes.put(pos, time);
    }

    private void findProcessMaxCharacters(){
        int maxNumberCharacters = 1;
        for (Process p : scheduling.processes){
            int strLength = p.name.length();
            if (strLength > maxNumberCharacters){
                maxNumberCharacters = strLength;
            }
        }
        maxProcessCharacters = maxNumberCharacters;
    }

    @Override
    protected void drawTable(String[][] allText){
        super.ExtendTableText(5);
        textRows = 1;
        super.drawTable(allText);

        
        //tableText.set(0, "Hello");
        //tableText.set(4, "Bye");

    }

    @Override
    protected void drawCell(CellType cellType, String text, int maxLength, boolean doInput){
        super.drawCell(cellType, text, maxLength, doInput);
        textRows = 4;

        appendMainTime();
    }

    private void appendMainTime(){
        boolean addLast = mainTimesIndex ==  mainTimes.size() - 2;
        StringBuilder strBuilder = new StringBuilder(tableText.get(4));
        int initial = strBuilder.length();
        int totalLength = tableText.get(1).length();
        int diff = totalLength - initial;
        if (addLast){
            int finalVal = mainTimes.get(mainTimes.size() - 1);
            int digits = finalVal % 10;
            strBuilder.append(String.format("%-" + (diff - digits) + "s%" + digits + "s", mainTimes.get(mainTimesIndex).toString(), finalVal));
        }
        else{
            strBuilder.append(String.format("%-" + diff + "s", mainTimes.get(mainTimesIndex).toString()));
        }

        tableText.set(4, strBuilder.toString());
        mainTimesIndex++;
    }

    @Override
    protected void ExtendTableText(int amount){

    }

}
