<template>
  <div class="blogList">
    <ul class="list">
      <li v-for="(item, index) in list" :key="index">
        <el-card @click.native="openArticle(item.articleId)" class="box-card" shadow="hover">
          <p style="font-size: 18px; font-weight: 600; text-align: center;"><span>{{ item.title }}</span></p>
          <el-descriptions :colon="false" :column="2" style="font-size:16px; font-weight:400;">
            <el-descriptions-item ><span>&nbsp;&nbsp;<i class="el-icon-user"></i>&nbsp;{{item.authorName}}</span></el-descriptions-item>
            <el-descriptions-item><i class="el-icon-time"></i>&nbsp;{{item.createTime | dateFormatter }}</el-descriptions-item>
            <el-descriptions-item><span>【内容简介】:&nbsp;</span>{{ item.description | omitted }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </li>
    </ul>

    <div class="pagination">
      <div class="count-show">
        <span>
          共: {{total}} 记录
        </span>
      </div>
      <el-pagination :current-page="currentPage"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"
                     :page-size="pageSize"
                     :page-sizes="[5, 10, 20]"
                     layout="prev, pager, next, jumper"
                     :total="total">
      </el-pagination>
      <div class="custom-pagination">
        每页记录
        <el-select v-model="pageSize" size="small" placeholder="" @change="handleSizeChange">
          <el-option v-for="(item,index) in perpage"
                     :key="index"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-select>
      </div>
    </div>
  </div>

</template>
<script>
  export default {
    created() {
      this.getBlogList();
    },
    data() {
      return {
        list: [],
        total: 0,
        currentPage: 1,  //默认第一页
        pageSize: 5,  //默认展示10条数据
        perpage: [
          { value: 5, label: "5" },
          {value: 10, label: "10"},
          {value: 20, label: "20"}
        ],
      }
    },
    filters: {
      omitted(value) {
        if (value === '') return ''
        if (value.length > 200) {
          return value.slice(0, 200) + ' ......'
        }
        return value
      },
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
      async getBlogList() {
        this.axios({
          method: 'post',
          url: '/api/blog/list',
          headers: {
            "Content-Type": "application/json; charset=UTF-8",
            "auth": "Bearer "+ localStorage.getItem("auth")
          },
          withCredentials: true,
          data: JSON.stringify({
            "currentPage": this.currentPage,
            "pageSize": this.pageSize,
          })
        }).then(successResponse => {
          //处理成功
          if (successResponse.data.code === 200) {
            let token = successResponse.headers.auth;
            if (token != null && token != '') {
              localStorage.setItem("auth", token)
            }
            this.list = successResponse.data.data.list || [];
            this.total = successResponse.data.data.pagination.total;
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
      openArticle(articleId) {
        this.$router.push({
          path: '/quark/blog/view',
          query: {
            id: articleId
          }
        });
      },
      handleSizeChange(val) {
        //重置当前页为第一页
        this.pageSize = val
        this.currentPage = 1
        // 重新加载页面数据
        this.getBlogList() 
      },
      handleCurrentChange(val) {
        //点击改变当前页
        this.currentPage = val
        // 重新加载页面数据
        this.getBlogList()
      },
    }
  }
</script>
<style>
  .blogList {
    color: rgb(203 220 222);
    margin: auto;
    vertical-align: middle;
  }

  .box-card {
    background-color: #FFFFFF;
    width: 100%;
    margin-block: auto;
    margin-top: 3px;
    display: block;
    text-align: left;
  }

  .box-card .hover {
    border:1px thin blue;
  }

  .pagination {
    color: #FFFFFF;
    margin: auto;
    width: 90%;
    display: flex;
    justify-content: space-around;
  }

  ul li {
    list-style: none;
  }
</style>
