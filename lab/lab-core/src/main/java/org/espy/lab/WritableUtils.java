package org.espy.lab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public final class WritableUtils {

    private WritableUtils() {
    }

    public static void save(Writable writable, String dir, String fileName) throws FileNotFoundException {
        File file = new File(dir);
        file.mkdirs();
        try (PrintWriter writer = new PrintWriter(dir + "/" + fileName)) {
            writable.write(writer);
        }
    }
}
