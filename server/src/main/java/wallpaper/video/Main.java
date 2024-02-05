package wallpaper.video;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    private final static Gson gson = new Gson();

    private final static Function<File, Project> getEntity = file -> {
        String json = FileUtil.readUtf8String(file);
        return gson.fromJson(json, Project.class)
                .setPath(file.getParent())
                .setId(file.getParentFile().getName());
    };

    private final static List<Project> files = new ArrayList<>();

    private static synchronized void loadList(String path) {
        List<Project> projectList = FileUtil.loopFiles(path, file -> file.getName().equals("project.json"))
                .stream()
                .map(getEntity)
                .filter(project -> "video".equalsIgnoreCase(project.getType()))
                .collect(Collectors.toList());
        files.clear();
        files.addAll(projectList);
    }

    public static void main(String[] args) {
        String path = "E:\\SteamLibrary\\steamapps\\workshop\\content\\431960";
        loadList(path);

        WatchMonitor watchMonitor = WatchMonitor.createAll(path, new SimpleWatcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                loadList(path);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                loadList(path);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                loadList(path);
            }
        });
        watchMonitor.start();

        HttpUtil.createServer(8080)
                .addAction("/api/list", (req, res) -> {
                    List<Project> list = files;
                    String body = req.getBody();
                    SearchDto dto = gson.fromJson(body, SearchDto.class);

                    String text = dto.getText();

                    if (StrUtil.isNotBlank(text)) {
                        list = list.stream()
                                .filter(item ->
                                        StrUtil.containsIgnoreCase(item.getId(), text) ||
                                                StrUtil.containsIgnoreCase(item.getTitle(), text) ||
                                                StrUtil.containsIgnoreCase(item.getDescription(), text) ||
                                                StrUtil.containsIgnoreCase(item.getType(), text) ||
                                                CollUtil.contains(item.getTags(), text)
                                ).collect(Collectors.toList());
                    }


                    List<ProjectVo> projectVos = BeanUtil.copyToList(list, ProjectVo.class);
                    res.write(gson.toJson(projectVos), ContentType.JSON.getValue());
                    res.close();
                })
                .addAction("/api/preview", (req, res) -> {
                    ListValueMap<String, String> params = req.getParams();
                    String id = params.get("id", 0);
                    Optional<Project> first = files.stream().filter(item -> item.getId().equals(id)).findFirst();
                    if (first.isEmpty()) {
                        return;
                    }
                    Project project = first.get();
                    String file = project.getPath() + File.separator + project.getPreview();

                    res.setContentType("image/gif");
                    res.write(new File(file));
                    res.close();
                })
                .addAction("/api/play", (req, res) -> {
                    ListValueMap<String, String> params = req.getParams();
                    String id = params.get("id", 0);
                    Optional<Project> first = files.stream().filter(item -> item.getId().equals(id)).findFirst();
                    if (first.isEmpty()) {
                        return;
                    }
                    Project project = first.get();
                    String file = project.getPath() + File.separator + project.getFile();

                    res.setHeader("Accept-Ranges", "bytes");
                    res.setHeader("Content-Type", "video/mp4");
                    res.setHeader("Content-Disposition", "inline;filename=" + project.getFile());
                    res.setHeader("Content-Length", String.valueOf(new File(file).length()));

                    res.write(new File(file));
                    res.close();
                })
                .setRoot(Thread.currentThread().getContextClassLoader().getResource("dist").getFile())
                .start();
    }
}
