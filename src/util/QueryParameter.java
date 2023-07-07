package util;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final public class QueryParameter {

    private QueryParameter() {
    }

    public static Map<String, String> getQueryParams(String url) {
        Map<String, String> params = new HashMap<>();

        String[] pairs = URI.create(url).getQuery().split("&");

        for (String pair : pairs) {
            String[] rs = pair.split("=");
            if (rs.length >= 2) {
                params.put(rs[0], rs[1]);
            }
        }
        return params;
    }

    public static Optional<String> getQueryValueByKey(String url, String key) {
        Map<String, String> params = getQueryParams(url);
        return params.containsKey(key) ? Optional.of(params.get(key)) : Optional.empty();
    }

}
