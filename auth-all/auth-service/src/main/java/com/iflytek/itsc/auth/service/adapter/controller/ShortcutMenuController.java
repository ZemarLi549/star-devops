package com.iflytek.itsc.auth.service.adapter.controller;

import com.iflytek.itsc.auth.service.integration.ResourceManagerService;
import com.iflytek.itsc.auth.service.domain.dto.FirstLevelMenu;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Classname ShortcutMenuController
 * @Description 快捷菜单Controller
 * @Date 2023/12/18 15:07
 * @Created by wxqiu
 */
@RestController
public class ShortcutMenuController {

    @Resource
    ResourceManagerService resourceManagerService;

    @GetMapping(value = "/shortcutMenu")
    public RestResponse<List<FirstLevelMenu>> findShortcutMenu() {
        return RestResponse.buildSuccess(resourceManagerService.findShortcutMenu());
    }


    @PostMapping("/saveShortcutMenu")
    public RestResponse saveShortcutMenu(@RequestBody @Valid FirstLevelMenu firstLevelMenu) {
        resourceManagerService.saveShortcutMenu(firstLevelMenu);
        return RestResponse.buildSuccess();
    }

    @PostMapping("deleteShortcutMenu/{menuId}")
    public RestResponse deleteShortcutMenu(@PathVariable("menuId") String menuId) {
        resourceManagerService.deleteShortcutMenu(menuId);
        return RestResponse.buildSuccess();
    }


}
