public class Lines {

    //https://en.wikipedia.org/wiki/Box-drawing_character 

    public static final char verticalDotted = '\u254E';

    private static Lines thin;
    private static Lines thick;
    private static final char[] thinChars = new char[]{
        '\u2500', '\u2502', '\u250C', '\u2510', '\u2514', '\u2518', '\u251C', '\u2524', '\u252C', '\u2534', '\u253C'
    };
    private static final char[] thickChars = new char[]{
        '\u2501', '\u2503', '\u250F', '\u2513', '\u2517', '\u251B', '\u2520', '\u2528', '\u252F', '\u2537', '\u254B'
    };

    public static Lines thin(){
        if (thin != null){
            return thin;
        }
        thin = new Lines(thinChars);
        return thin;
    }
    public static Lines thick(){
        if (thick != null){
            return thick;
        }
        thick = new Lines(thickChars);
        return thick;
    }

    private char[] unicodeChars;
    public char horizontal(){
        return unicodeChars[0];
    }
    public char vertical(){
        return unicodeChars[1];
    }
    public char upperLeftCorner(){
        return unicodeChars[2];
    }
    public char upperRightCorner(){
        return unicodeChars[3];
    }
    public char lowerLeftCorner(){
        return unicodeChars[4];
    }
    public char lowerRightCorner(){
        return unicodeChars[5];
    }
    public char verticalRight(){
        return unicodeChars[6];
    }
    public char verticalLeft(){
        return unicodeChars[7];
    }
    public char horizontalDown(){
        return unicodeChars[8];
    }
    public char horizontalUp(){
        return unicodeChars[9];
    }
    public char plus(){
        return unicodeChars[10];
    }

    private Lines(char[] unicodeChars){
        this.unicodeChars = unicodeChars;
    }
}
