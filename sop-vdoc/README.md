# 星迹可观测平台文档中心

## 一、本地运行

1. 安装node环境, 版本建议：node>=19.1.0、npm>=8.19.3

2. 切换到对应分支

3. 执行npm install安装依赖

4. 执行npm run docs:dev或npm run docs:dev/private运行, 浏览器打开http://localhost:5173/xxx地址

## 二、打包构建

npm run docs:build（或npm run docs:build/private）

## 三、部署相关说明

质效平台执行的打包指令为npm run docs:dev/private，打包产物为最新的文件包，不包含历史版本信息，顶部导航栏去除了历史版本链接，用于测试环境部署、私有化部署、最终产物收集

可观测中心化部署的文档中心地址在172.30.34.73，部署需要执行npm run docs:dev打包，其包含了历史版本的链接。版本更新具体步骤如下：

1、新建分支feature_Va.b.c

2、修改config.mjs中CurrentVersion为Va.b.c，在VersionList数组最后添加Va.b.c

```JavaScript
// 以V4.0.5升级到V4.0.6为例
// 修改前
const CurrentVersion = "V4.0.5";
const VersionList = ["V4.0.2", "V4.0.3", "V4.0.4", "V4.0.5"]

// 修改后
const CurrentVersion = "V4.0.6";
const VersionList = ["V4.0.2", "V4.0.3", "V4.0.4", "V4.0.5", "V4.0.6"]
```

3、更新文档后，执行npm run docs:build打包，打包后文件位于/.vitepress/dist

4、在服务器前端目录sop_doc中新建当前版本目录，放入打包后文件。同时修改ng配置，添加当前版本的代理地址，并将/sop_doc/重定向到当前版本



## 四、其他说明

#### markdown中添加链接

```
<!-- 相对路径 -->

[首页](../README.md)  
[配置参考](../reference/config.md)  
[快速上手](./getting-started.md)

<!-- 绝对路径 -->

[指南 > 介绍](/guide/introduction.md)  
[配置参考 > markdown.links](/reference/config.md#links)

<!-- URL -->

[GitHub](https://github.com)
```
markdown中图片资源加载同理，可采用相对路径和绝对路径的方式
```
![名称](路径)
```

#### 添加菜单目录

1、将markdown文件放入对应的目录下

2、找到menu.js中sidebar函数对应的位置，按已有格式添加菜单对象。其中`text`对应菜单名称，`link`对应菜单路径。注：根路径`/`对应docs文件夹，最终菜单由父级的`base`属性+`link`确定，以快速接入-采控接入为例，路径对应`/quick/采控接入.md`
```javascript
"/quick/": [
    {
        text: "接入指南",
        base: "/quick/",
        items: [
            { text: "采控接入", link: "采控接入.md" },
        ]
    }
]
```

#### 锚点菜单
右侧锚点菜单默认将渲染markdown文档的2-4级标题

#### 需要输入`{{aaa}}`的情况
vitepress会默认对`{{}}`进行语法解析，如果需要使用原始内容需要将内容进行如下调整
```
<!-- 原始内容 -->
`{{ $labels.busigroup }}`

<!-- 修改为 -->
<code v-pre>{{ $labels.busigroup }}</code>
```

#### 需要在无序列表中使用{}的情况
vitepress会默认无序列表中的{}进行语法解析，如果需要显示原始内容需要将内容进行如下调整
```
<!-- 原始内容 -->
- 启动命令：st_datasleuth插件的启动命令为 chmod +x ./start.sh &&./start.sh {service} {data_group}

<!-- 修改为 -->
- 启动命令：st_datasleuth插件的启动命令为 <span v-pre>chmod +x ./start.sh &&./start.sh {service} {data_group}</span> 

<!-- 也可以修改为 -->
- 启动命令：st_datasleuth插件的启动命令为 `chmod +x ./start.sh &&./start.sh {service} {data_group}`表示这里是代码

通过上面可以看到，对于显示有问题的通用解决方案是使用<标签 v-pre>内容</标签>的方式阻止vue进行变量解析
```

#### Markdown 扩展
VitePress内置的Markdown扩展，包括代码高亮“错误”和“警告”、自定义容器、数学方程等
具体可参考https://vitepress.dev/zh/guide/markdown