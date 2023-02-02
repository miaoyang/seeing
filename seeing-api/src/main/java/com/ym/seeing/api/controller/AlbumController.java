package com.ym.seeing.api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ym.seeing.api.domain.Album;
import com.ym.seeing.api.domain.ImageAndAlbum;
import com.ym.seeing.api.domain.Images;
import com.ym.seeing.api.domain.vo.PageResultBean;
import com.ym.seeing.api.service.IAlbumService;
import com.ym.seeing.api.service.IUserService;
import com.ym.seeing.api.service.ImageAndAlbumService;
import com.ym.seeing.api.util.UserUtil;
import com.ym.seeing.core.domain.Msg;
import com.ym.seeing.core.domain.User;
import com.ym.seeing.core.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: Yangmiao
 * @Date: 2023/2/1 17:04
 * @Desc:
 */
@Controller
@Slf4j
public class AlbumController {
    @Autowired
    private IAlbumService albumService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ImageAndAlbumService imageAndAlbumService;

    @PostMapping("/admin/getGalleryList")
    @ResponseBody
    public Msg getGalleryList(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();

        User user = UserUtil.getUser();
        user = userService.getUsers(user);
        Album album = new Album();
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer pageNum = jsonObj.getInteger("pageNum");
        Integer pageSize = jsonObj.getInteger("pageSize");
        Integer albumtitle = jsonObj.getInteger("albumtitle");
        if (!UserUtil.hasAdmin()) {
            album.setUserId(user.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Album> albums = null;
        try {
            albums = albumService.selectAlbumURLList(album);
            PageInfo<Album> rolePageInfo = new PageInfo<>(albums);
            msg.setData(rolePageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setInfo("获取数据异常");
            msg.setCode("500");
        }
        return msg;
    }

    @PostMapping("/admin/deleGallery")
    @ResponseBody
    public Msg deleteGallery(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            JSONArray albumKeyList = jsonObject.getJSONArray("albumkeyList");
            for (int i = 0; i < albumKeyList.size(); i++) {
                if (subject.hasRole("admin")) {
                    albumService.deleteAlbum(albumKeyList.getString(i));
                } else {
                    Album album = new Album();
                    album.setAlbumKey(albumKeyList.getString(i));
                    final Album alb = albumService.selectAlbum(album);
                    if (alb.getUserId().equals(user.getId())) {
                        albumService.deleteAlbum(albumKeyList.getString(i));
                    }
                }
            }
            msg.setInfo("画廊已成功删除");
        } catch (Exception e) {
            msg.setCode("500");
            msg.setInfo("画廊删除失败");
            e.printStackTrace();
        }
        return msg;

    }

    /**
     * 后台列表要修改时的查询表单
     *
     * @param data
     * @return
     */
    @PostMapping("/admin/getAlbumListForKey")
    @ResponseBody
    public Msg getAlbumListForKey(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String key = jsonObject.getString("key");

            User user = UserUtil.getUser();
            Album album = new Album();
            album.setAlbumKey(key);
            if (UserUtil.hasAdmin()) {
                album.setUserId(null);
            } else {
                album.setUserId(user.getId());
            }
            JSONObject jsonObj = new JSONObject();
            Album album1 = albumService.selectAlbum(album);
            List<Images> imagesList = imageAndAlbumService.selectImgByAlbumKey(key);
            jsonObj.put("album", album1);
            jsonObj.put("imagesList", imagesList);
            msg.setData(jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
            msg.setInfo("获取画廊数据错误");
        }
        return msg;
    }

    @PostMapping("/getAlbumImgList")
    @ResponseBody
    public Msg getAlbumImgList(@RequestParam("data") String data) {
        Msg msg = new Msg();
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray("imguidlist");
        JSONArray json = albumService.getAlbumList(jsonArray);
        msg.setData(json);
        return msg;
    }

    @PostMapping("/SaveForAlbum")
    @ResponseBody
    public Msg saveForAlbum(@RequestParam(value = "data", defaultValue = "") String data) {
        data = StringEscapeUtils.unescapeHtml4(data);
        Msg msg = new Msg();
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String albumtitle = jsonObject.getString("albumtitle");
            String password = jsonObject.getString("password");
            JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("albumlist"));
            if (null != password) {
                password = password.replace(" ", "");
                if (password.replace(" ", "").equals("") || password.length() < 3) {
                    msg.setCode("110403");
                    msg.setInfo("密码长度不得小于三位有效字符");
                    return msg;
                }
            }
            if (albumtitle == null || jsonArray.isEmpty()) {
                msg.setCode("110404");
                msg.setInfo("标题和图片参数不能为空");
                return msg;
            }
            Subject subject = SecurityUtils.getSubject();
            User u = (User) subject.getPrincipal();
            String uuid = "TOALBUM" + UUID.randomUUID().toString().replace("-", "").toLowerCase().substring(0, 7) + "N";

            Album album = new Album();
            album.setAlbumTitle(albumtitle);
            album.setCreateDate(DateUtil.dateToStr(new Date()));
            album.setPassword(password);
            album.setAlbumKey(uuid);
            if (u == null) {
                album.setUserId(0);
            } else {
                album.setUserId(u.getId());
            }
            albumService.addAlbum(album);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject img = jsonArray.getJSONObject(i);
                ImageAndAlbum imgAndAlbum = new ImageAndAlbum();
                imgAndAlbum.setImgName(img.getString("imgurl"));
                imgAndAlbum.setAlbumKey(uuid);
                imgAndAlbum.setNotes(img.getString("notes"));
                albumService.addAlbumByImgAndAlbum(imgAndAlbum);
            }
            final JSONObject json = new JSONObject();
            json.put("url", uuid);
            json.put("title", albumtitle);
            json.put("password", password);
            msg.setCode("200");
            msg.setInfo("成功创建画廊链接");
            msg.setData(json);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
            msg.setInfo("创建画廊链接失败");
        }
        return msg;
    }


    @PostMapping("/admin/UpdateForAlbum")
    @ResponseBody
    public Msg updateForAlbum(@RequestParam(value = "data", defaultValue = "") String data) {
        data = StringEscapeUtils.unescapeHtml4(data);
        Msg msg = new Msg();
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String albumkey = jsonObject.getString("albumkey");
            String albumtitle = jsonObject.getString("albumtitle");
            String password = jsonObject.getString("password");
            JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("albumlist"));
            if (null != password) {
                password = password.replace(" ", "");
                if (password.replace(" ", "").equals("") || password.length() < 3) {
                    msg.setCode("110403");
                    msg.setInfo("密码长度不得小于三位有效字符");
                    return msg;
                }
            }
            if (albumtitle == null || jsonArray.isEmpty()) {
                msg.setCode("110404");
                msg.setInfo("标题和图片参数不能为空");
                return msg;
            }

            Album album = new Album();
            album.setAlbumTitle(albumtitle);
            album.setPassword(password);
            album.setAlbumKey(albumkey);

            albumService.updateAlbum(album);
            imageAndAlbumService.deleteImgAndAlbumByKey(albumkey);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject img = jsonArray.getJSONObject(i);
                ImageAndAlbum imgAndAlbum = new ImageAndAlbum();
                imgAndAlbum.setImgName(img.getString("imgurl"));
                imgAndAlbum.setAlbumKey(albumkey);
                imgAndAlbum.setNotes(img.getString("notes"));
                albumService.addAlbumByImgAndAlbum(imgAndAlbum);
            }
            final JSONObject json = new JSONObject();
            json.put("url", albumkey);
            json.put("title", albumtitle);
            json.put("password", password);
            msg.setCode("200");
            msg.setInfo("画廊修改成功");
            msg.setData(json);
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("500");
            msg.setInfo("修改画廊失败");
        }
        return msg;
    }


    @PostMapping("/checkPass")
    @ResponseBody
    public Msg checkPass(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject json = new JSONObject();
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(data);
            String key = jsonObject.getString("key");
            Album album = new Album();
            album.setAlbumKey(key);
            Album a = albumService.selectAlbum(album);
            if (a != null) {
                json.put("album", a);
                json.put("exist", true);
                if (a.getPassword() != null && !a.getPassword().equals("")) {
                    json.put("passType", true);
                } else {
                    json.put("passType", false);
                }
                msg.setData(json);
            } else {
                json.put("exist", false);
                msg.setData(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCode("110500");
            msg.setInfo("操作失败");
            msg.setData(json);
        }
        return msg;
    }

    @PostMapping("/getAlbumList")
    @ResponseBody
    public Msg getAlbumList(@RequestParam(value = "data", defaultValue = "") String data) {
        Msg msg = new Msg();
        JSONObject json = new JSONObject();
        JSONObject jsonObject = JSONObject.parseObject(data);
        Integer pageNum = jsonObject.getInteger("pageNum");
        Integer pageSize = jsonObject.getInteger("pageSize");
        String albumkey = jsonObject.getString("albumkey");
        String password = jsonObject.getString("password");
        if (null != password) {
            password = password.replace(" ", "");
        }
        Album album = new Album();
        album.setAlbumKey(albumkey);
        Album a = albumService.selectAlbum(album);
        if (a == null) {
            msg.setCode("110404");
            msg.setInfo("画廊地址不存在");
        } else {
            PageHelper.startPage(pageNum, pageSize);
            if (a.getPassword() == null || (a.getPassword().replace(" ", "")).equals("")) {
                List<Images> imagesList = imageAndAlbumService.selectImgByAlbumKey(albumkey);
                PageInfo<Images> rolePageInfo = new PageInfo<>(imagesList);
                PageResultBean<Images> pageResultBean = new PageResultBean<>(rolePageInfo.getTotal(), rolePageInfo.getList());
                json.put("imagesList", pageResultBean);
            } else {
                if (a.getPassword().equals(password)) {
                    List<Images> imagesList = imageAndAlbumService.selectImgByAlbumKey(albumkey);
                    PageInfo<Images> rolePageInfo = new PageInfo<>(imagesList);
                    PageResultBean<Images> pageResultBean = new PageResultBean<>(rolePageInfo.getTotal(), rolePageInfo.getList());
                    json.put("imagesList", pageResultBean);
                } else {
                    msg.setCode("110403");
                    msg.setInfo("画廊密码错误");
                }
            }
            json.put("titlename", a.getAlbumTitle());
            msg.setData(json);
        }
        return msg;
    }

}
