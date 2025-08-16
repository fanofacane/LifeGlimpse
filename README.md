#LifeGlimpse
-社交短视频平台2.0
-我把之前写的代码重构了一遍,算是给我的这个项目画上一个完美句号
-代码更加规范
-复用性更高
-可读性更好
#新功能
-加入标签算法,使用滑动窗口和用户行为动态计算用户偏好,精确的推荐视频
-Feed流推送好友动态视频
-ChatClient存储记忆至数据库
-去除原有的springWebsocket,使用Netty框架
-mybatis替换为Mybatis-plus减少不必要的crud
#技术栈
-**SpringBoot** **SpringAI** **Netty** **Mysql** **Mybatis-plus** **Redis** **MinIo**