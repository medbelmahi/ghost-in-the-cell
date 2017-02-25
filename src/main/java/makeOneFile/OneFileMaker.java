package makeOneFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Mohamed BELMAHI on 25/09/2016.
 */
public class OneFileMaker {
    
    public static final String PLAYER_FILE_PATH = "D:\\CodinGame\\ghost-in-the-cell\\src\\main\\java\\playerFile\\Player.java";
    public static final String PACKAGE_PATH = "D:\\CodinGame\\ghost-in-the-cell\\src\\main\\java\\ghostinthecell";
    public static final int UPDATE_RANGE_IN_SECOND = 10; //seconds
    
    public static void main(final String[] args) {
        while (true) {
            System.out.println("Starting --> " + new Date());
            final File hyperSonicPackage = new File(PACKAGE_PATH);

            String content = filesContent(hyperSonicPackage);

            content = "import java.util.*;\n" + content;
            content = "import java.awt.*;\n" + content;
            content = "import java.util.List;\n" + content;
            content = "import java.awt.geom.Line2D;\n" + content;
            content = "import java.awt.geom.Point2D;\n" + content;

            makePlayerFile(content);
    
            try {
                Thread.sleep(UPDATE_RANGE_IN_SECOND * 1000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    
    private static void makePlayerFile(final String content) {
        final File file = new File(PLAYER_FILE_PATH);

        try (FileOutputStream fop = new FileOutputStream(file)) {

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            final byte[] contentInBytes = content.getBytes(StandardCharsets.UTF_8);

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");
    
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String filesContent(final File file) {

        String filesContent = "";

        if (file.isDirectory()) {
    
            for (final File f : file.listFiles()) {
                filesContent += "\n" + filesContent(f);
            }

        } else {
            BufferedReader br = null;

            try {

                String sCurrentLine;

                br = new BufferedReader(new FileReader(file));

                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.startsWith("package") || sCurrentLine.startsWith("import")) {
                        continue;
                    } else if (sCurrentLine.startsWith("public class") || sCurrentLine
                            .startsWith("public abstract class") || sCurrentLine.startsWith("public enum")) {
                        sCurrentLine = sCurrentLine.replaceFirst("public ", "");
                    }
                    //System.out.println(sCurrentLine);
                    filesContent += "\n" + sCurrentLine;
                }
    
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) br.close();
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return filesContent;

    }

}