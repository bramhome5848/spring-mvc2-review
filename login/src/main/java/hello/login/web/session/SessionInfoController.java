package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 세션 타임아웃 설정
 - 세션은 사용자가 로그아웃을 직접 호출하여 session.invalidate() 가 실행 될 경우 삭제
 - 대부분의 사용자는 로그아웃을 선택하지 않고, 그냥 웹 브라우저를 종료
 - 문제는 HTTP 가 비 연결성(ConnectionLess)이므로 서버 입장에서는 해당 사용자가 웹 브라우저를 종료한 것인지 아닌지를 인식할 수 없음
 - 서버에서 세션 데이터를 언제 삭제해야 하는지 판단하기가 어려움

 * 남아 있는 세션의 관리
 - 남아있는 세션을 무한정 보관시 다음과 같은 문제 발생
 -> 세션과 관련된 쿠키(JSESSIONID)를 탈취 당했을 경우 오랜 시간이 지나도 해당 쿠키로 악의적인 요청이 가능
 -> 세션은 기본적으로 메모리에 생성되기 때문에 메모리의 크기가 무한하지 않으므로 꼭 필요한 경우만 생성해서 사용해야 함

 * 세션의 종료 시점
 - 생성 시점으로부터 30분
 -> 30분이 지나면 세션이 삭제되기 때문에, 사이트를 이용하다가 또 로그인을 해서 세션을 생성해야 하는 번거로움 발생
 - 대안은 세션 생성 시점이 아닌 사용자가 서버에 최근 요청한 시간을 기준으로 30분 유지
 -> 사용자가 서비스를 사용하고 있으면, 세션의 생존 시간이 30분으로 계속 늘어나고 30분 마다 로그인해야하는 번거로움이 사라짐
 -> HttpSession 은 해당 방식을 사용

 * 세션 타임아웃 발생
 - 세션의 타임아웃 시간은 해당 세션과 관련된 JSESSIONID 를 전달하는 HTTP 요청이 있을 경우 다시 초기화 됨
 -> session.getLastAccessedTime() : 최근 세션 접근 시간
 -> LastAccessedTime + MaxInactiveInterval 시간이 지나면, WAS 가 내부에서 해당 세션을 제거

 * 실무에서 주의할 점
 - 세션에는 최소한의 데이터만 보관해야 함
 - login 용 Member 객체를 따로 만들어서 최소한의 데이터만 보관
 - 보관한 데이터 용량 * 사용자 수 만큼의 메모리 사용량이 급격하게 늘어나서 장애로 이어질 수 있기 때문
 */
@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session == null) {
            return "세션이 없습니다.";
        }

        //세션 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name = {}, value = {}", name, session.getAttribute(name)));

        log.info("sessionId = {}", session.getId());  //세션Id, JSESSIONID 의 값
        log.info("maxInactiveInterval = {}", session.getMaxInactiveInterval());   //세션의 유효 시간, 예) 1800초, (30분)
        log.info("creationTime = {}", new Date(session.getCreationTime()));   //세션 생성일시

        //세션과 연결된 사용자가 최근에 서버에 접근한시간, 클라이언트에서 서버로 sessionId(JSESSIONID)를 요청한 경우에 갱신됨
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));

        //새로 생성된 세션인지, 아니면 이미 과거에 만들어졌고, 클라이언트에서 서버로 sessionId(JSESSIONID) 를 요청해서 조회된 세션인지 여부
        log.info("isNew={}", session.isNew());

        return "세션 출력";
    }
}