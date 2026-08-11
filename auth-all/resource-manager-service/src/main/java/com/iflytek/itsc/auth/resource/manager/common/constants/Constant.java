package com.iflytek.itsc.auth.resource.manager.common.constants;

import com.iflytek.itsc.core.constant.CoreConstant;

/**
 * @author xdkong2
 * @date 2023/12/4
 * @desc 统一常量
 **/
public class Constant extends CoreConstant {

    /**
     * 子平台类型
     */
    public static final String MODULE_TYPE = "MODULE_TYPE";
    /**
     * 工作台类型
     */
    public static final String MODULE_TYPE_WORK_BENCH = "WORK_BENCH";
    /**
     * 默认密码
     */
    public static final String DEFAULT_P_W_D = "DEFAULT_PWD";

    /**
     * 用户数据类型
     */
    public static final String USER_DATA = "USER_DATA";

    /**
     * 父级id
     */
    public static final Long DEFAULT_PARENT_ID = 0L;
    public static final String DEFAULT_PARENT_ID_STR = "0";
    /**
     * 默认不加密
     */
    public static final String DEFAULT_SECURITY_TYPE = "default";
    /**
     * 三未信案密码服务平台进行加密
     */
    public static final String SAN_SECURITY_TYPE = "san";
    /**
     * 使用海泰房源密码服务平台进行加密
     */
    public static final String HAITAI_SECURITY_TYPE = "haitai";

    /**
     * 部署的模块信息
     */
    public static final String DEPLOY_MODULE = "DEPLOY_MODULE";
    public static final String DEPLOY_MODE_SINGLE = "single";
    public static final String DEPLOY_MODE_ALL = "all";

    public static final String DM_DRIVER_NAME = "dm.jdbc.driver.DmDriver";
}
