package priv.juergenie.vrasland.database

import org.mapdb.DB
import org.mapdb.DBMaker
import priv.juergenie.vrasland.common.Constant.Companion.CONFIG

/**
 * 内置数据库的相关配置
 */
class DbConfigure {
    companion object {
        val db: DB

        init {
            val dbpath = CONFIG.get("database.dbpath", "./vrasland.db")
            val concurrency = CONFIG.get("database.concurrencyScale", "64").toInt()
            db = DBMaker.fileDB((dbpath)).
                    fileMmapEnableIfSupported().    // 启用内存映射，提高读写速度
                    fileMmapPreclearDisable().      // 启用 Mmap 优化，提高读写速度
                    cleanerHackEnable().            // 启用针对 JVM 的 Hack 部分，解决一部分 bug
                    closeOnJvmShutdown().           // 启用 JVM 关闭时的自动关闭功能，提高数据文件的安全等级
                    transactionEnable().            // 启用事务，提高数据安全等级
                    concurrencyScale(concurrency).  // 启用并发度配置
                    make()
        }
    }
}
