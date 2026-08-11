// 服务出现的错误链路数
export const Errors = {
  variable: "$serviceIds: [String!]!,$duration: Duration!",
  query: `
    services: getErrorPm(serviceIds: $serviceIds, duration: $duration){
      id
      value
    }
    `,
};

export const ErrorsPm = {
  variable: "$serviceIds: [String!]!,$duration: Duration!",
  query: `
    services: getErrorPm(serviceIds: $serviceIds, duration: $duration){
      label
      values {
        values {value}
      }
    }
    `,
};

