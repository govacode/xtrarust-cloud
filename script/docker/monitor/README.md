```shell
cd script/docker/monitor
chmod -R 755 ./grafana/dashboards
# 运行
docker compose -p monitor up -d
# 停止
docker compose -p monitor down
```