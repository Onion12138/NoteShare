package com.ecnu.note.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author onion
 * @date 2020/2/12 -11:03 上午
 */
public class AuthUtil {
    public static String getEmail() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String rawToken = request.getHeader("token");
        if(StringUtils.isEmpty(rawToken))
            throw new RuntimeException("请先登录");
        String token = rawToken.substring(5);
        try {
            return JwtUtil.parseJwt(token);
        } catch (Exception e){
            throw new RuntimeException("token无效,请重新登录");
        }
    }

}
