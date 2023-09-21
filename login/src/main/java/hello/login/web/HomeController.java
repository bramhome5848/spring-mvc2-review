package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * required -> false
     * 로그인 하지 않은 사용자(쿠키 값이 없는 사용자)도 들어오기 때문에 false 로 지정
     */
    //@GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if(memberId == null) {
            return "home";
        }

        Member loginMember = memberRepository.findById(memberId);   //로그인

        if(loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {
        Member member = (Member)sessionManager.getSession(request); //세션 관리자에 저장된 회원 정보 조회

        if(member == null) {
            return "home";
        }

        //로그인
        model.addAttribute("member", member);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {
        //create:true -> session 이 없는 경우 새로운 세션을 생성함 -> 로그인하지 않았을 경우에도 의미없는 세션 생성
        //create:false -> 세션을 찾아서 사용할 경우 새로운 세션을 생성하지 않아야 함.
        HttpSession session = request.getSession(false);
        if(session == null) {
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /**
     * @SessionAttribute
     - 세션을 더 편리하게 사용할 수 있도록 스프링이 지원하는 기능
     - 로그인 된 사용자를 찾을 때는 해당 기능을 사용, 해당 기능은 세션을 생성하지는 않음
     */
    //@GetMapping("/")
    public String homeLoginSpringV3(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        //세션에 회원 데이터가 없으면 home
        if(loginMember == null) {
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /**
     * ArgumentResolver -> 직접 Argument 에 대한 해결을 구현
     * ArgumentResolver 를 활용하면 공통 작업이 필요할 때 컨트롤러를 더욱 편리하게 사용
     */
    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {
        //세션에 회원 데이터가 없으면 home
        if(loginMember == null) {
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}