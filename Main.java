
public class Main{

    public static void main(String[] args){
        
        Scheduling scheduling = ProcessQuery.query(true, true);
        CLI.clearScreen();
        Table table = new Table(scheduling.input);
        CLI.printArrln(table.getTextArr());

    }

}