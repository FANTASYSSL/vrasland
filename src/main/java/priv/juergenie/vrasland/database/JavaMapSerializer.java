//package priv.juergenie.vrasland.database;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import org.jetbrains.annotations.NotNull;
//import org.mapdb.DataInput2;
//import org.mapdb.DataOutput2;
//import org.mapdb.Serializer;
//
//import java.io.IOException;
//import java.util.Map;
//
//public class JavaMapSerializer implements Serializer<Map<String, Object>> {
//    private Gson gson = new GsonBuilder().disableInnerClassSerialization().disableHtmlEscaping().create();
//
//    @Override
//    public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull Map<String, Object> stringObjectMap) throws IOException {
//        dataOutput2.writeUTF(gson.toJson(stringObjectMap));
//    }
//
//    @Override
//    public Map<String, Object> deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
//        return null;
//    }
//
//    @Override
//    public int fixedSize() {
//        return 0;
//    }
//
//    @Override
//    public boolean isTrusted() {
//        return false;
//    }
//
//    @Override
//    public int compare(Map<String, Object> first, Map<String, Object> second) {
//        return 0;
//    }
//
//    @Override
//    public boolean equals(Map<String, Object> first, Map<String, Object> second) {
//        return false;
//    }
//
//    @Override
//    public int hashCode(@NotNull Map<String, Object> o, int seed) {
//        return 0;
//    }
//
//    @Override
//    public boolean needsAvailableSizeHint() {
//        return false;
//    }
//
//    @Override
//    public Map<String, Object> deserializeFromLong(long input, int available) throws IOException {
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> clone(Map<String, Object> value) throws IOException {
//        return null;
//    }
//}
