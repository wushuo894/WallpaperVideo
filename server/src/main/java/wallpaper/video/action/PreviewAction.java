package wallpaper.video.action;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import wallpaper.video.entity.Project;
import wallpaper.video.util.ProjectUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 封面
 */
public class PreviewAction implements Action {
    /**
     * 避免频繁4k读 添加封面缓存
     */
    private final static Map<String, byte[]> imageCache = new HashMap<>();

    private final static ReentrantLock LOCK = new ReentrantLock();

    @Override
    public void doAction(HttpServerRequest req, HttpServerResponse res) throws IOException {
        ListValueMap<String, String> params = req.getParams();
        String id = params.get("id", 0);

        if (imageCache.containsKey(id)) {
            res.setContentType("image/gif");
            res.write(imageCache.get(id));
            res.close();
            return;
        }

        Optional<Project> first = ProjectUtil.FILES.stream().filter(item -> item.getId().equals(id)).findFirst();
        if (first.isEmpty()) {
            return;
        }
        Project project = first.get();
        String file = project.getPath() + File.separator + project.getPreview();

        byte[] bytes = FileUtil.readBytes(file);

        res.setContentType("image/gif");
        res.write(bytes);
        res.close();

        ThreadUtil.execute(() -> {
            LOCK.lock();
            if (!imageCache.containsKey(id)) {
                // 为了让bytes及时释放不要使用上面的bytes 而是分配到锁后再进行及时读取
                imageCache.put(id, FileUtil.readBytes(file));
            }
            LOCK.unlock();
        });
    }
}
