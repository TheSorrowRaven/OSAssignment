import java.util.ArrayList;

public class ProcessQuery {
    
    public static Scheduling Query(boolean priority, boolean quantum){

        int total = 3 + (priority ? 1 : 0);   //name, burst time, arrival time = 3
        ArrayList<String[]> text2d = new ArrayList<String[]>();

        text2d.add(new String[]{  });

        text2d.add(new String[total]);


        String[][] allText = text2d.toArray(new String[0][]);
        Table table = new Table(allText);

        return null;    //TODO
    }

}
