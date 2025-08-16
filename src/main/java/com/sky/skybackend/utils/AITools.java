package com.sky.skybackend.utils;

import com.sky.skybackend.domain.vo.VideoVO;
import com.sky.skybackend.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AITools {
    @Autowired
    private VideoService videoService;
    @Tool(description = "当用户要查询帖子和作品时使用这个方法")
    public String questionSearch(String searchText) {
        try {
            List<VideoVO> vos = videoService.getMatchVideo(searchText, null);
                    int i = 0;
                    String text="";
                    for (VideoVO videoVO : vos) {
                        if (i++ >= 5) {
                            break;
                        }
                        String title = videoVO.getTitle();
                        Integer id = videoVO.getId();
                        String link = String.format("visitor/%s", videoVO.getUserId());
                        String mediaUrl=videoVO.getAvatar();
                            text+="<a href ='"+link+"' style='margin-right: 95px;text-decoration: none;'>"+title+"</a>"+"<video src='"+mediaUrl+"' controls style='width:150px;height:150px;object-fit:cover;'></video>";
                    }
                    System.out.println(text);
                    return text;
                }catch (Exception e){
                    return "没有找到结果";
        }
        }
}
