-- 1. API Key 管理表 (一对多：一个用户可以有多个 API Key)
CREATE TABLE `api_key` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '归属用户ID',
  `key_value` VARCHAR(128) NOT NULL UNIQUE COMMENT 'Key的值，例如 sk-xxxx',
  `name` VARCHAR(64) COMMENT '用户给Key起的别名',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `expire_time` DATETIME COMMENT '过期时间，null为永久',
  `deleted` TINYINT DEFAULT 0
) COMMENT = 'API密钥表';

-- 2. 用户额度表 (基于大模型业务必须配备)
CREATE TABLE `user_quota` (
  `user_id` BIGINT PRIMARY KEY,
  `total_tokens` BIGINT DEFAULT 0 COMMENT '总购买/赠送的令牌数',
  `used_tokens` BIGINT DEFAULT 0 COMMENT '已消耗的令牌数',
  `version` INT DEFAULT 0 COMMENT '乐观锁版本号，防止并发扣减写覆盖'
) COMMENT = '用户调用额度表';
