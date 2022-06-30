<template>
  <div class="quark">
    <el-container>
      <el-header height="80px" width="100%">
        <div class="logo">
          <el-row class="avatar-basic">
            <el-col>
              <el-avatar shape="square" :size="60" :src="quarkUrl"></el-avatar>
            </el-col>
          </el-row>
        </div>
      </el-header>

      <el-aside>
        <div class="navigator">
          <el-row class="navigator-row">
            <el-col>
              <el-menu class="el-menu-vertical" router :default-openeds=defaultOpen :default-active="activeMenu" mode="vertical" text-color="#666" active-text-color = "#409eff" >
                <el-submenu index="1">
                  <template slot="title">
                    <i class="el-icon-menu"></i>
                    <span slot="title">&nbsp;&nbsp;&nbsp;主&nbsp;页&nbsp;&nbsp;&nbsp;</span>
                  </template>
                  <el-menu-item index="/quark/blog/list">文章列表</el-menu-item>
                  <el-menu-item index="/quark/blog/new">发布文章</el-menu-item>
                </el-submenu>
                <el-submenu index="2">
                  <template slot="title">
                    <i class="el-icon-s-comment"></i>
                    <span slot="title">博客管理</span>
                  </template>
                  <el-menu-item index="/quark/category">分类</el-menu-item>
                  <el-menu-item index="/quark/comment">评论</el-menu-item>
                </el-submenu>
                <el-submenu index="3">
                  <template slot="title">
                    <i class="el-icon-user-solid"></i>
                    <span slot="title">个人管理</span>
                  </template>
                  <el-menu-item index="/quark/profile">个人资料</el-menu-item>
                  <el-menu-item index="/quark/log">操作日志</el-menu-item>
                </el-submenu>
              </el-menu>
            </el-col>
          </el-row>
        </div>
      </el-aside>

      <el-main>
        <div class="content-view">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </div>
</template>
<script>
  import QuarkLogo from '@/assets/logo.png'
  export default {
    data() {
      return {
        quarkUrl: QuarkLogo,
        //  默认展开菜单项
        defaultOpen: ['1'],
      }
    },
    computed: {
      activeMenu() {
        let title = this.$route.meta.title
        document.title = title
        if (this.$route.meta.apiActiveMenu) {
          return this.$route.meta.apiActiveMenu
        }
        return this.$route.path;
      }
    },
    methods: {
      handleOpen(key, keyPath) {
      },
      handleClose(key, keyPath) {
      }
    }
  }
</script>

<style scoped>

  .el-container {
    height: 100%;
  }

  .el-header {
    border-bottom: 1px solid rgb(192 193 200);
    color: #333;
    text-align: center;
  }

  .navigator {
    width: 15%;
    text-align: left;
    border-right: 1px solid #E9EEF3;
    display: block;
    position: absolute;
    margin-left: auto;
    margin-bottom: 0;
    background-color: rgb(255,255,255);
    overflow-x: hidden;
  }

  .content-view {
    width: 85%;
    display: block;
    position: absolute;
    margin-left: 12%;
  }

  .el-menu-vertical {
    height: 100vh;
  }

</style>
