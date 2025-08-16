package com.sky.skybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.skybackend.domain.pojo.Message;
import com.sky.skybackend.domain.vo.ChatUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


import java.util.List;

@Mapper
public interface ChatMapper extends BaseMapper<Message> {

    List<Integer> getChatHistoryIds(Integer userId);

    List<ChatUserVO> getChatUsersWithFullDetails(List<Integer> chatUserIds, Integer currentUserId);
    @Select("select * from message where (`from` = #{targetId} AND `to` = #{currentId}) or (`from` = #{currentId} AND `to` = #{targetId}) ORDER BY create_time ASC")
    List<Message> getChatMessageList(Integer currentId, Integer targetId);

    @Update("update message set is_read = 1 where `from`=#{id} and `to` =#{currentId} and is_read= 0 ")
    void clearUnread(Integer id,Integer currentId);
}
