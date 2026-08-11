<template>
	<div class="slider">
		<div class="top">
			<div class="content">
				<div class="bg-img-div">
					<img ref="img" id="bg-img" src="" alt=""/>
				</div>
				<div ref="imgBg" class="slider-img-div" id="slider-img-div">
					<img ref="sliderBg" id="slider-img" src="" alt=""/>
				</div>
				<div
					class="slider_result slider_fail"
					:class="{ slider_showTip: showTip }"
				>
					<div class="slider_result_box" style="padding-top: 10%">
						<div class="slider_result_content">请正确拼合图像</div>
					</div>
				</div>
			</div>
			<div class="slider-move">
				<div class="slider-move-track">拖动滑块完成拼图</div>
				<div ref="btn" class="slider-move-btn" id="slider-move-btn" />
			</div>
		</div>
		<div class="bottom">
			<div
				ref="close"
				class="close-btn"
				@click="handleClose"
				title="关闭验证"
			/>
			<div
				ref="refresh"
				@click="reset"
				class="refresh-btn"
				id="slider-refresh-btn"
				title="刷新验证"
			/>
		</div>
	</div>
</template>

<script lang="ts">
import { defineComponent, onMounted, PropType, ref, watch } from "vue";
import useVerification from '@/hooks/useVerification';

export default defineComponent({
	emits: ["verification-code", "close"],
	props: {
		dialogStatus: {
			type: Boolean as PropType<boolean>,
			default: false
		}
	},
	setup(props, { expose, emit, slots, attrs }) {
		let load: any = null;
		const btn = ref<HTMLElement | null>(null);
		const img = ref<HTMLImageElement | null>(null);
		const imgBg = ref<HTMLImageElement | null>(null);
		const sliderBg = ref<HTMLImageElement | null>(null);
		const close = ref<HTMLElement | null>(null);
		const refresh = ref<HTMLElement | null>(null);
		const showTip = ref(false);
		const reset = () => {
			load && load();
		};
		// 出错
		const error = () => {
			showTip.value = true;
			setTimeout(() => {
				reset();
				showTip.value = false;
			}, 1000);
		};
		const handleClose = () => {
			emit("close");
		};
		watch(
			() => props.dialogStatus,
			newStatus => {
				if (newStatus) {
					reset();
				}
			},
			{
				immediate: true
			}
		);
		onMounted(async () => {
			if (
				btn.value &&
				img.value &&
				imgBg.value &&
				sliderBg.value &&
				close.value &&
				refresh.value
			) {
				const { currentCaptchaConfig, currentCaptchaId, reload, result } =
					await useVerification(
						btn.value,
						img.value,
						imgBg.value,
						sliderBg.value,
						close.value,
						refresh.value,
						emit
					);
				load = reload;
			}
		});
		expose({
			reset,
			error
		});
		return {
			btn,
			img,
			imgBg,
			sliderBg,
			showTip,
			close,
			refresh,
			reset,
			handleClose
		};
	}
});
</script>

<style lang="scss" scoped>
.slider {
	background-color: #fff;
	width: 278px;
	height: 285px;
	z-index: 999;
	border-radius: 6px;
	box-shadow: 0 0 11px 0 #999999;
	.top {
		padding: 9px;
		padding-bottom: 5px;
		border-bottom: 1px solid #eee;
	}
}

.slider .content {
	width: 100%;
	height: 159px;
	position: relative;
	overflow: hidden;
}

.bg-img-div {
	width: 100%;
	height: 100%;
	position: absolute;
	transform: translate(0px, 0px);
}

.slider-img-div {
	height: 100%;
	position: absolute;
	transform: translate(0px, 0px);
}

.bg-img-div img {
	width: 100%;
}

.slider-img-div img {
	height: 100%;
}

.slider .slider-move {
	height: 50px;
	width: 100%;
	margin: 15px 0 0;
	position: relative;
}

.slider .bottom {
	height: 45px;
	padding: 12px;
	width: 100%;
}

.refresh-btn,
.close-btn,
.slider-move-track,
.slider-move-btn {
	background: url('@/assets/imgs/sprite.1.2.4.png') no-repeat;
}

.refresh-btn,
.close-btn {
	display: inline-block;
}

.slider-move .slider-move-track {
	line-height: 38px;
	font-size: 14px;
	text-align: center;
	white-space: nowrap;
	color: #88949d;
	-moz-user-select: none;
	-webkit-user-select: none;
	user-select: none;
}

.slider {
	user-select: none;
}

.slider-move .slider-move-btn {
	transform: translate(0px, 0px);
	background-position: -5px 11.79625%;
	position: absolute;
	top: -12px;
	left: 0;
	width: 66px;
	height: 66px;
}

.slider-move-btn:hover,
.close-btn:hover,
.refresh-btn:hover {
	cursor: pointer;
}

.bottom .close-btn {
	width: 20px;
	height: 20px;
	background-position: 0 44.86874%;
}

.bottom .refresh-btn {
	width: 20px;
	height: 20px;
	background-position: 0 81.38425%;
	margin-left: 10px;
}

.slider_result {
	position: absolute;
	left: 0;
	z-index: 999;
	width: 100%;
	color: white;
	bottom: -25px;
	height: 24px;
	transition: bottom 0.3s ease;
	&.slider_showTip {
		bottom: 0;
	}
	&.slider_fail {
		background-color: #de715b;
	}
	.slider_result_content {
		position: absolute;
		top: 0;
		text-indent: 16px;
		font-size: 14px;
		line-height: 24px;
		height: 24px;
	}
}
</style>
