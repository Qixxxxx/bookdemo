package service.impl;

import dto.AppointmentBook;
import enums.AppointStateEnum;
import exception.AppointException;
import exception.NoNumberException;
import exception.RepeatAppointException;
import org.slf4j.Logger;
import dao.AppointmentBookDao;
import dao.BookDao;
import dto.AppointExecution;
import entity.Book;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.BookService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BookDao bookDao;

    @Autowired
    private AppointmentBookDao appointmentBookDao;


    @Override
    public Book getById(long bookId) {
        return bookDao.queryById(bookId);
    }

    @Override
    public List<Book> getList() {
        List<Book> bookList;
        List<Book> allBookList = new ArrayList<>();
        long bookId = 0;
        int pageSize = 100;
        do {
            bookList = bookDao.queryAll(bookId, pageSize);
            if (!bookList.isEmpty()) {
                bookId = bookList.stream()
                        .map(Book::getBookId)
                        .max(Comparator.comparing(i -> i))
                        .orElse(Long.MAX_VALUE);
                allBookList.addAll(bookList);
            }
        } while (bookList.size() == 100);

        return allBookList;
    }

    @Override
    @Transactional
    /**
     * 使用Transactional注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */
    public AppointExecution appoint(long bookId, long studentId) {
        try {
            // 减库存
            int updateRows = bookDao.reduceNumber(bookId);
            if (updateRows <= 0) {      // 库存不足
                throw new NoNumberException("no number");
            } else {
                // 执行预约操作
                int insert = appointmentBookDao.insertAppointment(bookId, studentId);
                if (insert <= 0) {// 重复预约
                    throw new RepeatAppointException("repeat appoint");
                } else {// 预约成功
                    AppointmentBook appointment = appointmentBookDao.queryByKeyWithBook(bookId, studentId);
                    return new AppointExecution(bookId, AppointStateEnum.SUCCESS, appointment);
                }
            }
        } catch (NoNumberException e1) {
            throw e1;
        } catch (RepeatAppointException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppointException("appoint inner error:" + e.getMessage());
        }
    }
}
