package priv.juergenie.vrasland.utils;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtil {
    public static String getStartedPath(Class cls) {
//        String str = null;
//
//        try {
//            File file = new File(ResourceUtils.getURL("classpath:").getPath());
//            if (file == null) file = new File("");
//            str = file.getAbsolutePath();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return str;

        ApplicationHome home = new ApplicationHome(cls);
        return home.getSource().getAbsolutePath().replace("\\", "/");
    }
}
