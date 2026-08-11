import { defineStore } from "pinia";
import { ref } from "vue";

export const useAlarmStore = defineStore("alarm", () => {
  const alarmDates = ref([
    new Date(new Date().getTime() - 3600 * 1000 * 24 * 30),
    new Date(),
  ]);
  const setAlarmDates = (dates: Date[]) => {
    alarmDates.value = dates
  };
  const getAlarmDuration = () => {
    return {
      startTime: Math.round(alarmDates.value[0].getTime() / 1000),
      etime: Math.round(alarmDates.value[1].getTime() / 1000),
    };
  };
  return {
    alarmDates,
    setAlarmDates,
    getAlarmDuration
  };
});
