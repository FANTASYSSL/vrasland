package priv.juergenie.vrasland.bean;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.*;


public class ResultBeanTest {
    @Test
    public void create() {
        List<String> data = new ArrayList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        out.println(new Result<List<String>>().isOk().send("hello world").body(data).getStatue());
    }
}
