import type { Directive } from "vue";

const getInput = (el: HTMLElement): HTMLInputElement | null =>
  el instanceof HTMLInputElement ? el : el.querySelector("input");

function dispatchEvent(el: HTMLElement, type: string) {
  const evt = new Event(type, { bubbles: true, cancelable: true });
  el.dispatchEvent(evt);
}

interface ElType extends HTMLElement {
  inputEle: HTMLInputElement;
  __blurHandler__: () => any;
  __keyHandler__: (event: KeyboardEvent) => any;
}

const Trim: Directive = {
  mounted(el: ElType) {
    const inputEle = getInput(el);

    if (!inputEle) return;
    const trimFn = () => {      
      const newVal = el.inputEle.value.trim() || "";
      if (el.inputEle.value !== newVal) {
        el.inputEle.value = newVal;
        dispatchEvent(inputEle, "input");
      }
    };

    // 考虑部分回车搜索时尚未失去焦点
    const keydownFn = (event: KeyboardEvent) => {
      if (event.key === "Enter") {
        trimFn();
      }
    };

    el.inputEle = inputEle;
    el.__blurHandler__ = trimFn;
    el.__keyHandler__ = keydownFn;
    inputEle?.addEventListener("blur", trimFn);
    inputEle?.addEventListener("keydown", keydownFn);
  },
  beforeUnmount(el: ElType) {
    el.removeEventListener("blur", el.__blurHandler__);
    el.removeEventListener("keydown", el.__keyHandler__);
  },
};

export default Trim;
