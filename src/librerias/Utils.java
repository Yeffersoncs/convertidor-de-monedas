package librerias;

public class Utils {
    /**
     * Returns if the provided string is numeric.
     * This does not check if a number fits inside a type, only if it's a valid number.
     * @param str the string to check.
     * @param period can be set to 'n' for integer numbers.
     */
    public static boolean isNumeric(String str, char period) {
        if (str == null || str.length() == 0)
            return false;
        char[] data = str.toCharArray();
        
        int i = 0;
        if (data[0] == '-' && data.length > 1) i = 1;

        if (period == 'n') {
        	for (; i < data.length ; i++) {
                if (data[i] < '0' || data[i] > '9') // Character.isDigit() can go here too.
                    return false;
            }
        }
        else {
        	for (; i < data.length ; i++) {
                if (((data[i] < '0' || data[i] > '9') && data[i] != period)) { // Character.isDigit() can go here too.
                    return false;
            }
        }
	}
        return true;
    }

    static public void printTabulated(String[] printables, int padding, int printablesPerLine) {
        for (int i = 0; i < printables.length; i++) {
            System.out.printf("%-" + printablesPerLine + "s", printables[i]);
            if (i % padding == 0)
                System.out.println();
        }

    }
}
