package wallpaper.video.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import lombok.Getter;
import lombok.Setter;
import wallpaper.video.entity.Project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectUtil {

    public static final ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(1)
            .setMaxPoolSize(1)
            .setWorkQueue(new LinkedBlockingQueue<>(2))
            .build();

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

    /**
     * 加载列表 避免频繁扫盘加了30秒的锁
     */
    public static void loadList() {
        try {
            executor.submit(() -> {
                boolean empty = FILES.isEmpty();

                List<Project> projectList = Stream.of(FileUtil.ls(path))
                        .map(file -> new File(file.getAbsolutePath() + File.separator + "project.json"))
                        .filter(File::exists)
                        .map(getEntity)
                        .filter(project -> StrUtil.equalsIgnoreCase(project.getType(), "video"))
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
            });
        } catch (RejectedExecutionException e) {
            // 队列满了
        }
    }

    /**
     * 监控目录变化实时更新目录
     */
    public static void startWatch() {
        WatchMonitor.createAll(path, new SimpleWatcher() {
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
        }).start();
    }
}
