package wallpaper.video.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JSON {
    public final static Gson gson = new com.google.gson.Gson();

    public static  <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json,classOfT);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

}
