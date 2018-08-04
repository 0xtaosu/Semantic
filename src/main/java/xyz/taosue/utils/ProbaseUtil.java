package xyz.taosue.utils;

import xyz.taosue.entity.Triple;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tao
 */
public class ProbaseUtil {

    /**
     * 证书
     */
    private static HostnameVerifier hv = (urlHostName, session) -> {
        System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                + session.getPeerHost());
        return true;
    };

    /**
     * 标注实体
     *
     * @param instance
     * @param topK
     * @return
     */
    public static Set queryEntityByProb(String instance, int topK) {
        // 翻墙
        System.setProperty("https.proxySet", "true");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "1080");
        String urlText = "https://concept.research.microsoft.com/api/Concept/ScoreByProb?instance=" + instance + "&topK=" + topK;
        try {
            // 0.设置忽略证书
            HttpsCertificatesUtil.trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            //1.创建URL
            URL url = new URL(urlText.replace(" ", "%20"));
            System.out.println("<=" + url.toString());
            //2.创建连接
            InputStream is = url.openStream();
            //3.转换为JSON
            JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(is));
            System.out.println(json.toJSONString());
            return json.keySet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断两者是否共源
     *
     * @param triple
     * @param scale
     * @return
     */
    public static Triple judgeEntity(Triple triple, int scale) {
        Set<String> result = new HashSet<>();
        Set subSet = queryEntityByProb(triple.getSubj(), scale);
        result.addAll(subSet);
        Set objSet = queryEntityByProb(triple.getObj(), scale);
        result.retainAll(objSet);
        //判断是否有交集
        if (!result.isEmpty()) {
            System.out.println(triple.getSubj() + "和" + triple.getObj() + "是同类");
            triple.setType("0");
            triple.setSubClasses(subSet.isEmpty() ? null : (String) subSet.toArray()[subSet.size() - 1]);
            triple.setObjClasses(objSet.isEmpty() ? null : (String) objSet.toArray()[objSet.size() - 1]);
            return triple;
        }
        System.out.println(triple.getSubj() + "和" + triple.getObj() + "不是同类");
        triple.setType("1");
        triple.setSubClasses(subSet.isEmpty() ? null : (String) subSet.toArray()[subSet.size() - 1]);
        triple.setObjClasses(objSet.isEmpty() ? null : (String) objSet.toArray()[objSet.size() - 1]);
        return triple;
    }
}
