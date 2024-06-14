# mongo-stable-api-springboot-health

This repo is a minimal reproduction of a bug with `MongoHealthIndicator` relying on `isMaster` Mongo command, which is not part of Mongo's stable API.

## Steps to reproduction

To reproduce the error, we just need to configure the mongo connection to use stable API V1 with `strict` set to `true` and call the health check.

- Clone/checkout `main` branch
- Start a docker mongodb, the springboot application and call the `/actuator/health` endpoint
```shell
docker compose up -d mongo
mvn spring-boot:run
curl localhost:8080/actuator/health
```
- Results:
  - The curl command reports `DOWN` status
```json
{"status":"DOWN"}
```
  - The application logs show an exception 

```shell
org.springframework.data.mongodb.UncategorizedMongoDbException: Command failed with error 323 (APIStrictError): 'Provided apiStrict:true, but the command isMaster is not in API Version 1. Information on supported commands and migrations in API Version 1 can be found at https://dochub.mongodb.org/core/manual-versioned-api' on server 127.0.0.1:27017. The full response is {"ok": 0.0, "errmsg": "Provided apiStrict:true, but the command isMaster is not in API Version 1. Information on supported commands and migrations in API Version 1 can be found at https://dochub.mongodb.org/core/manual-versioned-api", "code": 323, "codeName": "APIStrictError", "$clusterTime": {"clusterTime": {"$timestamp": {"t": 1718360852, "i": 1}}, "signature": {"hash": {"$binary": {"base64": "/HB9HlzsMyy/+JyUth7jk1XgthQ=", "subType": "00"}}, "keyId": 7380301170985664517}}, "operationTime": {"$timestamp": {"t": 1718360852, "i": 1}}}
        at org.springframework.data.mongodb.core.MongoExceptionTranslator.translateExceptionIfPossible(MongoExceptionTranslator.java:135) ~[spring-data-mongodb-4.3.0.jar:4.3.0]
        at org.springframework.data.mongodb.core.MongoTemplate.potentiallyConvertRuntimeException(MongoTemplate.java:3010) ~[spring-data-mongodb-4.3.0.jar:4.3.0]
        at org.springframework.data.mongodb.core.MongoTemplate.execute(MongoTemplate.java:584) ~[spring-data-mongodb-4.3.0.jar:4.3.0]
        at org.springframework.data.mongodb.core.MongoTemplate.executeCommand(MongoTemplate.java:516) ~[spring-data-mongodb-4.3.0.jar:4.3.0]
        at org.springframework.boot.actuate.data.mongo.MongoHealthIndicator.doHealthCheck(MongoHealthIndicator.java:46) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.AbstractHealthIndicator.health(AbstractHealthIndicator.java:82) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthIndicator.getHealth(HealthIndicator.java:37) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointWebExtension.getHealth(HealthEndpointWebExtension.java:94) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointWebExtension.getHealth(HealthEndpointWebExtension.java:47) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointSupport.getLoggedHealth(HealthEndpointSupport.java:172) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointSupport.getContribution(HealthEndpointSupport.java:145) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointSupport.getAggregateContribution(HealthEndpointSupport.java:156) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointSupport.getContribution(HealthEndpointSupport.java:141) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointSupport.getHealth(HealthEndpointSupport.java:110) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointSupport.getHealth(HealthEndpointSupport.java:81) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointWebExtension.health(HealthEndpointWebExtension.java:80) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.health.HealthEndpointWebExtension.health(HealthEndpointWebExtension.java:69) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
        at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
        at org.springframework.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:281) ~[spring-core-6.1.8.jar:6.1.8]
        at org.springframework.boot.actuate.endpoint.invoke.reflect.ReflectiveOperationInvoker.invoke(ReflectiveOperationInvoker.java:74) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.endpoint.annotation.AbstractDiscoveredOperation.invoke(AbstractDiscoveredOperation.java:60) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.endpoint.web.servlet.AbstractWebMvcEndpointHandlerMapping$ServletWebOperationAdapter.handle(AbstractWebMvcEndpointHandlerMapping.java:327) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at org.springframework.boot.actuate.endpoint.web.servlet.AbstractWebMvcEndpointHandlerMapping$OperationHandler.handle(AbstractWebMvcEndpointHandlerMapping.java:434) ~[spring-boot-actuator-3.3.0.jar:3.3.0]
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
        at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
        at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:255) ~[spring-web-6.1.8.jar:6.1.8]
        at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:188) ~[spring-web-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:926) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:831) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.24.jar:6.0]
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.1.8.jar:6.1.8]
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.24.jar:6.0]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:195) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.1.8.jar:6.1.8]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.8.jar:6.1.8]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.1.8.jar:6.1.8]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.8.jar:6.1.8]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:109) ~[spring-web-6.1.8.jar:6.1.8]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.8.jar:6.1.8]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.1.8.jar:6.1.8]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.1.8.jar:6.1.8]
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:164) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:140) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:167) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:482) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:344) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:389) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:896) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1741) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1190) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
        at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: com.mongodb.MongoCommandException: Command failed with error 323 (APIStrictError): 'Provided apiStrict:true, but the command isMaster is not in API Version 1. Information on supported commands and migrations in API Version 1 can be found at https://dochub.mongodb.org/core/manual-versioned-api' on server 127.0.0.1:27017. The full response is {"ok": 0.0, "errmsg": "Provided apiStrict:true, but the command isMaster is not in API Version 1. Information on supported commands and migrations in API Version 1 can be found at https://dochub.mongodb.org/core/manual-versioned-api", "code": 323, "codeName": "APIStrictError", "$clusterTime": {"clusterTime": {"$timestamp": {"t": 1718360852, "i": 1}}, "signature": {"hash": {"$binary": {"base64": "/HB9HlzsMyy/+JyUth7jk1XgthQ=", "subType": "00"}}, "keyId": 7380301170985664517}}, "operationTime": {"$timestamp": {"t": 1718360852, "i": 1}}}
        at com.mongodb.internal.connection.ProtocolHelper.getCommandFailureException(ProtocolHelper.java:205) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.InternalStreamConnection.receiveCommandMessageResponse(InternalStreamConnection.java:431) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.InternalStreamConnection.sendAndReceive(InternalStreamConnection.java:354) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.UsageTrackingInternalConnection.sendAndReceive(UsageTrackingInternalConnection.java:114) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.DefaultConnectionPool$PooledConnection.sendAndReceive(DefaultConnectionPool.java:743) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.CommandProtocolImpl.execute(CommandProtocolImpl.java:76) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.DefaultServer$DefaultServerProtocolExecutor.execute(DefaultServer.java:209) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.DefaultServerConnection.executeProtocol(DefaultServerConnection.java:115) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.DefaultServerConnection.command(DefaultServerConnection.java:83) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.DefaultServerConnection.command(DefaultServerConnection.java:74) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.connection.DefaultServer$OperationCountTrackingConnection.command(DefaultServer.java:299) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.createReadCommandAndExecute(SyncOperationHelper.java:270) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.lambda$executeRetryableRead$3(SyncOperationHelper.java:188) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.lambda$withSourceAndConnection$0(SyncOperationHelper.java:124) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.withSuppliedResource(SyncOperationHelper.java:149) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.lambda$withSourceAndConnection$1(SyncOperationHelper.java:123) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.withSuppliedResource(SyncOperationHelper.java:149) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.withSourceAndConnection(SyncOperationHelper.java:122) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.lambda$executeRetryableRead$4(SyncOperationHelper.java:186) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.lambda$decorateReadWithRetries$12(SyncOperationHelper.java:289) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.async.function.RetryingSyncSupplier.get(RetryingSyncSupplier.java:67) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.executeRetryableRead(SyncOperationHelper.java:191) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.SyncOperationHelper.executeRetryableRead(SyncOperationHelper.java:173) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.internal.operation.CommandReadOperation.execute(CommandReadOperation.java:48) ~[mongodb-driver-core-5.0.1.jar:na]
        at com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor.execute(MongoClientDelegate.java:153) ~[mongodb-driver-sync-5.0.1.jar:na]
        at com.mongodb.client.internal.MongoDatabaseImpl.executeCommand(MongoDatabaseImpl.java:196) ~[mongodb-driver-sync-5.0.1.jar:na]
        at com.mongodb.client.internal.MongoDatabaseImpl.runCommand(MongoDatabaseImpl.java:165) ~[mongodb-driver-sync-5.0.1.jar:na]
        at com.mongodb.client.internal.MongoDatabaseImpl.runCommand(MongoDatabaseImpl.java:160) ~[mongodb-driver-sync-5.0.1.jar:na]
        at org.springframework.data.mongodb.core.MongoTemplate.lambda$executeCommand$3(MongoTemplate.java:516) ~[spring-data-mongodb-4.3.0.jar:4.3.0]
        at org.springframework.data.mongodb.core.MongoTemplate.execute(MongoTemplate.java:582) ~[spring-data-mongodb-4.3.0.jar:4.3.0]
        ... 73 common frames omitted
```

## Regular behaviour

To reproduce the error, we just need to configure the mongo connection to use stable API V1 with `strict` set to `true` and call the health check.

- Clone/checkout `not_strict` branch
- Start a docker mongodb, the springboot application and call the `/actuator/health` endpoint
```shell
docker compose up -d mongo
mvn spring-boot:run
curl localhost:8080/actuator/health
```
- Results:
  - The curl command reports `UP` status
```json
{"status":"UP"}
```
  - No exception shows in the application's logs 