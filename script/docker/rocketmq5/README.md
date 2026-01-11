> 注意修改`broker/conf/broker.conf`中`brokerIP1`为本机`IP`

```shell
cd script/docker/rocketmq5
chmod -R 777 ./broker
chmod -R 777 ./namesrv

# 运行
docker compose -p rocketmq up -d
# 停止
docker compose -p rocketmq down
```