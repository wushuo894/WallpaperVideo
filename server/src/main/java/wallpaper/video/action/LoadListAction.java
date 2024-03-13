package wallpaper.video.action;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import wallpaper.video.util.ProjectUtil;

/**
 * 列表
 */
public class LoadListAction implements Action {
    @Override
    public void doAction(HttpServerRequest req, HttpServerResponse res) {
        ProjectUtil.loadList();
        res.write("1").close();
    }
}
