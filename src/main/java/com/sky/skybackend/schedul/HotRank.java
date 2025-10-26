package com.sky.skybackend.schedul;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.skybackend.domain.pojo.Video;
import com.sky.skybackend.domain.vo.HotVideo;
import com.sky.skybackend.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.sky.skybackend.Enum.ActionType.countGetters;
import static com.sky.skybackend.Enum.Constant.*;

@Slf4j
@Component
public class HotRank {
    
    @Autowired
    private VideoMapper videoMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每小时执行一次热度排行榜计算
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hotRank() {
        log.info("开始执行热度排行榜计算");
        
        try {
            // 使用最小堆维护Top10热度视频
            PriorityQueue<HotVideo> minHeap = new PriorityQueue<>(Comparator.comparing(HotVideo::getHot));
            TopK topK = new TopK(10, minHeap);
            
            // 分批处理视频数据
            int offset = 0;
            List<Video> videos;
            do {
                // 分批查询视频数据
                LambdaQueryWrapper<Video> queryWrapper=new LambdaQueryWrapper<>(Video.class)
                        .select(Video::getId, Video::getTitle, Video::getShareCount,
                                Video::getWatchCount, Video::getLikeCount, Video::getCollectCount,
                                Video::getCommentCount,Video::getCreateTime)
                        .last("LIMIT " + BATCH_SIZE + " OFFSET " + offset);

                videos = videoMapper.selectList(queryWrapper);

                // 计算每个视频的热度
                for (Video video : videos) {
                    double hotScore = calculateHotScore(video);
                    HotVideo hotVideo = new HotVideo(hotScore, video.getId(), video.getTitle());
                    topK.add(hotVideo);
                }
                
                offset += BATCH_SIZE;
                
            } while (videos.size() == BATCH_SIZE);

            System.out.println("offset:"+ offset);
            // 获取Top10热度视频
            List<HotVideo> topVideos = topK.get();
            System.out.println("Top10热度视频："+topVideos);
            // 清除旧的Redis数据
            redisTemplate.delete(REDIS_HOT_RANK_KEY);
            
            // 批量写入Redis有序集合
            if (!topVideos.isEmpty()) {

                // 构造写入Redis的数据
                for (HotVideo hotVideo : topVideos) {
                    // 将视频对象转换为JSON字符串（不包含热度字段）
                    HotVideo video = new HotVideo();
                    video.setVideoId(hotVideo.getVideoId());
                    video.setTitle(hotVideo.getTitle());

                    String videoJson = JSON.toJSONString(video);
                    redisTemplate.opsForZSet().add(REDIS_HOT_RANK_KEY, videoJson, hotVideo.getHot());
                }
            }
            
            log.info("热度排行榜计算完成，共处理 {} 个视频", topVideos.size());
        } catch (Exception e) {
            log.error("热度排行榜计算失败", e);
        }
    }
    /**
     * 热门视频
     * 每个3小时执行
     */
    @Scheduled(cron = "0 0 */3 * * ?")
    public void hotVideo(){
        int id;
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(3);
        List<Integer> hotVideos = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DATE);
        List<Video> videos = getVideo(SEARCH_DEFAULT_VIDEO_ID, dayAgo, SEARCH_VIDEOS_LIMIT);
        //分页查询LIMIT:1000
        while (!videos.isEmpty()){
            for (Video video : videos) {
                double score = calculateHotScore(video);
                if (score > DEFAULT_HOT_VIDEO_SCORE) hotVideos.add(video.getId());
            }
            id = videos.getLast().getId();
            videos = getVideo(id, dayAgo, SEARCH_VIDEOS_LIMIT);
        }
        //set存储便于后续取出
        if (!hotVideos.isEmpty()){
            String key =HOT_VIDEO+today;
            redisTemplate.opsForSet().add(key, hotVideos.toArray(new Object[0]));
        }
    }
    /**
     * @param videoId 视频ID
     * @param dayAgo
     * @param limit 数量
     * 获取视频
     */
    public List<Video> getVideo(Integer videoId,LocalDateTime dayAgo,Integer limit){
        LambdaQueryWrapper<Video> lambdaQuery =new LambdaQueryWrapper<>();
        lambdaQuery.select(Video::getId, Video::getShareCount, Video::getWatchCount,
                        Video::getLikeCount, Video::getCollectCount,
                        Video::getCommentCount,Video::getCreateTime)
                .gt(Video::getId,videoId)
                .ge(Video::getCreateTime,dayAgo)
                .last("LIMIT "+limit);
        return videoMapper.selectList(lambdaQuery);
    }
    /**
     * 计算视频热度分数
     * @param video 视频对象
     * @return 热度分数
     */
    private double calculateHotScore(Video video) {
        // 基础权重计算
        double score = countGetters.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().apply(video) * entry.getKey().getWeight())
                .sum();

        // 时间衰减因子
        LocalDateTime now = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(video.getCreateTime(), now);
        double timeDecay = Math.exp(-0.05 * hours); // 指数衰减
        
        // 随机因子
        double randomFactor = new Random().nextDouble() * 0.01 + 0.995; // 0.995-1.005 (±0.5%)

        // 综合计算热度分数
        return score * timeDecay * randomFactor;
    }
}
