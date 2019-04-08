package priv.juergenie.vrasland.core;

public class Vrasland {
    private static Vrasland instance = null;

    public static Vrasland getInstance() {
        if (instance == null)
            instance = new Vrasland();
        return instance;
    }


}
