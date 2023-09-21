package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 스프링 인터셉터
 - 서블릿 필터와 같이 웹과 관련된 공통 관심 사항을 효과적으로 해결할 수 있는 기술
 - 서블릿 필터가 서블릿이 제공하는 기술이라면, 스프링 인터셉터는 스프링 MVC 가 제공하는 기술
 - 둘다 웹과 관련된 공통 관심 사항을 처리하지만, 적용되는 순서와 범위, 그리고 사용방법이 다름

 * 스프링 인터셉터 흐름 -> 인터셉터는 스프링 MVC 가 제공하는 기능이기에 디스패터 서블릿 이후에 등장
 - HTTP 요청 ->WAS-> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러
 - 로그인 사용자 : HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러
 - 비로그인 사용자 : HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터(적절하지 않은 요청이라 판단, 컨트롤러 호출 X)

 * 인터셉터 체인
 * HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 인터셉터1 -> 인터셉터2 -> 컨트롤러
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    /**
     * preHandle
     - 컨트롤러 호출 전에 호출(더 정확히는 핸들러 매핑 이후 핸들러 어댑터 호출 전에 호출)
     - preHandle 의 응답값이 true 이면 다음으로 진행하고, false 이면 더는 진행하지 않음
     - false 인 경우 나머지 인터셉터는 물론이고, 핸들러 어댑터도 호출되지 않음
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid);

        //@RequestMapping 사용 -> HandlerMethod 가 사용됨
        //정적 리소스 사용 -> ResourceHttpRequestHandler 가 사용됨
        if(handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; //호출할 컨트롤러 메서드의 모든 정보가 포함되어 있음
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        return true;
    }

    /**
     * postHandle
     - 컨트롤러 호출 후에 호출(더 정확히는 핸들러 어댑터 호출 후 리턴 받은 후에 호출)
     - 컨트롤러에서 예외가 발생하면 postHandle 은 호출되지 않음
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    /**
     * afterCompletion
     - 뷰가 렌더링 된 이후에 호출
     - afterCompletion 은 항상 호출됨
     - 예외를 파라미터로 받아 어떤 예외가 발생했는지 로그로 출력할 수 있음
     - afterCompletion 은 예외가 발생해도 호출됨
     - 예외가 발생하면 postHandle() 는 호출되지 않으므로 예외와 무관하게 공통 처리를 하려면 afterCompletion() 을 사용
     - 예외가 발생하면 afterCompletion() 에 예외 정보를 포함해서 호출
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);

        if(ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}
