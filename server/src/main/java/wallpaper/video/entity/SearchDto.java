package wallpaper.video.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 搜索
 */
@Data
@Accessors(chain = true)
public class SearchDto {
    /**
     * 搜索的文本
     */
    private String text;

    /**
     * 页数
     */
    private Integer pageNo;

    /**
     * 大小
     */
    private Integer size;
}
