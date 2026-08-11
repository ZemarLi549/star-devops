import line_charts_icon from '@/assets/imgs/line_charts_icon.png'
import line_charts_icon_select from "@/assets/imgs/line_charts_icon_select.png"

// 根据动态数据, 获取echart options
export function dynaminicHisLineOption(yData, xData, hoverIndex) {
  const data = (yData || []).map((item, index) => {
    let obj = {}
    if (item.statisticsItems) {
      item.statisticsItems.forEach(element => {
        obj[`level${element.type}`] = element.num || 0
      });
    }
    return {
      date: xData[index],
      total: item.total,
      ...obj
    }
  })
  const option = {
    grid: {
      top: 30,
      left: 40,
      right: 30,
      bottom: 40,
    },
    legend: {
      type: "plain",
      bottom: "5",
        textStyle: {
          color:'#666',
      },
      show: false,
      selectedMode: false,
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
        formatter:function(value, index) {
          if(index===0){ 
            return ``
          }else if(index === data.length - 1) {
            return ``;
          }
            
          const dateArr = value.split('-')
    
          dateArr.shift()
          return dateArr.join('-');
        },
        textStyle: {
            color: '#8E939E'
        }
      },
      data: xData
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
        itemStyle: {
          borderColor: '#fff',
          borderWidth:2,
        },
        symbol: (value, params) => {
          if (hoverIndex === params.dataIndex) {
            return "image://" + line_charts_icon_select
          } else {
            return "image://" + line_charts_icon
          }
        },
        symbolSize: (value, params) => {
          if (hoverIndex === params.dataIndex) {
            return 26
          } else {
            return 10
          }
        },
        showAllSymbol: false,
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

const alarmLevel = {
  0: {name: '恢复', color: '#97A8CB'},
  1: {name: '信息', color: '#1F69FF'},
  2: {name: '警告', color: '#f7bb21'},
  3: {name: '次要', color: '#fa830c'},
  4: {name: '重要', color: '#f4319d'},
  5: {name: '严重', color: '#fa3946'},
}
const alarmState = {
  0: {name: '待处理', color: '#ffa54c'},
  1: {name: '处理中', color: '#2353b5'},
  2: {name: '已关闭', color: '#97a8cb'}
}
export function dynaminicPieLevelOption(data) {
  const pieData = data.statisticsItems.map((item) => {
    return {
      value: item.num,
      name: alarmLevel[item.type].name,
      percent: Number(item.proportion / 100).toFixed(2) + '%',
      type: item.type
    }
  })

  const option = {
    tooltip: {
      trigger: "item",
      formatter: function (params: any) {
        const data = params.data
        
        const tooltipHtml = `
          <div class="flex-bt text">
            <span class="point alarm-level-${data.type}">${data['name']}：</span><span>${data['value']}（${data['percent']}）</span>
          </div>
              `;
        return tooltipHtml;
      }, // 自定义提示框的显示内容
      className: "control-alarm-pie-level-tooltip",
    },
    title: {
      top: 'center',
      text: [
          '{value|' + (data.total>999999?999999:data.total) + '}',
          '{name|' + '告警总量' + '}',
      ].join('\n'),

      left: '30%',
      textAlign: 'center',
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
      show: true,
      selectedMode: false,
      orient: 'vertical',
      left: '55%',
      height: '70%',
      top: 'center',
      itemGap: 25,
      type:'scroll',
      itemWidth: 12, // 设置图例项的宽度
      itemHeight: 12, // 设置图例项的高度
      icon: 'path://M6,6 C9.3137085,6 12,8.6862915 12,12 C12,15.3137085 9.3137085,18 6,18 C2.6862915,18 0,15.3137085 0,12 C0,8.6862915 2.6862915,6 6,6 Z M6,9 C4.34314575,9 3,10.3431458 3,12 C3,13.6568542 4.34314575,15 6,15 C7.65685425,15 9,13.6568542 9,12 C9,10.3431458 7.65685425,9 6,9 Z" id="蓝色圆圈-信息"',
      formatter: function (name) {
        for (let i = 0; i < pieData.length; i++) {
          if (name == pieData[i].name) {
            return `{name|${name}：${pieData[i].value}}{string|${pieData[i].percent}}`;
          }
        }
      },
      textStyle: {
        rich: {
          name: {
            fontSize: 14,
            padding:[3,0,0,0],
            width: 100
          },
          num: {
            fontSize: 14,
            padding:[3,0,0,0],
            width: 50
          },
          string: {
            fontSize: 14,
            padding:[3,0,0,0],
          },
        },
      },
    },
    series: [
      {
        name: "告警数量",
        type: "pie",
        radius: [55, 90],
        center: ['30%', '50%'],
        avoidLabelOverlap: true,
        emphasis: {
          scaleSize:12,
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
        labelLayout(params) {
          return {
              verticalAlign: 'middle',
              align: 'center'
          }
        },
        data: pieData,
        color: pieData.map(item => alarmLevel[item.type].color),
      },
    ],
  };
  return option;
}
export function dynaminicPieTaskOption(data) {

  const pieData = alertTaskHandle(data)

  const option = {
    tooltip: {
      trigger: "item",
    },
    legend: {
      show: true,
      selectedMode: false,
      orient: 'vertical',
      left: '55%',
      height: '60%',
      top: 'center',
      itemGap: 25,
      itemWidth: 12, // 设置图例项的宽度
      itemHeight: 12, // 设置图例项的高度
      icon: 'circle',
      data: pieData['innerData'].map(item => item.name),
      formatter: function (name) {
        for (let i = 0; i < pieData.innerData.length; i++) {
          if (name == pieData.innerData[i].name) {
            return `{name|${name}}{string|${pieData.innerData[i].percent}}`;
          }
        }
      },
      textStyle: {
        rich: {
          name: {
            fontSize: 14,
            padding:[3,0,0,0],
            width: 100
          },
          string: {
            fontSize: 14,
            padding:[3,0,0,0],
          },
        },
      },
    },
    series: [
      {
        name: "告警数量",
        type: "pie",
        center: ['30%', '50%'],
        radius: [80, 90],
        avoidLabelOverlap: true,
        itemStyle: {
          borderWidth: 4,
          borderColor: '#ffffff',
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
        tooltip: {
          show: true,
          formatter: function (params) {
            return `${params.marker}${params.name}`;
          },
          borderColor: '#ffffff'
        },
        data: pieData['outerData'],
      },
      {
        name: "任务类型",
        type: "pie",
        center: ['30%', '50%'],
        radius: [35, 78],
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
        // labelLine: {
        //     show: false,
        //     showAbove:true,
        //   },
        tooltip: {
          show: true,
          formatter: function (params) {
            return `${params.marker}${params.name}: ${params.data.percent}` ;
          },
          borderColor: '#ffffff'
        },
        itemStyle: {
          borderWidth: 4,
          borderColor: '#ffffff',
        },
        emphasis: {
          scaleSize:12,
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

// 根据时间戳范围输出每一天，作为横坐标
export function getDaysInRange(timeRange, hasYear = false) {
  const result = [];

  if(timeRange) {
    const startTime = timeRange[0].getTime()
    const endTime = timeRange[1].getTime()
  
    const start = new Date(startTime);
    const end = new Date(endTime);

    start.setHours(0, 0, 0, 0);
    end.setHours(0, 0, 0, 0);

    let current = new Date(start);

    while (current.getTime() <= end.getTime()) {
      const year = current.getFullYear().toString();
      const month = (current.getMonth() + 1).toString().padStart(2, '0');
      const day = current.getDate().toString().padStart(2, '0');
      if (hasYear) {
        result.push(`${year}-${month}-${day}`);
      } else {
        result.push(`${month}-${day}`);
      }
      

      current.setDate(current.getDate() + 1);
    }
  }
  
  return result;
}

const alertIntervalHandle = (alert_interval, key = undefined) => {
  const data = key ? alert_interval : alert_interval['statisticsItems']
  if (!key && alert_interval.total === 0) return []
  
    return data.map(item => {
      return {
        value: item.num,
        name: alarmLevel[item.type].name,
        percent: `${(item.proportion / 100).toFixed(2)}%` || "0%",
        itemStyle: {
          normal: {
            color: alarmLevel[item.type].color,
          }
        }
      }
    })
}

const alertTaskHandle = (alert_task) => {
  let innerData = []
  let outerData = []
  if (alert_task) {
    innerData = alert_task.items.map((item, index) => {
      outerData.push(...alertIntervalHandle(item.items,"task"))
      return {
        value: item.num,
        percent: `${(item.proportion / 100).toFixed(2)}%` || "0%",
        name: alarmState[item.state].name,
        itemStyle: {
          normal: {
            color: alarmState[item.state].color,
          }
        }
       }
     })
  }
  return {
    innerData,
    outerData
  }
}