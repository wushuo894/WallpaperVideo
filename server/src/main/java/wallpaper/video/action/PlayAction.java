package wallpaper.video.action;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import lombok.Cleanup;
import wallpaper.video.entity.Project;
import wallpaper.video.util.ProjectUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 视频播放
 */
public class PlayAction implements Action {
    public static Boolean VIDEO_CACHE = Boolean.FALSE;
    private static final List<String> VIDEO_CACHE_ING_ID = new ArrayList<>();

    @Override
    public void doAction(HttpServerRequest req, HttpServerResponse res) throws IOException {
        ListValueMap<String, String> params = req.getParams();
        String id = params.get("id", 0);
        Optional<Project> first = ProjectUtil.FILES.stream().filter(item -> item.getId().equals(id)).findFirst();
        if (first.isEmpty()) {
            return;
        }
        Project project = first.get();
        String fileName = project.getPath() + File.separator + project.getFile();

        File file = new File(fileName);
        File videoCache = new File("videoCache" + File.separator + project.getId() + "." + FileTypeUtil.getType(file));

        if (VIDEO_CACHE) {
            if (videoCache.exists()) {
                file = videoCache;
            }
        }


        res.setHeader("Accept-Ranges", "bytes");
        res.setHeader("Content-Type", "video/mp4");
        res.setHeader("Content-Disposition", "inline;filename=" + project.getId() + "." + FileUtil.extName(project.getFile()));
        res.setHeader("Content-Length", String.valueOf(file.length()));

        try (res) {
            @Cleanup
            BufferedInputStream inputStream = FileUtil.getInputStream(file);
            @Cleanup
            OutputStream outputStream = res.getOut();
            IoUtil.copy(inputStream, outputStream, 40960);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (VIDEO_CACHE) {
            // 避免同视频反复缓存
            AtomicReference<File> atomicReference = new AtomicReference<>(file);
            synchronized (VIDEO_CACHE_ING_ID) {
                if (VIDEO_CACHE_ING_ID.contains(project.getId())) {
                    return;
                }
                if (videoCache.exists()) {
                    return;
                }
                VIDEO_CACHE_ING_ID.add(project.getId());
            }
            ThreadUtil.execute(() -> {
                File temp = new File("videoCache" + File.separator + project.getId() + ".temp");
                FileUtil.copy(atomicReference.get(), temp, true);
                FileUtil.move(temp, videoCache, false);
            });
        }
    }
}
