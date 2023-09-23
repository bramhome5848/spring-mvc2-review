package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 자바 직접 실행 예외
 - 자바의 메인 메서드를 직접 실행시 main 이라는 이름의 쓰레드가 실행
 - 실행 도중 예외를 잡지 못하고 처음 실행한 main 메서드를 넘어서 예외가 던져지면 예외 정보를 남기고 해당 쓰레드 종료

 * 웹 애플리케이션 예외
 - 애플리케이션에서 예외를 잡지 못하고, 서블릿 밖으로 예외가 전달되는 경우
 -> WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
 -> Exception 의 경우 서버 내부에서 처리할 수 없는 오류가 발생한 것으로 생각하여 HTTP 상태 코드 500을 반환

 * response.sendError(HTTP 상태 코드, 오류 메시지)
 * 오류가 발생했을 때 HttpServletResponse 가 제공하는 sendError 라는 메서드를 사용가능
 -> 사용시 당장 예외가 발생하는 것은 아니지만, 서블릿 컨테이너에게 오류가 발생했다는 점을 전달 가능
 -> 사용시 HTTP 상태 코드와 오류 메시지도 추가 가능

 * sendError 흐름
 -> WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(response.sendError())
 -> response.sendError() 를 호출시 response 내부에는 오류가 발생했다는 상태를 저장
 -> 서블릿 컨테이너는 응답 전에 response 에 sendError() 가 호출되었는지 확인
 -> 호출되었다면 설정한 오류 코드에 맞추어 기본 오류 페이지를 제공
 */
@Slf4j
@Controller
public class ServletExController {

    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }

    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 오류!");
    }

    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }
}
