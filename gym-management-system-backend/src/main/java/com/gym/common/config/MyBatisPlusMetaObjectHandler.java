package com.gym.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * 用于自动填充 createTime 和 updateTime 字段
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充 createTime 字段
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        // 自动填充 updateTime 字段
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动填充 updateTime 字段
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
