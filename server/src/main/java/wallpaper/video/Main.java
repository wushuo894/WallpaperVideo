package wallpaper.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.server.SimpleServer;
import wallpaper.video.action.PlayAction;
import wallpaper.video.action.RootAction;
import wallpaper.video.util.ActionUtil;
import wallpaper.video.util.ProjectUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    private static String path = "Z:\\SteamLibrary\\steamapps\\workshop\\content\\431960";

    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));

        String osName = System.getProperty("os.name");

        if (StrUtil.containsIgnoreCase(osName, "Mac OS X")) {
            path = "~/Library/Application Support/Steam/steamapps/workshop/content/431960";
        }

        int port = 8080;

        if (args.length % 2 > 0) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        Map<String, String> envMap = System.getenv();
        Map<String, String> argsMap = CollUtil.split(List.of(args), 2)
                .stream()
                .collect(Collectors.toMap(it -> it.get(0), it -> it.get(1)));

        map.putAll(argsMap);
        map.putAll(envMap);

        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            String k = stringStringEntry.getKey();
            String v = stringStringEntry.getValue();
            if (List.of("-p", "--port","PORT").contains(k)) {
                port = Integer.parseInt(v);
            }
            if (List.of("-f", "--file","FILE").contains(k)) {
                path = v;
            }
            if (List.of("-vc", "--videoCache","VideoCache").contains(k)) {
                PlayAction.VIDEO_CACHE = StrUtil.equalsIgnoreCase(v, "true");
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
        ActionUtil.loadAction(server);
        server.addAction("/", new RootAction())
                .start();
    }
}
