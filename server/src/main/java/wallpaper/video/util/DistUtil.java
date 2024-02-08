package wallpaper.video.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.URLUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class DistUtil {
    /**
     * 释放前端文件
     *
     * @return
     */
    @SneakyThrows
    public static File getDistFile() {
        URL dist = ResourceUtil.getResource("dist");

        if (Objects.isNull(dist)) {
            return null;
        }
        File root = new File(dist.getFile());
        String protocol = dist.getProtocol();
        if (protocol.equals("jar")) {
            root = new File("").getAbsoluteFile();

            File distFile = new File(root + File.separator + "dist");

            if (distFile.exists()) {
                return distFile;
            }

            JarFile jarFile = URLUtil.getJarFile(dist);
            List<JarEntry> distList = ListUtil.toList(jarFile.entries());
            for (ZipEntry jarEntry : distList) {
                String name = jarEntry.getName();
                if (!name.startsWith("dist")) {
                    continue;
                }
                if (jarEntry.isDirectory()) {
                    FileUtil.mkdir(root + File.separator + name);
                    continue;
                }
                @Cleanup
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                FileUtil.writeFromStream(inputStream, root + File.separator + name);
            }
            return distFile;
        }

        System.out.println("protocol = " + protocol);

        if (protocol.equals("resource")) {
            String resources = ResourceUtil.readUtf8Str("resources.json");
            List list = JSON.fromJson(resources, List.class);

            root = new File("").getAbsoluteFile();

            File distFile = new File(root + File.separator + "dist");

            if (distFile.exists()) {
                return distFile;
            }

            for (Object fileName : list) {
                @Cleanup
                InputStream inputStream = ResourceUtil.getStream(fileName.toString());
                FileUtil.writeFromStream(inputStream, root + File.separator + fileName);
            }
            return distFile;
        }

        return root;
    }
}
