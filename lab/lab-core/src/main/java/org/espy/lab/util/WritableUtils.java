package org.espy.lab.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public final class WritableUtils {

    private WritableUtils() {
    }

    public static void saveInFile(Writable writable, String dir, String fileName) throws FileNotFoundException {
        File file = new File(dir);
        if (!file.exists() && !file.mkdirs()) {
            throw new FileNotFoundException("Can't create directories");
        }
        try (PrintWriter writer = new PrintWriter(dir + "/" + fileName)) {
            writable.write(writer);
        }
    }
}
