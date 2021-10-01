import java.util.ArrayList;
import java.util.HashMap;

public class GanttChart extends Table {

    private Scheduling scheduling;
    private ProcessLogs processLogs;

    private int multiplyingFactor;

    private static final char fillInChar = ' ';
    private HashMap<Integer, ArrivalProcessLog[]> arrivalTimes = new HashMap<Integer, ArrivalProcessLog[]>();
    private HashMap<Integer, ProcessLog> remainingTimes = new HashMap<Integer, ProcessLog>();
    private ArrayList<Integer> mainTimes = new ArrayList<Integer>();
    private int columnIndex = 0;


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
        if (log instanceof ProcessLog){
            ProcessLog pLog = (ProcessLog)log;
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

        drawArrivalTimes();
    }


    private void drawArrivalTimes(){
        String text = tableText.get(4);
        int length = text.length();
        StringBuilder timesStrBuilder = new StringBuilder(tableText.get(4));
        StringBuilder indicatorStrBuilder = new StringBuilder(tableText.get(5));
        StringBuilder arrivalTimesValueBuilder = new StringBuilder(tableText.get(6));
        StringBuilder arrivalTimesBuilder = new StringBuilder(tableText.get(7));
        String append = repeat(length + 1, ' ');
        indicatorStrBuilder.append(append);
        arrivalTimesValueBuilder.append(append);
        arrivalTimesBuilder.append(append);

        ArrayList<StringBuilder> pushedArrivalTimes = new ArrayList<StringBuilder>();
        pushedArrivalTimes.add(arrivalTimesBuilder);

        for (Integer a : arrivalTimes.keySet()){
            ArrivalProcessLog[] apLogs = arrivalTimes.get(a);
            for (ArrivalProcessLog apLog : apLogs){
                int position = unitPositions.get(apLog.arrivalTime);

                int i = 0;
                while (true){
                    StringBuilder addingSB = pushedArrivalTimes.get(i);

                    if (addingSB.charAt(position) != ' '){
                        pushedArrivalTimes.add(new StringBuilder(append));
                        super.ExtendTableText(1);
                        i++;
                        continue;
                    }
                    else{
                        setStrToBuilder(addingSB, apLog.getArrivingString(), position);
                        break;
                    }
                }


                if (timesStrBuilder.charAt(position) == ' '){
                    timesStrBuilder.setCharAt(position, Lines.verticalDotted);
                    
                    setStrToBuilder(arrivalTimesValueBuilder, "[" + apLog.arrivalTime + "]", position - 1);
                }
                else{
                    arrivalTimesValueBuilder.setCharAt(position, Lines.verticalDotted);
                }
                indicatorStrBuilder.setCharAt(position, Lines.verticalDotted);
            }
        }


        tableText.set(4, timesStrBuilder.toString());
        tableText.set(5, indicatorStrBuilder.toString());
        tableText.set(6, arrivalTimesValueBuilder.toString());
        for (int i = 0; i < pushedArrivalTimes.size(); i++){
            tableText.set(7 + i, pushedArrivalTimes.get(i).toString());
        }

    }

    private void setStrToBuilder(StringBuilder builder, String insert, int startPos){
        for (int i = 0; i < insert.length(); i++){
            int iPos = startPos + i;
            char c = insert.charAt(i);
            if (builder.length() <= iPos){
                builder.append(repeat(iPos - builder.length() + 1, ' '));
            }
            builder.setCharAt(iPos, c);
        }
    }

    @Override
    protected void drawCell(CellType cellType, String text, int maxLength, boolean doInput){
        super.drawCell(cellType, text, maxLength, doInput);
        textRows = 4;

        appendMainTime();
        appendRemainingTime();
        addUnitPositions();
        //appendArrivalTime();
        columnIndex++;
    }

    private ArrayList<Integer> unitPositions = new ArrayList<Integer>();
    private int lastUnitPositionIndex = 0;
    private void addUnitPositions(){
        
        boolean last = columnIndex ==  mainTimes.size() - 2;
        // if (first){
        //     unitPositions.add(0);
        // }
        String text = tableText.get(4);
        int textLength = text.length();
        int operatingLength = textLength - lastUnitPositionIndex + (columnIndex > 0 ? 1 : 0);

        int total = operatingLength / multiplyingFactor - (last ? 1 : 0);

        for (int i = 0; i < total; i++){
            unitPositions.add(lastUnitPositionIndex + (i) * multiplyingFactor);
        }
        if (last){
            unitPositions.add(textLength - 2);
        }

        lastUnitPositionIndex = textLength;
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
