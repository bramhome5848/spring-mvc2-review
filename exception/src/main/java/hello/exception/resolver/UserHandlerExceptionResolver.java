package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * API 예외 처리 - HandlerExceptionResolver 활용 -> 예외를 여기서 마무리 하기!
 - 예외가 발생하면 WAS 까지 예외가 던져지고, WAS 에서 오류 페이지 정보를 찾아서 다시 /error 를 호출하는 과정은 너무 복잡
 - ExceptionResolver 를 활용시 예외가 발생했을 때 복잡한 과정 없이 여기에서 문제를 해결
 - ExceptionResolver 를 사용하면 컨트롤러에서 예외가 발생해도 ExceptionResolver 에서 예외를 처리
 - 예외가 발생해도 서블릿 컨테이너까지 예외가 전달되지 않고, 스프링 MVC 에서 예외 처리는 끝이남
 - 결과적으로 WAS 입장에서는 정상 처리 -> 예외를 이곳에서 모두 처리할 수 있다는 것이 핵심
 */
@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {

        try {
            if(ex instanceof UserException) {
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if("application/json".equals(acceptHeader)) {  //api
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    String result = objectMapper.writeValueAsString(errorResult);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);

                    return new ModelAndView();  //빈 ModelAndView 를 반환하면 뷰를 렌더링 하지 않고, 정상 흐름으로 리턴
                } else {
                    return new ModelAndView("error/500");   //TEXT/HTML -> view 지정
                }
            }
        } catch(IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
