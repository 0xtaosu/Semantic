package xyz.taosue.utils;

/**
 * 证书
 *
 * @author tao
 */
public class HttpsCertificatesUtil {

    /**
     * @throws Exception
     */
    public static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    /**
     *
     */
    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        /**
         * @param certs
         * @return
         */
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        /**
         * @param certs
         * @return
         */
        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        /**
         * @param certs
         * @param authType
         * @throws java.security.cert.CertificateException
         */
        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        /**
         * @param certs
         * @param authType
         * @throws java.security.cert.CertificateException
         */
        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}
