package wallpaper.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.server.SimpleServer;
import lombok.SneakyThrows;
import wallpaper.video.action.PlayAction;
import wallpaper.video.util.ActionUtil;
import wallpaper.video.util.DistUtil;
import wallpaper.video.util.ProjectUtil;

import java.util.List;

public class Main {

    private static String path = "Z:\\SteamLibrary\\steamapps\\workshop\\content\\431960";

    @SneakyThrows
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

        for (List<String> strings : CollUtil.split(List.of(args), 2)) {
            String k = strings.get(0);
            String v = strings.get(1);
            if (List.of("-p", "--port").contains(k)) {
                port = Integer.parseInt(v);
            }
            if (List.of("-f", "--file").contains(k)) {
                path = v;
            }
            if (List.of("-vc", "--videoCache").contains(k)) {
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
        server.setRoot(DistUtil.getDistFile())
                .start();
    }
}
