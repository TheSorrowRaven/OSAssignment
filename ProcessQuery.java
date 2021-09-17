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

            int fails = 0;
            String input = "";
            while (true){
                Table table = new Table(text2d.toArray(new String[0][]));
                CLI.printArrln(table.getTextArr());
                if (fails > 0){
                    System.out.println("'" + input + "' is not a proper value. Please try again");
                }
                CLI.print("Enter Quantum: ");
                try{
                    input = CLI.in.nextLine();
                    scheduling.quantum = Integer.parseInt(input);
                    break;
                }
                catch (Exception e){
                }
                fails++;
            }
        }
        return scheduling;
    }

    private static Scheduling processQuery(ArrayList<String[]> text2d, int total){
        int totalProcesses = 0;
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
                newProcess[i] = value.toString();
            }
            if (finish){
                break;
            }
            totalProcesses++;
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
        return scheduling;
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
            int value = Integer.parseInt(input);
            return value;
        }
        catch (Exception e){
        }
        return queryCell(text2d, true, input);
    }


}
