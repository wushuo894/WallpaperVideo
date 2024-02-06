package wallpaper.video.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Page<T> {
    /**
     * 当前页数
     */
    private Integer pageNo;
    /**
     * 分页大小
     */
    private Integer size;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 总条数
     */
    private Integer total;
    /**
     * 列表
     */
    private List<T> list;
}
