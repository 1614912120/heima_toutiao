package aliyun;
 
import com.alibaba.fastjson.JSONArray;
import com.aliyun.imageaudit20191230.*;
import com.aliyun.imageaudit20191230.models.ScanTextRequest;
import com.aliyun.imageaudit20191230.models.ScanTextResponse;
import com.aliyun.imageaudit20191230.models.ScanTextResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
 
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
 
 
import java.util.*;
 
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class GreenTextScan {
 
    private String accessKeyId;
    private String secret;
 
    public Map greeTextScan(String content) throws Exception {
        /*
          初始化配置对象com.aliyun.teaopenapi.models.Config
          Config对象存放 AccessKeyId、AccessKeySecret、endpoint等配置
         */
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(secret);
        // 访问的域名
        config.endpoint = "imageaudit.cn-shanghai.aliyuncs.com";
        Client client = new Client(config);
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels0 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("spam");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels1 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("politics");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels2 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("abuse");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels3 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("terrorism");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels4 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("porn");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels5 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("flood");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels6 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("contraband");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels labels7 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestLabels()
                .setLabel("ad");
        com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestTasks tasks0 = new com.aliyun.imageaudit20191230.models.ScanTextRequest.ScanTextRequestTasks()
                .setContent(content);
        com.aliyun.imageaudit20191230.models.ScanTextRequest scanTextRequest = new com.aliyun.imageaudit20191230.models.ScanTextRequest()
                .setTasks(java.util.Arrays.asList(
                        tasks0
                ))
                .setLabels(java.util.Arrays.asList(
                        labels0,
                        labels1,
                        labels2,
                        labels3,
                        labels4,
                        labels5,
                        labels6,
                        labels7
                ));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        Map<String, String> resultMap = new HashMap<>();
        try {
            // 复制代码运行请自行打印API的返回值
            ScanTextResponse response = client.scanTextWithOptions(scanTextRequest, runtime);

            //System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(response)));
            if (response.getStatusCode() == 200) {
                String str ="";

                List<ScanTextResponseBody.ScanTextResponseBodyDataElements> elements = response.body.getData().elements;
                List<ScanTextResponseBody.ScanTextResponseBodyDataElementsResults> results = elements.get(0).results;
                List<ScanTextResponseBody.ScanTextResponseBodyDataElementsResultsDetails> details = results.get(0).getDetails();
                if(details != null) {
                    for (ScanTextResponseBody.ScanTextResponseBodyDataElementsResultsDetails detail : details) {
                        String context = detail.contexts.get(0).getContext();
                        str+= context;
                    }
                }

//                for (ScanTextResponseBody.ScanTextResponseBodyDataElements element : elements) {
//                    String context = element.results.get(0).details.get(0).contexts.get(0).getContext();
//                    str+= context;
//                }
                String suggestion = response.body.getData().elements.get(0).results.get(0).suggestion;
//
                resultMap.put("data", str);
                resultMap.put("suggestion", suggestion);

                return resultMap;
            } else {
                return null;
            }
 
 
        } catch (TeaException error) {
            // 获取整体报错信息
            System.out.println(com.aliyun.teautil.Common.toJSONString(error));
            // 获取单个字段
            System.out.println(error.getCode());
            error.printStackTrace();
        }
        return null;
    }
}