<template>
  <div class="editor">
    <div id="e">
    </div>
  </div>
</template>

<script>
  import E from "wangeditor"
  export default {
    name: 'editor',
    data() {
      return {
        content: "",
        editor: null,
        info_: null
      }
    },
    model: {
      prop: 'desc',
      event: 'change'
    },
    watch: {
      isClear(val) {
        // console.log(val)
        if (val) {
          this.editor.txt.clear()
          // this.info_=null
        }
      },
      desc(value) {
        //console.log("desc",value)
        if (value != this.editor.txt.html()) {
          this.editor.txt.html(this.desc)
        }
      }
    },
    props: {
      desc: {
        type: String,
        default: ""
      },
      //业务中我们经常会有添加操作和编辑操作，添加操作时，我们需清除上一操作留下的缓存
      isClear: {
        type: Boolean,
        default: false
      }
    },

    methods: {
      initE() {
        this.editor = new E('#e')
        this.editor.config.placeholder = "请输入文章内容"
        this.editor.config.debug = true
        this.editor.config.onchangeTimeout = 1000 // 单位 ms
        this.editor.config.uploadImgShowBase64 = false;  // 配置上传图片为base64
        this.editor.config.withCredentials = true;  // withCredentials（跨域传递 cookie）
        this.editor.config.showLinkImg = false;   // 隐藏“网络图片”tab
        this.editor.config.uploadImgAccept = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
        this.editor.config.uploadImgMaxLength = 1
        this.editor.config.uploadImgTimeout = 3 * 60 * 1000; // 设置图片上传超时时间
        this.editor.config.pasteFilterStyle = false;  // 关闭粘贴样式的过滤
        this.editor.config.pasteIgnoreImg = true;  // 忽略粘贴内容中的图片
        this.editor.config.uploadFileName = 'file'
        this.editor.config.uploadImgServer = '/api/upload/image'; // 你的服务器地址
        this.editor.config.uploadImgHooks = {
          before: function (xhr, editor, files) {
            // 图片上传之前触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象，files 是选择的图片文件

            // 如果返回的结果是 {prevent: true, msg: 'xxxx'} 则表示用户放弃上传
            // return {
            //     prevent: true,
            //     msg: '放弃上传'
            // }
          },
          success: function (xhr, editor, result) {
            // 图片上传并返回结果，图片插入成功之后触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象，result 是服务器端返回的结果
          },
          fail: function (xhr, editor, result) {
            // 图片上传并返回结果，但图片插入错误时触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象，result 是服务器端返回的结果
          },
          error: function (xhr, editor) {
            // 图片上传出错时触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象
          },
          timeout: function (xhr, editor) {
            // 图片上传超时时触发
            // xhr 是 XMLHttpRequst 对象，editor 是编辑器对象
          },
          customInsert: function (insertImgFn, result) {
            result.data.forEach(item => { insertImgFn(item.url) })
            
          }
          //customInsert: function (insertImg, result, editor) {
            // 图片上传并返回结果，自定义插入图片的事件（而不是编辑器自动插入图片！！！）
            // insertImg 是插入图片的函数，editor 是编辑器对象，result 是服务器端返回的结果
            // result 必须是一个 JSON 格式字符串！！！否则报错
          //  let url = result.data[0].url;
          //  insertImg(url)
          //}
        }
        this.editor.config.onchange = (html) => {
          this.info_ = html // 绑定当前逐渐地值
          this.$emit('change', this.info_) // 将内容同步到父组件中
        }
        this.editor.config.menus = [
          'head',  // 标题
          'bold',  // 粗体
          'fontSize',  // 字号
          'fontName',  // 字体
          'italic',  // 斜体
          'underline',  // 下划线
          'strikeThrough',  // 删除线
          'indent',  //缩进
          'foreColor',  // 文字颜色
          'backColor',  // 背景颜色
          'link',  // 插入链接
          'list',  // 列表
          'justify',  // 对齐方式
          'quote',  // 引用
          'emoticon',  // 表情
          'image',  // 插入图片
          'table',  // 表格
          'code',  // 插入代码
          //'splitLine',  //分割线
          'undo',  // 撤销
          'redo'  // 重复
        ]
        // 自定义配置颜色（字体颜色、背景色）
        this.editor.config.colors = [
          '#000000',
          '#eeece0',
          '#1c487f',
          '#4d80bf',
          '#c24f4a',
          '#8baa4a',
          '#7b5ba1',
          '#46acc8',
          '#f9963b',
          '#ffffff'
        ]
        this.editor.config.emotions = [
          {
            title: 'emoji',  // tab 的标题
            type: 'emoji', // 'emoji' / 'image'
            // emoji 表情，content 是一个数组即可
            content: '😀 😃 😄 😁 😆 😅 😂 😊 😇 🙂 🙃 😉 😓 😪 😴 🙄 🤔 😬 🤐'.split(/\s/),
          }
        ]
        this.editor.create()
        // this.editor.txt.html(this.desc)
        //  this.editor.txt.html(this.desc)
      }
    },
    mounted() {
      this.initE();
    }
  }
</script>

<style scoped>
  .editor {
    background-color: rgb(255 255 255);
    color: #000000;
    text-align: left;
  }
</style>
