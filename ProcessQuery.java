import java.util.ArrayList;

public class ProcessQuery {
    
    private static final String inputString = "{input}";

    public static Scheduling query(boolean priority, boolean quantum){

        int total = 3 + (priority ? 1 : 0);   //name, burst time, arrival time = 3
        ArrayList<String[]> text2d = new ArrayList<String[]>();

        if (priority){
            text2d.add(new String[]{ "Process", "Burst Time", "Arrival Time", "Priority" });
        }
        else{
            text2d.add(new String[]{ "Process", "Burst Time", "Arrival Time" });
        }
        Scheduling scheduling = processQuery(text2d, total);
        if (quantum){
            if (scheduling.isDefault){
                scheduling.quantum = 3;
            }
            else{
                scheduling.quantum = queryQuantum(text2d);
            }
        }
        return scheduling;
    }

    private static int queryQuantum(ArrayList<String[]> text2d){
        int fails = 0;
        String input = "";
        int quantum;
        while (true){
            CLI.clearScreen();
            Table table = new Table(text2d.toArray(new String[0][]));
            CLI.printArrln(table.getTextArr());
            if (fails > 0){
                System.out.println("'" + input + "' is not a proper value. Please try again");
            }
            CLI.print("Enter Quantum: ");
            try{
                input = CLI.in.nextLine();
                quantum = Integer.parseInt(input);

                return quantum;
            }
            catch (Exception e){
            }
            fails++;
        }
    }

    private static Scheduling processQuery(ArrayList<String[]> text2d, int total){
        int totalProcesses = 0;
        boolean doDefault = false;
        while (true){
            String[] newProcess = new String[total];
            text2d.add(newProcess);
            newProcess[0] = "P" + totalProcesses;
            boolean finish = false;
            for (int i = 1; i < newProcess.length; i++){
                newProcess[i] = inputString;
                Integer value = queryCell(text2d, false, "");
                if (value == null){
                    text2d.remove(text2d.size() - 1);
                    finish = true;
                    break;
                }
                //Check default
                else if (value == Integer.MAX_VALUE){
                    doDefault = true;
                    while (text2d.size() > 1){
                        text2d.remove(1);
                    }
                    finish = true;
                    break;
                }
                newProcess[i] = value.toString();
            }
            if (finish){
                break;
            }
            totalProcesses++;
        }

        if (doDefault){
            if (total == 4){
                fillDefaultProcessesPriority(text2d);
            }
            else{
                fillDefaultProcesses(text2d);
            }
        }

        Process[] processes = new Process[text2d.size() - 1];
        for (int i = 1; i < text2d.size(); i++){
            String[] strings = text2d.get(i);
            if (strings.length == 4){
                processes[i - 1] = new Process(strings[0], Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                continue;
            }
            processes[i - 1] = new Process(strings[0], Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
        }
        Scheduling scheduling = new Scheduling();
        scheduling.processes = processes;
        scheduling.input = text2d.toArray(new String[0][]);
        scheduling.isDefault = doDefault;
        return scheduling;
    }
    private static void fillDefaultProcessesPriority(ArrayList<String[]> text2d){
        text2d.add(new String[]{"P0", "6", "0", "3"});
        text2d.add(new String[]{"P1", "4", "1", "3"});
        text2d.add(new String[]{"P2", "6", "5", "1"});
        text2d.add(new String[]{"P3", "6", "6", "1"});
        text2d.add(new String[]{"P4", "6", "7", "5"});
        text2d.add(new String[]{"P5", "6", "8", "6"});
    }
    private static void fillDefaultProcesses(ArrayList<String[]> text2d){
        text2d.add(new String[]{"P0", "6", "0"});
        text2d.add(new String[]{"P1", "4", "1"});
        text2d.add(new String[]{"P2", "6", "5"});
        text2d.add(new String[]{"P3", "6", "6"});
        text2d.add(new String[]{"P4", "6", "7"});
        text2d.add(new String[]{"P5", "6", "8"});
    }
    
    private static Integer queryCell(ArrayList<String[]> text2d, boolean reInput, String previousInput){
        CLI.clearScreen();
        Table table = new Table(text2d, inputString);
        CLI.println("Type the values into the table");
        CLI.println("Type 'n' to complete table");
        if (reInput){
            CLI.println("ERROR: '" + previousInput + "' is not an integer!");
        }
        CLI.printArr(table.getTextArr());
        String input = "";
        try{
            input = CLI.in.nextLine();
            if (input.equals("n")){
                return null;
            }
            else if(input.equals(Main.specialDefaultCase)){
                return Integer.MAX_VALUE;
            }
            int value = Integer.parseInt(input);
            return value;
        }
        catch (Exception e){
        }
        return queryCell(text2d, true, input);
    }


}
