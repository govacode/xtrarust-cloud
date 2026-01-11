# 构建 RabbitMQ 镜像

> 此步骤可选, `docker-compose.yml`已集成build功能

```shell
cd script/docker/rabbitmq
# 构建镜像 启用延迟队列插件
docker build -t rabbitmq:4.2.0-management-delay .
# 查看镜像
docker images
IMAGE                             ID             DISK USAGE   CONTENT SIZE   EXTRA
rabbitmq:4.2.0-management         6431bbd0f22d        426MB          128MB        
rabbitmq:4.2.0-management-delay   b2123860d7a4        423MB          125MB
```
# 单机

```shell
cd script/docker/rabbitmq/single-node
chmod -R 777 ./data
chmod -R 777 ./logs
# 运行
docker compose -p rabbitmq up -d
# 进入容器
docker exec -it rabbitmq /bin/bash
# 查看已安装插件(可看到[E*] rabbitmq_delayed_message_exchange       4.2.0)
rabbitmq-plugins list
# 停止
docker compose -p rabbitmq down
```

访问: http://127.0.0.1:15672, 输入用户名: admin、密码: admin@123即可登陆至`RabbitMQ`管控台

# 集群

> RabbitMQ4 镜像队列已被废弃<br>
> 传统的通过policy设置镜像队列方式已过时<br>
> rabbitmqctl set_policy ha-all "^" '{"ha-mode":"exactly","ha-params":2,"ha-sync-mode":"automatic","queue-master-locator": "min-masters"}' --priority 10 --apply-to queues<br>
> 应使用仲裁队列

```java
Map<String, Object> args = new HashMap<>();
args.put("x-queue-type", "quorum");
channel.queueDeclare("my_quorum_queue", true, false, false, args);
```

```shell
cd script/docker/rabbitmq/cluster
chmod -R 777 {rabbitmq1,rabbitmq2,rabbitmq3}
# 运行
docker compose -p rabbitmq-cluster up -d

# 手动构建集群
docker exec -it rabbitmq2 bash
root@rabbitmq2:/# rabbitmqctl stop_app
root@rabbitmq2:/# rabbitmqctl join_cluster rabbitmq1@rabbitmq1
root@rabbitmq2:/# rabbitmqctl start_app
root@rabbitmq2:/# exit
docker exec -it rabbitmq3 bash
root@rabbitmq3:/# rabbitmqctl stop_app
root@rabbitmq3:/# rabbitmqctl join_cluster rabbitmq1@rabbitmq1
root@rabbitmq3:/# rabbitmqctl start_app
root@rabbitmq3:/# exit

# 查看集群状态
docker exec -it rabbitmq1 rabbitmqctl cluster_status

# 停止
docker compose -p rabbitmq-cluster down
```