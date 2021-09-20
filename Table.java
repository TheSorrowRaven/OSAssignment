import java.util.ArrayList;

public class Table {
    
    protected enum RowType{
        top,
        mid,
        bottom,
        single  //one row per table
    }
    protected enum CellType{
        topLeft,
        top,
        topRight,
        midLeft,
        mid,
        midRight,
        bottomLeft,
        bottom,
        bottomRight,

        leftSingleRow,
        midSingleRow,
        rightSingleRow,

        topSingleColumn,
        midSingleColumn,
        bottomSingleColumn,

        single  //single cell per table
    }

    protected int columns;

    public ArrayList<String> tableText = new ArrayList<String>();
    protected Integer[] maxLengthPerColumn;

    protected int textRows = 0;

    private boolean hasInput;
    private String inputText;
    private boolean foundInput;

    protected boolean padding = true;

    //Only calls from GanttChart
    protected Table(){
        
    }

    public Table(String[][] allText){
        if (allText.length == 0){
            CLI.log("!Empty table!");
            this.columns = 0;
            return;
        }
        this.columns = allText[0].length;
        drawTable(allText);
    }
    public Table(String[][] allText, String inputText){
        
        if (allText.length == 0){
            CLI.log("!Empty table!");
            this.columns = 0;
            return;
        }
        this.columns = allText[0].length;
        hasInput = true;
        this.inputText = inputText;
        drawTable(allText);
    }
    public Table(ArrayList<String[]> allTextArr, String inputText){
        this(allTextArr.toArray(new String[0][]), inputText);
    }
    public String[] getTextArr(){
        return tableText.toArray(new String[0]);
    }

    protected void drawTable(String[][] allText){
        if (allText.length == 0){
            CLI.log("!Empty Table!");
            return;
        }
        ArrayList<Integer> maxLengthColumn = new ArrayList<Integer>();
        for (int i = 0; i < allText.length; i++){
            String[] rowTexts = allText[i];
            for (int j = 0; j < rowTexts.length; j++){
                if (rowTexts[j] == null){
                    if (maxLengthColumn.size() == j){
                        maxLengthColumn.add(1);
                    }
                    continue;
                }
                int stringLength = rowTexts[j].length() + (padding ? 2 : 0);    //+2 Padding
                if (maxLengthColumn.size() == j){
                    maxLengthColumn.add(stringLength);
                    continue;
                }
                maxLengthColumn.set(j, Math.max(stringLength, maxLengthColumn.get(j)));
            }
        }
        maxLengthPerColumn = maxLengthColumn.toArray(new Integer[0]);

        if (allText.length == 1){
            drawRow(RowType.single, allText[0]);
            return;
        }
        drawRow(RowType.top, allText[0]);
        for (int i = 1; i < allText.length - 1; i++){
            drawRow(RowType.mid, allText[i]);
        }
        drawRow(RowType.bottom, allText[allText.length - 1]);
    }

    protected void drawRow(RowType type, String[] cellTexts){
        int length = cellTexts.length;
        if (length == 0){
            CLI.log("!No columns?!");
            return;
        }

        if (length != columns){
            CLI.log("!Row column different to table columns!");
            return;
        }

        boolean doInput = false;
        if (hasInput){
            for (int i = 0; i < cellTexts.length; i++){
                if (cellTexts[i] == null){
                    continue;
                }
                if (cellTexts[i].equals(inputText)){
                    doInput = true;
                }
            }
        }
        
        switch (type){
            case top:
                if (length == 1){
                    drawCell(CellType.topSingleColumn, cellTexts[0], maxLengthPerColumn[0], doInput);
                    break;
                }
                drawRowStandard(CellType.topLeft, CellType.top, CellType.topRight, cellTexts, doInput);
            break;
            case mid:
                if (length == 1){
                    drawCell(CellType.midSingleColumn, cellTexts[0], maxLengthPerColumn[0], doInput);
                    break;
                }
                drawRowStandard(CellType.midLeft, CellType.mid, CellType.midRight, cellTexts, doInput);
            break;
            case bottom:
                if (length == 1){
                    drawCell(CellType.bottomSingleColumn, cellTexts[0], maxLengthPerColumn[0], doInput);
                    break;
                }
                drawRowStandard(CellType.bottomLeft, CellType.bottom, CellType.bottomRight, cellTexts, doInput);
            break;
            case single:
                if (length == 1){
                    drawCell(CellType.single, cellTexts[0], maxLengthPerColumn[0], doInput);
                    break;
                }
                drawRowStandard(CellType.leftSingleRow, CellType.midSingleRow, CellType.rightSingleRow, cellTexts, doInput);
            break;
        }
    }
    protected void drawRowStandard(CellType left, CellType mid, CellType right, String[] text, boolean doInput){
        int length = text.length;
        drawCell(left, text[0], maxLengthPerColumn[0], doInput);
        for (int i = 1; i < length - 1; i++){
            drawCell(mid, text[i], maxLengthPerColumn[i], doInput);
        }
        drawCell(right, text[length - 1], maxLengthPerColumn[length - 1], doInput);
    }

    private String formatText(boolean foundInput, int totalLength, String text){
        return formatText(foundInput, totalLength, text, '\0', true);
    }
    private String formatText(boolean foundInput, int totalLength, String text, char line){
        return formatText(foundInput, totalLength, text, line, false);
    }
    private String formatText(boolean foundInput, int totalLength, String text, char line, boolean space){
        return (foundInput ? " " : String.format("%" + (totalLength - (space ? 1 : 2) + (padding ? 0 : 1)) + "s" + (padding ? " " : ""), text + (space ? line : "")) + (space ? "" : line));
    }

    protected void drawCell(CellType cellType, String text, int maxLength, boolean doInput){

        if (hasInput && foundInput){
            return;
        }
        if (doInput){
            if (text == inputText){
                foundInput = true;
            }
        }

        int totalLength = maxLength + 1;

        int a, b, c;
        switch (cellType){
            case topLeft:
                ExtendTableText(2);
                a = textRows;
                b = textRows + 1;
                tableText.set(a, tableText.get(a) + Lines.thick().upperLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text));
                break;
            case top:
                a = textRows - 2;
                b = textRows - 1;
                tableText.set(a, tableText.get(a) + Lines.thick().horizontalDown() + repeat(totalLength - 1, Lines.thick().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text));
                break;
            case topRight:
                a = textRows - 2;
                b = textRows - 1;
                tableText.set(a, tableText.get(a) + Lines.thick().horizontalDown() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().upperRightCorner());
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                break;
            case midLeft:
                ExtendTableText(2);
                a = textRows;
                b = textRows + 1;
                tableText.set(a, tableText.get(a) + Lines.thick().verticalRight() + repeat(totalLength - 1, Lines.thin().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text));
                break;
            case mid:
                a = textRows - 2;
                b = textRows - 1;
                tableText.set(a, tableText.get(a) + Lines.thin().plus() + repeat(totalLength - 1, Lines.thin().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text));
                break;
            case midRight:
                a = textRows - 2;
                b = textRows - 1;
                tableText.set(a, tableText.get(a) + Lines.thin().plus() + repeat(totalLength - 1, Lines.thin().horizontal()) + Lines.thick().verticalLeft());
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                break;
            case bottomLeft:
                ExtendTableText(doInput ? 2 : 3);
                a = textRows;
                b = textRows + 1;
                c = textRows + 2;
                tableText.set(a, tableText.get(a) + Lines.thick().verticalRight() + repeat(totalLength - 1, Lines.thin().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().lowerLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()));
                break;
            case bottom:
                int diff = doInput ? 1 : 0;
                a = textRows - 3 + diff;
                b = textRows - 2 + diff;
                c = textRows - 1 + diff;
                tableText.set(a, tableText.get(a) + Lines.thin().plus() + repeat(totalLength - 1, Lines.thin().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().horizontalUp() + repeat(totalLength - 1, Lines.thick().horizontal()));
                break;
            case bottomRight:
                diff = doInput ? 1 : 0;
                a = textRows - 3 + diff;
                b = textRows - 2 + diff;
                c = textRows - 1 + diff;
                tableText.set(a, tableText.get(a) + Lines.thin().plus() + repeat(totalLength - 1, Lines.thin().horizontal()) + Lines.thick().verticalLeft());
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().horizontalUp() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().lowerRightCorner());
                break;
            case leftSingleRow:
                ExtendTableText(doInput ? 2 : 3);
                a = textRows;
                b = textRows + 1;
                c = textRows + 2;
                tableText.set(a, tableText.get(a) + Lines.thick().upperLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().lowerLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()));
                break;
            case midSingleRow:
                diff = doInput ? 1 : 0;
                a = textRows - 3 + diff;
                b = textRows - 2 + diff;
                c = textRows - 1 + diff;
                tableText.set(a, tableText.get(a) + Lines.thick().horizontalDown() + repeat(totalLength - 1, Lines.thick().horizontal()));
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().horizontalUp() + repeat(totalLength - 1, Lines.thick().horizontal()));
                break;
            case rightSingleRow:
                diff = doInput ? 1 : 0;
                a = textRows - 3 + diff;
                b = textRows - 2 + diff;
                c = textRows - 1 + diff;
                tableText.set(a, tableText.get(a) + Lines.thick().horizontalDown() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().upperRightCorner());
                tableText.set(b, tableText.get(b) + Lines.thin().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().horizontalUp() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().lowerRightCorner());
                break;
            case topSingleColumn:
                ExtendTableText(2);
                a = textRows;
                b = textRows + 1;
                tableText.set(a, tableText.get(a) + Lines.thick().upperLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().upperRightCorner());
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                break;
            case midSingleColumn:
                ExtendTableText(2);
                a = textRows;
                b = textRows + 1;
                tableText.set(a, tableText.get(a) + Lines.thick().verticalRight() + repeat(totalLength - 1, Lines.thin().horizontal()) + Lines.thick().verticalLeft());
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                break;
            case bottomSingleColumn:
                ExtendTableText(hasInput ? 2 : 3);
                a = textRows;
                b = textRows + 1;
                c = textRows + 2;
                tableText.set(a, tableText.get(a) + Lines.thick().verticalRight() + repeat(totalLength - 1, Lines.thin().horizontal()) + Lines.thick().verticalLeft());
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().lowerLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().lowerRightCorner());
                break;
            case single:
                ExtendTableText(hasInput ? 2 : 3);
                a = textRows;
                b = textRows + 1;
                c = textRows + 2;
                tableText.set(a, tableText.get(a) + Lines.thick().upperLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().upperRightCorner());
                tableText.set(b, tableText.get(b) + Lines.thick().vertical() + formatText(foundInput, totalLength, text, Lines.thick().vertical()));
                if (doInput) break;
                tableText.set(c, tableText.get(c) + Lines.thick().lowerLeftCorner() + repeat(totalLength - 1, Lines.thick().horizontal()) + Lines.thick().lowerRightCorner());
                break;
        }
        textRows = tableText.size();
    }
    protected void ExtendTableText(int amount){
        for (int i = 0; i < amount; i++){
            tableText.add("");
        }
    }
    protected static String repeat(int count, char with) {
        return new String(new char[count]).replace('\0', with);
    }

}
