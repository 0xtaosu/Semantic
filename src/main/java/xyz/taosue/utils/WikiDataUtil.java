package xyz.taosue.utils;

import xyz.taosue.entity.Triple;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tao
 */
public class WikiDataUtil {
    private static final String KEY_GOOGLE = "AIzaSyAPPo9TLzYtboJ0mSnyFR2yuXqyrRyR44A";
    private static final int LIMIT =10;

    /**
     * 补充知识
     *
     * @param entity
     * @return
     */
    public static List<Triple> queryTriple(String entity) {
        // 翻墙
        System.setProperty("https.proxySet", "true");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "1080");
        List<Triple> tripleList = new ArrayList<>();
        String urlText = "https://kgsearch.googleapis.com/v1/entities:search?query=" + entity + "&key=" + KEY_GOOGLE + "&limit="+LIMIT+"&indent=True";
        try {
            //1.创建URL
            URL url = new URL(urlText.replace(" ", "%20"));
            System.out.println("<="+url.toString());
            //2.创建连接
            InputStream is = url.openStream();
            //3.转换为JSON
            JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(is));
            //4.处理JSON
            JSONArray itemList = (JSONArray) json.get("itemListElement");
            itemList.forEach(item -> {
                JSONObject resultJson = (JSONObject) ((JSONObject) item).get("result");
                System.out.println("=>" + resultJson.toJSONString());
                //obj:
                String obj = (String) resultJson.get("name");
                JSONArray typeList = resultJson.get("@type") == null ? new JSONArray() : (JSONArray) resultJson.get("@type");
                System.out.println("=>" + typeList.toString());
                typeList.forEach(type -> {
                    Triple triple = new Triple();
                    //pred:
                    String pred = type.toString();
                    triple.setSubj(entity);
                    triple.setPred(pred);
                    triple.setObj(obj);
                    triple.setType("");
                    tripleList.add(triple);
                });
            });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return tripleList;
    }
}
