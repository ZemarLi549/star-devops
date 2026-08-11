import { ElMessage } from "element-plus";
export const buildHandleResultMsg = (resArr, handleTypeText) => {
    if (resArr[0]) {
        ElMessage({
            message: `${handleTypeText}成功`,
            type: "success",
        });
    } else if (resArr[2]) {
        ElMessage({
            message: `${handleTypeText}失败, 未识别启用的分派策略`,
            type: "warning",
        }); 
    } else {
        ElMessage({
            message: `已被他人${handleTypeText}, 任务状态发生变化`,
            type: "warning",
        }); 
    }
}
export const buildMultiHandleResultMsg = (
    resArr,
    handleTypeText
) => {
    const total = resArr.reduce((pre, cur) => pre + cur, 0);
    if (resArr[0] === total) {
        ElMessage({
            message: `批量${handleTypeText}成功`,
            type: "success",
        });
    } else if (resArr[0] + resArr[1] + resArr[3] === 0) {
        ElMessage({
            message: `批量${handleTypeText}失败, 未识别到启用的分派策略`,
            type: "warning",
        });
    } else {
        const successMsg = resArr[0] ? `${resArr[0]}条${handleTypeText}成功` : '';
        const changeMsg = resArr[1] ? `${resArr[1]}条被他人${handleTypeText}` : '';
        const colsedMsg = resArr[3] ? `${resArr[3]}条自动关闭` : ''
        const message = [successMsg, colsedMsg, changeMsg].filter(item => item !== '').join(', ')
        ElMessage({
            message,
            type: "warning",
        })
    }
};
