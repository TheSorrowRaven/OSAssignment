import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GanttChart extends Table {

    private Scheduling scheduling;
    private ProcessLogs processLogs;

    private int multiplyingFactor;

    private static final char fillInChar = ' ';
    private HashMap<Integer, ArrivalProcessLog[]> arrivalTimes = new HashMap<Integer, ArrivalProcessLog[]>();
    private HashMap<Integer, ProcessLog> remainingTimes = new HashMap<Integer, ProcessLog>();
    private ArrayList<Integer> mainTimes = new ArrayList<Integer>();
    private int columnIndex = 0;

    private HashMap<ArrivalProcessLog, Integer> arrivalToTextPos = new HashMap<ArrivalProcessLog, Integer>();


    public GanttChart(SchedulingAlgorithm algorithm){
        super();
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
            int units = log.getDiff();
            row[i] = fillStringToMaxLength(name, units);
            mainTimes.add(log.startTime);
            checkPutRemainingTimes(i, log);
            checkPutArrivalTimes(i, log);
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
        int actualMax = units * multiplyingFactor;
        int remaining = actualMax - current;
        remaining = Math.max(remaining, 0);
        return str + repeat(remaining, fillInChar);
    }

    private void checkPutArrivalTimes(int i, Log log){
        if (log.arrivalProcessLogs == null || log.arrivalProcessLogs.length == 0){
            return;
        }
        arrivalTimes.put(i, log.arrivalProcessLogs);
    }

    private void checkPutRemainingTimes(int i, Log log){
        if (log instanceof ProcessLog pLog){
            if (pLog.remainingTime > 0){
                remainingTimes.put(i, pLog);
            }
        }
    }

    private void findProcessMaxCharacters(){
        int maxNumberCharacters = 1;
        for (Process p : scheduling.processes){
            int strLength = p.name.length();
            if (strLength > maxNumberCharacters){
                maxNumberCharacters = strLength;
            }
        }
        multiplyingFactor = Math.max(maxNumberCharacters, 4);
    }

    @Override
    protected void drawTable(String[][] allText){
        super.ExtendTableText(8);
        textRows = 1;
        super.drawTable(allText);

        //addVerticalDottedLines();
        
        //tableText.set(0, "Hello");
        //tableText.set(4, "Bye");

    }

    private void addVerticalDottedLines(){
        for (Map.Entry<ArrivalProcessLog, Integer> pair : arrivalToTextPos.entrySet()){
            int arrivalTime = pair.getKey().arrivalTime;
            int pos = pair.getValue();
            
            replaceDottedLinesArrivalTime(pos, arrivalTime);
        }

    }

    @Override
    protected void drawCell(CellType cellType, String text, int maxLength, boolean doInput){
        super.drawCell(cellType, text, maxLength, doInput);
        textRows = 4;

        appendMainTime();
        appendRemainingTime();
        //appendArrivalTime();
        columnIndex++;
    }

    private void appendArrivalTime(){
        if (!arrivalTimes.containsKey(columnIndex)){
            return;
        }

        ArrivalProcessLog[] apLogs = arrivalTimes.get(columnIndex);
        for (ArrivalProcessLog apLog : apLogs){
            String text = apLog.getArrivingString();
            
            StringBuilder strBuilder = new StringBuilder(tableText.get(7));
            int textPosition = columnIndex + (strBuilder.length() == 0 ? 0 : 1) + multiplyingFactor * apLog.arrivalTime;
            CLI.log(strBuilder.length());
            int requiredSpacing = textPosition - tableText.get(7).length();
            int stretched = requiredSpacing + text.length();
            String formatText = "%" + Math.max(stretched, text.length()) + "s";
            CLI.log(formatText);
            strBuilder.append(String.format(formatText, text));
    
            tableText.set(7, strBuilder.toString());

            arrivalToTextPos.put(apLog, textPosition);
        }

    }

    private void replaceDottedLinesArrivalTime(int position, int arrivalTime){

        StringBuilder timesStrBuilder = new StringBuilder(tableText.get(4));
        StringBuilder declutterStrBuilder = new StringBuilder(tableText.get(5));
        StringBuilder indicatorLineBuilder = new StringBuilder(tableText.get(6));
        String padding = repeat(tableText.get(7).length(), ' ');
        indicatorLineBuilder.append(padding);
        declutterStrBuilder.append(padding);

        declutterStrBuilder.setCharAt(position, Lines.verticalDotted);
        String timeText = Integer.toString(arrivalTime);
        if (timesStrBuilder.charAt(position) == ' '){
            timesStrBuilder.setCharAt(position, Lines.verticalDotted);
            for (int i = 0; i < timeText.length(); i++){
                indicatorLineBuilder.setCharAt(position + i, timeText.charAt(i));
            }
            
        }
        else{
            indicatorLineBuilder.setCharAt(position, Lines.verticalDotted);
            for (int i = 0; i < timeText.length(); i++){
                timesStrBuilder.setCharAt(position + i, timeText.charAt(i));
            }
        }

        tableText.set(4, timesStrBuilder.toString());
        tableText.set(5, declutterStrBuilder.toString());
        tableText.set(6, indicatorLineBuilder.toString());

    }

    private void appendRemainingTime(){
        if (!remainingTimes.containsKey(columnIndex)){
            return;
        }

        ProcessLog pLog = remainingTimes.get(columnIndex);
        String text = pLog.getRemainingStr();
        //int time = pLog.endTime;

        StringBuilder strBuilder = new StringBuilder(tableText.get(0));
        int requiredSpacing = tableText.get(1).length() - strBuilder.length();
        int stretched = requiredSpacing + text.length();
        String formatText = "%" + stretched + "s";
        strBuilder.append(String.format(formatText, text));

        tableText.set(0, strBuilder.toString());
    }

    private ArrayList<Integer> unitPositions = new ArrayList<Integer>();

    private void appendMainTime(){
        boolean addLast = columnIndex ==  mainTimes.size() - 2;
        int currentTime = mainTimes.get(columnIndex);
        StringBuilder strBuilder = new StringBuilder(tableText.get(4));
        int initialLength = strBuilder.length();
        int requiredLength = tableText.get(1).length();
        int requiredSpacing = requiredLength - initialLength;
        if (addLast){
            Integer finalVal = mainTimes.get(mainTimes.size() - 1);
            int digits = (finalVal.toString()).length();
            String formatText = "%-" + (requiredSpacing - (digits - 1)) + "s%" + (digits) + "s";
            strBuilder.append(String.format(formatText, Integer.toString(currentTime), finalVal));
        }
        else{
            String formatText = "%-" + requiredSpacing + "s";
            strBuilder.append(String.format(formatText, Integer.toString(currentTime)));
        }

        // for (int i = unitPositions.size() - 1; i < currentTime + (addLast ? 1 : 0); i++){
        //     unitPositions.add(requiredSpacing);
        // }

        tableText.set(4, strBuilder.toString());
    }

    @Override
    protected void ExtendTableText(int amount){

    }

}
