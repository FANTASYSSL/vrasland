package priv.juergenie.vrasland.utils;

public class ClassUtil {

    public static int isInstance(Object obj, Class... cls) {

        for (int i = 0; i < cls.length; i++)
            if (cls[i].isInstance(obj))
                return i;

        return -1;
    }
}
