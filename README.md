# SpringSecurity + ElasticSearch
Security 的无状态登录，一些配置，和Elasticsearh的使用

## 启动项目步骤

- 配置文件
  - 创建数据库，在项目根目录，有四个sql文件，创建表t_internet_info、t_user、t_user_role、t_role
  - 更改application.yml，配置数据源
  - 更改elasticsearch的username、host、port、password

- 三个登录用户

  - root > admin > user
  - 根据登录接口/doLogin，username=xxx,password=xxx
  - root es操作

  然后在访问接口。