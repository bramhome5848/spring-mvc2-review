package hello.login.web.filter;


import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 인증 필터 예외(화이트 리스트)
 - 홈, 회원가입, 로그인 화면, css 같은 리소스에는 접근할 수 있어야 함
 - 화이트 리스트 경로는 인증과 무관하게 항상 허용, 화이트 리스트를 제외한 나머지 경로는 인증 체크 로직을 적용

 * 인증 로직
 - 미인증 사용자는 로그인 화면으로 리다이렉트 수행

 * 문제점
 - 로그인 이후에 다시 홈으로 이동해버리면, 원하는 경로를 다시 찾아가야 하는 불편함 존재
 - 상품 관리 화면을 보려고 들어갔다가 로그인 화면으로 이동하면 로그인 이후에 다시 상품 관리 화면으로 들어가는 것이 편리함
 - 개발자 입장에서는 좀 귀찮을 수 있어도 사용자 입장으로 보면 편리한 기능
 - 해당 기능을 위해 현재 요청한 경로인 requestURI 를 /login 에 쿼리 파라미터로 함께 전달
 - /login 컨트롤러에서 로그인 성공시 해당 경로로 이동하는 기능은 추가로 개발해야 함
 */
@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout","/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if(isLoginCheckPath(requestURI)) { //화이트 리스트를 제외한 모든 경로에 체크 로직 적용
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);

                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 {}", requestURI);
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);

                    // 필터를 더는 진행하지 않음, 이후 필터, 서블릿, 컨트롤러가 더는 호출되지 않음.
                    // 앞서 redirect 를 사용했기 때문에 redirect 가 응답으로 적용되고 요청이 끝!
                    return;
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;    //예외 로깅 가능 하지만, 톰캣까지 예외를 보내주어야 함
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
