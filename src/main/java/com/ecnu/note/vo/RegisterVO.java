package com.ecnu.note.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * @author onion
 * @date 2020/1/27 -6:50 下午
 */
@Data
public class RegisterVO {
    @Email(message = "请输入符合规范的邮箱")
    private String email;
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @Length(min = 6, max = 16, message = "密码长度在6-16位")
    private String password;
    @Length(min = 6, max = 6, message = "请输入正确的验证码")
    private String code;
}
