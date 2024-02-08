package wallpaper.video.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.server.SimpleServer;
import cn.hutool.http.server.action.Action;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class ActionUtil {
    public static void loadAction(SimpleServer server) {
        String packageName = "wallpaper.video.action";
        Set<Class<?>> classes = ClassUtil.scanPackage(packageName);

        String protocol = ResourceUtil.getResource("dist").getProtocol();
        if (protocol.equals("resource")) {
            classes = new HashSet<>();
            String s = ResourceUtil.readUtf8Str("reflect-config.json");
            Gson gson = JSON.gson;
            JsonArray asJsonArray = gson.fromJson(s, JsonArray.class);
            for (JsonElement element : asJsonArray) {
                JsonObject asJsonObject = element.getAsJsonObject();
                String name = asJsonObject.get("name").getAsString();
                if (!name.startsWith(packageName)) {
                    continue;
                }
                try {
                    classes.add(Class.forName(name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Class<?> aClass : classes) {
            String simpleName = aClass.getSimpleName();
            if (!simpleName.endsWith("Action")) {
                continue;
            }

            String apiUrl = "/api/" + simpleName.replace("Action", "").toLowerCase();
            System.out.println("add api : " + apiUrl);
            server.addAction(apiUrl, (Action) ReflectUtil.newInstance(aClass));
        }
    }
}
