package wallpaper.video.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.SimpleServer;
import cn.hutool.http.server.action.Action;

import java.util.Set;

public class ActionUtil {
    public static void loadAction(SimpleServer server) {
        Set<Class<?>> classes = ClassUtil.scanPackage("wallpaper.video.action");
        for (Class<?> aClass : classes) {
            String simpleName = aClass.getSimpleName();
            if (!simpleName.endsWith("Action")) {
                continue;
            }
            String apiUrl = "/api/" + StrUtil.lowerFirst(simpleName.replace("Action", ""));
            server.addAction(apiUrl, (Action) ReflectUtil.newInstanceIfPossible(aClass));
        }
    }
}
