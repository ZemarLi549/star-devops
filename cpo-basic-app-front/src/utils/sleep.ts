export function sleep(time: number, callback?: Function): Promise<any> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      callback && callback();
      resolve("");
    }, time);
  });
}
