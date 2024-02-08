package wallpaper.video;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ClassUtil;
import lombok.SneakyThrows;
import wallpaper.video.util.JSON;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Reflect {
    public static void main(String[] args) {
        System.out.println(ResourceUtil.getResource("").getProtocol());
        Set<Class<?>> classes = ClassUtil.scanPackage("wallpaper.video");
        List<Map<String, ? extends Serializable>> collect = classes.stream()
                .map(aClass -> Map.of(
                        "name", aClass.getName(),
                        "allDeclaredConstructors", true
                )).collect(Collectors.toList());
        FileUtil.writeUtf8String(JSON.toJson(collect), new File("server/src/main/resources/reflect-config.json"));

        Set<String> dist = getResources("dist");
        FileUtil.writeUtf8String(JSON.toJson(dist), new File("server/src/main/resources/resources.json"));
    }

    @SneakyThrows
    public static Set<String> getResources(String resource) {
        Set<String> resources = new HashSet<>();
        URL dist = ResourceUtil.getResource(resource);
        String distName = FileUtil.file(dist).toString();
        for (File file : FileUtil.loopFiles(dist.getFile())) {
            String fileName = file.getPath().replace(distName, "").replace("\\", "/");
            resources.add(resource + fileName);
        }
        return resources;
    }

}
