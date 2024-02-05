package wallpaper.video;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * project.json
 */
@Data
@Accessors(chain = true)
public class ProjectVo {

    /**
     * 编号
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 类型
     */
    private String type;
}
