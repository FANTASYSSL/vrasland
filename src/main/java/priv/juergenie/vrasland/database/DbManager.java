//package priv.juergenie.vrasland.database;
//
//import org.mapdb.DB;
//import org.mapdb.Serializer;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//
//@Repository
//public class DbManager {
//    @Resource
//    private DB db;
//
//    public VraslandTableList listRepository(String name) {
//        if (StringUtils.isEmpty(name))
//            return null;
//        else
//            return new VraslandTableList(db.indexTreeList(name).
//                    layout(64, 8).
//                    createOrOpen()
//            );
//    }
//
//    public VraslandTableMap mapRepository(String name) {
//        if (StringUtils.isEmpty(name))
//            return null;
//        else
//            return new VraslandTableMap(db.treeMap(name, Serializer.STRING, Serializer.JAVA).maxNodeSize(64).createOrOpen());
//    }
//}
