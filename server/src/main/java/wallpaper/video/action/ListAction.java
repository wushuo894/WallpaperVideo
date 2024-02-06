package wallpaper.video.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import wallpaper.video.entity.Page;
import wallpaper.video.entity.Project;
import wallpaper.video.entity.ProjectVo;
import wallpaper.video.entity.SearchDto;
import wallpaper.video.util.JSON;
import wallpaper.video.util.ProjectUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListAction implements Action {
    @Override
    public void doAction(HttpServerRequest req, HttpServerResponse res) throws IOException {
        List<Project> list = ProjectUtil.FILES;
        String body = req.getBody();
        SearchDto dto = JSON.fromJson(body, SearchDto.class);

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

        Integer pageNo = dto.getPageNo();
        Integer size = dto.getSize();

        int total = list.size();
        int start = PageUtil.getStart(pageNo, size);
        int end = PageUtil.getEnd(pageNo, size);
        int totalPage = PageUtil.totalPage(total, size);

        list = CollUtil.sub(list, start, end);

        List<ProjectVo> projectVos = BeanUtil.copyToList(list, ProjectVo.class);

        Page<ProjectVo> page = new Page<ProjectVo>()
                .setSize(size)
                .setPageNo(pageNo)
                .setList(projectVos)
                .setTotal(total)
                .setTotalPage(totalPage);

        res.write(JSON.toJson(page), ContentType.JSON.getValue());
        res.close();
    }
}
