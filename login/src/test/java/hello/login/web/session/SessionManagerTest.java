package hello.login.web.session;

import hello.login.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    /**
     * HttpServletRequest , HttpServletResponse 객체를 직접 사용할 수 없기 때문에
     * 테스트에서 비슷한 역할을 해주는 가짜 MockHttpServletRequest , MockHttpServletResponse 를 사용
     */
    @Test
    void sessionTest() {
        //세션 생성 - 웹 브라우저로 나간 것
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        sessionManager.createSession(member, response);

        //요청에 응답 쿠키 저장 - 웹 브라우저의 요청이라 가정
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        //세션 조회 - 웹 브라우저 요청에 대한 처리
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);

        //세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull();
    }
}