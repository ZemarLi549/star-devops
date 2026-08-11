export const OVERLAY_OPACITY = 0.75;
export const OVERLAY_PADDING = 0;

export const SHOULD_ANIMATE_OVERLAY = true;
export const SHOULD_OUTSIDE_CLICK_CLOSE = false;
export const ALLOW_KEYBOARD_CONTROL = true;
export const SHOULD_OUTSIDE_CLICK_NEXT = true;

export const ESC_KEY_CODE = 27;
export const LEFT_KEY_CODE = 37;
export const RIGHT_KEY_CODE = 39;

export const ID_OVERLAY = "driver-page-overlay";
export const ID_STAGE = "driver-highlighted-element-stage";
export const ID_POPOVER = "driver-popover-item";

export const CLASS_DRIVER_HIGHLIGHTED_ELEMENT = "driver-highlighted-element";
export const CLASS_POSITION_RELATIVE = "driver-position-relative";
export const CLASS_FIX_STACKING_CONTEXT = "driver-fix-stacking";

export const CLASS_STAGE_NO_ANIMATION = "driver-stage-no-animation";
export const CLASS_POPOVER_TIP = "driver-popover-tip";
export const CLASS_POPOVER_TITLE = "driver-popover-title";
export const CLASS_POPOVER_DESCRIPTION = "driver-popover-description";
export const CLASS_POPOVER_FOOTER = "driver-popover-footer";
export const CLASS_CLOSE_BTN = "driver-close-btn";
export const CLASS_CLOSE_EVENT = "close-click-event";
export const CLASS_NEXT_STEP_BTN = "driver-next-btn";
export const CLASS_PREV_STEP_BTN = "driver-prev-btn";
export const CLASS_BTN_DISABLED = "driver-disabled";
export const CLASS_CLOSE_ONLY_BTN = "driver-close-only-btn";
export const CLASS_NAVIGATION_BTNS = "driver-navigation-btns";
export const CLASS_STEPS_COUNTER = "driver-steps-counter";

// NOTE: It must match the one set in the animations in CSS file
export const ANIMATION_DURATION_MS = 300;

// language=HTML
export const POPOVER_HTML = (className = "") => `
  <div id="${ID_POPOVER}" class="${className}">
    <button class="${CLASS_CLOSE_BTN} ${CLASS_CLOSE_EVENT}">
      <i class="el-icon ${CLASS_CLOSE_EVENT}"><svg class="${CLASS_CLOSE_EVENT}" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024"><path fill="currentColor" d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z"></path></svg></i>
    </button>
    <div class="${CLASS_POPOVER_TIP}">
    </div>
    <div class="${CLASS_POPOVER_TITLE}">Popover Title</div>
    <div class="${CLASS_POPOVER_DESCRIPTION}">Popover Description</div>
    <div class="${CLASS_POPOVER_FOOTER}">
      <span class="${CLASS_STEPS_COUNTER}">Steps Counter</span>
      <span class="driver-btn-group ${CLASS_NAVIGATION_BTNS}">
        <button class="${CLASS_PREV_STEP_BTN}">上一步</button>
        <button class="${CLASS_NEXT_STEP_BTN}">下一步</button>
      </span>
    </div>
  </div>`;

export const OVERLAY_HTML = `<div id="${ID_OVERLAY}"></div>`;
export const STAGE_HTML = `<div id="${ID_STAGE}"></div>`;
