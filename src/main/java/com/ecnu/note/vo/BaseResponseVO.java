package com.ecnu.note.vo;

import lombok.Data;

/**
 * @author onion
 * @date 2020/1/23 -10:28 上午
 */
@Data
public class BaseResponseVO {
    private Integer code;
    private String message;
    private Object data;

    private BaseResponseVO(){}

    public static BaseResponseVO success(){
        BaseResponseVO response = new BaseResponseVO ();
        response.setCode(200);
        response.setMessage("success");
        return response;
    }

    public static BaseResponseVO success(Object data){
        BaseResponseVO response = new BaseResponseVO();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static BaseResponseVO error(String message) {
        BaseResponseVO response = new BaseResponseVO();
        response.setCode(200);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
    public static BaseResponseVO serviceException(Exception e){
        BaseResponseVO response = new BaseResponseVO();
        response.setCode(-1);
        response.setMessage(e.getMessage());
        return response;
    }

}
