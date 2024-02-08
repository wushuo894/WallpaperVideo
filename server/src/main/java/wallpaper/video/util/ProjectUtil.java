package wallpaper.video.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.thread.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import wallpaper.video.entity.Project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectUtil {

    @Getter
    @Setter
    public static String path;

    /**
     * 转换为实体
     */
    private final static Function<File, Project> getEntity = file -> {
        String json = FileUtil.readUtf8String(file);
        return JSON.fromJson(json, Project.class)
                .setPath(file.getParent())
                .setId(file.getParentFile().getName());
    };

    public final static List<Project> FILES = new ArrayList<>();

    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 加载列表 避免频繁扫盘加了30秒的锁
     */
    @SneakyThrows
    public static void loadList() {
        ThreadUtil.execute(() -> {
            if (LOCK.isLocked()) {
                return;
            }
            LOCK.lock();

            boolean empty = FILES.isEmpty();

            List<Project> projectList = Stream.of(FileUtil.ls(path))
                    .map(file -> new File(file.getAbsolutePath() + File.separator + "project.json"))
                    .filter(File::exists)
                    .map(getEntity)
                    .peek(project -> {
                        if (empty) {
                            FILES.add(project);
                        }
                    })
                    .filter(project -> "video".equalsIgnoreCase(project.getType()))
                    .collect(Collectors.toList());
            if (!empty) {
                FILES.clear();
                FILES.addAll(projectList);
            }
            ThreadUtil.sleep(30, TimeUnit.SECONDS);
            LOCK.unlock();
        });
    }

    /**
     * 监控目录变化实时更新目录
     */
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
