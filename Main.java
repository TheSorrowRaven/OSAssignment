
public class Main{

    public static void main(String[] args){
        
        CLI.clearScreen();
        CLI.pressEnter(true);


        String[][] text = new String[][]{

            {"{input}", "Hello"},
            {"1", "2"}

        };


        Table t = new Table(text, "{input}");
        CLI.printArr(t.getTextArr());
        
        CLI.flushIn();
        String te = CLI.in.nextLine();
        text[0][0] = te;
        
        t = new Table(text);
        CLI.printArrln(t.getTextArr());
    }

}