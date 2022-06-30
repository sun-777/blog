<template>
  <div class="create-blog">
    <el-form ref="blog" :model="blog" :rules="rules">
      <el-form-item prop="title" class="title">
        <el-input placeholder="请输入标题" v-model="blog.title" maxlength="60" show-word-limit>
          <template slot="prepend">
            &nbsp;&nbsp;&nbsp;标&nbsp;题&nbsp;&nbsp;&nbsp;
          </template>
        </el-input>
      </el-form-item>

      <el-form-item prop="description">
        <el-input type="textarea" v-model="blog.description" placeholder="请输入内容摘要" maxlength="498" rows="4" show-word-limit>
          <template slot="prepend">
            内容摘要
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="content">
        <EDITOR v-model="blog.content" :isClear="isClear" placeholder="文章内容"></EDITOR>
      </el-form-item>
        <div class="blog-commit">
          <el-button type="primary" @click="handleCommit">提交</el-button>
        </div>
    </el-form>
  </div>
</template>

<script>
  import editor from '@/components/WangEditor';

  export default {
    components: {
      EDITOR: editor,
    },
    data() {
      return {
        blog: {
          title: '',
          description: '',
          content: '',
        },
        isClear: false,
        rules: {
          title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        },
      }
    },
    methods: {
      handleCommit() {
        if (this.blog.title === '') {
          this.$message.error("标题不能为空")
          return;
        }

        if (this.blog.content === '') {
          this.$message.error("文章内容不能为空")
          return;
        }

        this.axios({
          method: 'post',
          url: 'api/blog/add',
          headers: {
            "Content-Type": "application/json; charset=UTF-8",
            "auth": "Bearer " + localStorage.getItem("auth")
          },
          withCredentials: true,
          data: JSON.stringify({
            "title": this.blog.title,
            "description": this.blog.description,
            "content": this.blog.content,
          }),
        }).then(successResponse => {
          //处理成功
          if (successResponse.data.code === 200) {
            let token = successResponse.headers.auth;
            if (token != null && token != '') {
              localStorage.setItem("auth", token)
            }
            this.$router.push('/quark/blog/list');
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
  }</script>

<style scoped>

  .title{
    margin-top: 2px;
  }


  .create-blog {
    width: 95%;
    color: rgb(203 220 222);
    vertical-align: middle;
    margin: auto;
    overflow-x: hidden;
  }

  .login .blog-commit {
    margin-top:10px;
  }
</style>
