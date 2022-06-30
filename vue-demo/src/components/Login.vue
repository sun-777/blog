<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" >
      <el-form-item prop="email" >
        <el-input placeholder="请输入登录邮箱" v-model="loginForm.email" maxlength="32" show-word-limit>
          <template slot="prepend">
            &nbsp;&nbsp;&nbsp;邮&nbsp;箱&nbsp;&nbsp;&nbsp;
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password" >
        <span></span>
        <el-input class="login-password" placeholder="请输入登录密码" v-model="loginForm.password" show-password>
          <template slot="prepend">
            登录密码
          </template>
        </el-input>
      </el-form-item>

      <div class="login-submit">
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <el-button>取消</el-button>
      </div>
    </el-form>
  </div>
</template>

<script> 
  export default {
    data() {
      let validateEmail = (rule, value, callback) => {
        const regexEmail = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/
        setTimeout(() => {
          if (regexEmail.test(value)) {
            callback()
          } else {
            callback(new Error('请输入有效的邮箱'))
          }
        }, 100)
      }
      let validatePassword = (rule, value, callback) => {
        const regexPassword = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,18}$/
        setTimeout(() => {
          if (regexPassword.test(value)) {
            callback()
          } else {
            callback(new Error('密码格式错误！必须包含6-18为字母和数字'))
          }
        }, 100)
      }
      return {
        loginForm: {
          email: '',
          password: ''
        },
        loginRules: {
          email: [{ required: true, message: '请输入登录邮箱', trigger: 'blur' }, { validator: validateEmail, trigger: "blur" }],
          password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }, { validator: validatePassword, trigger: "blur" }]
        },
      }
    },
    methods: {
      handleLogin() {
        this.axios({
          method: 'post',
          headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "Access-Control-Allow-Origin": "*"
          },
          url: '/api/login',
          withCredentials: true,
          params: {
            email: this.loginForm.email,
            password: this.loginForm.password
          }
        }).then(successResponse => {
          //处理成功
          if (successResponse.data.code === 200) {
            //利用localStorage存储到本地
            let token = successResponse.headers.auth;
            if (token != null && token != '') {
              localStorage.setItem("auth", token)
            }
            this.$router.push('/quark');
          } else {
            this.$message.error("" + successResponse.data.message)
          }
        }).catch(failResponse => {
          //this.$message.error("输入邮箱或密码信息错误，请重新输入！" + failResponse.data.message);
        });
      }
    }
  }</script>

<style scoped>
  .login {
    width: 40%;
    height:100%;
    color: rgb(203 220 222);
    margin: auto;
    vertical-align: central
  }
  .login .login-password {
     margin: 8px 0px 0px 0px;
  }
  .login .login-submit{
     margin: 8px 0px 0px 0px;
  }
</style>
