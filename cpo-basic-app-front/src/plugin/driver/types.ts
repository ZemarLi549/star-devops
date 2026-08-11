/* eslint-disable @typescript-eslint/no-misused-new */
/* eslint-disable @typescript-eslint/no-namespace */

export interface Driver {
	/**
	 * Refers to the global document object
	 */
	document: Document;

	/**
	 * Refers to the global window object
	 */
	window: Window;

	/**
	 * If the driver is active or not
	 */
	isActivated: boolean;

	/**
	 * Flag for if the current move was prevented. It is used in
	 * onNext() or onPrevious() callbacks to stop the current transition
	 */
	currentMovePrevented: boolean;

	/**
	 * Refers to the array of steps to be presented if any
	 */
	steps: Array<DriverNameSpace.Step>;

	/**
	 * Refers to step index that is currently active
	 */
	currentStep: number;

	/**
	 * Refers to the overlay for the screen
	 */
	overlay: DriverNameSpace.Overlay;

	options: DriverNameSpace.DriverOptions;

	/**
	 * Public getter for steps property
	 */
	getSteps(): Array<DriverNameSpace.Step>;

	/**
	 * Public setter for steps property
	 */
	setSteps(steps): void;

	/**
	 * Does the required bindings for DOM Events
	 */
	bind(): void;

	/**
	 * Listener for the click event, to decide if
	 * to next/previous step, reset the overlay etc
	 * @param {Event} e
	 */
	onClick(e: Event): void;

	/**
	 * Refreshes the driver and resets the position for stage
	 * and popover on resizing the window
	 */
	onResize(): void;

	/**
	 * Refreshes and repositions the popover and the overlay
	 */
	refresh(): void;

	/**
	 * Makes it operable with keyboard
	 * @param {Event} e
	 */
	onKeyUp(e: Event): void;

	/**
	 * Handles the internal next event
	 */
	handleNext(): void;

	/**
	 * Handles the internal previous event
	 */
	handlePrevious(): void;

	/**
	 * Prevents the current move. Useful in `onNext` if you want to
	 * perform some asynchronous task and manually move to next step
	 */
	preventMove(): void;

	/**
	 * Moves to the previous step if possible
	 * otherwise resets the overlay
	 */
	movePrevious(): void;

	/**
	 * Moves to the next step if possible
	 * otherwise resets the overlay
	 */
	moveNext(): void;

	/**
	 * Prevents the current move. Useful in `onNext` if you want to
	 * perform some asynchronous task and manually move to next step
	 */
	// eslint-disable-next-line @typescript-eslint/adjacent-overload-signatures
	preventMove(): void;

	/**
	 * Checks if can be moved to next step
	 * @return {boolean}
	 */
	hasNextStep(): boolean;

	/**
	 * Checks if can be moved to previous step
	 * @return {boolean}
	 */
	hasPreviousStep(): boolean;

	/**
	 * Resets the steps and clears the overlay
	 */
	reset(immediate?: boolean): void;

	/**
	 * Checks if there is any highlighted element or not
	 * @return {boolean}
	 */
	hasHighlightedElement(): boolean;

	/**
	 * Gets the currently highlighted element if any
	 * @return {DriverNameSpace.Element}
	 */
	getHighlightedElement(): DriverNameSpace.Element | null;

	/**
	 * Gets the last highlighted element if any
	 * @return {DriverNameSpace.Element}
	 */
	getLastHighlightedElement(): DriverNameSpace.Element | null;

	/**
	 * Defines the steps to be used in multi-step driver
	 * @param {Array<DriverNameSpace.Step>} steps
	 */
	defineSteps(steps: Array<DriverNameSpace.Step>): void;

	/**
	 * Prepares {DriverNameSpace.Element} from the given step definition
	 * @param {DriverNameSpace.Step | string} step query selector or step definition for the step
	 * @param {Array<DriverNameSpace.Step>} allSteps all the given steps
	 * @param {number} stepIndex array index for the current step
	 */
	prepareElementFromStep(
		step: DriverNameSpace.Step | string,
		allSteps: Array<DriverNameSpace.Step>,
		stepIndex: number
	): void;

	/**
	 * Starts presenting the set steps from the given index
	 * @param {number} index
	 */
	start(index?: number): void;

	/**
	 * Highlights the given element. Element can be a query selector or a step definition
	 * @param {string | DriverNameSpace.Step} element
	 */
	highlight(element: string | DriverNameSpace.Step): void;
}

export namespace DriverNameSpace {
	export interface Step {
		/**
		 * Query selector representing the DOM Element
		 */
		element: string | HTMLElement | Node;

		/**
		 * Color of stage when this step is active
		 * @default #ffffff
		 */
		stageBackground?: string;

		/**
		 * Options representing popover for this step
		 */
		popover?: DriverNameSpace.PopoverOptions;

		/**
		 * Is called when the next element is about to be highlighted
		 */
		onNext?: (element: DriverNameSpace.Element) => void;

		/**
		 * Is called when the previous element is about to be highlighted
		 */
		onPrevious?: (element: DriverNameSpace.Element) => void;
	}

	export interface Element {
		/**
		 * Refers to the DOM element that this class wraps
		 */
		node: Node | HTMLElement;
		/**
		 * Refers to the global Document object
		 */
		document: Document;
		/**
		 * Refers to the global window object
		 */
		window: Window;
		/**
		 * Options for this element
		 */
		options: DriverNameSpace.ElementOptions;
		/**
		 * Refers to the overlay that wraps the body
		 */
		overlay: DriverNameSpace.Overlay;
		/**
		 * Refers to the Popover object to be displayed against this element
		 */
		popover: DriverNameSpace.Popover;
		/**
		 * Refers to the stage that will be displayed behind this element
		 */
		stage: DriverNameSpace.Stage;

		/**
		 * Checks if the give element is in view port or not
		 * @return {boolean}
		 */
		isInView(): boolean;

		/**
		 * Brings the current DOMElement in view
		 */
		bringInView(): void;

		/**
		 * Gets the position of element on screen
		 * @return {DriverNameSpace.Position}
		 */
		getCalculatedPosition(): DriverNameSpace.Position;

		/**
		 * Manually scrolls to current element if scrollInToView is not supported
		 */
		scrollManually(): void;

		/**
		 * Is called when the current element is deselected
		 * @param {boolean} hideStage
		 */
		onDeselected(hideStage?: boolean): void;

		/**
		 * Is called when element is about to be highlighted
		 */
		onHighlightStarted(): void;

		/**
		 * Is called when element has been successfully highlighted
		 */
		onHighlighted(): void;

		/**
		 * Shows the stage on the current element
		 */
		showStage(): void;

		/**
		 * Hides the popover from the current element if visible
		 */
		hidePopover(): void;

		/**
		 * Shows the popover on current element if possible
		 */
		showPopover(): void;

		/**
		 * Gets the full page size
		 */
		getFullPageSize(): DriverNameSpace.ElementSize;

		/**
		 * Checks if the current element is same as passed element
		 * @param {DriverNameSpace.Element} element
		 */
		isSame(element: DriverNameSpace.Element): void;

		/**
		 * Gets the node that this element refers to
		 * @return {Node | HTMLElement}
		 */
		getNode(): Node | HTMLElement;

		/**
		 * Gets the size of current element
		 * @return {DriverNameSpace.ElementSize}
		 */
		getSize(): DriverNameSpace.ElementSize;

		/**
		 * Gets the popover on current element if any
		 * @returns {DriverNameSpace.Popover}
		 */
		getPopover(): DriverNameSpace.Popover;

		/**
		 * Removes the highlight classes from current element if any
		 */
		removeHighlightClasses(): void;

		/**
		 * Adds the highlight classes to current element if required
		 */
		addHighlightClasses(): void;

		/**
		 * Walks through the parents of the current element and fixes
		 * the stacking context
		 */
		fixStackingContext(): void;

		/**
		 * Checks if we can make the current element relative or not
		 * @return {boolean}
		 */
		canMakeRelative(): boolean;

		/**
		 * Get current element's CSS attribute value
		 * @return {string}
		 */
		getStyleProperty(): string;
	}

	export interface Overlay {
		/**
		 * Options to modify the overlay behavior
		 */
		options: DriverNameSpace.DriverOptions;

		/**
		 * Refers to currently highlighted element
		 */
		highlightedElement: DriverNameSpace.Element | null;

		/**
		 * Refers to element highlighted before currently highlighted element
		 */
		lastHighlightedElement: DriverNameSpace.Element | null;

		/**
		 * Refers to timeout handler used to animate while resetting
		 */
		hideTimer: number | null;

		/**
		 * Refers to global object Window
		 */
		window: Window;

		/**
		 * Refers to global object Document
		 */
		document: Document;

		/**
		 * Prepares the DOM element for overlay and appends to body
		 */
		attachNode(): void;

		/**
		 * Highlights the given Element while resetting the existing one
		 * @param {DriverNameSpace.Element} element
		 */
		highlight(element: DriverNameSpace.Element): void;

		/**
		 * Shows the overlay while appending to body if it is not there already
		 */
		show(): void;

		/**
		 * Gets the highlighted element in overlay if any
		 * @return {DriverNameSpace.Element | null}
		 */
		getHighlightedElement(): DriverNameSpace.Element | null;

		/**
		 * Gets the element highlighted before current element if any
		 * @return {DriverNameSpace.Element | null}
		 */
		getLastHighlightedElement(): DriverNameSpace.Element | null;

		/**
		 * Removes the overlay and deselects the highlighted element. Does that with animation
		 * by default or without animation if immediate is set to false
		 * @param {boolean} immediate
		 */
		clear(immediate?: boolean): void;

		/**
		 * Removes the overlay node if it exists
		 */
		removeNode(): void;

		/**
		 * Refreshes the overlay i.e. sets the size according to current window size
		 * And moves the highlight around if necessary
		 */
		refresh(): void;
	}

	export interface Popover {
		node: Node | HTMLElement;
		tipNode: Node | HTMLElement;
		titleNode: Node | HTMLElement;
		descriptionNode: Node | HTMLElement;
		footerNode: Node | HTMLElement;
		nextBtnNode: Node | HTMLElement;
		prevBtnNode: Node | HTMLElement;
		closeBtnNode: Node | HTMLElement;
		window: Window;
		document: Document;

		/**
		 * Prepares the DOM element for popover and appends to the body
		 */
		attachNode(): void;

		/**
		 * Hides the popover if visible
		 */
		hide(): void;

		/**
		 * Sets the initial state for popover before changing position
		 */
		setInitialState(): void;

		/**
		 * Shows the popover at the given position
		 * @param {DriverNameSpace.Position} position
		 */
		show(position: DriverNameSpace.Position): void;

		/**
		 * Renders the buttons in the footer of the popover
		 */
		renderFooter(): void;

		/**
		 * Positions the popover to the left of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnLeft(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the left-center of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnLeftCenter(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the left-bottom of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnLeftBottom(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the right of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnRight(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the right-center of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnRightCenter(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the right-bottom of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnRightBottom(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the top of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnTop(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the top-center of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnTopCenter(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the top-right of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnTopRight(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the bottom of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnBottom(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the bottom-center of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnBottomCenter(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the bottom-right of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnBottomRight(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover to the middle center of the given element position
		 * @param {DriverNameSpace.Position} position
		 */
		positionOnMidCenter(position: DriverNameSpace.Position): void;

		/**
		 * Positions the popover automatically around the element position
		 * @param {DriverNameSpace.Position} position
		 */
		autoPosition(position: DriverNameSpace.Position): void;

		/**
		 * Gets the title node for popover
		 * @returns {Node | HTMLElement}
		 */
		getTitleNode(): Node | HTMLElement;

		/**
		 * Gets the description node for popover
		 * @returns {Node | HTMLElement}
		 */
		getDescriptionNode(): Node | HTMLElement;
	}

	export interface Stage extends Element {
		/**
		 * Prepares the node and appends to body if not there already
		 */
		attachNode(): void;

		/**
		 * Hides the stage by removing the node
		 */
		hide(): void;

		/**
		 * Sets the default properties on the node
		 */
		setInitialStyle(): void;

		/**
		 * Shows the stage at provided position
		 * @param {DriverNameSpace.Position} position
		 */
		show(position: DriverNameSpace.Position): void;
	}

	export interface Position {
		/**
		 * Checks if the given position is valid and can be highlighted
		 * @return {boolean}
		 */
		canHighlight(): boolean;

		/**
		 * Checks if the given position is same as the passed position
		 * @param {DriverNameSpace.Position} position
		 */
		equals(position: DriverNameSpace.Position): void;
	}

	export interface ScreenCoordinates {
		x: number;
		y: number;
	}

	export interface ElementSize {
		width: number;
		height: number;
	}

	export interface PopoverOptions {
		/**
		 * Title for the popover
		 */
		title?: string;

		/**
		 * Description for the popover
		 */
		description: string;

		/**
		 * Whether to show control buttons or not
		 * @default true
		 */
		showButtons?: boolean;

		/**
		 * Text on the button in the final step
		 * @default 'Done'
		 */
		doneBtnText?: string;

		/**
		 * Text on the close button
		 * @default 'Close'
		 */
		closeBtnText?: string;

		/**
		 * Text on the next button
		 * @default 'Next'
		 */
		nextBtnText?: string;

		/**
		 * Text on the previous button
		 * @default 'Previous'
		 */
		prevBtnText?: string;

		/**
		 * Total number of elements with popovers
		 * @default 0
		 */
		totalCount?: number;

		/**
		 * Additional offset of the popover
		 * @default 0
		 */
		offset?: number;

		/**
		 * Counter for the current popover
		 * @default 0
		 */
		currentIndex?: number;

		/**
		 * If the current popover is the first one
		 * @default true
		 */
		isFirst?: boolean;

		/**
		 * If the current popover is the last one
		 * @default true
		 */
		isLast?: boolean;

		/**
		 * Position for the popover on element
		 * @default auto
		 */
		position?: string;

		/**
		 * className for the popover on element
		 */
		className?: string;
	}

	export interface DriverOptions {
		/**
		 * Whether to animate while transitioning from one highlighted
		 * element to another
		 * @default true
		 */
		animate?: boolean;

		/**
		 * Opacity for the overlay
		 * @default 0.75
		 */
		opacity?: number;

		/**
		 * Distance of elements corner from the edges of the overlay
		 * @default 10
		 */
		padding?: number;

		/**
		 * Options to be passed to scrollIntoView if supported by browser
		 * @default { behavior: 'instant', block: 'center' }
		 */
		scrollIntoViewOptions?: ScrollIntoViewOptions;

		/**
		 * Clicking outside the highlighted element should reset driver or not
		 * @default true
		 */
		allowClose?: boolean;

		/**
		 * Whether to allow controlling steps through keyboard
		 * @default true
		 */
		keyboardControl?: boolean;

		/**
		 * Clicking outside the highlighted element should move next
		 * @default false
		 */
		overlayClickNext?: boolean;

		/**
		 * Background color for the stage behind the highlighted element
		 * @default '#ffffff'
		 */
		stageBackground?: string;

		/**
		 * Whether to show control buttons or not
		 * @default true
		 */
		showButtons?: boolean;

		/**
		 * Text on the button in the final step
		 * @default 'Done'
		 */
		doneBtnText?: string;

		/**
		 * Text on the close button
		 * @default 'Close'
		 */
		closeBtnText?: string;

		/**
		 * Text on the next button
		 * @default 'Next'
		 */
		nextBtnText?: string;

		/**
		 * Text on the previous button
		 * @default 'Previous'
		 */
		prevBtnText?: string;

		/**
		 * className for the driver popovers
		 */
		className?: string;

		/**
		 * Callback to be called when element is about to be highlighted
		 * @param {DriverNameSpace.Element} element
		 * @returns any
		 */
		onHighlightStarted?: (element: DriverNameSpace.Element) => void;

		/**
		 * Callback to be called when element has been highlighted
		 * @param {DriverNameSpace.Element} element
		 * @returns any
		 */
		onHighlighted?: (element: DriverNameSpace.Element) => void;

		/**
		 * Callback to be called when element has been deselected
		 * @param {DriverNameSpace.Element} element
		 * @returns any
		 */
		onDeselected?: (element: DriverNameSpace.Element) => void;

		/**
		 * Is called when the overlay is about to reset
		 */
		onReset?: (element: DriverNameSpace.Element) => void;

		/**
		 * Is called when the next element is about to be highlighted
		 */
		onNext?: (element: DriverNameSpace.Element) => void;

		/**
		 * Is called when the previous element is about to be highlighted
		 */
		onPrevious?: (element: DriverNameSpace.Element) => void;
	}

	export type ElementOptions = DriverNameSpace.DriverOptions;

	export type StageOptions = ElementOptions;
}
