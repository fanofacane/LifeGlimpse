package com.sky.skybackend.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20180509.VideoAsyncScanRequest;
import com.aliyuncs.green.model.v20180509.VideoAsyncScanResultsRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云视频审核工具类（同步获取结果版）
 * 功能：传入视频URL，自动完成审核任务提交、轮询结果，最终返回结构化审核结果
 */
public class AliyunVideoAuditTool {

    // 阿里云客户端（单例，避免重复创建连接）
    private static IAcsClient acsClient;

    // 审核场景（可根据需求调整，如增加"ad"广告检测等）
    private static final List<String> DEFAULT_SCENES = Arrays.asList("porn", "terrorism");

    // 轮询配置
    private static final int MAX_RETRY = 30; // 最大轮询次数（默认30次）
    private static final int INTERVAL_SECONDS = 5; // 轮询间隔（秒，默认5秒）


    /**
     * 初始化工具类（必须先调用，建议项目启动时初始化）
     *
     * @param regionId        地域ID（如cn-shanghai、cn-beijing）
     * @param accessKeyId     RAM用户AccessKey ID
     * @param accessKeySecret RAM用户AccessKey Secret
     */
    public static void init(String regionId, String accessKeyId, String accessKeySecret) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            // 注册Green服务端点
            DefaultProfile.addEndpoint(regionId, "Green", "green." + regionId + ".aliyuncs.com");
            acsClient = new DefaultAcsClient(profile);
            System.out.println("阿里云视频审核工具初始化成功");
        } catch (Exception e) {
            throw new RuntimeException("阿里云视频审核工具初始化失败：" + e.getMessage(), e);
        }
    }


    /**
     * 核心方法：提交视频审核并获取结果
     *
     * @param videoUrl 公网可访问的视频HTTP/HTTPS URL
     * @return 审核结果（JSON格式，包含各场景检测结果）
     * @throws Exception 当审核失败或超时未返回结果时抛出
     */
    public static JSONObject auditVideo(String videoUrl) throws Exception {
        // 校验客户端是否初始化
        if (acsClient == null) {
            throw new RuntimeException("请先调用init()方法初始化工具类");
        }

        // 1. 提交审核任务，获取taskId
        String taskId = submitAuditTask(videoUrl);
        System.out.println("视频审核任务提交成功，taskId：" + taskId);

        // 2. 轮询获取审核结果
        return pollAuditResult(taskId);
    }


    /**
     * 提交审核任务
     */
    private static String submitAuditTask(String videoUrl) throws Exception {
        VideoAsyncScanRequest request = new VideoAsyncScanRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setMethod(com.aliyuncs.http.MethodType.POST);
        request.setConnectTimeout(3000); // 连接超时时间
        request.setReadTimeout(6000);    // 读取超时时间

        // 构建任务参数
        List<Map<String, Object>> tasks = new ArrayList<>();
        Map<String, Object> task = new LinkedHashMap<>();
        task.put("dataId", UUID.randomUUID().toString()); // 业务唯一标识（可选，用于关联本地业务）
        task.put("url", videoUrl); // 视频URL
        tasks.add(task);

        // 构建请求体
        JSONObject data = new JSONObject();
        data.put("scenes", DEFAULT_SCENES); // 审核场景
        data.put("tasks", tasks);
        data.put("seed", UUID.randomUUID().toString()); // 随机种子（可选）

        // 设置请求内容
        request.setHttpContent(
                data.toJSONString().getBytes("UTF-8"),
                "UTF-8",
                FormatType.JSON
        );

        // 发送请求
        HttpResponse response = acsClient.doAction(request);
        if (!response.isSuccess()) {
            throw new RuntimeException("提交审核任务失败，HTTP状态码：" + response.getStatus());
        }

        // 解析响应，获取taskId
        JSONObject respJson = JSON.parseObject(new String(response.getHttpContent(), "UTF-8"));
        if (respJson.getIntValue("code") != 200) {
            throw new RuntimeException("提交审核任务失败：" + respJson.getString("msg"));
        }

        JSONArray taskResults = respJson.getJSONArray("data");
        if (taskResults.isEmpty()) {
            throw new RuntimeException("提交审核任务后未返回taskId");
        }

        JSONObject taskResult = taskResults.getJSONObject(0);
        if (taskResult.getIntValue("code") != 200) {
            throw new RuntimeException("单个任务处理失败：" + taskResult.getString("msg"));
        }

        return taskResult.getString("taskId");
    }


    /**
     * 轮询获取审核结果
     */
    private static JSONObject pollAuditResult(String taskId) throws Exception {
        VideoAsyncScanResultsRequest request = new VideoAsyncScanResultsRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setMethod(com.aliyuncs.http.MethodType.POST);

        // 轮询逻辑
        for (int i = 0; i < MAX_RETRY; i++) {
            // 构建请求参数（查询指定taskId的结果）
            JSONObject data = new JSONObject();
            data.put("taskIds", Arrays.asList(taskId));
            request.setHttpContent(
                    data.toJSONString().getBytes("UTF-8"),
                    "UTF-8",
                    FormatType.JSON
            );

            // 发送查询请求
            HttpResponse response = acsClient.doAction(request);
            if (!response.isSuccess()) {
                System.out.println("第" + (i + 1) + "次轮询失败，HTTP状态码：" + response.getStatus());
                TimeUnit.SECONDS.sleep(INTERVAL_SECONDS);
                continue;
            }

            // 解析查询结果
            JSONObject respJson = JSON.parseObject(new String(response.getHttpContent(), "UTF-8"));
            if (respJson.getIntValue("code") != 200) {
                System.out.println("第" + (i + 1) + "次轮询响应异常：" + respJson.getString("msg"));
                TimeUnit.SECONDS.sleep(INTERVAL_SECONDS);
                continue;
            }

            JSONArray results = respJson.getJSONArray("data");
            if (results.isEmpty()) {
                System.out.println("第" + (i + 1) + "次轮询未获取到结果，继续等待...");
                TimeUnit.SECONDS.sleep(INTERVAL_SECONDS);
                continue;
            }

            JSONObject result = results.getJSONObject(0);
            String status = result.getString("status");
            switch (status) {
                case "PROCESSING":
                    // 任务处理中，继续轮询
                    System.out.println("第" + (i + 1) + "次轮询：任务处理中...");
                    TimeUnit.SECONDS.sleep(INTERVAL_SECONDS);
                    break;
                case "FINISHED":
                    // 任务完成，返回结果
                    System.out.println("审核完成，获取结果成功");
                    return result;
                default:
                    // 任务失败（如视频无法访问、格式不支持等）
                    throw new RuntimeException("审核任务失败，状态：" + status + "，原因：" + result.getString("msg"));
            }
        }

        // 超过最大轮询次数仍未完成
        throw new RuntimeException("审核超时，超过最大轮询次数（" + MAX_RETRY + "次）");
    }


    // ------------------------------ 测试示例 ------------------------------
    public static void main(String[] args) {
        try {
            // 1. 初始化（实际项目中建议在启动类初始化一次）
            String regionId = "cn-shanghai"; // 替换为你的地域
            String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"); // 从环境变量获取
            String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"); // 从环境变量获取
            AliyunVideoAuditTool.init(regionId, accessKeyId, accessKeySecret);

            // 2. 调用审核（传入视频URL）
            String videoUrl = "http://47.96.156.182:9000/origin/sky/63afda6939ba2466be19158c4c77f98c.mp4"; // 替换为你的视频URL
            JSONObject auditResult = AliyunVideoAuditTool.auditVideo(videoUrl);

            // 3. 打印结果（实际项目中根据业务解析）

            System.out.println("审核结果：" + JSON.toJSONString(auditResult, String.valueOf(true)));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("审核失败：" + e.getMessage());
        }
    }
}