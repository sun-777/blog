package com.blog.quark.controller;

import static com.blog.quark.common.util.Constant.TOKEN_JWT;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.quark.common.Password;
import com.blog.quark.common.Result;
import com.blog.quark.common.util.JwtUtil;
import com.blog.quark.common.util.validator.EmailValidator;
import com.blog.quark.entity.User;
import com.blog.quark.service.UserQueryService;

@RestController
public class LoginController implements BaseController {
    //private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);
    
    @Autowired
    private UserQueryService queryService;
    
    @PostMapping("/login")
    public Result<?> login(@RequestParam("email")String email, @RequestParam("password")Password password) {
        String errorMsg = "邮箱格式错误";
        if (EmailValidator.getInstance().isValid(email)) {
            try {
                User user = queryService.getByEmail(email);
                if (null != user) {
                    if (password.equals(user.getPassword())) {
                        // 跨域访问允许访问的响应头的内容
                        HttpServletResponse response = BaseController.response();
                        response.addHeader("Access-Control-Expose-Headers", TOKEN_JWT);
                        response.addHeader(TOKEN_JWT, JwtUtil.generateToken(user));
                        return Result.success();
                    }
                    errorMsg = "密码错误";
                } else {
                    errorMsg = "邮箱未注册";
                }
            } catch (Exception e) {
                errorMsg = e.getCause().getMessage();
            }
        }
        return Result.error().setMessage(errorMsg);
    }
    
}
