
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.IOException;

public class CLI {
    
    private static final boolean usePrintWriter = true;

    public static Scanner in = new Scanner(System.in);
    private static PrintWriter pw = new PrintWriter(System.out, true);

    private static void out(String str){
        if (usePrintWriter){
            pw.print(str);
            pw.flush();
            return;
        }
        System.out.print(str);
    }
    private static void outln(String str){
        if (usePrintWriter){
            pw.println(str);
            pw.flush();
            return;
        }
        pw.println(str);
    }

    public static void print(Object obj){
        out(obj.toString());
    }
    public static void println(Object obj){
        outln(obj.toString());
    }

    public static void printArr(String[] text){
        for (int i = 0; i < text.length - 1; i++){
            println(text[i]);
        }
        print(text[text.length - 1]);
    }
    public static void printArrln(String[] text){
        for (int i = 0; i < text.length; i++){
            println(text[i]);
        }
    }

    public static void flushIn(){
        in.nextLine();
    }

    public static String debugPrepend = "\nDEBUG: ";
    public static boolean debugPrint = true;

    private static boolean canDebugLog(){
        return debugPrint;
    }
    public static void log(Object obj){
        if (!canDebugLog()){
            return;
        }
        outln(debugPrepend + obj.toString());
    }


    public static void clearScreen(boolean warnUser){
        System.out.print("\033[H\033[2J");
        System.out.flush();
        boolean fail = true;
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            fail = false;
        } catch (InterruptedException e) {
        } catch (IOException e) {
        }
        if (fail && warnUser){
            println("\n=== This environment does not support clear screen. ===\n");
        }
    }
    public static void clearScreen(){
        clearScreen(true);
    }

    public static void pressEnter(){
        try {
            System.in.read();
        } catch (IOException e) {
        }
    }
    public static void pressEnter(boolean askUser){
        if (askUser){
            println("Press Enter To Continue");
        }
        pressEnter();
    }

}
