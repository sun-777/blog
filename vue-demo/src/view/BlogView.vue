<template>
  <div class="blogView">
    <ul class="list">
      <li>
        <el-card :id="data" class="box-card" shadow="hover">
          <div style="font-size:16px; font-weight:400;">
            <p style="font-size: 18px; font-weight: 600; text-align: center;"><span>{{ data.title }}</span></p>
            <p style="font-size: 14px; font-weight: 400; text-align: center;"><span>&nbsp;&nbsp;</span><i class="el-icon-user"></i>&nbsp;{{data.authorName}}<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><i class="el-icon-time"></i>&nbsp;{{data.createTime | dateFormatter }}</p>
            <p><span>&nbsp;</span></p>
            <p><span style="font-weight:bold">【内容简介】:&nbsp;</span>{{ data.description }}</p>
            <p><span>&nbsp;</span></p>
            <div v-html="data.content" style="text-align: left;" ></div>
          </div>
        </el-card>
      </li>
    </ul>
  </div>
</template>
<script>
  export default {
    created() {
      this.articleId = this.$route.query.id;
      this.getBlogView(this.articleId);
    },
    data() {
      return {
        articleId: '',
        data: ''
      }
    },
    mounted() {
    },
    filters: {
      dateFormatter: function (value) {
        let date = new Date(value);
        let y = date.getFullYear();
        let MM = date.getMonth() + 1;
        MM = MM < 10 ? "0" + MM : MM;
        let d = date.getDate();
        d = d < 10 ? "0" + d : d;
        let h = date.getHours();
        h = h < 10 ? "0" + h : h;
        let m = date.getMinutes();
        m = m < 10 ? "0" + m : m;
        let s = date.getSeconds();
        s = s < 10 ? "0" + s : s;
        return y + "-" + MM + "-" + d + " " + h + ":" + m;
      }
    },
    methods: {
      async getBlogView(id) {
        this.axios({
          method: 'get',
          url: '/api/blog/view',
          headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "auth": "Bearer " + localStorage.getItem("auth")
          },
          withCredentials: true,
          params: {
            articleId: id
          }
        }).then(successResponse => {
          //处理成功
          if (successResponse.data.code === 200) {
            let token = successResponse.headers.auth;
            if (token != null && token != '') {
              localStorage.setItem("auth", token)
            }
            this.data = successResponse.data.data;
          } else {
            let relogin = successResponse.headers.relogin;
            if (relogin != null && relogin != '') {
              this.$message.error("token无效，需要重新登录")
              this.$router.push(relogin);
            } else {
              let msg = successResponse.data.message;
              if (msg != null && msg != '') {
                this.$message.error("" + msg)
              }
            }
          }
        }).catch(failResponse => {
          //this.$message.error("输入邮箱或密码信息错误，请重新输入！" + failResponse.data.message);
        });
      },
    }
  }
</script>
<style>

  blogView {
    color: rgb(203 220 222);
    vertical-align: middle;
    margin:auto;
  }

  .box-card {
    background-color: #FFFFFF;
    display: block;
    text-align: left;
  }

  ul li {
    list-style: none;
  }
</style>
