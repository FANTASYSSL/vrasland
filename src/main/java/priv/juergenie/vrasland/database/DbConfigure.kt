package priv.juergenie.vrasland.database

import org.mapdb.DB
import org.mapdb.DBMaker
import priv.juergenie.vrasland.common.RunParams

open class DbConfigure {
    open fun db(params: RunParams): DB {
        return DBMaker.fileDB((if (params.dbpath.isEmpty()) "." else params.dbpath) + "/vrasland.db").
                fileMmapEnableIfSupported().                    // 启用内存映射，提高读写速度
                fileMmapPreclearDisable().                      // 启用 Mmap 优化，提高读写速度
                cleanerHackEnable().                            // 启用针对 JVM 的 Hack 部分，解决一部分 bug
                closeOnJvmShutdown().                           // 启用 JVM 关闭时的自动关闭功能，提高数据文件的安全等级
                transactionEnable().                            // 启用事务，提高数据安全等级
                concurrencyScale(params.concurrency.toInt()).   // 启用并发度配置
                make()
    }
}