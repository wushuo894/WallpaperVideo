package wallpaper.video;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * project.json
 */
@Data
@Accessors(chain = true)
public class Project {

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
     * 文件
     */
    private String file;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 类型
     */
    private String type;

    /**
     * 预览
     */
    private String preview;

    /**
     * 路径
     */
    private String path;
}
