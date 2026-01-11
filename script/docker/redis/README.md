# 单机

```shell
cd script/docker/redis/single-node
chmod -R 777 ./data
# 运行
docker compose -p redis up -d
# 停止
docker compose -p redis down
```

# 集群

> 注意修改`redis.conf`中`cluster-announce-ip`为宿主机`IP`

```shell
cd script/docker/redis/cluster
chmod -R 777 {7001,7002,7003,7004,7005,7006}
# 运行
docker compose -p redis-cluster up -d
# 停止
docker compose -p redis-cluster down

# 集群创建 使用宿主机IP
docker exec -it redis-7001 /bin/bash
# 容器内部执行
redis-cli --cluster create --cluster-replicas 1 192.168.10.104:7001 192.168.10.104:7002 192.168.10.104:7003 192.168.10.104:7004 192.168.10.104:7005 192.168.10.104:7006 -a 123456
redis-cli -p 7001 -c -a 123456
```