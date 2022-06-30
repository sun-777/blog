
## 一、已经实现的功能

### 1、用户注册、登录

​        登录后，使用JWT生成token，进行页面操作时，通过拦截器验证 Token，每次操作刷新Token。

### 2、博客内容的新建、显示、修改

**①、保存富文本内容：**

​        富文本内容（HTML文本格式）存入数据库时；将<img>标签src属性进行调整为图片文件名，然后存入数据库表中。

**②、读取数据库中富文本内容（HTML文本）**

​        读取富文本内容到前端时，将<img>标签src属性调整为图片文件在FTP文件服务器中的http url链接；从而保证前端页面可正确访问图片资源。

**③、文件服务器可通过application.yml中自定义属性配置**

​        “ftp-proxy-base-url”配置的改变需要相应的调整Nginx代理设置。




## 二、业务相关说明

### 1、JSch

​        使用JSch，通过SFTP协议，实现图片文件资源在文件服务器端的上传、移动、删除等操作；下载功能接口未使用，因为是通过Nginx反向代理实现HTTP访问FTP文件资源。

### 2、MyBatis

* Mapper接口使用了公共的BasicProvider、BatchProvider接口，实体类Mapper接口只需要按约定配置后，基本的CURD操作不用额外写SQL。
* Mapper接口需要提供一个BASIC_RESULT_MAP，作用是映射实体类与数据库表中的列名字段，当表字段跟随业务发生变化时，只需要修改BASIC_RESULT_MAP映射即可，无需变动Mapper接口类。

### 3、SpringBoot框架

* 实体类使用了自定义的@MappingTable注解（将实体类与数据库表名绑定），在启动类上配置注解@MappingTableScan，实现自动扫描使用了注解@MappingTable的类。
* 通过监听ContextRefreshedEvent事件，执行配置初始化。

###  4、前后端分离，部署时前端静态文件整合到后端

​        由于前端使用了动态路由，故在整合后使用了过滤器UrlFilter，对动态路由部分的请求URL做了相应处理，以便正确请求数据。




## 三、基础配置（文件服务器以centos或redhat为例）

​        博客内容是以HTML文本格存储到数据库中的。当把前端页面内容（HTML文本格式）持久化到后台时，会将图片资源单独上传到FTP文件服务器（通过JSch登陆文件服务器使用SFTP协议上传图片资源）。当从数据库读取HTML文本内容时，其中的图片资源会通过HTTP服务从FTP服务器中访问。

​        要实现以上需求，需要在文件服务器上安装、配置SSH、vsftpd、Nginx。

### **1、配置防火墙**

```shell
// 开放SSH服务22号端口；FTP服务20、21号端口；vsftpd被动模式的下限、上限端口号（指定为51719-51739）
# firewall-cmd --zone=public --add-port={20-22, 51719-51739}/tcp --permanent
// 重启防火墙，使添加的规则生效
# firewall-cmd --reload
```

### **2、安装ssh服务器、配置并重启ssh服务**

```shell
# yum install openssh-server
# vi /etc/ssh/sshd_config
// 修改相应的属性配置为：
//               PermitRootLogin yes 
//               PasswordAuthentication yes
# systemctl restart sshd
```

### **3、搭建FTP服务器**

- #### 确认是否安装vsftpd，没有则安装

  ```shell
  # rpm -qa | grep vsftpd
  # yum install vsftpd
  ```

- #### 配置vsftpd

  ```shell
  #vi /etc/vsftpd/vsftpd.conf
  // 修改相应的属性配置为：
  //        #允许匿名用户访问
  //        anonymous_enable=YES
  //        #启用被动模式，设置侦听端口号51719 ~ 51739，并注释配置connect_from_port_20=YES
  //        pasv_enable=YES
  //        pasv_min_port=51719
  //        pasv_max_port=51739
  //        #指定根目录
  //        anon_root=/opt/blog/upload
  //        #不允许上传
  //        anon_upload_enable=NO
  //        #只读模式
  //        anon_world_readable_only=YES
  ```

- #### 设置文件服务器hostname

  ```shell
  // 向hostname文件中写入主机名
  # echo “centos” >/etc/hostname
  // 查看主机名是否正确
  # cat /etc/hostname
  ```

- #### 设置服务器ip和hostname映射

  ```shell
  // 追加写入映射信息到hosts文件（根据实际ip配置）
  # echo "192.168.1.30  centos" >> /etc/hosts
  ```

- 重启FTP服务

  ```shell
  # systemctl restart vsftpd
  ```

### **4、安装、配置Nginx**

- 安装Nginx

  ```shell
  # yum install nginx
  # systemctl enable nginx
  # systemctl start nginx
  ```

- 配置Nginx

  ```shell
  # vi /etc/nginx/nginx.conf
  // 配置HTTP服务访问FTP服务器（/opt/blog/upload/为vsftpd根目录）内容如下
  server {
       listen     80;
       listen     [::]:80;
       server_name  localhost;    
       location /upload/ {
            alias /opt/blog/upload/;
            autoindex on;
       }
  }
  ```

- 重新加载Nginx配置

  ```
  # systemctl restart nginx
  ```
