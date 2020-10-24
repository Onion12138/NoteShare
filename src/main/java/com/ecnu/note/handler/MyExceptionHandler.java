package com.ecnu.note.handler;

import com.ecnu.note.vo.BaseResponseVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author onion
 * @date 2020/3/28 -5:41 下午
 */
@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public BaseResponseVO serviceExceptionHandler(RuntimeException e){
        return BaseResponseVO.serviceException(e);
    }
}
