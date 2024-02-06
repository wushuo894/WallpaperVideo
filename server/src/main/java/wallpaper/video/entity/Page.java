package wallpaper.video.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Page<T> {
    private Integer pageNo;
    private Integer size;
    private Integer totalPage;
    private Integer total;
    /**
     * 列表
     */
    private List<T> list;
}
