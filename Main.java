import java.util.Arrays;

public class Main{

    public static final String specialDefaultCase = "default";
    public static final boolean startFrom0 = false;

    public static void main(String[] args){
        
        int choice = showStartScreen(null);

        processSchedulingChoice(choice);

    }

    private static void processSchedulingChoice(int input){
        boolean priority = false;
        boolean quantum = false;
        if (input == 4 || input == 5){
            priority = true;
        }
        if (input == 1){
            quantum = true;
        }
        Scheduling scheduling = ProcessQuery.query(priority, quantum);
        SchedulingAlgorithm algorithm = null;
        switch (input){
            case 1:
                algorithm = new RoundRobin(scheduling, false, false);
            break;
            case 2:
                algorithm = new PreemptiveSJF(scheduling, false, false);
            break;
            case 3:
                algorithm = new NonPreemptiveSJF(scheduling, false, false);
            break;
            case 4:
                algorithm = new PreemptivePriority(scheduling, false, false);
            break;
            case 5:
                algorithm = new NonPreemptivePriority(scheduling, false, false);
            break;
        }
        algorithm.simulate();
        showResults(algorithm);
    }

    private static void showResults(SchedulingAlgorithm algorithm){
        CLI.clearScreen();
        //Show Gantt Chart

        String[][] text2d = algorithm.scheduling.input;
        appendResultsToTable(text2d, algorithm.scheduling.processes);
        Table resultsTable = new Table(text2d);
        CLI.printArrln(resultsTable.getTextArr());

        showGanttChart(algorithm);
    }

    private static void showGanttChart(SchedulingAlgorithm algorithm){
        GanttChart gc = new GanttChart(algorithm);
        CLI.printArrln(gc.getTextArr());
    }

    private static void appendResultsToTable(String[][] text2d, Process[] processes){
        int newRowLength = text2d[0].length + 3;   //3 - Finish Time, TurnAround Time, Waiting Time

        String[] newTitleRow = Arrays.copyOf(text2d[0], newRowLength);
        putIntoStrArr(newTitleRow, "Finish Time", "Turnaround Time", "Waiting Time");
        text2d[0] = newTitleRow;

        for (int i = 1; i < text2d.length; i++){
            String[] oldRow = text2d[i];
            String[] newRow = Arrays.copyOf(oldRow, newRowLength);
            Process process = processes[i - 1];
            putIntoStrArr(newRow, process.finishTime, process.turnAroundTime, process.waitingTime);
            
            text2d[i] = newRow;
        }
    }

    private static void putIntoStrArr(String[] strArr, Object val1, Object val2, Object val3){
        int size = strArr.length;
        strArr[size - 1] = val3.toString();
        strArr[size - 2] = val2.toString();
        strArr[size - 3] = val1.toString();
    }


    private static int showStartScreen(String previousInput){
        CLI.clearScreen();
        showTitle();
        return showChoices(previousInput);
    }

    private static void showTitle(){
        CLI.println("======Operating System======");
        CLI.println("--CPU Scheduling Algorithm--");
    }


    private static int showChoices(String previousInput){
        showChoiceTable();
        return askForChoiceInput(1, 5, previousInput);
    }

    private static void showChoiceTable(){
        String[][] schedulingChoices = new String[][]{
            { "No.", "CPU Scheduling Algorithm" },
            { "1", "Round Robin"},
            { "2", "Preepmptive SJF"},
            { "3", "Non Preemptive SJF" },
            { "4", "Preemptive Priority" },
            { "5", "Non Preemptive Priority" }
        };
        Table schedulingChoiceTable = new Table(schedulingChoices);
        CLI.printArrln(schedulingChoiceTable.getTextArr());
    }

    private static int askForChoiceInput(int min, int max, String previousInput){
        if (previousInput != null){
            CLI.println("ERROR: '" + previousInput + "' is not a valid choice!");
        }
        CLI.println("Please enter a number from " + min + " to " + max);
        String input = "";
        try{
            input = CLI.in.nextLine();
            int value = Integer.parseInt(input);
            if (value >= min && value <= max){
                return value;
            }
        }
        catch (Exception e){
        }
        return showStartScreen(input);
    }

}