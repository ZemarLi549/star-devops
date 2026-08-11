export function handleTime(time: number) {
  switch (true) {
    case time < 60:
      return [time, "秒"];
    case time < 60 * 60:
      return [(time / 60).toFixed(1), "分"];
    case time < 60 * 60 * 24:
      return [(time / 60 / 60).toFixed(1), "小时"];
    case time < 60 * 60 * 24 * 30:
      return [(time / 60 / 60 / 24).toFixed(1), "天"];
    default:
      return [(time / 60 / 60 / 24 / 30).toFixed(1), "月"];
  }
}

export function getDuration(dates: Date[]) {
  return {
    stime: Math.round(dates[0].getTime() / 1000),
    etime: Math.round(dates[1].getTime() / 1000),
  };
}

export function timestampToDate(timestamp) {
  if (!timestamp) return '-'
  timestamp = timestamp * 1000
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const seconds = date.getSeconds().toString().padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
