package cn.net.rjnetwork.exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import cn.net.rjnetwork.results.ResultBody;

/**
 * @author huzhenjie
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理自定义的业务异常
     * @param
     * @param
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody bizExceptionHandler(Exception e){
        log.error("报错信息：{}",e.getLocalizedMessage(),e);
        return ResultBody.error(e.getMessage());
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResultBody handler404(NoHandlerFoundException e){
        log.error("报错信息：{}",e.getLocalizedMessage(),e);
        return ResultBody.error("000404","路由不存在");
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResultBody error(Exception e){
        log.error("报错信息：{}",e.getLocalizedMessage(),e);
        return ResultBody.error("000009",e.getMessage());
    }

}
