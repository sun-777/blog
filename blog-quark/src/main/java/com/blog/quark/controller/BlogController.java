package com.blog.quark.controller;

import com.blog.quark.common.Result;
import com.blog.quark.common.util.JwtUtil;
import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.context.resultmap.BaseResultMap;
import com.blog.quark.entity.Article;
import com.blog.quark.entity.Content;
import com.blog.quark.entity.User;
import com.blog.quark.entity.column.ColumnInfo;
import com.blog.quark.entity.field.EntityField;
import com.blog.quark.id.generator.IdGenerators;
import com.blog.quark.service.BlogService;
import com.blog.quark.service.FileServerService;
import com.blog.quark.service.UserQueryService;
import com.blog.quark.vo.BlogListVo;
import com.blog.quark.vo.BlogListVo.BlogOutline;
import com.blog.quark.vo.BlogListVo.Pagination;
import com.blog.quark.vo.BlogViewVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.blog.quark.common.util.Constant.FILE_SEPARATOR;

@RequestMapping("/blog")
@RestController
public class BlogController implements BaseController {
    @Resource
    private BlogService blogService;
    @Resource
    private UserQueryService userQueryService;
    
    // 当富文本编辑器中有图片时，需要将图片在文件服务器中的地址变换处理
    @Resource
    private FileServerService uploadService;
    
    private static final Pattern IMAGE_REGEX = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMAGE_SRC_REGEX = Pattern.compile("src\\s*=\\s*['\"](.+?)['\"]", Pattern.CASE_INSENSITIVE);
    
    
    
    @PostMapping("/add")
    public Result<?> addBlog(@RequestBody Map<String, String> map){
        String title = map.get("title");
        String description = map.get("description");
        String content = map.get("content");
        
        // 从请求头中获取当前UserId
        Long currentUserId = (Long) Objects.requireNonNull(BaseController.getFromToken()).get(JwtUtil.CLAIM_KEY_USERID);
        
        // 移除<img>标签中src属性的http url，变更为文件名
        Set<String> imageSrcPropertySet = new HashSet<>();
        content = removeImageHttpRef(content, imageSrcPropertySet);
        
        LocalDateTime current = LocalDateTime.now();
        Article article = new Article(IdGenerators.getId(),
                title,
                currentUserId,
                description,
                null,
                current);
        article.setContent(new Content(IdGenerators.getId(), content, current));
        try {
            blogService.add(article);
            
            // 成功写入数据库，则移动富文本编辑器中上传的图片到有日期层级的目录

            // 此处不需要等待返回结果，线程后台在文件服务器上执行移动操作
            imageSrcPropertySet.forEach(name -> uploadService.move(GlobalConfig.getUploadImageFullPath(name, false),
                    GlobalConfig.getUploadImageFullPath(name),
                    new CountDownLatch(1)));
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error().setMessage("保存博客内容失败");
    }
    
    
    @PostMapping("/list")
    public Result<?> listBlog(@RequestBody Pagination pagination){
        BlogListVo blogListVo = new BlogListVo();
        //获取记录总数
        Long total = blogService.count();
        
        Long limit = pagination.getPageSize();
        Long offset = pagination.getCurrentPage();
        pagination.setTotal(total);
        blogListVo.setPagination(pagination);
        
        if (null != limit && null != offset && limit >= 0 && offset > 0) {
            offset = (offset - 1) * limit;
            ColumnInfo createTimeColumnInfo = BaseResultMap.getColumnInfo(EntityField.getQualifiedFieldName(Article::getCreateTime));
            // 获取分页的信息，根据createTime对应的列名来排序
            List<Article> articleList = blogService.get(offset, limit, createTimeColumnInfo.getColumn(), false);
            if (null == articleList || articleList.isEmpty()) {
                return Result.error(blogListVo);
            }
            
            // 根据author（即用户Id），查询用户信息
            List<Long> authorList = articleList.stream()
                    .map(Article::getAuthor)
                    .distinct()
                    .collect(Collectors.toUnmodifiableList());
            final Map<Long, User> userMap = userQueryService.get(authorList).stream().collect(Collectors.toUnmodifiableMap(User::getUserId, Function.identity()));
            
            // 组装前端页面需要的信息
            List<BlogOutline> list = articleList.stream().map(it -> new BlogOutline(it, userMap.get(it.getAuthor()).getNickname())).collect(Collectors.toUnmodifiableList());
            
            return Result.success(blogListVo.setList(list));
        }
        return Result.error(blogListVo);
    }
    
    
    @GetMapping("/view")
    public Result<?> getBlog(@RequestParam("articleId")Long articleId){
        // 从请求头中获取当前UserId
        Long currentUserId = (Long) Objects.requireNonNull(BaseController.getFromToken()).get(JwtUtil.CLAIM_KEY_USERID);
        
        Article article = blogService.get(articleId);
        if (null == article) {
            return Result.error("article not exists.");
        }
        
        Content content = article.getContent();
        String htmlContent = addImageHttpRef(content.getContent());
        content.setContent(htmlContent);
        
        final User user = userQueryService.get(article.getAuthor());
        return Result.success(new BlogViewVo(article, user.getNickname(), user.getUserId().equals(currentUserId)));
    }
    
    
    @PostMapping("/edit")
    public Result<?> updateBlog(@RequestBody Map<String, String> map){
        // 从请求头中获取当前UserId
        Long currentUserId = (Long) Objects.requireNonNull(BaseController.getFromToken()).get(JwtUtil.CLAIM_KEY_USERID);
        Long articleId = Long.parseLong(map.get("articleId"));
        Article article = blogService.get(articleId);
        
        String errorMsg = "修改博客内容失败";
        // 验证文章作者和当前用户是否一致。一致才可修改文章
        if (null != article) {
            if (currentUserId.equals(article.getAuthor())) {
                Long contentId = Long.parseLong(map.get("contentId"));
                String title = map.get("title");
                String description = map.get("description");
                String content = map.get("content");
    
                // 获取【当前更新的博客内容】中的<img>标签中的src属性的图片文件名
                final Set<String> currentSet = new HashSet<>();
                content = removeImageHttpRef(content, currentSet);
                // 获取【更新前的博客内容】中的<img>标签中的src属性的图片文件名
                Set<String> prevSet = new HashSet<>();
                addImageHttpRef(article.getContent().getContent(), prevSet);
    
                Article updateArticle = new Article(articleId,
                        title,
                        null,
                        description,
                        null,
                        null);
                updateArticle.setContent(new Content(contentId, content, LocalDateTime.now()));
                try {
                    blogService.update(updateArticle);
                    
                    // 成功将修改后的博客内容写入数据库后: 
                    //      1、提取已经删除的图片文件名，如果有，则从文件服务器中删除
                    List<String> deletedImgList = prevSet.stream().filter(it -> !currentSet.contains(it)).collect(Collectors.toUnmodifiableList());
                    deletedImgList.forEach(name -> uploadService.delete(GlobalConfig.getUploadImageFullPath(name), new CountDownLatch(1)));
                    //      2、提取新增的图片文件名，如果有，则移动新增加的图片到有日期层级的目录
                    List<String> newAddImgList = currentSet.stream().filter(it -> !prevSet.contains(it)).collect(Collectors.toUnmodifiableList());
                    newAddImgList.forEach(name -> uploadService.move(GlobalConfig.getUploadImageFullPath(name, false),
                            GlobalConfig.getUploadImageFullPath(name, true),
                            new CountDownLatch(1)));
                    
                    return Result.success().setMessage("已保存修改的文章内容");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                errorMsg = "当前登录用户非博客原作者，修改失败";
            }
        } else {
            errorMsg = "需要更新的博客不存在";
        }
        return Result.error(errorMsg);
    }
    

    /**
     *  从数据库读取博客内容（HTML文本，它的img标签中src属性是图片的文件名）至前端页面时， 
     *  需要将img标签中src中的图片名转换为可访问的http url链接（通过Nginx反向代理访问FTP上的图片文件）；如果直接加载到前端页面，图片是无法正常显示的。
     * 
     * @param htmlContent   HTML格式文本
     * @return  返回调整img标签src属性后的HTML文本
     */
    public static String addImageHttpRef(final String htmlContent) {
        return regulateImgSrcProperty(htmlContent, null, true);
    }
    
    /**
     *  从数据库读取博客内容（HTML文本，它的img标签中src属性是图片的文件名）至前端页面时， 
     *  需要将img标签中src中的图片名转换为可访问的http url链接（通过Nginx反向代理访问FTP上的图片文件）；如果直接加载到前端页面，图片是无法正常显示的。
     * 
     * @param htmlContent   HTML格式文本
     * @param imgNameSet    获取img标签中src属性中的图片文件名
     * @return  返回调整img标签src属性后的HTML文本
     */
    public static String addImageHttpRef(final String htmlContent, final Set<String> imgNameSet) {
        return regulateImgSrcProperty(htmlContent, imgNameSet, true);
    }
    
    
    /**
     *  将前端的博客内容（HTML文本）写入数据库时， 将img标签中src属性的http url链接转换为图片文件名（增强可维护性）
     * 
     * @param htmlContent   HTML格式文本
     * @return  返回调整img标签src属性后的HTML文本
     */
    public static String removeImageHttpRef(final String htmlContent) {
        return removeImageHttpRef(htmlContent, null);
    }
    
    /**
     *  将前端的博客内容（HTML文本）写入数据库时， 将img标签中src属性的http url链接转换为图片文件名（增强可维护性）
     * 
     * @param htmlContent   HTML格式文本
     * @param imgNameSet  获取img标签中src属性中的图片文件名
     * @return  返回调整img标签src属性后的HTML文本
     * 
     *  使用示例：
     *      Set<String> imageSrcPropertySet = new HashSet<>();
            String content = removeImageHttpRef(content, imageSrcPropertySet);
     */
    public static String removeImageHttpRef(final String htmlContent, final Set<String> imgNameSet) {
        return regulateImgSrcProperty(htmlContent, imgNameSet, false);
    }
    
    
    
    /**
     * 调整HTML文本img标签中的src属性。
     * 
     * @param htmlContent   HTML格式文本
     * @param imgNameSet   保存HTML文本中所有img标签中src属性的文件名（非http url）
     * @param addBaseHttpUrl  是否添加
     *   博客内容存放的是HTML格式文本，其中img标签中src属性是文件名（非路径或http url）
     *      true: 表示从数据库读取博客内容（HTML文本）到前端； 此时需要将img标签的src属性中的图片文件名 转换为 可访问此图片的http url链接
     *      false: 表示从前端获取的博客内容（HTML文本）保存至数据库； 此时需要将img标签中src属性的图片http url链接 转换为 图片文件名
     * @return  返回调整img标签中src属性后的HTML文本
     */
    private static String regulateImgSrcProperty(final String htmlContent, final Set<String> imgNameSet, boolean addBaseHttpUrl) {
        StringBuilder sb = new StringBuilder(htmlContent.length() + 1024);
        MessageFormat mf = new MessageFormat("src=\"{0}\"");
        Matcher imgMatcher = IMAGE_REGEX.matcher(htmlContent);
        while (imgMatcher.find()) {
            // <img ...>标签数据
            String imgStr = imgMatcher.group();
            StringBuilder subBuilder = new StringBuilder(256);
            Matcher imgSrcPropertyMatcher = IMAGE_SRC_REGEX.matcher(imgStr);
            if (imgSrcPropertyMatcher.find()) {
                // <img ...>标签数据中的src属性
                String imageSrcProperty = imgSrcPropertyMatcher.group(1);
                if (addBaseHttpUrl) {
                    if (null != imgNameSet) {
                        imgNameSet.add(imageSrcProperty);
                    }
                    imageSrcProperty = GlobalConfig.getHttpRefByImageName(imageSrcProperty);
                } else {
                    imageSrcProperty = imageSrcProperty.substring(imageSrcProperty.lastIndexOf(FILE_SEPARATOR) + 1);
                    if (null != imgNameSet) {
                        imgNameSet.add(imageSrcProperty);
                    }
                }
                imageSrcProperty = mf.format(new Object[] {imageSrcProperty});
                // 修改<img ...>标签中的src属性内容
                imgSrcPropertyMatcher.appendReplacement(subBuilder, imageSrcProperty).appendTail(subBuilder);
            }
            imgMatcher.appendReplacement(sb, subBuilder.toString());
        }
        imgMatcher.appendTail(sb);
        return sb.toString();
    }
    
    
}
