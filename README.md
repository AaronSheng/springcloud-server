#### 整体架构
- SpringBoot + Jersey提供Restful规范API；
- SpringBoot Admin提供服务监控和通知功能；
- Consul作为注册中心提供服务注册和发现功能；
- SpringCloud Openfeign提供RPC调用；
- SpringCloud Config作为注册中心拉取github配置提供配置服务；
- SpringCloud Sleuth提供全链路日志跟跟踪；
- SpringCloud Zipkin + Elasticsearch提供全链路检索功能。

#### Admin监控中心
SpringBoot Admin提供服务注册发现后的监控功能，包括服务上下线通知、metrics数据，jvm数据等。

#### Config配置中心
配置文件地址：`https://github.com/AaronSheng/springcloud-server.git`
配置文件目录：`config/`

#### Sleuth+Zipkin链路跟踪
Sleuth支持Slf4j, Openfeign等，提供全链路跟踪功能，结合Zipkin提供日志落地和检索功能。

官方Zipkin提供的jar启动服务，Elasticsearch作为日志存储提供检索功能，RabbitMQ提供日志收集功能。  
`STORAGE_TYPE=elasticsearch ES_HOST_127.0.0.1:9200 RABBIT_ADDRESSES=localhost:5672 RABBIT_USER=guest RABBIT_PASSWORD=guest java -jar zipkin.jar`

