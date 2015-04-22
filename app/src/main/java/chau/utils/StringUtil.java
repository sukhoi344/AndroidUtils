package chau.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;

/** Provides some String utility methods
 * Created by chauthai on 12/18/14.
 */
public class StringUtil {

    /** Buffer size for InputStream */
    private static final int BUFF_SIZE = 2048;

    /**
     * Get string from an InputStream object
     * @param is
     * @return null if fail
     * @throws java.io.IOException problem with the InputStream object
     * @throws java.lang.NullPointerException null InputStream
     */
    public static String getString(InputStream is) throws IOException, NullPointerException {
        if (is == null)
            throw new NullPointerException("null InputStream object");

        char[] buf = new char[BUFF_SIZE];

        Reader r = new InputStreamReader(is, "UTF-8");
        StringBuilder s = new StringBuilder();

        while (true) {
            int n = r.read(buf);
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }

        return s.toString();
    }

    /**
     * Get string from file
     * @param file a text file
     * @return string which contains content of the text file
     */
    public static String getString(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("null file");

        StringBuilder sb = new StringBuilder();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }


        return sb.toString();
    }

    /**
     * Convert a formatted phone number into a plain digit-only
     * phone number
     * @param phoneNumber
     * @return empty string if failed.
     */
    public static String getPlainPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty())
            return "";

        String newPhoneNumber = new String(phoneNumber);

        newPhoneNumber = newPhoneNumber.replace(" ", "");
        newPhoneNumber = newPhoneNumber.replace("(", "");
        newPhoneNumber = newPhoneNumber.replace("-", "");
        newPhoneNumber = newPhoneNumber.replace(")", "");
        newPhoneNumber = newPhoneNumber.replace("+", "");

        if (!newPhoneNumber.startsWith("1") && newPhoneNumber.length() == 10) {
            newPhoneNumber = "1" + newPhoneNumber;
        }

        return newPhoneNumber;
    }

    /**
     * Format plain phone number into a formatted string
     * @param phoneNumber
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            return phoneNumber;
        }

        char[] chars = phoneNumber.toCharArray();
        StringBuilder sb = new StringBuilder();

        if(chars.length == 10) {
            sb.append("(" + chars[0]);
            sb.append(chars[1]);
            sb.append(chars[2]);
            sb.append(")-");
            sb.append(chars[3]);
            sb.append(chars[4]);
            sb.append(chars[5]);
            sb.append("-");
            sb.append(chars[6]);
            sb.append(chars[7]);
            sb.append(chars[8]);
            sb.append(chars[9]);

            return sb.toString();

        } else if (chars.length == 11) {
            sb.append("(" + chars[1]);
            sb.append(chars[2]);
            sb.append(chars[3]);
            sb.append(")-");
            sb.append(chars[4]);
            sb.append(chars[5]);
            sb.append(chars[6]);
            sb.append("-");
            sb.append(chars[7]);
            sb.append(chars[8]);
            sb.append(chars[9]);
            sb.append(chars[10]);

            return sb.toString();
        }

        return phoneNumber;
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
