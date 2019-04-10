package priv.juergenie.vrasland.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import priv.juergenie.vrasland.common.Constant;

@Configuration
public class DbManager {
    @Bean
    @Scope(Constant.SCOPE_SINGLETON) // 构造单例模式的数据库Bean
    public DB db(@Value("${dbPath:}")String dbPath, @Value("${concurrencyScale:64}")int concurrencyScale) {
        return DBMaker.fileDB((dbPath.isEmpty() ? "." : dbPath) + "/vrasland.db").
                fileMmapEnableIfSupported().        // 启用内存映射，提高读写速度
                fileMmapPreclearDisable().          // 启用 Mmap 优化，提高读写速度
                cleanerHackEnable().                // 启用针对 JVM 的 Hack 部分，解决一部分 bug
                closeOnJvmShutdown().               // 启用 JVM 关闭时的自动关闭功能，提高数据文件的安全等级
                transactionEnable().                // 启用事务，提高数据安全等级
                concurrencyScale(concurrencyScale). // 启用并发度配置
                make();
    }
}
