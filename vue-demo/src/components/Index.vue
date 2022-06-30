<template>
  <div class="index">
    <!-- login -->
    <div class="login-register">
      <div class="logo">
        <el-row class="avatar-basic">
          <el-col>
            <el-avatar shape="circle" :size="80" :src="quarkUrl"></el-avatar>
          </el-col>
        </el-row>
      </div>
      <div class="register">
        <h3><span>立即加入 Quark</span></h3>
        <el-button class="register-button" @click="handle_register()" type="primary" round>注册</el-button>
      </div>
      <div class="separator-line">
        <el-divider style="color:rgba(0,0,0,0)"></el-divider>
      </div>
      <div class="have-account">
        <h3><span>已有账号?</span></h3>
      </div>
      <div>
        <el-button class="login-button" @click="handle_login()" type="primary" round>登录</el-button>
      </div>
    </div>
  </div>
</template>

<script>
  import QuarkLogo from '@/assets/logo.png'
  export default {
    name: 'Index',
    data() {
      return {
        quarkUrl: QuarkLogo,
        login: 'Login',
        register: 'Register'
      }
    },
    methods: {
      handle_login() {
        this.$router.push({ name: this.login });
      },
      handle_register() {
        this.$router.push({ name: this.register });
      }
    }
  }
</script>

<style scoped>
  index {
    -webkit-text-size-adjust: 100%;
    overflow: hidden;
    font-size: 14px;
  }

  .login-register {
    width: 32%;
    height: 32%;
    margin: auto;
    align-content: center;
    color: rgb(203 220 222);
  }

  .register-button {
    margin: 5px auto;
    text-align:center;
  } 

  .separator-line{
    margin:auto;
    width:50%;
  }

  h1, h2, h3 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }

</style>
