package wallpaper.video.action;

import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import wallpaper.video.entity.Project;
import wallpaper.video.util.ProjectUtil;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * 视频播放
 */
public class PlayAction implements Action {
    @Override
    public void doAction(HttpServerRequest req, HttpServerResponse res) throws IOException {
        ListValueMap<String, String> params = req.getParams();
        String id = params.get("id", 0);
        Optional<Project> first = ProjectUtil.FILES.stream().filter(item -> item.getId().equals(id)).findFirst();
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
    }
}
