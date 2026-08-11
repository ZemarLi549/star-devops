const fun = function (evt) {
  let target = evt.target;
  if (target.nodeName == "SPAN") {
    target = evt.target.parentNode;
  }
  target.blur();
};

const Btn = {
  mounted(el) {
    el.addEventListener("focus", fun);
  },
  unmounted(el) {
    el.removeEventListener("focus", fun);
  },
};

export default Btn
