package priv.juergenie.vrasland.utils;

import org.springframework.boot.system.ApplicationHome;

public class FileUtil {
    /**
     * 获取项目的启动路径。
     * @param cls
     * @return
     */
    public static String getStartedPath(Class cls) {
        ApplicationHome home = new ApplicationHome(cls);
        return home.getSource().getAbsolutePath().replace("\\", "/");
    }
}
