// 根据动态数据, 获取echart options
import line_charts_icon from '@/assets/imgs/control/line_charts_icon.png'
import line_charts_icon_select from "@/assets/imgs/control/line_charts_icon_select.png";

import dayjs from "dayjs";

export function dynaminicHisLineOption(alert_day_list, dataIndex, time) {
  const data = alertDayListHandle(alert_day_list, time)
  const option = {
    grid: {
      top: 30,
      left: 30,
      right: 8,
      bottom: 50,
    },
    legend: {
      type: "plain",
      itemHeight:10,
      itemWidth:24,
      inactiveColor: '#8E939E',
      lineHeight: 24,
      bottom: "0",
        textStyle: {
          color: '#2C3645',
          fontSize:12,
      },
      selectedMode:false,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: "#f2f3f5",
        },
      },
      axisTick: {
        show: false,
        alignWithLabel:true,
      },
      axisLabel: {
        formatter: function (value, index) {
          if(index===0){ 
            return ``
        }else if(index === data.length - 1) 
            return ``;
          const dateArr = value.split('-')
    
          dateArr.shift()
          return dateArr.join('-');
        },
          textStyle: {
            color: '#8E939E'
          }
      },
      data: data.map(item=>item.date)
    },
    yAxis: {
      type: "value",
      splitLine:{
        lineStyle:{
          color:'#f2f3f5',
        },
      },
      axisLine: {
        lineStyle: {
          color: "#8E939E",
        },
      },
    },
    tooltip: {
      trigger: "axis",
      className: "control-alarm-total-tooltip",
      axisPointer: {
        type: 'line',
        lineStyle: {
          color:'#BCC4D0',
        },
      },
      formatter: function (params: any) {
        const dataIndex = params[0].dataIndex;
        const obj = data[dataIndex];
        
        const tooltipHtml = `
          <div class="date">${obj['date']}</div>
          <div class="gap"></div>
          <div class="flex-bt total-alarm">
            <span>告警总数</span><span>${obj['total']}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-5">严重</span><span>${obj['level5']}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-4">重要</span><span>${obj['level4']}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-3">次要</span><span>${obj['level3']}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-2">警告</span><span>${obj['level2']}</span>
          </div>
          <div class="flex-bt text">
            <span class="point alarm-level-1">信息</span><span>${obj['level1']}</span>
          </div>
              `;
        return tooltipHtml;
      },
    },
    series: [
      {
        name: "总告警数",
        data: data.map((item) => item.total),
        type: "line",
        color: "#1f69ff",
        smooth: true,
        symbol: (value, params) => {
          return "image://" + line_charts_icon
        },
        symbolSize: (value, params) => {
          return 10
        },
        markPoint: {
          data: [
            { coord: [dataIndex !== -1 ? dataIndex : 9999, dataIndex !== -1 ? data[dataIndex].total : 0] }
          ],
          animation: true,
          animationDuration:300,
          symbol: (value, params) => {
              return "image://" + line_charts_icon_select
          },
          symbolSize: (value, params) => {
              return 26
          },
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth:2,
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "rgba(31,105,255,0.12)", // 渐变起始颜色
              },
              {
                offset: 1,
                color: "rgba(31,105,255,0.06)", // 渐变结束颜色
              },
            ],
          },
        },
      },
    ],
  };
  return option;
}

export function dynaminicPieLevelOption(alert_interval) {
  const pieData = alertIntervalHandle(alert_interval)
  const option = {
    tooltip: {
      trigger: "item",
      formatter: function (params: any) {
        const data = params.data
        const tooltipHtml = `
          <div class="flex-bt text">
            <span class="point alarm-level-${data['type']}">${data['name']}：</span><span>${data['value']}（${data['percent']}）</span>
          </div>
              `;
        return tooltipHtml;
      }, // 自定义提示框的显示内容
      className: "control-alarm-pie-level-tooltip",
    },
    title: {
      top: 92,
      text: [
          '{value|' + (alert_interval.total>999999?999999:alert_interval.total) + '}',
          '{name|' + '告警总量' + '}',
      ].join('\n'),

      left: 'center',
      textStyle: {
          rich: {
              value: {
                color: '#262626',
                fontSize: 24,
                fontWeight: 700,
                lineHeight:31,
              },
              name: {
                  color: '#4B5B76',
                  fontSize: 12,
              },
          },
      },
  },
    legend: {
      show:pieData.length?true:false,
      bottom:-5,
      orient: "horizontal",
      type:'scroll',
      itemWidth: 12, // 设置图例项的宽度
      itemHeight: 12, // 设置图例项的高度
      icon: 'path://M6,6 C9.3137085,6 12,8.6862915 12,12 C12,15.3137085 9.3137085,18 6,18 C2.6862915,18 0,15.3137085 0,12 C0,8.6862915 2.6862915,6 6,6 Z M6,9 C4.34314575,9 3,10.3431458 3,12 C3,13.6568542 4.34314575,15 6,15 C7.65685425,15 9,13.6568542 9,12 C9,10.3431458 7.65685425,9 6,9 Z" id="蓝色圆圈-信息"',
      formatter: function (name) {
        for (let i = 0; i < pieData.length; i++) {
          if (name == pieData[i].name) {
            return `{name|${name} :}  {num|${pieData[i].value}}`;
          }
        }
      },
      textStyle: {
        rich: {
          name: {
            fontSize: 14,
          },
          num: {
            fontSize: 14,
          },
        },
      },
      selectedMode:false,
    },
    series: [
      {
        name: "告警数量",
        type: "pie",
        left: 0, 
        top:-20,
        radius: ["55", "94"],
        avoidLabelOverlap: true,
        emphasis: {
          scaleSize: 12,
        },
        label: {
          show: true,
          position: "inside",
          formatter: function (d) {
            return `{b|${d.name}}`;
            },
           rich: {
            a: {
               fontSize: 10,
                color: '#fff',
                lineHeight: 14,
            },
            b: {
              fontSize: 10,
              lineHeight: 14,
              color: '#fff',
            },
          },
        },
        // 占位圆样式
        emptyCircleStyle: {
          color:'#E4E7F2',
        },
        labelLayout(params) {
          return {
              verticalAlign: 'middle',
              align: 'center'
          }
      },
        data: pieData,
      },
      
    ],
  };
  return option;
}


export function dynaminicPieTaskOption(alert_task) {
  const pieData  = alertTaskHandle(alert_task)
  const option = {
    tooltip: {
      trigger: "item",
      className: "control-alarm-pie-task-tooltip",
      formatter: function (params) {
        let tip = '';
        if (params.seriesIndex === 0) {
          tip = `<span class="point alarm-level-${params.data.type}">${params.name}</span>`
        } else if (params.seriesIndex === 1) {
          tip = `<div class="flex-bt text">
            <span class="point point-state-${params.data.state}">${params.name}：</span><span>${params.data.percent}</span>
          </div>`
       }
       return tip ;
    }
    },
    series: [
      {
        name: "告警总量",
        type: "pie",
        top:-20,
        radius: [84, 94],
        avoidLabelOverlap: true,
        itemStyle: {
          borderWidth: 3,
          borderColor: '#f5f7fc',
        },
        emphasis: {
          scaleSize:12,
        },

        label: {
          normal: {
            show: false,
          },
        },
        labelLine: {
          normal: {
            show: false,
          },
        },
      // 占位圆样式
      emptyCircleStyle: {
        color:'#E4E7F2',
      },
        data: pieData['outerData'],
      },
      {
        name: "任务",
        type: "pie",
        top:-20,
        radius: [35, 84],
        avoidLabelOverlap: true,
          label: {
              position:'inside',
              show: true,
              formatter: '{a|{b}}',
              rich: {
                a: {
                  color: '#fff',
                  height: 16,
                  fontSize: 10,
                  align: 'center',
                  verticalAlign: 'bottom',
                  width:20,
                },
              },
        },
          labelLine: {
            show: false,
            showAbove:true,
          },
        itemStyle: {
          borderWidth: 3,
          borderColor: '#f5f7fc',
        },
        emphasis: {
          scaleSize:12,
        },
           // 占位圆样式
           emptyCircleStyle: {
            color:'#E4E7F2',
          },
        data: pieData['innerData'],
      },
    ],
  };
  return option;
}

// 处理时间和单位
export function handleTime(time) {
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

const alertDayListHandle = (alert_day_list, time) => {
  const startUnix = time[0].getTime()
  const data = alert_day_list.map((item, index) => {
    const obj = {}
    item['statisticsItems'].forEach(element => {
      obj[`level${element.type}`] = element.num || 0;
    });
    return {
      date: dayjs(startUnix + index * 86400000).format('YYYY-MM-DD'),
      total: item['total'],
      ...obj
    }
  })
  return data
}

const alertIntervalHandle = (alert_interval, key = undefined) => {
  const data = key ? alert_interval.reverse() : alert_interval['statisticsItems'].reverse()
  if (!key && alert_interval.total === 0) return []
    const color = [ "#1F69FF","#f7bb21", "#fa830c", "#f4319d", "#fa3946",]
    return data.map(item => {
      return {
        value: item.num,
        name: alarmLevel[item.type],
        percent: `${(item.proportion / 100).toFixed(2)}%` || "0%",
        itemStyle: {
          color: color[item.type - 1],
        },
        type:item.type,
      }
    })
}
const alertTaskHandle = (alert_task) => {
  let innerData = []
  let outerData = []
  const innerColor = ["#FFA54C", "#2353B5", "#97A8CB"]
  if (alert_task.total > 0) {
    innerData = alert_task.items.map(item => {
      outerData.push(...alertIntervalHandle(item.items,"task"))
      return {
        value: item.num,
        percent: `${(item.proportion / 100).toFixed(2)}%` || "0%",
        name: alarmState[item.state],
        itemStyle: {
          color: innerColor[item.state],
        },
        state:item.state,
       }
     })
  }
  
  return {
    innerData,
    outerData
  }
}

const alarmLevel = ['恢复', '信息', '警告','次要','重要','严重']
const alarmState = ['待处理', '处理中', '已关闭']