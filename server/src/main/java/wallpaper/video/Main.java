package wallpaper.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.server.SimpleServer;
import cn.hutool.http.server.action.Action;
import lombok.SneakyThrows;
import wallpaper.video.util.DistUtil;
import wallpaper.video.util.ProjectUtil;

import java.util.List;
import java.util.Set;

public class Main {

    private static String path = "Z:\\SteamLibrary\\steamapps\\workshop\\content\\431960";

    @SneakyThrows
    public static void main(String[] args) {
        int port = 8080;

        if (args.length % 2 > 0) {
            return;
        }

        for (List<String> strings : CollUtil.split(List.of(args), 2)) {
            String k = strings.get(0);
            String v = strings.get(1);
            if (List.of("-p", "--port").contains(k)) {
                port = Integer.parseInt(v);
                continue;
            }
            if (List.of("-f", "--file").contains(k)) {
                path = v;
            }
        }

        if (!FileUtil.exist(path)) {
            System.out.println("文件不存在 " + path);
            System.exit(1);
        }

        ProjectUtil.setPath(path);
        ProjectUtil.loadList();
        ProjectUtil.startWatch();

        SimpleServer server = HttpUtil.createServer(port);

        Set<Class<?>> classes = ClassUtil.scanPackage("wallpaper.video.action");
        for (Class<?> aClass : classes) {
            String simpleName = aClass.getSimpleName();
            if (!simpleName.endsWith("Action")) {
                continue;
            }

            String apiUrl = "/api/" + simpleName.replace("Action", "").toLowerCase();
            server.addAction(apiUrl, (Action) ReflectUtil.newInstanceIfPossible(aClass));
        }

        server.setRoot(DistUtil.getDistFile())
                .start();
    }
}
