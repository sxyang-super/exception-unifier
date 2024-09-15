# 异常管理服务器

这是一个简单的独立服务器，用于满足异常统一器在远程模式下的基本需求。

实际上，你可以参考此项目，使用自己熟悉的技术栈实现自己的服务器。

你需要做的就是实现以下所有必需的端点。

## 如何启动

### 开发环境

`npm run dev`

### 生产环境

`npm run start`

## 端点列表

* （可选）返回服务器健康状态

````http request
GET http://localhost:8080/healthcheck
````

此端点应始终返回 "OK" 并带有 200 状态码，表明服务器运行正常。

* （可选）为给定模块ID配置异常代码前缀

````http request
POST http://localhost:8080/prefix
Content-Type: application/json

{
"moduleId": "com.sample.module-id",
"exceptionCodePrefix": "SAMPLE-PREFIX"
}
````

* （可选）删除给定模块ID的配置

````http request
DELETE http://localhost:8080/prefix?moduleId=com.sample.module-id
````

* （可选）更新给定模块ID的配置

````http request
PUT http://localhost:8080/prefix
Content-Type: application/json

{
"moduleId": "com.sample.module-id",
"exceptionCodePrefix": "ANOTHER-SAMPLE-PREFIX"
}
````

* （必选）获取给定模块ID的异常代码前缀

````http request
GET http://localhost:8080/prefix/com.sample.module-id
````

用于获取给定模块ID的已配置异常代码前缀。

如果是未知的模块ID，应该返回 404 状态码。

* （可选）获取所有已配置的异常代码前缀

````http request
GET http://localhost:8080/prefix
````

