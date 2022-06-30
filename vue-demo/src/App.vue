<template>
  <div id="app">
    <router-view />
  </div>
</template>

<!-- import JavaScript -->
<script src="https://unpkg.com/vue@2.6.14/dist/vue.min.js"></script>
<script src="https://unpkg.com/axios@0.24.0/dist/axios.min.js"></script>
<script src="https://unpkg.com/element-ui@2.15.6/lib/index.js"></script>
<script src="https://cdn.jsdelivr.net/npm/wangeditor@3.1.1/dist/wangEditor.min.js"></script>
<script>



  export default {
    name: 'App',
  }

</script>

<style>
  #app {
    height: 100%;
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    text-align: center;
  }
</style>
