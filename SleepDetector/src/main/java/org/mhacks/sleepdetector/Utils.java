package org.mhacks.sleepdetector;

import java.io.FileInputStream;
import java.io.InputStream;

public class Utils {
    //Default endchar of \0
    public static String readFile(String file) {
        try {
            byte[] buffer = new byte[128];
            InputStream is = new FileInputStream(file);
            int len = is.read(buffer);
            is.close();

            if (len > 0) {
                int i;
                for (i=0; i<len; i++) {
                    if (buffer[i] == '\0') {
                        break;
                    }
                }
                return new String(buffer, 0, 0, i);
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
