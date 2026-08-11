// 动态加载静态图片资源
export const getImgFromDirectory = (diretory = 'imgs', filename) => {
    return new URL(`../assets/${diretory}/${filename}`, import.meta.url).href;
};