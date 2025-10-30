-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: sky_platform
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论唯一标识，自增主键',
  `video_id` int NOT NULL COMMENT '关联的视频ID，对应视频表的主键',
  `user_id` int NOT NULL COMMENT '发表评论的用户ID，对应用户表的主键',
  `content` text NOT NULL COMMENT '评论的具体内容',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '该评论的点赞数量，默认0',
  `parent_id` int DEFAULT NULL COMMENT '父评论ID，用于实现评论回复功能',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '该评论的子评论数量，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论的创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1630429186 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (-1859194878,3,1,'11',0,NULL,0,'2025-10-30 19:38:35'),(-1490096127,1,1,'11',0,NULL,0,'2025-10-30 19:24:38'),(-1305530366,1,1,'这是一条父评论',0,NULL,0,'2025-08-15 16:08:40'),(-1221607423,1,1,'yep！！',0,NULL,0,'2025-08-15 15:36:32'),(-202403838,1,1,'子评论',0,-1305530366,0,'2025-08-15 16:40:02'),(-123123123,1,1,'子评论',0,-202403838,0,'2025-08-15 16:40:02'),(191844354,1,1,'你好',0,NULL,0,'2025-08-15 16:09:32'),(1060093953,1,1,'晚上好',0,NULL,0,'2025-08-15 16:12:02'),(1314124124,1,1,'字评论',0,-1305530366,0,'2025-10-30 17:59:56'),(1630429185,1,1,'这是一条评论',0,NULL,0,'2025-08-15 16:15:13');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `follow_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_user_id_follow_id` (`user_id`,`follow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `from` varchar(255) NOT NULL COMMENT '发送者',
  `to` varchar(255) NOT NULL COMMENT '接收者',
  `content` text NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL DEFAULT (now()) COMMENT '创建时间',
  `is_read` int NOT NULL DEFAULT '0' COMMENT '是否已读(0:未读,1:已读)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_configs`
--

DROP TABLE IF EXISTS `system_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_configs` (
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `description` varchar(500) DEFAULT NULL COMMENT '配置描述',
  `config_type` varchar(50) DEFAULT 'string' COMMENT '配置类型：string,int,float,json',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_configs`
--

LOCK TABLES `system_configs` WRITE;
/*!40000 ALTER TABLE `system_configs` DISABLE KEYS */;
INSERT INTO `system_configs` VALUES ('score_update_interval','3600','得分更新间隔(秒)','int','2025-08-14 11:24:38','2025-08-14 11:24:38'),('sliding_window_days','30','滑动窗口天数','int','2025-08-14 11:24:38','2025-08-14 11:24:38'),('time_decay_factor','0.95','时间衰减因子','float','2025-08-14 11:24:38','2025-08-14 11:24:38');
/*!40000 ALTER TABLE `system_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID，主键，自增长',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称，唯一且非空',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES (1,'二次元'),(9,'健身'),(15,'动漫'),(28,'历史文化'),(29,'哲学'),(24,'园艺'),(13,'宠物'),(10,'家居生活'),(30,'心理学'),(22,'手工'),(4,'摄影'),(12,'教育'),(21,'数码产品'),(3,'旅行'),(8,'时尚'),(26,'汽车'),(14,'游戏'),(6,'电影'),(16,'电视剧'),(11,'科技'),(20,'穿搭'),(17,'纪录片'),(23,'绘画'),(19,'美妆'),(2,'美食'),(25,'育儿'),(7,'读书'),(18,'运动'),(27,'金融'),(5,'音乐');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `nick_name` varchar(50) NOT NULL DEFAULT '默认用户',
  `password` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT '还没有设置个性签名！！',
  `sex` tinyint(1) NOT NULL DEFAULT '1',
  `avatar` varchar(255) NOT NULL DEFAULT '',
  `ip` varchar(255) NOT NULL DEFAULT '未知' COMMENT '用户IP地址',
  `fans_count` int NOT NULL DEFAULT '0' COMMENT '粉丝数',
  `follow_count` int NOT NULL DEFAULT '0' COMMENT '关注数',
  `age` int NOT NULL DEFAULT '0' COMMENT '年龄',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '获赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2915719323@qq.com','常温水','123456','还没有设置个性签名！！',1,'http://47.96.156.182:9000/origin/sky/ac9b11a900e9fe77bdffd70437466663.jpg','未知',0,0,18,0,'2025-08-14 15:39:18'),(2,'29157193231@qq.com','默认用户','123456','还没有设置个性签名！！',1,'http://47.96.156.182:9000/origin/sky/a0ec6b89b08d9f049d459b918e0d9ecf.jpg','未知',0,0,0,0,'2025-08-14 17:05:11');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_action`
--

DROP TABLE IF EXISTS `user_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_action` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '行为记录唯一ID，自增主键',
  `user_id` int NOT NULL COMMENT '用户ID，关联用户表，不能为空',
  `video_id` int DEFAULT NULL COMMENT '视频ID，关联视频表，与comment_id二选一存在',
  `comment_id` int DEFAULT NULL COMMENT '评论ID，关联评论表，与video_id二选一存在',
  `type` int NOT NULL COMMENT '行为类型：0-点赞，1-收藏，2-观看',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间，默认当前时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2016325634 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_action`
--

LOCK TABLES `user_action` WRITE;
/*!40000 ALTER TABLE `user_action` DISABLE KEYS */;
INSERT INTO `user_action` VALUES (-1829892095,1,-1771171839,NULL,1,'2025-08-15 11:24:19'),(-1242689534,1,1638797313,NULL,1,'2025-08-15 11:30:30'),(-441577471,1,1638797313,NULL,2,'2025-08-15 11:30:14'),(-114421758,1,-1771171839,NULL,2,'2025-08-15 11:24:37'),(409866242,1,-1322381310,NULL,2,'2025-08-15 11:27:27'),(485363713,1,1529790466,NULL,2,'2025-08-15 11:23:21'),(573444097,1,1529790466,NULL,1,'2025-08-15 11:23:53');
/*!40000 ALTER TABLE `user_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_tag_score`
--

DROP TABLE IF EXISTS `user_tag_score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_tag_score` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `tag_id` varchar(255) NOT NULL COMMENT '标签ID',
  `raw_score` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '原始得分',
  `normalized_score` decimal(5,4) NOT NULL DEFAULT '0.0000' COMMENT '归一化得分(0-1)',
  `window_start_time` timestamp NOT NULL COMMENT '窗口开始时间',
  `window_end_time` timestamp NOT NULL COMMENT '窗口结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_score` (`user_id`,`normalized_score` DESC),
  KEY `idx_updated_at` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户标签得分预计算表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_tag_score`
--

LOCK TABLES `user_tag_score` WRITE;
/*!40000 ALTER TABLE `user_tag_score` DISABLE KEYS */;
INSERT INTO `user_tag_score` VALUES (145,'1','1',0.4483,0.4483,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(146,'1','2',0.7241,0.7241,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(147,'1','3',1.0000,1.0000,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(148,'1','4',0.8276,0.8276,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(149,'1','5',0.2759,0.2759,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(150,'1','6',0.2759,0.2759,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(151,'1','7',0.5517,0.5517,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(152,'1','8',0.2759,0.2759,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(153,'1','9',0.2759,0.2759,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(154,'1','11',0.2759,0.2759,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(155,'1','12',0.2759,0.2759,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(156,'1','13',0.4483,0.4483,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(157,'1','15',0.1724,0.1724,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(158,'1','16',0.2759,0.2759,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(159,'1','20',0.1724,0.1724,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30'),(160,'1','21',0.1724,0.1724,'2025-07-16 11:30:31','2025-08-15 11:30:31','2025-08-15 11:30:30','2025-08-15 11:30:30');
/*!40000 ALTER TABLE `user_tag_score` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video`
--

DROP TABLE IF EXISTS `video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `user_id` bigint NOT NULL COMMENT '发布者ID，关联user表',
  `title` varchar(255) NOT NULL COMMENT '视频标题',
  `description` text COMMENT '视频描述',
  `type` int NOT NULL DEFAULT '0' COMMENT '分类ID',
  `url` varchar(512) NOT NULL COMMENT '视频源地址',
  `open` tinyint(1) NOT NULL DEFAULT '0' COMMENT '公开/私密，0：公开，1：私密，默认为0',
  `review_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0-待审核，1-审核通过，2-审核驳回',
  `review_content` varchar(512) DEFAULT NULL COMMENT '审核内容/驳回原因',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `collect_count` int NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `comment_count` int NOT NULL DEFAULT '0',
  `watch_count` int NOT NULL DEFAULT '0',
  `share_count` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2116976646 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='视频表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video`
--

LOCK TABLES `video` WRITE;
/*!40000 ALTER TABLE `video` DISABLE KEYS */;
INSERT INTO `video` VALUES (1,1,'测试数据','5555',0,'http://47.96.156.182:9000/origin/sky/63afda6939ba2466be19158c4c77f98c.mp4',0,1,'2',11,22,1,0,0,'2025-10-29 20:15:40'),(2,2,'滴滴答答的','32131',0,'http://47.96.156.182:9000/origin/sky/86f07c7f3a4508e9d1700ef3818d1051.mp4',0,1,'2',2,1,0,0,0,'2025-10-29 20:15:40'),(3,1,'下雨天','222',0,'http://47.96.156.182:9000/origin/sky/ad8e29e283a69720973ef5ced5be7f60.mp4',0,1,'2',3,1,1,0,0,'2025-10-29 20:16:09'),(4,2,'打晴天','11',0,'http://47.96.156.182:9000/origin/sky/921896b96b8c2173c7c92d0a657fcd81.mp4',0,1,'2',3,1,0,0,0,'2025-10-29 20:16:09'),(5,1,'去爬山','3232',0,'http://47.96.156.182:9000/origin/sky/70b02b439ed0b4167cf2df8925d0849b.mp4',0,0,'2',0,0,0,0,0,'2025-10-30 19:30:25');
/*!40000 ALTER TABLE `video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_tag_relation`
--

DROP TABLE IF EXISTS `video_tag_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_tag_relation` (
  `video_id` int NOT NULL COMMENT '视频ID，关联videos表的主键',
  `tag_id` int NOT NULL COMMENT '标签ID，关联tags表的主键',
  PRIMARY KEY (`video_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频与标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_tag_relation`
--

LOCK TABLES `video_tag_relation` WRITE;
/*!40000 ALTER TABLE `video_tag_relation` DISABLE KEYS */;
INSERT INTO `video_tag_relation` VALUES (1,1),(1,2),(1,3),(2,3),(1,4),(2,4),(3,4),(4,4),(5,4),(3,5),(5,5),(1,6),(6,6),(1,7),(4,7),(1,8),(2,8),(1,9),(4,11),(1,12),(2,13),(3,13),(5,15),(4,16),(1,20),(5,21);
/*!40000 ALTER TABLE `video_tag_relation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-30 19:46:28
