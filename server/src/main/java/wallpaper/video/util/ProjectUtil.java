package wallpaper.video.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import lombok.Getter;
import lombok.Setter;
import wallpaper.video.entity.Project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectUtil {

    @Getter
    @Setter
    public static String path;

    private final static Function<File, Project> getEntity = file -> {
        String json = FileUtil.readUtf8String(file);
        return JSON.fromJson(json, Project.class)
                .setPath(file.getParent())
                .setId(file.getParentFile().getName());
    };

    public final static List<Project> FILES = new ArrayList<>();

    private static final ReentrantLock LOCK = new ReentrantLock();

    public static void loadList() {
        if (LOCK.tryLock()) {
            return;
        }
        LOCK.lock();
        List<Project> projectList = FileUtil.loopFiles(path, file -> file.getName().equals("project.json"))
                .stream()
                .map(getEntity)
                .filter(project -> "video".equalsIgnoreCase(project.getType()))
                .collect(Collectors.toList());
        FILES.clear();
        FILES.addAll(projectList);
        LOCK.unlock();
    }

    public static void startWatch() {
        WatchMonitor watchMonitor = WatchMonitor.createAll(path, new SimpleWatcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                loadList();
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                loadList();
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                loadList();
            }
        });
        watchMonitor.start();
    }
}
