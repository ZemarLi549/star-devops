package com.iflytek.itsc.auth.service.common.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.DefaultResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH;

/**
 * @Classname CaptchaResourceStoreConfig
 * @Description 验证码配置
 * @Date 2023/12/14 19:11
 * @Created by wxqiu
 */
@Component
public class CaptchaResourceStoreConfig extends DefaultResourceStore {
    public CaptchaResourceStoreConfig(){
        // 滑块验证码 模板 (系统内置)
        Map<String, Resource> template1 = new HashMap<>(4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/matrix.png")));
        Map<String, Resource> template2 = new HashMap<>(4);
        template2.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/matrix.png")));
        // 旋转验证码 模板 (系统内置)
        Map<String, Resource> template3 = new HashMap<>(4);
        template3.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template3.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));
        template3.put(SliderCaptchaConstant.TEMPLATE_MATRIX_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/matrix.png")));

        // 1. 添加一些模板
        addTemplate(CaptchaTypeConstant.SLIDER, template1);
        addTemplate(CaptchaTypeConstant.SLIDER, template2);
        addTemplate(CaptchaTypeConstant.ROTATE, template3);

        // 2. 添加自定义背景图片
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image1.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image2.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image3.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image4.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image5.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image6.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image7.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image8.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image9.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image10.png"));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captchaimages/image11.png"));
        addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "captchaimages/image11.png"));
        addResource(CaptchaTypeConstant.CONCAT, new Resource("classpath", "captchaimages/image11.png"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "captchaimages/image11.png"));
    }
}
