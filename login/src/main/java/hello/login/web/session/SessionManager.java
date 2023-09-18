package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션
 - 세션을 사용해서 서버에서 중요한 정보를 관리
 - 쿠키 값은 변조 가능 -> 예상 불가능한 복잡한 세션Id를 사용(회원과 관련된 정보는 전혀 클라이언트에 전달하지 않음, 임의의 랜덤값을 전달)
 - 쿠키에 보관하는 정보는 클라이언트 해킹시 유출 가능성 -> 세션Id가 유출되어도 여기에는 중요한 정보가 없음

 * 쿠키 탈취 후 사용
 - 해커가 토큰을 해킹해도 시간이 지나면 사용 할 수 없도록 서버에 서세션의 만료시간을 짧게(예: 30분) 유지
 - 해킹이 의심되는 경우 서버에서 해당 세션을 강제로 제거
 */
@Component
public class SessionManager {

    private final String SESSION_COOKIE_NAME = "mySessionId";
    private final Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     */
    public void createSession(Object value, HttpServletResponse response) {
        //세션 id를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        //쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(mySessionCookie);
    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie == null) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }
}
