package chau.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
}
