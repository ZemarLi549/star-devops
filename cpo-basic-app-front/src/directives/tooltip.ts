import { h, render } from "vue";
import type { DirectiveBinding, ObjectDirective, VNode } from "vue";
import { ElTooltip, ElConfigProvider, useGlobalConfig } from "element-plus";
import type { ElTooltipProps } from "element-plus";

// 支持传入tooltip的一些配置
type TooltipBindingType = Partial<
  Pick<
    ElTooltipProps,
    | "appendTo"
    | "effect"
    | "enterable"
    | "hideAfter"
    | "offset"
    | "placement"
    | "popperClass"
    | "popperOptions"
    | "showAfter"
    | "showArrow"
  >
>;

interface ExtendsHTMLElement extends HTMLElement {
  __mouseenter_handle: any;
}

let removePopper: any = null;

// 使用方式<div v-tooltip>11111</div>
const Tooltip: ObjectDirective = {
  mounted(
    el: ExtendsHTMLElement,
    binding: DirectiveBinding<TooltipBindingType>
  ) {
    const createTooltip = () => {
      const isShow = autoShowToolTip(el); // 判断内部是否溢出
      const config = useGlobalConfig(); // 获取全局命名空间
      if (isShow) {
        if (removePopper?.el === el) {
          return;
        }
        removePopper?.();
        let vm = h(
          ElConfigProvider,
          {
            namespace: config.value?.namespace || "el",
          },
          [
            h(ElTooltip, {
              trigger: "hover",
              virtualRef: el,
              virtualTriggering: true,
              placement: "top",
              content: el.innerHTML,
              transition: "none",
              onHide: () => {
                removePopper?.();
              },
              ...binding.value,
            }),
          ]
        );
        let container = document.createElement("div");
        render(vm, container);
        (vm.children as Array<VNode>)[0].component!.exposed!.onOpen();
        removePopper = () => {
          render(null, container);
          container = null;
          vm = null;
          removePopper = null;
        };
        removePopper.el = el;
      }
    };
    const handle = () => {
      createTooltip();
    };
    el.addEventListener("mouseenter", handle);
    el.__mouseenter_handle = handle;
  },
  beforeUnmount(el: ExtendsHTMLElement) {
    removePopper?.();
    el.removeEventListener("mouseenter", el.__mouseenter_handle);
    delete el.__mouseenter_handle;
  },
};

const autoShowToolTip = (el: HTMLElement) => {
  const range = document.createRange();
  range.setStart(el, 0);
  range.setEnd(el, el.childNodes.length);
  let rangeWidth = range.getBoundingClientRect().width;
  let rangeHeight = range.getBoundingClientRect().height;
  const offsetWidth = rangeWidth - Math.floor(rangeWidth);
  const offsetHeight = rangeHeight - Math.floor(rangeHeight);
  if (offsetWidth < 0.001) {
    rangeWidth = Math.floor(rangeWidth);
  }
  if (offsetHeight < 0.001) {
    rangeHeight = Math.floor(rangeHeight);
  }
  const { width: cellChildWidth, height: cellChildHeight } =
    el.getBoundingClientRect();
  const { top, left, right, bottom } = getPadding(el);
  const horizontalPadding = left + right;
  const verticalPadding = top + bottom;
  if (
    rangeWidth + horizontalPadding > cellChildWidth ||
    rangeHeight + verticalPadding > cellChildHeight
  ) {
    return true;
  }
  return false;
};

const getPadding = (el: HTMLElement) => {
  const style = window.getComputedStyle(el, null);
  const paddingLeft = Number.parseInt(style.paddingLeft, 10) || 0;
  const paddingRight = Number.parseInt(style.paddingRight, 10) || 0;
  const paddingTop = Number.parseInt(style.paddingTop, 10) || 0;
  const paddingBottom = Number.parseInt(style.paddingBottom, 10) || 0;
  return {
    left: paddingLeft,
    right: paddingRight,
    top: paddingTop,
    bottom: paddingBottom,
  };
};

export default Tooltip;
