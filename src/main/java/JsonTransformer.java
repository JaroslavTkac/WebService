import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ResponseTransformer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

public class JsonTransformer implements ResponseTransformer{
    private static final Logger log = LoggerFactory.getLogger(JsonTransformer.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

    static <T extends Object> T  fromJson(String json, Class<T> classe) {
        return gson.fromJson(json, classe);
    }

    public static <T> T convert(String urlEncoded, Class<T> type, String encoding) throws Exception {
        try {
            Map<String, Object> map = asMap(urlEncoded, encoding);
            String json = gson.toJson(map);
            return fromJson(json, type);
        }
        catch (Exception e) {
            log.error("Converting URL to JSON ", e);
            throw new Exception(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(String urlEncoded, String encoding) throws UnsupportedEncodingException {

        log.debug(urlEncoded);

        Map<String, Object> map = new LinkedHashMap<>();

        for (String keyValue : urlEncoded.trim().split("&")) {

            String[] tokens = keyValue.trim().split("=");
            String key = tokens[0];
            String value = tokens.length == 1 ? null : URLDecoder.decode(tokens[1], encoding);
            log.debug(value);

            String[] keys = key.split("\\.");
            Map<String, Object> pointer = map;

            for (int i = 0; i < keys.length - 1; i++) {

                String currentKey = keys[i];
                Map<String, Object> nested = (Map<String, Object>) pointer.get(keys[i]);

                if (nested == null) {
                    nested = new LinkedHashMap<>();
                }

                pointer.put(currentKey, nested);
                pointer = nested;
            }

            pointer.put(keys[keys.length - 1], value);
        }

        return map;
    }

}
