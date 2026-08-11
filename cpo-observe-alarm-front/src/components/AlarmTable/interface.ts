export interface ColumnProps {
  label?: string; // 表头标签
  width?: number; // 列宽度
  property?: string; // 对应字段
  show?: boolean; // 是否显示
  headerInfo?: string; // 表头是否显示文字提示
  formatter?: Function; // 自定义表格内容显示
  fixed?: boolean | string; // 固定列
  disDraggable?: boolean; // 禁止表头的排序
  sortable?: boolean|string; // 列内容排序
  'sort-method'?: Function; // 列内容排序
  'min-width'?: number; // 列最小宽度
}