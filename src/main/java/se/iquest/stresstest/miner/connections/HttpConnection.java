package se.iquest.stresstest.miner.connections;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.HttpUrl.Builder;

public abstract class HttpConnection extends UsrPassConnection
{
    public HttpConnection()
    {
        super();
    }

    public HttpConnection(String name, String address, int port, String username, String password)
    {
        super(name, address, port, username, password);
    }

    public HttpConnection(String name, String address, int port, String description, String username, String password)
    {
        super(name, address, port, description, username, password);
    }

    public URL buildUrl(String additionalUri) throws IllegalArgumentException
    {
        return buildUrl(additionalUri, new HashMap<>());
    }
    
    public URL buildUrl(String additionalUri, Map<String,String> params) throws IllegalArgumentException
    {
        Pattern p = Pattern.compile("^(https?):\\/\\/([^\\/]*)(\\/?.*)");
        Matcher m = p.matcher(this.getAddress());
        if (m.matches()) {
            Builder builder = new HttpUrl.Builder()
                    .scheme(m.group(1))
                    .host(m.group(2));
            if (this.port != 0)
                builder.port(this.port);
            
            String fullPath = m.group(3);
            for (String path: fullPath.concat("/").concat(additionalUri).split("\\/")) {
                if (!path.isEmpty()) {
                    builder.addPathSegment(path);
                }
            }
            
            if (fullPath.endsWith("/")) {
                builder.addPathSegment("");
            }
            
            if (!params.isEmpty()) {
                for (String key: params.keySet()) {
                    builder.addQueryParameter(key, params.get(key));
                }
            }
            
            return builder.build().url();
        } else {
            throw new IllegalArgumentException(String.format("Can't parse Address: %s", this.address));
        }
    }

    protected Map<String, String> parseSetCookie(List<String> setCookie, String cookieToGet)
    {
        if (setCookie == null) return null;
        Map<String, String> cookieMap = new HashMap<>();
        Pattern p1 = Pattern.compile("(\\S*)=([^;]*)");
        
        for (String setCookieHeaderstr: setCookie) {
            Matcher m1 = p1.matcher(setCookieHeaderstr);
            
            while (m1.find()) {
                cookieMap.put(m1.group(1), m1.group(2));
            }
            
            if (cookieMap.containsKey(cookieToGet)) {
                break;
            }
            cookieMap = new HashMap<>();
        }
        return cookieMap;
    }
    
    static SSLContext insecureContext() throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] noopTrustManager = new TrustManager[]{
            new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
        };
        SSLContext sc = SSLContext.getInstance("ssl");
        sc.init(null, noopTrustManager, null);
        return sc;
    }
}
