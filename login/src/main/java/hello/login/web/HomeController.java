package hello.login.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 도메인
 - 화면, UI, 기술 인프라 등등의 영역은 제외한 시스템이 구현해야 하는 핵심 비즈니스 업무 영역
 - 향후 web 을 다른 기술로 바꾸어도 도메인은 그대로 유지할 수 있어야 함
 -> web 은 domain 을 알고 있지만 domain 은 web 을 모르도록 설계해야 함
 -> web 은 domain 에 의존하지만, domain 은 web 에 의존하지 않는다고 표현
 -> web 패키지를 모두 삭제해도 domain 에는 영향이 없도록 의존관계를 설정하는 것이 중요
 -> domain 은 web 을 참조하면 안됨!!
 */
@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}