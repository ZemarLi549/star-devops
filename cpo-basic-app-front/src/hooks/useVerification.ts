import user, { IVeriCode } from "@/apis/user";
import { nextTick, ref } from "vue";

export interface Config {
	startTime: Date;
	trackArr: Array<any>;
	movePercent: number;
	bgImageWidth: number;
	bgImageHeight: number;
	sliderImageWidth: number;
	sliderImageHeight: number;
	end: number;
	btn: HTMLElement;
	img: HTMLImageElement;
	close: HTMLElement;
	refresh: HTMLElement;
	startX: number;
	startY: number;
	moveX: number;
	stopTime: Date;
}

export interface IVerified extends Config {
	id: string;
	startSlidingTime: Date;
	entSlidingTime: Date;
	trackList: Array<any>;
}

export interface IVerifiedType {
	id: string;
	captchaTrack: IVerified;
}

export default async (
	btn: HTMLElement,
	img: HTMLImageElement,
	imgBg: HTMLImageElement,
	sliderImage: HTMLImageElement,
	close: HTMLElement,
	refresh: HTMLElement,
	emit: Function
): Promise<any> => {
	// 初始化返回值
	const currentCaptchaConfig = ref<Config>(null as any);
	const currentCaptchaId = ref<string>(null as any);
	const verCode = ref<IVeriCode>(null as any);
	const result = ref(false);

	// 初始化参数配置
	const initConfig = (
		bgImageWidth: number,
		bgImageHeight: number,
		sliderImageWidth: number,
		sliderImageHeight: number,
		end: any,
		btn: HTMLElement,
		img: HTMLImageElement,
		imgBg: HTMLImageElement,
		close: HTMLElement,
		refresh: HTMLElement
	) => {
		currentCaptchaConfig.value = {
			startTime: new Date(),
			trackArr: [],
			movePercent: 0,
			bgImageWidth,
			bgImageHeight,
			sliderImageWidth,
			sliderImageHeight,
			end,
			btn,
			img,
			imgBg,
			close,
			refresh
		} as any as Config;
	};

	// const clearPreventDefault = (event: Event) => {
	//   if (event.preventDefault) {
	//     event.preventDefault()
	//   }
	// }

	// const clearAllPreventDefault = ($div: Array<HTMLElement>) => {
	//   for (let el of $div) {
	//     el.addEventListener('touchmove', clearPreventDefault, false)
	//   }
	// }

	// const reductionAllPreventDefault = ($div: Array<HTMLElement>) => {
	//   for (let el of $div) {
	//     el.removeEventListener('touchmove', clearPreventDefault, false)
	//   }
	// }

	const down = (event: any) => {
		const targetTouches = event.originalEvent
			? event.originalEvent.targetTouches
			: event.targetTouches;
		let startX = event.pageX;
		let startY = event.pageY;
		if (startX === undefined) {
			startX = Math.round(targetTouches[0].pageX);
			startY = Math.round(targetTouches[0].pageY);
		}
		currentCaptchaConfig.value.startX = startX;
		currentCaptchaConfig.value.startY = startY;

		const pageX = currentCaptchaConfig.value.startX;
		const pageY = currentCaptchaConfig.value.startY;
		const startTime = currentCaptchaConfig.value.startTime;
		const trackArr = currentCaptchaConfig.value.trackArr;
		trackArr.push({
			x: pageX - startX,
			y: pageY - startY,
			type: "down",
			t: new Date().getTime() - startTime.getTime()
		});
		// pc
		window.addEventListener("mousemove", move);
		window.addEventListener("mouseup", up);
		// 手机端
		window.addEventListener("touchmove", move, true);
		window.addEventListener("touchmove", move, false);
		window.addEventListener("touchend", up, false);
		doDown(currentCaptchaConfig.value.btn);
	};

	const doDown = (el: HTMLElement) => {
		el.style.backgroundPosition = "-5px 31.0092%";
	};

	const move = (event: any) => {
		if (window.TouchEvent && event instanceof window.TouchEvent) {
			event = event.touches[0];
		}
		const pageX = Math.round(event.pageX || 0);
		const pageY = Math.round(event.pageY || 0);
		const startX = currentCaptchaConfig.value.startX || 0;
		const startY = currentCaptchaConfig.value.startY || 0;
		const startTime = currentCaptchaConfig.value.startTime;
		const end = currentCaptchaConfig.value.end;
		const bgImageWidth = currentCaptchaConfig.value.bgImageWidth;
		const trackArr = currentCaptchaConfig.value.trackArr;
		let moveX = pageX - startX;
		const track = {
			x: pageX - startX,
			y: pageY - startY,
			type: "move",
			t: new Date().getTime() - startTime.getTime()
		};
		trackArr.push(track);
		if (moveX < 0) {
			moveX = 0;
		} else if (moveX > end) {
			moveX = end;
		}
		currentCaptchaConfig.value.moveX = moveX;
		currentCaptchaConfig.value.movePercent = moveX / bgImageWidth;
		doMove(currentCaptchaConfig);
	};

	const doMove = (currentCaptchaConfig: any) => {
		const { moveX, btn, imgBg } = currentCaptchaConfig.value;
		btn.style.transform = `translate(${moveX}px, 0px)`;
		imgBg.style.transform = `translate(${moveX}px, 0px)`;
	};

	const up = (event: any) => {
		window.removeEventListener("mousemove", move);
		window.removeEventListener("mouseup", up);
		window.removeEventListener("touchmove", move);
		window.removeEventListener("touchend", up);
		if (window.TouchEvent && event instanceof window.TouchEvent) {
			event = event.changedTouches[0];
		}
		currentCaptchaConfig.value.stopTime = new Date();
		const pageX = Math.round(event.pageX || 0);
		const pageY = Math.round(event.pageY || 0);
		const startX = currentCaptchaConfig.value.startX || 0;
		const startY = currentCaptchaConfig.value.startY || 0;
		const startTime = currentCaptchaConfig.value.startTime;
		const trackArr = currentCaptchaConfig.value.trackArr;

		const track = {
			x: pageX - startX,
			y: pageY - startY,
			type: "up",
			t: new Date().getTime() - startTime.getTime()
		};

		trackArr.push(track);
		// printLog("up", track)

		// 发送验证
		valid();
	};

	// const reset = () => {
	//   const { btn, img } = currentCaptchaConfig.value
	//   btn.style.backgroundPosition = '-5px 11.79625%'
	//   btn.style.transform = 'translate(0px, 0px)'
	//   img.style.transform = 'translate(0px, 0px)'
	//   currentCaptchaId.value = ''
	// }

	const load = async () => {
		try {
			const data = await user.captcha();
			verCode.value = data;
			const { id, captcha } = verCode.value;
			currentCaptchaId.value = id;
			const { backgroundImage, sliderImage: sliderImg } = captcha;
			img.setAttribute("src", backgroundImage);
			sliderImage.setAttribute("src", sliderImg);

			nextTick(() => {
				const imgWidth = img.clientWidth;
				const imgHeight = img.clientHeight;
				const sliderWidth = sliderImage.clientWidth;
				const sliderHeight = sliderImage.clientHeight;
				initConfig(
					imgWidth,
					imgHeight,
					sliderWidth,
					sliderHeight,
					206,
					btn,
					img,
					imgBg,
					close,
					refresh
				);
			});
		} catch (error: any) {
			console.log(error);
		}
	};

	const reload = async () => {
		btn.style.backgroundPosition = "-5px 11.79625%";
		btn.style.transform = "translate(0px, 0px)";
		imgBg.style.transform = "translate(0px, 0px)";
		currentCaptchaId.value = "";
		await load();
	};

	const valid = async () => {
    if (!currentCaptchaId.value) return;
		const params: IVerifiedType = {
			id: currentCaptchaId.value,
			captchaTrack: {
				id: currentCaptchaId.value,
				...currentCaptchaConfig.value,
				startSlidingTime: currentCaptchaConfig.value.startTime,
				entSlidingTime: currentCaptchaConfig.value.stopTime,
				trackList: currentCaptchaConfig.value.trackArr
			}
		};
		emit("verification-code", params);
		// const data: boolean = await check(params)
		// if (data) {
		//   result.value = data
		//   emit('verification-code', params)
		// }
	};

	await load();

	nextTick(() => {
		currentCaptchaConfig.value.btn.addEventListener("mousedown", down);
		currentCaptchaConfig.value.btn.addEventListener("touchstart", down);
		currentCaptchaConfig.value.close.addEventListener("click", () => {});
		currentCaptchaConfig.value.btn.addEventListener("click", () => {});
	});

	return {
		currentCaptchaConfig,
		currentCaptchaId,
		reload,
		result
	};
};
