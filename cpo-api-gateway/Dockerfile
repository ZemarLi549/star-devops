FROM krakend:2.13 AS builder

WORKDIR /etc/krakend
COPY krakend.json /etc/krakend/krakend.json

RUN krakend check -d -c /etc/krakend/krakend.json

FROM krakend:2.13

RUN apk upgrade --no-cache --no-interactive
COPY --from=builder /etc/krakend/krakend.json /etc/krakend/krakend.json

EXPOSE 9000

CMD ["run", "-dc", "/etc/krakend/krakend.json"]
