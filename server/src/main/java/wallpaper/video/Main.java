package wallpaper.video;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import lombok.Cleanup;
import lombok.SneakyThrows;
import wallpaper.video.entity.Project;
import wallpaper.video.entity.ProjectVo;
import wallpaper.video.entity.SearchDto;
import wallpaper.video.util.DistUtil;
import wallpaper.video.util.JSON;
import wallpaper.video.util.ProjectUtil;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

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

        ProjectUtil.setPath(path);
        ProjectUtil.loadList();
        ProjectUtil.startWatch();

        HttpUtil.createServer(port)
                .setRoot(DistUtil.getDistFile())
                .start();
    }
}
