# 秒杀项目

## 主要技术栈
springboot+mybatis-plus+redis+mysql+rabbitmq


## 技术亮点
- 使用mybatis-plus逆行身生成pojo、controller等代码，提升了开发效率
- 使用两次md5加密（一次在前端，一次在后端）保证用户密码的安全性，后端注册时随机生成盐（本项目没有注册功能，只是我的想法）
- 分布式session，用户登录后随机生成一个token，并保存在redis中
- 在系统启动的时候将商品数据加载进redis，实现数据预热
- 使用rabbitmq实现异步下单、流量削峰。秒杀时减少redis中的库存数量，使用mq发送消息修改数据库。
- 使用redis和rabbitmq提高并发量
- 加入验证码功能，可以防止脚本快速的下单，同时也在一定程度上分散了流量
