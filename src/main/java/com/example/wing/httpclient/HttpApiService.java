package com.example.wing.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by CodeGenerator on 2018/03/20.
 *
 * @author fang
 */
@Component
@Slf4j
public class HttpApiService {

    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    private RequestConfig config;

    private static int OK = 200;

    private static int CREATE_OK = 201;

    private static int ACCEPT_OK = 202;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 发送请求(表单提交)
     *
     * @param map 参数
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> postByForm(String url, MultiValueMap<String, String> map) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        log.info("keys:" + "。" + "请求参数打印：" + request.toString());
        return restTemplate.postForEntity(url, request, String.class);
    }


    /**
     * 发送请求(json提交)
     *
     * @param url
     * @param requestJson
     * @param bodyType
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> ResponseEntity<T> postByJson(String url, String requestJson, Class<T> bodyType) throws Exception {
        return postByJson(url, requestJson, bodyType, this.restTemplate);
    }

    /**
     * 发送请求(json提交)
     *
     * @param url
     * @param requestJson
     * @param bodyType
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> ResponseEntity<T> postByJson(String url, String requestJson, Class<T> bodyType, RestTemplate restTemplate) throws Exception {
        log.info("keys=," + "请求参数：" + "url = [" + url + "], requestJson = [" + requestJson + "], bodyType = [" + bodyType + "]");
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        return restTemplate.postForEntity(url, entity, bodyType);
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public ResponseEntity<String> postByJson(String url, HttpHeaders headers, String requestJson) throws Exception {
        log.info("keys=" + "," + "url = [" + url + "], headers = [" + headers + "], requestJson = [" + requestJson + "]");
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        return restTemplate.postForEntity(url, entity, String.class);
    }

    /**
     * 默认返回类型为String
     *
     * @param url
     * @param requestJson
     * @return
     * @throws Exception
     */
    public ResponseEntity<String> postByJson(String url, String requestJson) throws Exception {
        return postByJson(url, requestJson, String.class);
    }

    /**
     * 发送/获取 服务端数据(主要用于解决发送put,delete方法无返回值问题).
     *
     * @param url      绝对地址
     * @param method   请求方式
     * @param bodyType 返回类型
     * @param <T>      返回类型
     * @return 返回结果(响应体)
     */
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Class<T> bodyType, JSONObject params)
            throws Exception {
        log.info("keys=" +"," + "请求参数：" + "url = [" + url + "], method = [" + method + "], bodyType = [" + bodyType + "], params = [" + params + "]");
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);
        MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype(), Charset.forName("UTF-8"));
        // 请求体
        headers.setContentType(mediaType);
        String str = null;
        if (params != null && !params.isEmpty()) {
            str = params.toString();
        }
        // 发送请求
        HttpEntity<String> entity = new HttpEntity<>(str, headers);
        return restTemplate.exchange(url, method, entity, bodyType);
    }


    /**
     *  发送请求(url附带参数提交,头信息)
     * @param templateUrl 模板url（http://localhost:8080/api?param1={0}&param2={1}..）
     * @param headers
     * @param params
     * @return ResponseEntity<String>
     * @throws Exception
     */
    public ResponseEntity<String> postByParams(String templateUrl, HttpHeaders headers, Object... params) throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = getParamUrl(templateUrl, params);
        return restTemplate.postForEntity(url, entity, String.class);
    }


    /**
     * 发送请求(url附带参数提交)
     *
     * @param templateUrl 模板url（http://localhost:8080/api?param1={0}&param2={1}..）
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> postByParams(String templateUrl, Object... params) throws Exception {
        String url = getParamUrl(templateUrl, params);
        return restTemplate.postForEntity(url, null, String.class);
    }

    /**
     * 发送请求(url附带参数提交)
     *
     * @param templateUrl 模板url（http://localhost:8080/api?param1={0}&param2={1}..）
     * @param bodyType    返回值类型
     * @param params      参数
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> ResponseEntity<T> getByParams(String templateUrl, Class<T> bodyType, Object... params) throws Exception {
        log.info("keys=" +  "," + "请求参数" + "templateUrl = [" + templateUrl + "], bodyType = [" + bodyType + "], params = [" + Arrays.toString(params) + "]");
        String url = getParamUrl(templateUrl, params);
        return restTemplate.getForEntity(url, bodyType);
    }

    public String getParamUrl(String templateUrl, Object[] params) {
        String url = templateUrl;
        if (params != null && params.length > 0) {
            url = MessageFormat.format(templateUrl, params);
        }
        return url;
    }

    /**
     * @param templateUrl 模板url（http://localhost:8080/api?param1={0}&param2={1}）url已带参数
     * @param bodyType    返回值类型
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> ResponseEntity<T> getByParams(String templateUrl, Class<T> bodyType, HttpHeaders headers, String... params) throws Exception {
        log.info("keys=" +  "," + "请求参数" + "templateUrl = [" + templateUrl + "], bodyType = [" + bodyType + "], headers = [" + headers + "], params = [" + params + "]");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = getParamUrl(templateUrl, params);
        return restTemplate.exchange(url, HttpMethod.GET, entity, bodyType);
    }

    /**
     * 发送请求(url附带参数提交)
     *
     * @param templateUrl 模板url（http://localhost:8080/api?param1={0}&param2={1}..）
     * @param params      参数
     * @return
     * @throws Exception
     */
    public ResponseEntity<String> getByParams(String templateUrl, String... params) throws Exception {
        return getByParams(templateUrl, String.class, params);
    }

    /**
     * @param templateUrl  模板url（http://localhost:8080/api?param1={0}&param2={1}）url已带参数
     * @param bodyType     返回值类型
     * @param restTemplate
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> ResponseEntity<T> getWithHeaders(String templateUrl, Class<T> bodyType, RestTemplate restTemplate) throws Exception {
        log.info("keys=" +"," + "请求参数" + "templateUrl = [" + templateUrl + "], bodyType = [" + bodyType + "]");
        HttpHeaders headers = getHttpHeaders();
        headers.setConnection("close");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(templateUrl, HttpMethod.GET, entity, bodyType);
    }

    /**
     * 校验请求是否成功(兼容标签系统)
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> boolean isValidRequest(ResponseEntity<T> entity) {
        boolean flag = entity != null && (entity.getStatusCodeValue() == OK || entity.getStatusCodeValue() == CREATE_OK || entity.getStatusCodeValue() == ACCEPT_OK);
        if (!flag) {
            log.error("请求异常：HttpStatus=[{}]", entity != null ? entity.getStatusCodeValue() : null);
        }
        return flag;
    }

    /**
     * 获取第三方接口返回的错误码
     *
     * @param entity
     * @return
     */
    public Integer getErrorCode(ResponseEntity<JSONObject> entity) {
        return entity != null ? entity.getBody().getJSONObject("data").getInteger("customCode") : -111111;
    }

    /**
     * 获取第三方接口返回的错误信息
     *
     * @param entity
     * @return
     */
    public String getErrorMsg(ResponseEntity<JSONObject> entity) {
        return entity != null ? entity.getBody().getJSONObject("data").getString("message") : "error";
    }

    /**
     * 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    public String doGet(String url) throws Exception {
        // 声明 http get 请求
        HttpGet httpGet = new HttpGet(url);

        // 装载配置信息
        httpGet.setConfig(config);

        // 发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpGet);

        // 判断状态码是否为200
        if (response.getStatusLine().getStatusCode() == OK) {
            // 返回响应体的内容
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        return null;
    }

    /**
     * 带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    public String doGet(String url, Map<String, Object> map) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);

        if (map != null) {
            // 遍历map,拼接请求参数
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        // 调用不带参数的get请求
        return this.doGet(uriBuilder.build().toString());

    }

    /**
     * 带参数的post请求
     *
     * @param url
     * @param map
     * @return
     * @throws Exception
     */
    public HttpResult doPost(String url, Map<String, Object> map) throws Exception {
        // 声明httpPost请求
        HttpPost httpPost = new HttpPost(url);
        // 加入配置信息
        httpPost.setConfig(config);

        // 判断map是否为空，不为空则进行遍历，封装from表单对象
        if (map != null) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 构造from表单对象
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "UTF-8");

            // 把表单放到post里
            httpPost.setEntity(urlEncodedFormEntity);
        }

        // 发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                response.getEntity(), "UTF-8"));
    }

    /**
     * 带jsonString参数的post请求,设置head
     *
     * @param url
     * @param
     * @return
     * @throws Exception
     */
    public HttpResult doPostWithHead(String url, String reqBody, Map<String, String> headMap) throws Exception {
        // 声明httpPost请求
        HttpPost httpPost = new HttpPost(url);
        if (headMap != null && headMap.size() > 0) {
            for (String key : headMap.keySet()) {
                httpPost.addHeader(key, headMap.get(key));
            }
        }
        // 加入配置信息
        httpPost.setConfig(config);
        //数据字符串放到post里
        httpPost.setEntity(new StringEntity(reqBody, "UTF-8"));

        // 发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                response.getEntity(), "UTF-8"));
    }


    public HttpResult postByFormData(String url, List<InputStream> inputStreams, String fileParName,
                                     Map<String, Object> params, Map<String, String> headMap,String imageName) throws Exception {

        HttpPost httpPost = new HttpPost(url);
        if (headMap != null && headMap.size() > 0) {
            for (String key : headMap.keySet()) {
                httpPost.addHeader(key, headMap.get(key));
            }
        }
        String boundaryStr="---"+UUID.randomUUID().toString();
        httpPost.addHeader("Content-Type", "multipart/form-data;boundary=" + boundaryStr);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setBoundary(boundaryStr);
        builder.setCharset(Charset.forName("UTF-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        InputStream inputStream = null;
        for (int i = 0; i < inputStreams.size(); i++) {
            inputStream = inputStreams.get(i);

            builder.addBinaryBody(fileParName, inputStream,ContentType.MULTIPART_FORM_DATA,imageName);
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            builder.addTextBody(entry.getKey(), entry.getValue().toString());

        }
        org.apache.http.HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        log.info("keys:" +"。" + "请求参数打印：" + httpPost.toString());

        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                response.getEntity(), "UTF-8"));
    }

}