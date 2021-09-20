import java.util.ArrayList;

public class GanttChart extends Table {

    private SchedulingAlgorithm algorithm;
    private Scheduling scheduling;
    private ProcessLogs processLogs;

    private int maxProcessCharacters;

    private static final char fillInChar = '.';

    public GanttChart(SchedulingAlgorithm algorithm){
        super();
        this.algorithm = algorithm;
        scheduling = algorithm.scheduling;
        processLogs = algorithm.processLogs;
        
        initialize();

        ArrayList<Log> logs = processLogs.getStandardizedProcessLogs();
        columns = logs.size();

        String[] row = new String[logs.size()];
        for (int i = 0; i < row.length; i++){
            Log log = logs.get(i);
            String name = log.getName();
            //TODO if log.geDiff() is 0 aka 0 burst time
            int units = log.getDiff();
            row[i] = fillStringToMaxLength(name, units);
        }
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
        ExtendTableText(1);
        super.drawTable(allText);
        ExtendTableText(1);

        
        //Hello There
        //General Kenobi

    }

    @Override
    protected void drawCell(CellType cellType, String text, int maxLength, boolean doInput){
        super.drawCell(cellType, text, maxLength, doInput);
    }

}
