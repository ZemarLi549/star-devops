package com.iflytek.itsc.auth.resource.manager.domain.form;

import com.iflytek.itsc.auth.resource.manager.common.annotation.NotEmptyThenLength;
import com.iflytek.itsc.auth.resource.manager.common.annotation.NotEmptyThenNotBlank;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.DeleteGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.SaveBatchInitGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/8
 * @desc
 **/
@Data
public class SysMenuForm implements Serializable {
    private static final long serialVersionUID = -5752936353744712232L;
    /**
     * 菜单id
     */
    @NotNull(message = "菜单id不能为空", groups = {UpdateGroup.class, DeleteGroup.class, SaveBatchInitGroup.class})
    private Long menuId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Length(max = 100, message = "菜单名称长度不能超过100")
    private String menuName;

    /**
     * 父id
     */
    @NotNull(message = "父id不能为空")
    private Long parentId = Constant.DEFAULT_PARENT_ID;

    /**
     * 子平台模块
     */
    @NotBlank(message = "模块标识不能为空")
    private String moduleType;
    /**
     * 是否菜单分组
     */
    @NotNull(message = "是否菜单分组标识不能为空")
    private Boolean isgroup;
    /**
     * 是否菜单
     */
    @NotNull(message = "是否菜单标识不能为空")
    private Boolean ismenu;

    /**
     * 是否外链
     */
    @NotNull(message = "是否外链标识不能为空")
    private Boolean isoutlink = false;

    /**
     * 菜单地址
     */
    @Length(max = 100, message = "菜单地址长度不能超过100")
    private String menuPath;

    /**
     * 图标
     */
    @Length(max = 100, message = "图标长度不能超过100")
    private String icon;
    /**
     * 平级内序号
     */
    @NotNull(message = "序号不能为空")
    private Integer sortNum = 0;

    @NotBlank(message = "菜单编码不能为空" ,groups = {SaveBatchInitGroup.class})
    private String menuNo;

    /**
     * 接口地址
     */
    private List<ApiForm> apiList;

    @Data
    public static class ApiForm {
        @NotBlank(message = "路径名称不能为空")
        @Length(max = 100, message = "路径名称长度不能超过100")
        private String path;
        @Length(max = 10, message = "方法标识长度不能超过10")
        private String method;
    }
}
