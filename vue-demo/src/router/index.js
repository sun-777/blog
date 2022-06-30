import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/components/Index'
import Login from '@/components/Login'
import Register from '@/components/Register'
import Quark from '@/components/Quark'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Index',
      component: Index,
      meta: { title: 'Quark' }
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { title: '注册' }
    },
    {
      path: '/quark',
      name: 'Quark',
      component: Quark,
      meta: { title: '主页' },
      children: [
        {
          path: '/quark/blog/list',
          // 动态加载页面
          component: resolve => require(['@/view/BlogList.vue'], resolve),
          meta: {
            title: '博客内容'
          }
        },
        {
          path: '/quark/blog/new',
          component: resolve => require(['@/view/BlogNew.vue'], resolve),
          meta: {
            title: '博客内容'
          }
        },
        {
          path: '/quark/blog/view',
          component: resolve => require(['@/view/BlogView.vue'], resolve),
          meta: {
            title: '博客内容'
          }
        },
        {
          path: '/quark/category',
          component: resolve => require(['@/view/Category.vue'], resolve),
          meta: {
            title: '查看留言'
          }
        },
        {
          path: '/quark/comment',
          component: resolve => require(['@/view/Comment.vue'], resolve),
          meta: {
            title: '查看留言'
          }
        },
        {
          path: '/quark/profile',
          component: resolve => require(['@/view/Profile.vue'], resolve),
          meta: {
            title: '个人资料'
          }
        },
        {
          path: '/quark/log',
          component: resolve => require(['@/view/Log.vue'], resolve),
          meta: {
            title: '操作日志'
          }
        }
      ]
    }
  ]
});
