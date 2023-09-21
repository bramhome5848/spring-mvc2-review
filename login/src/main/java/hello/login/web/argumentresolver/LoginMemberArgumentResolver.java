package hello.login.web.argumentresolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * @Login 애노테이션이 있으면서 Member 타입이면 해당 ArgumentResolver 사용
     * support -> 지원하는 것인지 확인하는 역할
     * 참고
     - Class.isAssignableFrom(clazz) -> Class 가 어떤 클래스 or 인터페이스에게 상속되었는지 체크
     -> 부모(Class) 에 clazz(부모 or 자식)이 할당 가능하냐!
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        //login annotation 이 붙어 있는가
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        //Member Type 인가
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    /**
     * 컨트롤러 호출 직전에 호출 되어서 필요한 파라미터 정보를 생성
     * 세션에 있는 로그인 회원 정보인 member 객체를 찾아서 반환
     * 이후 스프링 MVC 는 컨트롤러의 메서드를 호출하면서 여기에서 반환된 member 객체를 파라미터에 전달
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);    //create -> false

        if(session == null) {
            return null;
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER);     //있으면 반환된 Member 가 return
    }
}
