package dao;

import entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookDao {
    /**
     * 通过ID查询单本图书
     *
     * @param id
     * @return
     */
    Book queryById(long id);

    /**
     * 减少馆藏数量
     *
     * @param bookId
     * @return 如果影响行数等于>1，表示更新的记录行数
     */
    int reduceNumber(long bookId);

    /**
     * 查询所有图书
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return
     */
    //这里为什么要给方法的参数添加@Param注解呢？
    // 是因为该方法有两个或以上的参数，一定要加，不然mybatis识别不了
    // 上面的BookDao接口的queryById方法和reduceNumber方法只有一个参数book_id，所以可以不用加 @Param注解，当然加了也无所谓
    List<Book> queryAll(@Param("offset") long offset, @Param("limit") int limit);




}
