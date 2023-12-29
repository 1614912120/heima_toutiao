package com.heima.gateway.filter;

import com.heima.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: AuthorizeFilter
 * Package: com.heima.gateway.filter
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 15:56
 * @Version 1.0
 */
@Component
@Slf4j
@Order(0) // 值越小越优先执行
public class AuthorizeFilter implements GlobalFilter {
    private static List<String> urlList = new ArrayList<>();
    // 初始化白名单 url路径
    static {
        urlList.add("/login/in");
        urlList.add("/v2/api-docs");
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取uri地址
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        for(String url : urlList) {
            if(path.contains(url)){
                return chain.filter(exchange);
            }
        }
        //获取请求头token 返回401停止请求
        String jwtToken = request.getHeaders().getFirst("token");
        if(StringUtils.isBlank(jwtToken)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        try {
            //获取载荷信息
            Claims claims = AppJwtUtil.getClaimsBody(jwtToken);
            log.info(claims.toString());
            //  -1：有效，0：有效，1：过期，2：过期
            int verifyToken = AppJwtUtil.verifyToken(claims);
            if(verifyToken<1) {
                //解析成功获取token中存放得id，并将id设置到请求头中，路由给其他微服务
                Object id = claims.get("id");
                request.mutate().header("userId",String.valueOf(id));
                //认证成功放行请求
                return chain.filter(exchange);
            }else {
                log.error("解析token失败,token过期");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("解析token失败",e.getMessage());
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
