package com.ym.seeing.rpc.service;

import com.ym.seeing.core.domain.User;
import com.ym.seeing.rpc.config.OpenFeignLogConfig;
import com.ym.seeing.rpc.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/25 16:40
 * @Desc:
 */
@FeignClient(name = "seeing-api",
        contextId = "remoteUserService",
        value = "seeing-api",
        fallbackFactory = RemoteUserFallbackFactory.class,
        configuration = OpenFeignLogConfig.class
)
public interface IRemoteUserService {

    @PostMapping("/user/getUser")
    public User getUser(@RequestBody User user);
}
