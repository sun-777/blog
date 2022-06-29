package com.blog.quark.controller;

import static com.blog.quark.common.util.Constant.SYS_PROFILE_TAG;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.blog.quark.common.Result;
import com.blog.quark.entity.Profile;
import com.blog.quark.entity.User;
import com.blog.quark.id.generator.IdGenerators;
import com.blog.quark.service.UserQueryService;
import com.blog.quark.service.UserRegisterService;

@RestController
public class RegisterController implements BaseController {
    
    //private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);
    
    @Autowired
    private UserRegisterService registerService;
//    @Autowired
//    private FileServerService uploadService;
    @Autowired
    private UserQueryService userQueryService;
    
    
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        String errorMsg = "注册失败，请重新注册";
        if (null != user) {
            // 根据邮箱查找用户
            if (null == userQueryService.getByEmail(user.getEmail())) {
                LocalDateTime current = LocalDateTime.now();
                
                user.setUserId(IdGenerators.getId());
                // 注册时，使用默认头像信息，所以不需要执行上传头像文件动作
                // 而且，默认头像名字使用了Tag（"@default"）标记。
                user.setProfile(getDefaultProfile(current));
                user.setCreateTime(LocalDateTime.now());
                try {
                    registerService.register(user);
                    return Result.success();
                } catch (Exception e) {
                    return Result.error().setMessage(errorMsg);
                }
            } else {
                errorMsg = "邮箱已经注册，请使用其它邮箱重新注册";
            }
        } else {
            errorMsg = "注册信息错误，请重新注册";
        }
        return Result.error().setMessage(errorMsg);
    }
    
    
    // 获取默认头像信息
    private Profile getDefaultProfile(LocalDateTime current) {
        return new Profile(IdGenerators.getId(), "default.png".concat(SYS_PROFILE_TAG), current);
    }
}
