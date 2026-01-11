# 单机

> 注意修改`docker-compose.yml`中`KAFKA_ADVERTISED_LISTENERS`指定的实际访问`IP`

```shell
cd script/docker/kafka/single-node
chmod -R 777 ./data
# 运行
docker compose -p kafka up -d
# 停止
docker compose -p kafka down
```

访问: http://127.0.0.1:19092/kafkaui 输入用户名: admin 密码: admin@123即可进入Kafka UI管控台