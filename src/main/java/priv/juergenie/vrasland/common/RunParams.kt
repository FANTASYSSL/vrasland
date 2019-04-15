package priv.juergenie.vrasland.common


/**
 * 用于存放所有启动参数
 */
class RunParams {
    /**
     * 基础支持数据库路径
     */
    lateinit var dbpath: String
    lateinit var concurrency: String
    /**
     * API 映射路径的根路径
     */
    lateinit var staticpath: String
}