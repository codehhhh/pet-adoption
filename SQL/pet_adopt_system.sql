/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : pet_adopt_system

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 11/03/2026 21:13:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for active_net
-- ----------------------------
DROP TABLE IF EXISTS `active_net`;
CREATE TABLE `active_net`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '行为信息表主键ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID，外键，关联的是用户表',
  `content_id` int NULL DEFAULT NULL COMMENT '内容ID',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '交互行为（1：浏览；2：点赞；3：收藏）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `content_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容模块，标识的是该互动类型属于什么模块',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 148 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '行为互动信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收货地址主键ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID，外键，关联的是用户表，标识这条收货地址信息是谁配置的',
  `detail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '详细地址',
  `addressee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收件人',
  `concat_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收件电话',
  `is_default` tinyint(1) NULL DEFAULT NULL COMMENT '是否是默认地址（0：非默认；1：默认地址）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '收货地址信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 63, '上海市浦东新区张江高科技园区博云路 2 号汇智国际商业中心 A 座 1503 室', '张三三', '13880901111', 0, '2025-06-16 17:50:37');
INSERT INTO `address` VALUES (2, 63, '广州市天河区天河北路 183 号芳草园小区 5 栋 3 单元 1002 室', '王建国', '13120201111', 1, '2025-06-16 17:55:42');
INSERT INTO `address` VALUES (3, 63, '北京市朝阳区建国路 88 号 SOHO 现代城 B 座 2305 室（前台代收）', '李婷', '13120909213', 0, '2025-06-16 18:11:15');
INSERT INTO `address` VALUES (5, 62, '测试地址', '张三', '13000001212', 1, '2025-06-18 09:38:48');
INSERT INTO `address` VALUES (6, 59, '测试地址', '章三', '13490786622', 0, '2025-06-19 15:30:02');
INSERT INTO `address` VALUES (7, 59, '测试', '张三', '13890908888', 1, '2025-06-21 16:02:10');

-- ----------------------------
-- Table structure for evaluations
-- ----------------------------
DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` int NULL DEFAULT NULL COMMENT '父级评论ID',
  `commenter_id` int NULL DEFAULT NULL COMMENT '评论者ID',
  `replier_id` int NULL DEFAULT NULL COMMENT '回复者ID',
  `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容类型',
  `content_id` int NULL DEFAULT NULL COMMENT '内容ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for evaluations_upvote
-- ----------------------------
DROP TABLE IF EXISTS `evaluations_upvote`;
CREATE TABLE `evaluations_upvote`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论点赞表ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `evaluations_id` int NULL DEFAULT NULL COMMENT '评论ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告表主键ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '公告内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '公告信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for pet
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '宠物信息表主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '宠物名',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '宠物封面图',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '宠物描述',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '宠物所在地',
  `age` int NULL DEFAULT NULL COMMENT '宠物年龄，以月为单位',
  `gender` int NULL DEFAULT NULL COMMENT '性别（0：公；1：母）',
  `pet_type_id` int NULL DEFAULT NULL COMMENT '宠物类别ID，外键，关联的是宠物类别表',
  `is_vaccine` tinyint(1) NULL DEFAULT NULL COMMENT '是否已经接种疫苗（0：未接种；1：已接种）',
  `is_recommend` tinyint(1) NULL DEFAULT NULL COMMENT '是否推荐（0：未推荐；1：已推荐）',
  `is_adopt` tinyint(1) NULL DEFAULT NULL COMMENT '领养状态（0：待领养；1：已领养）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '宠物信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet
-- ----------------------------
INSERT INTO `pet` VALUES (1, '【慵懒优雅】花花', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1767681509237花花的特点.png', '<ul><li><span style=\"color: rgba(0, 0, 0, 0.9); background-color: rgb(252, 252, 252); font-size: 16px;\">花花是一只活泼可爱的布偶猫，拥有雪白柔软的毛发和湛蓝如宝石般的大眼睛，性格温顺亲人，喜欢撒娇和蹭主人的腿。</span></li><li><span style=\"color: rgba(0, 0, 0, 0.9); background-color: rgb(252, 252, 252); font-size: 16px;\">它最爱玩逗猫棒，追逐时轻盈跳跃的样子像一朵飘动的云，因此得名\"花花\"。花花对新鲜事物充满好奇，常常歪着头观察周围，偶尔还会发出软萌的\"喵呜\"声，仿佛在和人对话。它特别爱干净，每天都会认真梳理毛发，午后喜欢蜷在阳光下发呆，慵懒又优雅。花花不仅是家里的开心果，更是治愈心灵的温暖小天使。</span></li></ul>', '幸福街1号仓库', 24, 1, 8, 1, 1, 1, '2025-06-05 15:10:15');
INSERT INTO `pet` VALUES (2, '忠诚田园犬', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17504907279132025062105595796.jpg', '<p><span style=\"color: rgba(0, 0, 0, 0.9); background-color: rgb(252, 252, 252); font-size: 16px;\">偶尔还会发出软萌的\"喵呜\"声，仿佛在和人对话。它特别爱干净，每天都会认真梳理毛发，午后喜欢蜷在阳光下发呆，慵懒又优雅。1243324</span></p>', '栗子街道102仓库', 32, 1, 9, 0, 1, 1, '2025-06-05 15:16:38');
INSERT INTO `pet` VALUES (3, '【活泼亲人】小橘猫“橙子”等待温暖的家', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17504907362372025062105582156.jpg', '<p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐾 2个月大的小橘猫，性格活泼好动，喜欢蹭人撒娇。已驱虫，健康无忧。适合喜欢互动的主人，给它一个充满爱的家吧！</span></p><p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐾 2个月大的小橘猫，性格活泼好动，喜欢蹭人撒娇。已驱虫，健康无忧。适合喜欢互动的主人，给它一个充满爱的家吧！</span></p><p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐾 2个月大的小橘猫，性格活泼好动，喜欢蹭人撒娇。已驱虫，健康无忧。适合喜欢互动的主人，给它一个充满爱的家吧！</span></p><p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐾 2个月大的小橘猫，性格活泼好动，喜欢蹭人撒娇。已驱虫，健康无忧。适合喜欢互动的主人，给它一个充满爱的家吧！🐾 2个月大的小橘猫，性格活泼好动，喜欢蹭人撒娇。已驱虫，健康无忧。适合喜欢互动的主人，给它一个充满爱的家吧！</span></p>', '广西南宁万象城', 32, 0, 9, 1, 1, 0, '2025-06-18 11:00:06');
INSERT INTO `pet` VALUES (4, '【高颜值】布偶猫“雪球”寻找专属家人', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17504907096062025062106035268.jpg', '<p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">❄️ 8个月大的布偶妹妹，蓝眼睛如星空般美丽，性格温柔黏人。已绝育，疫苗齐全，期待成为你的小公主。</span></p><p><br></p><p><br></p>', '广东广州白云区', 8, NULL, 8, 1, 1, 1, '2025-06-18 11:04:23');
INSERT INTO `pet` VALUES (5, '【乖巧安静】中华田园犬“小黑”求领养', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17504906949202025062106030082.jpg', '<p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐕 1岁大的小黑，聪明温顺，不吵不闹，已学会基本指令。希望找到一个有院子的家庭，陪伴你度过每一天。</span></p><p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐕 1岁大的小黑，聪明温顺，不吵不闹，已学会基本指令。希望找到一个有院子的家庭，陪伴你度过每一天。</span></p><p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐕 1岁大的小黑，聪明温顺，不吵不闹，已学会基本指令。希望找到一个有院子的家庭，陪伴你度过每一天。</span></p><p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🐕 1岁大的小黑，聪明温顺，不吵不闹，已学会基本指令。希望找到一个有院子的家庭，陪伴你度过每一天。</span></p><p><br></p>', '福建泉州', 12, 0, 9, 1, 0, 0, '2025-06-18 11:05:34');
INSERT INTO `pet` VALUES (6, '【流浪逆袭】三花猫“幸运”的蜕变之旅', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17504907444412025062106042568.jpg', '<p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🍀 1岁三花猫，曾被救助后恢复健康，亲人又懂事。领养它，给“幸运”一个真正的幸福猫生！</span></p><p><span style=\"color: rgb(64, 64, 64); background-color: rgb(255, 255, 255);\">🍀 1岁三花猫，曾被救助后恢复健康，亲人又懂事。领养它，给“幸运”一个真正的幸福猫生！</span></p>', '山东枣庄', 12, 0, 12, 1, 0, 0, '2025-06-18 11:06:27');
INSERT INTO `pet` VALUES (7, '【老年陪伴】8岁泰迪“豆豆”求养老家庭', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1767779386439豆豆.png', '<p><br></p>', '湖北黄冈', 19, NULL, 11, 1, 1, 0, '2025-06-18 11:07:56');
INSERT INTO `pet` VALUES (9, '隆隆', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1767778460168隆隆.png', '<p>隆隆是一只威风凛凛的缅因猫，身披浓密厚实的棕虎斑纹毛发，颈部的鬃毛如围脖般蓬松，琥珀色的眼睛像蕴藏着森林的神秘。它体型健硕却性格温和，对主人忠诚又粘人，总爱用宽厚的爪子轻轻拍打主人的手背撒娇。</p><p>・它最擅长玩毛线球，滚动时矫健有力的身姿像一头小狮子，“隆隆” 的名字便源于它踩过地板时沉稳的脚步声。隆隆对窗外的飞鸟格外关注，常常蹲坐在窗边一动不动，尾巴有节奏地轻轻摆动，偶尔发出低沉的 “咕噜” 声，像是在表达内心的期待。它很注重自身形象，每天都会花大量时间舔毛，夜晚喜欢趴在主人床边睡觉，安静又可靠。隆隆不仅是家中的守护者，更是带来安全感的暖心大宝贝。</p>', '幸福街2号仓库', 12, 0, 10, 1, 0, 0, '2026-01-07 17:35:43');
INSERT INTO `pet` VALUES (10, '火柴', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1772605290687火柴.png', '<p><span style=\"color: rgb(0, 0, 0);\">它是一只标准赤柴，毛色暖亮干净，耳朵立挺，有着柴犬标志性的治愈笑脸，眼神明亮又机灵，看起来阳光又可爱。 性格活泼亲人，聪明乖巧，不吵不闹，适应力强，很会察言观色。平时喜欢安静陪伴，也愿意出门散步，运动量适中，很适合喜欢干净、有耐心的家庭。 经历过流浪的它，格外珍惜温暖，懂事又忠诚，会用一辈子的陪伴回报你的善意。 它不求奢华，只想要一个稳定的家，一个不会放弃它的主人。 如果你喜欢爱笑、暖心、颜值高的小狗，小柴一定会成为你生活里最治愈的小太阳。 </span></p>', '幸福街2号救助站', 16, 0, 13, 1, 1, 0, '2026-03-04 13:54:17');
INSERT INTO `pet` VALUES (11, '吉吉', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1772603684886吉吉.png', '<p><span style=\"color: rgb(0, 0, 0);\">吉吉是一只乖巧安静的灰白色短毛吉娃娃，毛发光滑柔软，浅灰与奶白相间，长相精致秀气，一双大眼睛明亮又温柔，看起来干净又惹人疼。 它体型小巧，不占空间，性格安静温顺，不爱乱叫，非常适合公寓、上班族饲养。平时很黏人，喜欢贴贴、被人抱在怀里，是个安静又贴心的小陪伴。 虽然曾经流浪，但吉吉特别懂事，会乖乖上厕所，也不拆家、不调皮。它小小的身体里，藏着满满的依赖和忠诚，只想要一个稳定温暖的家，一个愿意一直疼爱它、不抛弃它的主人。 如果你喜欢安静、小巧、粘人的小狗，吉吉一定会用它全部的温柔，陪你度过每一个平凡又温暖的日子。</span></p>', '幸福街1号救助站', 12, 1, 14, 1, 0, 0, '2026-03-04 13:55:45');
INSERT INTO `pet` VALUES (12, '糯糯', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1772603825626糯糯.png', '<p><span style=\"color: rgb(0, 0, 0);\"> 糯糯是一只温顺安静的异国短毛猫，有着扁扁的小脸、圆圆的大眼睛，毛发厚实柔软，模样呆萌又温柔，天生自带“委屈可爱”滤镜，让人一看就心软。 它性格超级佛系，不爱吵闹、不调皮捣蛋，喜欢安静地趴在你身边陪你工作、追剧，是典型的“陪伴型小猫咪”。脾气超好，任人抚摸，非常亲人，一摸就会发出舒服的呼噜声。 虽然外表看起来憨憨的，其实心思细腻又黏人，渴望被疼爱、被拥抱。它不需要太大的空间，只想要一个安稳温暖的家，一个能把它捧在手心里、不离不弃的主人。 如果你喜欢安静、温顺、颜值又高的小猫咪，糯糯一定会用它全部的温柔，治愈你生活里所有的疲惫。 </span></p>', '广东省白云区', 12, 1, 15, 0, 1, 0, '2026-03-04 13:57:49');
INSERT INTO `pet` VALUES (13, '暖阳', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1772605142915暖阳.png', '<p><span style=\"color: rgb(0, 0, 0);\">暖阳是个浑身发着光的2岁柯基男孩，一身焦糖色与奶白色相间的毛发蓬松柔软，在阳光下像裹了层暖融融的蜜糖。标志性的立耳尖尖的，时刻竖着聆听世界的动静，圆溜溜的深棕色眼眸清澈又温顺，趴在草地上时，前爪轻垫脸颊，眼神里藏着小心翼翼的期盼，模样乖巧得让人心软。 作为成年柯基，暖阳早已褪去了幼犬的顽皮，养成了绝佳的生活习惯：会定点如厕，不拆家、不吵闹，散步时会乖乖跟在脚边，绝不会撒手没。它性格软糯黏人，自带“治愈系”buff，你摸它的头时，它会轻轻蹭你的掌心回应；你忙碌时，它便安静趴在一旁陪伴，懂事得让人心疼。 曾经的漂泊让它格外珍惜温暖，2岁的它正值壮年，既有着活泼的精力陪你散步玩耍，又有着沉稳的性格懂你喜怒哀乐。它不求锦衣玉食，只求一个遮风挡雨的小窝，和一个永远不会抛弃它的你。如果你想拥有一份百分百的忠诚与陪伴，不妨牵起暖阳的爪，带这个温柔的小家伙回家，让它用余生的欢喜，填满你的每一个平凡日子。</span></p>', '广西市彩虹区', 24, 0, 16, 1, 1, 0, '2026-03-04 13:59:21');
INSERT INTO `pet` VALUES (14, '砂糖橘', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1772603979562砂糖橘.png', '<p><span style=\"color: rgb(0, 0, 0);\">一身暖橘色毛发像小太阳一样亮眼，圆脸蛋、大眼睛，胖乎乎的模样软萌又治愈，自带招财福气相。 性格温顺亲人，黏人又乖巧，不挑食、好养活，脾气超好，随便撸都不生气，一摸就呼噜，是典型的治愈系小猫咪。 安静又懂事，喜欢贴贴陪伴，不拆家不捣乱，特别适合家养。它用一身温暖的橘色，等待一个能给它安稳、永远不抛弃它的家。 民间都说**十橘九胖，还有一只特别旺**，愿意带这只小福猫回家，让它用一生陪伴你吗？</span></p>', '广西市桂林区', 15, 0, 17, 0, 0, 0, '2026-03-04 14:01:13');

-- ----------------------------
-- Table structure for pet_adopt_order
-- ----------------------------
DROP TABLE IF EXISTS `pet_adopt_order`;
CREATE TABLE `pet_adopt_order`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '宠物领养单据主键ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID，外键，关联用户表，标识的是谁产生的订单数据',
  `pet_id` int NULL DEFAULT NULL COMMENT '宠物ID，外键，关联的是宠物表，标识的是哪只宠物被申请领养的',
  `address_id` int NULL DEFAULT NULL COMMENT '收货地址ID，外键，关联的是收货地址表',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '领养描述',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '单据状态（1：申请中；2：已审核；3：审核未通过；4：已完成）',
  `audit_error_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核不通过原因备注',
  `is_again_post` tinyint NULL DEFAULT NULL COMMENT '是否是再次提交（0：初次提交；1：再次提交）',
  `post_number` tinyint(1) NULL DEFAULT NULL COMMENT '提交次数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标记(0:未删除,1:已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_pet_id`(`pet_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '宠物领养单据信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_adopt_order
-- ----------------------------
INSERT INTO `pet_adopt_order` VALUES (17, 62, 1, 5, '<p>111</p>', 4, NULL, 0, 1, '2026-01-21 16:14:52', 0);
INSERT INTO `pet_adopt_order` VALUES (18, 59, 2, 7, '<p>222</p>', 4, NULL, 0, 1, '2026-01-24 15:58:01', 0);
INSERT INTO `pet_adopt_order` VALUES (19, 62, 3, 5, '<p>111</p>', 1, NULL, 0, 1, '2026-02-23 16:55:35', 0);
INSERT INTO `pet_adopt_order` VALUES (20, 66, 4, 11, '<p>11111</p>', 4, NULL, 0, 1, '2026-03-03 15:54:00', 0);


-- ----------------------------
-- Table structure for pet_health_record
-- ----------------------------
DROP TABLE IF EXISTS `pet_health_record`;
CREATE TABLE `pet_health_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pet_id` int NOT NULL COMMENT '宠物ID，关联pet表',
  `record_date` date NOT NULL COMMENT '记录日期',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `body_condition` tinyint NULL DEFAULT NULL COMMENT '身体状况(1:优秀 2:良好 3:一般 4:差)',
  `appetite` tinyint NULL DEFAULT NULL COMMENT '食欲情况(1:很好 2:正常 3:较差)',
  `energy_level` tinyint NULL DEFAULT NULL COMMENT '活力水平(1:活跃 2:正常 3:懒散)',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pet_post
-- ----------------------------
DROP TABLE IF EXISTS `pet_post`;
CREATE TABLE `pet_post`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '宠物经验帖子主键ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID，外键，关联的是用户表',
  `pet_type_id` int NULL DEFAULT NULL COMMENT '宠物类型ID，外键，关联的是宠物类别表',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '封面',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '内容',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '摘要',
  `is_audit` tinyint(1) NULL DEFAULT NULL COMMENT '是否已经审核（0：未审核；1：已审核）',
  `is_top` tinyint(1) NULL DEFAULT NULL COMMENT '是否置顶（0：否；1：是）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '宠物经验帖子信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_post
-- ----------------------------
INSERT INTO `pet_post` VALUES (1, 61, 1, '零食奖励法，宠物能多听懂20多种指令', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1749463108000Snipaste_2025-06-05_15-08-29.png', '<h3>养宠物狗的500字经验分享</h3><p>养狗是一项充满乐趣但也需要责任心的长期承诺。通过多年的养狗经验，我总结出以下实用建议：</p><p>​<strong>​1. 品种选择要慎重​</strong>​<br><br>不同犬种性格和需求差异很大。初次养狗建议选择金毛、拉布拉多等温顺易训的品种。我家金毛\"多多\"3岁，性格稳定，对儿童友好，是理想的家庭伴侣犬。</p><p>​<strong>​2. 科学喂养是关键​</strong>​<br><br>幼犬期要少量多餐（每天3-4次），成年后改为早晚两餐。建议选择优质狗粮，搭配新鲜蔬菜（胡萝卜、西兰花等）。切记不要喂食巧克力、葡萄等对狗有毒的食物。我家多多坚持定时定量喂养，毛发光亮，体检指标一直很健康。</p><p>​<strong>​3. 日常护理不能少​</strong>​</p><ul><li>​<strong>​梳毛​</strong>​：每周至少3次，金毛这类长毛犬更要每天梳理</li><li>​<strong>​洗澡​</strong>​：夏季每月2-3次，冬季每月1次，使用宠物专用浴液</li><li>​<strong>​牙齿​</strong>​：每周刷牙2-3次，预防牙结石</li><li>​<strong>​指甲​</strong>​：每月修剪1次，避免过长影响行走</li></ul><p>​<strong>​4. 训练要从小开始​</strong>​<br><br>3-6个月是最佳训练期。基础指令如\"坐\"、\"等\"、\"来\"要反复练习。我采用零食奖励法，多多现在能听懂20多种指令。定点排便训练尤其重要，前期要耐心引导，成功后家里卫生问题就解决了。</p><p>​<strong>​5. 医疗保健要重视​</strong>​<br><br>定期驱虫（每月体外，每季度体内）、每年接种疫苗必不可少。建议购买宠物医疗保险，我去年为多多做膝关节手术，保险报销了70%费用。老年犬要增加体检频率，多多7岁后我每半年带它做一次全面检查。</p><p>​<strong>​6. 心理需求要关注​</strong>​<br><br>狗狗也会感到孤独焦虑。我每天保证至少1小时互动时间，包括散步、游戏等。疫情期间居家办公，特意给多多买了智能玩具，避免它因主人长时间工作而感到被冷落。</p><p>养狗不仅是喂养，更是建立一种互相陪伴的生命联结。通过科学养护和情感投入，看着多多从顽皮小狗成长为稳重贴心的伙伴，这份幸福感无可替代。希望这些经验能帮助新晋狗主少走弯路。</p>', '幼犬期要少量多餐（每天3-4次），成年后改为早晚两餐。建议选择优质狗粮，搭配新鲜蔬菜（胡萝卜、西兰花等）。切记不要喂食巧克力、葡萄等对狗有毒的食物。我家多多坚持定时定量喂养，毛发光亮，体检指标一直很健康。', 1, NULL, '2025-06-09 17:58:47');
INSERT INTO `pet_post` VALUES (5, 1, 1, '养猫误区大揭秘！私藏的 6 个养猫技巧，猫主子更黏人', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1749628839000Snipaste_2025-06-05_15-08-06.png', '<p>养猫后发现主子总是爱答不理？甚至躲在沙发底下？其实猫咪的很多 “高冷” 行为，都是主人无意中踩了 “雷区”。这篇文章教你用猫咪的思维相处，让它主动蹭你求抱抱。</p><h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>1. 别用 “抱狗” 的方式抱猫</strong></span></h5><p>狗狗喜欢被搂在怀里，但猫咪的腹部和爪子非常敏感。正确抱法是：一只手托住前肢腋下，另一只手托住屁股，让它的身体紧贴你的胸口。如果猫咪挣扎，立刻松手，否则它会把 “被抱” 和痛苦关联。</p><h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>2. 猫砂盆：比 “干净” 更重要的是 “隐私”</strong></span></h5><p>猫咪如厕时需要安全感，猫砂盆别放在阳台、厨房等嘈杂区域，建议放在卧室衣柜旁或卫生间角落。猫砂至少每天铲一次，每周全换（膨润土猫砂厚度建议 5-8cm），换砂时留少量旧砂，避免气味变化让猫咪拒用。</p><h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>3. 零食投喂：建立 “条件反射” 的神器</strong></span></h5><p>想让猫咪听指令？用 freeze-dried 鸡肉粒做训练零食。每次喂食前，先叫它的名字，等它看你时再给零食，重复一周后，它会把 “回应名字” 和 “有好吃的” 关联起来。注意每天零食量不超过主食的 10%，避免挑食。</p><h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>4. 梳毛：比撸猫更让猫上瘾的互动</strong></span></h5><p>猫咪每天会花 30% 的时间舔毛，但长期舔毛易导致毛球症。用针梳（短毛猫）或排梳（长毛猫）顺着毛发生长方向梳，重点梳下巴、腹部和尾巴根。梳到舒服的地方，猫咪会主动拱起身体，这时可以趁机多梳几下，增进感情。</p><h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>5. 猫抓板：保护家具的 “必需品”</strong></span></h5><p>猫咪磨爪是天性，别因为它抓沙发就剪指甲（会损伤血线）。在沙发旁放立式猫抓板，撒少量猫薄荷粉，当它去抓时，用零食奖励。如果发现它抓家具，立刻用喷壶喷少量水（别直接喷猫脸），并引导到猫抓板旁。</p><h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>6. 读懂猫咪的 “身体语言”</strong></span></h5><ul><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>尾巴竖起且尖端弯曲</strong></span>：心情好，想让你摸；</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>耳朵后撇、瞳孔放大</strong></span>：害怕或生气，别靠近；</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>用头蹭你</strong></span>：标记领地，表达 “你是我的人”；</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>趴在地上露出肚子</strong></span>：信任你，但不一定想被摸肚子（大部分猫不喜欢）。<br></li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>结语</strong></span>：猫咪的爱很含蓄，需要主人用耐心解读它的需求。当你学会用它的方式相处，会发现高冷的猫主子其实藏着一颗黏人的心～</li></ul>', '猫咪每天会花 30% 的时间舔毛，但长期舔毛易导致毛球症。用针梳（短毛猫）或排梳（长毛猫）顺着毛发生长方向梳，重点梳下巴、腹部和尾巴根。梳到舒服的地方，猫咪会主动拱起身体，这时可以趁机多梳几下，增进感情。', 1, 0, '2025-06-11 16:00:56');
INSERT INTO `pet_post` VALUES (6, 59, 1, '领养了一只流浪猫，准备了这些东西，让它 3 天就信任我', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1750313708324Snipaste_2025-06-18_11-02-35.png', '<h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>内容</strong></span>：</h5><p>上周从救助站领养了一只 2 岁的流浪猫 “煤球”，接它回家前我列了份清单，现在它已经敢在我腿上踩奶了！分享给新手：<br></p><ol><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>心理准备</strong></span>：流浪猫可能有应激反应，提前学习 “渐进式脱敏法”（如用零食建立信任）。</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>物品清单</strong></span>：</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>医疗预备</strong></span>：提前联系附近 24 小时宠物医院（我选了 “宠爱国际”，急诊电话 400-xxxx），购买基础体检套餐（某团 199 元含驱虫 + 疫苗）。</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>饮食过渡</strong></span>：救助站给的猫粮和新买的 “渴望” 混喂 3 天，避免腹泻（煤球换粮时没拉肚子，亲测有效！）。</li></ol>', '上周从救助站领养了一只 2 岁的流浪猫 “煤球”，接它回家前我列了份清单，现在它已经敢在我腿上踩奶了！', 1, NULL, '2025-06-19 14:15:24');
INSERT INTO `pet_post` VALUES (7, 59, 2, '金毛 “土豆” 到家第 3 天融合，我做对了这 5 件事', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1750313785167Snipaste_2025-06-18_11-01-50.png', '<h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>内容</strong></span>：</h5><p>土豆是我从领养中心接回的 2 个月大金毛，现在它已经会自己去尿垫上厕所了！关键步骤：<br></p><ol><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>圈定安全区</strong></span>：用围栏把客厅角落围起来（1.5m×1.5m），放狗窝、食盆、尿垫，土豆前 2 天只在里面活动。</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>建立规律作息</strong></span>：</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>禁止过度安抚</strong></span>：土豆第一天晚上叫时，我在围栏旁放了件带气味的 T 恤，没抱上床，第 2 天就不叫了。</li></ol>', '圈定安全区：用围栏把客厅角落围起来（1.5m×1.5m），放狗窝、食盆、尿垫，土豆前 2 天只在里面活动', 1, NULL, '2025-06-19 14:16:49');
INSERT INTO `pet_post` VALUES (8, 59, 1, '阿橘从哈气到踩奶：我用 “零食训练法” 让流浪猫信任我', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1750313882684Snipaste_2025-06-18_11-03-10.png', '<p>阿橘是只 3 岁的流浪狸花，刚到家时躲在沙发下哈气，我用这招让它 1 周就主动贴贴：<br></p><ol><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>零食诱惑</strong></span>：每天用猫条（“小李子” 鸡肝味）在沙发边蹲喂，不伸手摸，第 4 天它主动凑过来吃。</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>模拟捕猎</strong></span>：用激光笔引导它运动（流浪猫天性爱捕猎），每次玩完给零食，建立 “互动 = 快乐” 的联想。</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>尊重边界</strong></span>：阿橘躲起来时绝不强行拖拽，用 “猫隧道” 玩具引诱它自己出来（某拼夕夕 19.9 元，阿橘现在天天钻）。</li></ol>', '用激光笔引导它运动（流浪猫天性爱捕猎），每次玩完给零食，建立 “互动 = 快乐” 的联想', 1, NULL, '2025-06-19 14:18:04');
INSERT INTO `pet_post` VALUES (9, 59, 2, '边牧一日三餐：不同阶段吃什么？营养师定制食谱', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1750313948026Snipaste_2025-06-18_11-01-43.png', '<h5 style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>内容</strong></span>：</h5><p>闪电是我领养的 1 岁边牧，根据营养师建议调整饮食后，毛发亮到反光！<br></p><ol><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>1-2 岁成长期</strong></span>：</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>3 岁后维持期</strong></span>：</li><li style=\"text-align: start;\"><span style=\"font-size: 16px;\"><strong>禁忌食物</strong></span>：闪电曾偷吃葡萄后呕吐，才知道葡萄、巧克力、洋葱绝对不能吃！（附中毒症状对照表）</li></ol>', '闪电曾偷吃葡萄后呕吐，才知道葡萄、巧克力、洋葱绝对不能吃！（附中毒症状对照表）', 1, NULL, '2025-06-19 14:19:09');
INSERT INTO `pet_post` VALUES (11, 1, 8, '【首次领养！！！】保姆级饲养方法', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1767949122849post.png', '<p><span style=\"font-size: 24px;\"><strong>第一次养猫教程（平民版）</strong></span><br>养猫前：品种真的不重要，如果有眼缘有缘分，用爱去养 每只毛孩子都会被你养成你想要的猫。<br>养猫后：<br>1.【关于疫苗】3联疫苗必打，美团团购一百左右包3针，后续每年只需打一针就行。<br>2.【关于驱虫】实在无需在宠物医院打，费用不低，网上能买药的平台买内驱外驱药只需十几块人民币，自己在网上学好驱虫步骤即可。驱虫时防舔的小圈圈也只要几块钱，且使用概率不高无需卖太贵的。<br>3.【关于耳螨】同上，耳螨药十几块网上都有得买，自己在家按照说明书自己操作即可，实在不必花费大几百在宠物店除。<br>4.【关于饮食】<br>吃饭：小猫我主张大家买贵的幼猫粮，但不需要买零食，零食吃多了猫咪的粑粑会特别臭，只吃猫粮的猫粑粑不埋都不臭。担心小猫饮食单一可以喂些蛋黄，虾仁，鸡胸肉，亲测实惠营养 小猫也爱吃。幼猫时期每天泡一条猫喝羊奶增强免疫力，羊奶大概10块钱左右能买到几十条了。<br>饮水：流动饮水机很有必要，如果是装碗里，小猫两天估计都喝不到一半。流动饮水机买十几块钱的，水壶烧开晾凉倒进去就好。<br>保健品：个人觉得猫咪保健品纯纯智商税，不建议买任何产品。<br>5.【关于娱乐】一根逗猫棒（最好是带鸡毛那种）➕一支激光笔就够，想跟猫咪玩就用逗猫棒，累了就用激光笔让小猫自己追着玩。猫别墅、猫窝买了之后猫咪也只会睡一两次就再也不会靠近，如果要买就买便宜的就行，扔了也不心疼。<br>6.【补充】<br>猫砂盆：最好一开始就买大一点的，不用等猫咪大了再换。猫砂也不要买太贵的，埋屎的东西 且一周一换，建议买最便宜的就行。网上十几块钱几十斤的到处有，不用担心便宜的味道大，贵的猫砂接屎 小猫屎臭的话味道该大还是大。定期铲猫屎、少喂零食猫条，味道真的不大！<br>猫咪指甲很好剪，指甲剪随便买小猫指甲剪就行，第一次不敢自己剪的可以去宠物医院打疫苗的时候问医生怎么剪，看着学，医生一般都不会拒绝。<br>猫咪臭大多是因为屁屁上挂着屎，每天记得给猫擦擦PP和眼屎</p><hr/><h3><span style=\"font-size: 24px;\">第一次养狗教程（平民版）</span></h3><p>养狗前：品相血统都是浮云，合眼缘、能陪伴才最重要！只要用耐心和责任对待，每只小狗都会成为你最忠诚的家人，无需盲目追求名贵品种。</p><p>养狗后：</p><ol><li>【关于疫苗】犬四联 / 六联疫苗是刚需，美团、大众点评团购价 100-150 元就能搞定 3 针基础免疫，后续每年补打 1 针加强针 + 1 针狂犬疫苗，性价比拉满。</li><li>【关于驱虫】完全不用去宠物医院花冤枉钱！网上正规平台买内驱药（如阿苯达唑）、外驱药（如非泼罗尼滴剂），一套下来才 20 块左右，跟着教程滴后背、喂药片就行。怕狗狗舔舐外驱药，几块钱的伊丽莎白圈买一个备用，使用率不高无需买贵的。</li><li>【关于耳道 / 皮肤护理】狗狗耳道容易积耳垢，网上十几块的洗耳液 + 棉签就能搞定，每周擦 1 次预防发炎；轻微皮屑、瘙痒，用宠物专用沐浴露（20-30 元一大瓶）洗澡即可，没必要花几百块做所谓 “药浴”。</li><li>【关于饮食】</li><li>吃饭：幼犬建议选性价比高的国产幼犬粮（不用执着进口粮），零食能省则省！零食吃多了狗狗挑食、口臭还软便，纯吃狗粮 + 自制辅食就够营养。平时煮点胡萝卜、西兰花、鸡胸肉（白水煮熟撕碎），偶尔喂个水煮蛋黄，成本低还健康，狗狗超爱吃。幼犬可以每天冲 1 包羊奶粉（10 块钱能买 30 条），增强抵抗力还补水。</li><li>饮水：狗狗喝水量大，普通水碗就行，但要每天换新鲜凉白开！流动饮水机不是必需品，预算有限的话，选一个不容易打翻的陶瓷碗，每天勤换水比啥都强。</li><li>保健品：除了老年犬或医生明确建议补充的情况，幼犬、成年犬的保健品基本是智商税！正常吃饭就能满足营养需求，别花冤枉钱。</li><li>【关于娱乐】一根拔河绳（10 块钱）+ 一个磨牙棒（5 块钱 / 根）就够了！狗狗爱互动就用拔河绳玩拉扯，独自在家时扔个磨牙棒，既能解闷又能防拆家。狗窝、狗别墅没必要买贵的，狗狗大概率会睡沙发、床，买个 20 块左右的耐咬垫子就行，脏了扔了也不心疼；笼子选折叠款（50-80 元），够狗狗转身躺下就好，不用追求超大尺寸。</li><li>【补充】</li><li>狗厕所 / 尿垫：幼犬训练定点排便，买最便宜的宠物尿垫（10 块钱 50 片）+ 简易狗厕所（30 元左右），铺在固定角落，多引导几次就能学会，不用买智能厕所。</li><li>狗粮碗、水碗选不锈钢材质（20 块一套），耐咬好清洗，比陶瓷、塑料耐用还省钱。</li><li>狗狗指甲剪（15 元左右）很好用，第一次不敢剪的话，带狗狗打疫苗时让医生示范，看着学一次就会，剪的时候避开血线就行。</li><li>狗狗体味重，多半是肛门腺没挤 + 耳朵脏！每周给狗狗挤 1 次肛门腺（网上有详细教程，几分钟搞定），勤擦耳朵、定期洗澡（夏天 10 天一次，冬天 20 天一次），体味自然消失。</li><li>遛狗装备：牵引绳选胸背带款（30 元左右），比项圈更护颈椎，牵引绳长度 1.5 米足够，不用买多功能豪华款，结实耐用就好。</li><li>狗狗掉毛是正常现象，每天用 10 块钱的针梳梳 5 分钟，既能减少掉毛，还能增进感情～</li></ol>', '新手养猫和养狗方法', 1, 1, '2026-01-09 16:59:07');

-- ----------------------------
-- Table structure for pet_type
-- ----------------------------
DROP TABLE IF EXISTS `pet_type`;
CREATE TABLE `pet_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '宠物类别主键ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '宠物类别名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '宠物类别表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_type
-- ----------------------------
INSERT INTO `pet_type` VALUES (8, '布偶猫');
INSERT INTO `pet_type` VALUES (9, '田园犬');
INSERT INTO `pet_type` VALUES (10, '缅因猫');
INSERT INTO `pet_type` VALUES (11, '泰迪犬');
INSERT INTO `pet_type` VALUES (12, '花猫');
INSERT INTO `pet_type` VALUES (13, '柴犬');
INSERT INTO `pet_type` VALUES (14, '吉娃娃');
INSERT INTO `pet_type` VALUES (15, '加菲猫');
INSERT INTO `pet_type` VALUES (16, '柯基');
INSERT INTO `pet_type` VALUES (17, '橘猫');

-- ----------------------------
-- Table structure for proposal_feedback
-- ----------------------------
DROP TABLE IF EXISTS `proposal_feedback`;
CREATE TABLE `proposal_feedback`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '建议与反馈表主键',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID，外键，关联的是用户表',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  `is_reply` tinyint(1) NULL DEFAULT NULL COMMENT '是否回复（0：未回复；1：已回复）',
  `reply_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '回复内容',
  `is_top` tinyint(1) NULL DEFAULT NULL COMMENT '是否是精华贴（0：否；1：是）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '建议与反馈信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `avatar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户头像',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `role` int NULL DEFAULT NULL COMMENT '用户角色',
  `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别：1-女；2-男；',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '用户注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin', '14e1b600b1fd579f47433b88e8d85291', '/api/v1.0/pet-adopt-api/file/getFile?fileName=1756105833648Snipaste_2025-05-20_15-13-00.png', '5567643@qq.com', 1, 2, '1990-06-05', '16766666666', '2024-10-19 12:53:05');
INSERT INTO `user` VALUES (59, 'lichunran', '李春然', '14e1b600b1fd579f47433b88e8d85291', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17488801328625.png', '123342@qq.com', 2, 1, '1985-07-11', '17687665323', '2025-05-28 17:54:48');
INSERT INTO `user` VALUES (61, 'guihua', '桂花', '14e1b600b1fd579f47433b88e8d85291', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17488801204072.png', '14324@qq.com', 2, 1, NULL, NULL, '2025-05-29 14:13:04');
INSERT INTO `user` VALUES (62, 'zhangsan', '张三', '14e1b600b1fd579f47433b88e8d85291', '/api/v1.0/pet-adopt-api/file/getFile?fileName=17485958396406.png', NULL, 2, NULL, NULL, NULL, '2025-05-29 15:39:59');
INSERT INTO `user` VALUES (63, 'zhangsan2', '追风筝的人', '14e1b600b1fd579f47433b88e8d85291', '/api/v1.0/house-rental-api/file/getFile?fileName=17507548294782.png', '432432@qq.com', 2, 2, '1996-07-17', '17687442010', '2025-05-29 15:41:04');

-- ----------------------------
-- Table structure for warning_question_template
-- ----------------------------
DROP TABLE IF EXISTS `warning_question_template`;
CREATE TABLE `warning_question_template`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `template_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `template_type` tinyint NOT NULL COMMENT '1-定期关怀 2-随机抽查 3-领养回访',
  `questions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题JSON',
  `frequency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MONTH' COMMENT '发送频率：WEEK/MONTH/QUARTER',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warning_questionnaire
-- ----------------------------
DROP TABLE IF EXISTS `warning_questionnaire`;
CREATE TABLE `warning_questionnaire`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `pet_id` int NOT NULL COMMENT '宠物ID',
  `question_type` tinyint NULL DEFAULT 1 COMMENT '问卷类型：1-定期关怀 2-随机抽查 3-预警触发',
  `questions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '问卷问题（JSON格式）',
  `answers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '用户答案（JSON格式）',
  `score` int NULL DEFAULT NULL COMMENT '问卷分数（0-100，越高越好）',
  `warning_level` tinyint NULL DEFAULT 0 COMMENT '预警级别：0-无 1-低 2-中 3-高',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-待回答 2-已提交 3-已处理',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截止时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_pet`(`user_id` ASC, `pet_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '预警问卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warning_record
-- ----------------------------
DROP TABLE IF EXISTS `warning_record`;
CREATE TABLE `warning_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `pet_id` int NOT NULL COMMENT '宠物ID',
  `warning_level` tinyint NOT NULL COMMENT '预警级别：1-低 2-中 3-高',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '预警原因',
  `suggestion` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '处理建议',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-待处理 2-已处理',
  `processed_by` int NULL DEFAULT NULL COMMENT '处理人ID',
  `processed_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_warning`(`user_id` ASC, `warning_level` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '预警记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warning_rule
-- ----------------------------
DROP TABLE IF EXISTS `warning_rule`;
CREATE TABLE `warning_rule`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `rule_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '规则名称',
  `condition_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '条件描述',
  `min_score` int NULL DEFAULT NULL COMMENT '最低分数',
  `max_score` int NULL DEFAULT NULL COMMENT '最高分数',
  `warning_level` tinyint NULL DEFAULT NULL COMMENT '预警级别',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '建议措施',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '预警规则表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
