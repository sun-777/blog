<template>
  <div class="login-form">
    <el-form ref="user" :model="user" :rules="dataRules">
      <el-form-item prop="email" >
        <el-input placeholder="请输入注册的邮箱" v-model="user.email" maxlength="32" show-word-limit>
          <template slot="prepend">
            注册邮箱
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="nickname" >
        <el-input placeholder="请输入用户名" v-model="user.nickname" maxlength="10" show-word-limit>
          <template slot="prepend">
            用户名&nbsp;&nbsp;&nbsp;
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password" >
        <el-input placeholder="请输入密码" v-model="user.password" show-password>
          <template slot="prepend">
            登录密码
          </template>
        </el-input>
      </el-form-item>
      <el-form-item >
        <el-col :span="11">
          <el-select v-model="user.gender" placeholder="请选择性别" style="width: 100%;" :required="true">
            <el-option label="男性" value="MALE"></el-option>
            <el-option label="女性" value="FEMALE"></el-option>
            <el-option label="第三性" value="THIRDSEX"></el-option>
          </el-select>
        </el-col>
        <el-col :span="2">&nbsp;</el-col>
        <el-col :span="11">
          <el-date-picker v-model="user.birth" type="date" :picker-options="pickerOptions" placeholder="请选择生日" style="width: 100%;"></el-date-picker>
        </el-col>
      </el-form-item>
      <el-form-item prop="introduction" >
        <el-input type="textarea" v-model="user.introduction" placeholder="请输入你的简介" maxlength="50" show-word-limit>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">注册</el-button>
        <el-button>取消</el-button>
      </el-form-item>

    </el-form>
  </div>
</template>

<script>export default {
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
        user: {
          email: '',
          nickname: '',
          password: '',
          gender: '',
          birth: '',
          introduction: '',
        },
        pickerOptions: {
          disabledDate: time => {
            return time.getTime() > Date.now() - 24*60*60*1000;
          }
        },
        dataRules: {
          email: [{ required: true, message: '请输入注册的邮箱', trigger: 'blur' }, { validator: validateEmail, trigger: "blur" }],
          nickname: [{ required: true, message: "请输入用户名", trigger: 'blur' }],
          password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { validator: validatePassword, trigger: "blur" }]
        },
      }
    },
    methods: {
      onSubmit() {
        //let userData = new FormData();
        //for (let key in this.user) {
        //  userData.append(key, this.user[key]);
        //}
        alert("" + JSON.stringify({
          'email': this.user.email,
          'nickname': this.user.nickname,
          'password': this.user.password,
          'gender': this.user.gender,
          'birth': this.user.birth,
          'introduction': this.user.introduction
        }));
        
        this.axios({
          method: 'post',
          url: '/api/register',
          headers: {
            "Content-Type": "application/json; charset=UTF-8",
            "Access-Control-Allow-Origin": "*"
          },
          withCredentials: true,
          data: JSON.stringify({
            'email': this.user.email,
            'nickname': this.user.nickname,
            'password': this.user.password,
            'gender': this.user.gender,
            'birth': this.user.birth,
            'introduction': this.user.introduction
          }),
        }).then(successResponse => {
          //处理成功
          if (successResponse.data.code === 200) {
            this.$router.push('/login');
          } else {
            this.$message.error("" + successResponse.data.message);
          }
        }).catch(failResponse => {
          //this.$message.error("注册失败: " + failResponse.data.msg);
        });
      }
    }
  }</script>

<style scoped>
  .login-form {
    width: 40%;
    margin: auto;
    color: rgb(203 220 222);
  }
</style>
