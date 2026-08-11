# Dialingtest
## 配置

1、设置机器为拨测点（开启拨测功能），需要将enable_input设置位true，设置为false表示取消该拨测点；

2、设置拨测点名称，只需要修改point_name的值。若无point_name参数，可手动添加。注：拨测点名称不能重复。

  相关配置如下:

```
[[inputs.dialtesting]]
  # 是否启用该input
  enable_input = true

  # 拨测点名称
  point_name = \"default\"

 ......

```
