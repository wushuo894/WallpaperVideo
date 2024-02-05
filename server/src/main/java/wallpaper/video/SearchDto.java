package wallpaper.video;

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
}
