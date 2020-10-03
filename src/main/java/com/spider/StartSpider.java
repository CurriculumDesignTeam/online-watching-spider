package com.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pojo.Movie;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JDK
 */
public class StartSpider {
    private static final String MOVIE_LABEL = "<span class=\"xing_vb4\">(.*?)</span>";
    private static final String MOVIE_URL = "/\\?m=vod-detail-id-(.*?).html";
    private static final String IMAGE_PATTERN = "<img class=\"lazy\" src=\"(.*?)\" ";
    private static final String NAME_PATTERN = "<h2>(.*?)</h2>";
    private static final String LANGUAGE_PATTERN = "语言：<span>(.*?)</span>";
    private static final String CATEGORY_PATTERN = "类型：<span>(.*?)[</span>|<a target]";
    private static final String SHOWING_PATTERN = "上映：<span>(.*?)</span>";
    private static final String INTRODUCE_PATTERN = "<span class=\"more\" txt=\".*\">(.*?)</span> ";
    private static final String PLAYURL_PATTERN = ".m3u8\" checked=\"\" />(.*?)</li>";
    private static final String ADMIN = "jdk";
    public static final Integer SUCCESS = 200;
    public static final Integer PAGES = 50;

    public static void main(String[] args) {
        String header = "http://zuidazy5.com/?m=vod-index-pg-";
        StartSpider spider = new StartSpider();
        for (int i=1;i<PAGES;i++){
            String url = header + i + ".html";
            spider.getHttp(url);
        }
    }

    public void getHttp(String url){
        try {
            String mainResult = getContent(url);
            List<String> urls = new ArrayList<String>();
            Matcher matcher = Pattern.compile(MOVIE_LABEL).matcher(mainResult);
            while (matcher.find()){
                Matcher matcher1 = Pattern.compile(MOVIE_URL).matcher(matcher.group());
                while (matcher1.find()){
                    urls.add("http://zuidazy5.com"+matcher1.group());
                }
            }
            for (String i : urls){
//                返回一个movie对象
                Movie movie = getElement(i);
                if ("伦理片".equals(movie.getCategory()) ||
                        "福利片".equals(movie.getCategory()) ){
                    continue;
                }
//                向影片表插入一行数据
                int movieId = insertMovie(movie);
//                向链接表插入当前影片的所有链接
                List<String> links = getPlayUrls(getContent(i));
                insertLink(links,movieId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer insertMovie(Movie movie) throws IOException{
        String url = "https://fengyezhan.xyz/kcsj/movie/add";
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("adminaccount",movie.getAccount());
        maps.put("moviename",movie.getName());
        maps.put("type",movie.getCategory());
        maps.put("language",movie.getLanguage());
        maps.put("time",movie.getShowing());
        maps.put("introduce",movie.getIntroduce());
        maps.put("avatar",movie.getImage());

        String result = Jsoup.connect(url)
                .timeout(10000)
                .ignoreContentType(true)
                .data(maps)
                .post()
                .body()
                .text();

        JSONObject json = JSON.parseObject(result);
        JSONObject data = json.getJSONObject("data");
        System.out.println("插入影片:"+data.getString("moviename"));
        return data.getInteger("movieid");
    }

    public String getContent(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection con = (HttpURLConnection) url1.openConnection();
        con.setRequestProperty("accept", "*/*");
        con.setRequestProperty("connection", "Keep-Alive");
        con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        con.connect();
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        if (con.getResponseCode() == SUCCESS){
            InputStream is = con.getInputStream();
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) != -1){
                array.write(bytes,0,length);
            }
        }
        return array.toString();
    }

    public String getMatcherResult(String pattern,String input){
        Matcher matcher = Pattern.compile(pattern).matcher(input);
        while (matcher.find()){
            return matcher.group(1).trim().replaceAll("&quot;","”");
        }
        return null ;
    }

    public List<String> getPlayUrls(String input){
        List<String> urls = new ArrayList<String>();
        Matcher matcher = Pattern.compile(PLAYURL_PATTERN).matcher(input);
        while (matcher.find()){
            urls.add(matcher.group(1));
        }
        return urls;
    }

    public void insertLink(List<String> links,Integer movieId) throws IOException {
        String[] temp;
        for (String link : links){
            Map<String, String> maps = new HashMap<String, String>();
            temp = link.split("\\$");
            String url = "https://fengyezhan.xyz/kcsj/link/add";
            maps.put("movieid", String.valueOf(movieId));
            maps.put("link",temp[1]);
            maps.put("name",temp[0]);
            String result = Jsoup.connect(url)
                    .timeout(10000)
                    .ignoreContentType(true)
                    .data(maps)
                    .post()
                    .body()
                    .text();
            System.out.println(temp[0]);
        }
    }

    public Movie getElement(String url) throws IOException {
        Movie movie = new Movie();
        String html = getContent(url);
        movie.setImage(getMatcherResult(IMAGE_PATTERN,html));
        movie.setName(getMatcherResult(NAME_PATTERN,html));
        movie.setCategory(getMatcherResult(CATEGORY_PATTERN,html));
        movie.setIntroduce(getMatcherResult(INTRODUCE_PATTERN,html));
        movie.setLanguage(getMatcherResult(LANGUAGE_PATTERN,html));
        movie.setShowing(getMatcherResult(SHOWING_PATTERN,html));
        movie.setAccount(ADMIN);
        return movie;
    }
}
