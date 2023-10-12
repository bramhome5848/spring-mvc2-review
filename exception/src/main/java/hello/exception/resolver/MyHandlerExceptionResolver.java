package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 스프링의 예외 처리
 - 스프링 MVC 는 컨트롤러(핸들러) 밖으로 예외가 던져진 경우 예외를 해결하고, 동작을 새로 정의할 수 있는 방법 제공
 - 컨트롤러 밖으로 던져진 예외를 해결하고, 동작 방식을 변경하고 싶으면 HandlerExceptionResolver 를 사용

 * 예외처리 과정(ExceptionResolver 적용 전)
 - 예외 발생시 postHandler 호출 x, afterCompletion 호출, was 로 예외전달, 이후 BasicErrorController 에 의해 처리

 * 예외처리 과정(ExceptionResolver 적용 후)
 - 예외 발생시 Dispatcher servlet 단계(was 예외전달 전)에서 Exception handler 를 통해 예외 해결 시도
 - 예외 해결 될 경우 render(model) 호출, afterCompletion 호출, was 로 정상 응답

 - 참고 : ExceptionResolver 로 예외를 해결해도 postHandle() 은 호출되지 않음

 * ExceptionResolver 의 ModelAndView 반환이유
 - try, catch 를 하듯이 Exception 을 처리해서 정상 흐름처럼 변경하는 것이 목적이기 때문

 * 반환 값에 따른 동작
 - 빈 ModelAndView : new ModelAndView() 처럼 빈 ModelAndView 를 반환하면 뷰를 렌더링 하지 않고, 정상 흐름으로 서블릿이 리턴
 - ModelAndView 지정 : ModelAndView 에 View , Model 등의 정보를 지정해서 반환하면 뷰를 렌더링
 - null 반환
 -> 다음 ExceptionResolver 실행, 처리할 수 있는 ExceptionResolver 가 없으면 예외 처리가 안되고 기존에 발생한 예외를 서블릿 밖으로(WAS) 던짐

 * ExceptionResolver 활용
 1. 예외 상태 코드 변환
 - 예외를 response.sendError(xxx) 호출로 변경해서 서블릿에서 상태 코드에 따른 오류를 처리하도록 위임
 - 이후 WAS 는 서블릿 오류 페이지를 찾아서 내부 호출, 예를 들어서 스프링부트가 기본으로 설정한 /error 가 호출
 2. 뷰 템플릿 처리
 - ModelAndView 에 값을 채워서 예외에 따른 새로운 오류 화면 뷰를 렌더링 해서 고객에게 제공
 3. API 응답 처리
 - response.getWriter().println(); 처럼 HTTP 응답 바디에 직접 데이터를 넣어주는 것도 가능
 - 여기에 JSON 으로 응답하면 API 응답 처리도 가능
 */
@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        try{
            if(ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView();
            }
        } catch(IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
