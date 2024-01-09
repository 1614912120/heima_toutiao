package com.heima.user.filter;

import com.heima.model.User.pojos.ApUser;
import com.heima.model.threadlocal.AppThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * ClassName: AppTokenFilter
 * Package: com.heima.user.filter
 * Description:
 *
 * @Author R
 * @Create 2024/1/9 11:20
 * @Version 1.0
 */
@Slf4j
@Order(1)
@WebFilter(filterName = "appTokenFilter",urlPatterns = "/*")
@Component
public class AppTokenFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //查看header里面有没有userid
        String userId = request.getHeader("userId");
        if(StringUtils.isNotBlank(userId)&& Integer.valueOf(userId) !=0) {
            ApUser apUser = new ApUser();
            apUser.setId(Integer.valueOf(userId));
            AppThreadLocalUtils.setUser(apUser);
        }

        filterChain.doFilter(servletRequest,servletResponse);
        AppThreadLocalUtils.clear();
    }
}
