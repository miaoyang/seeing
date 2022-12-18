package com.ym.seeing.rpc.factory;

import com.ym.seeing.core.domain.User;
import com.ym.seeing.rpc.service.IRemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/26 17:12
 * @Desc:
 */
@Component
@Slf4j
public class RemoteUserFallbackFactory implements FallbackFactory<IRemoteUserService> {

    @Override
    public IRemoteUserService create(Throwable cause) {
        log.error("用户调用RemoteUser服务失败：{}",cause.getMessage());
        return new IRemoteUserService() {
            @Override
            public User getUser(User user) {
                return null;
            }
        };
    }
}
