package hello.exception.api;

import hello.exception.exception.BadRequestException;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if(id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");  //발생시 에러 페이지500을 내부적으로 다시 호출함
        }

        if(id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        if(id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }

    /**
     * @ResponseStatus
     - 개발자가 직접 변경할 수 없는 예외(시스템, 라이브러리에서 제공하는 예외)에는 적용할 수 없음
     - 어노테이션을 직접 작성해야 하는데, 개발자가 코드를 수정할 수 없는 라이브러리의 예외 코드 같은 곳에는 적용할 수 없음
     - 어노테이션을 사용하기 때문에 조건에 따라 동적으로 변경하는 것도 어려움
     - ResponseStatusException 을 사용 -> ResponseStatusExceptionResolver 가 처리
     */
    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad", new IllegalArgumentException());
    }

    /**
     * DefaultHandlerExceptionResolver
     - 스프링 내부에서 발생하는 스프링 예외를 해결
     - 대표적으로 파라미터 바인딩 시점에 타입이 맞지 않으면 내부에서 TypeMismatchException 이 발생
     -> 이 경우 예외가 발생했기 때문에 그냥 두면 서블릿 컨테이너까지 오류가 올라가고, 결과적으로 500 오류가 발생

     * 파라미터 바인딩은 대부분 클라이언트가 HTTP 요청 정보를 잘못 호출해서 발생하는 문제
     - HTTP 에서는 이런 경우 상태 코드 400을 사용하도록 되어 있음
     - DefaultHandlerExceptionResolver 는 이것을 500 오류가 아니라 HTTP 상태 코드 400 오류로 변경
     -> response.sendError() 를 통해서 문제를 해결
     -> endError(400) 를 호출했기 때문에 WAS 에서 다시 오류 페이지(/error)를 내부 요청함

     * 결국 HandlerExceptionResolver 를 직접 사용하기 복잡하고 API 응답의 경우 response 에 직접 데이터를 넣어야 해서 불편함
     * ModelAndView 를 반환해야 하는 것도 API 응답에 잘 맞지 않음
     * 스프링이 제공하는 @ExceptionHandler 로 혁신적인 예외처리 기능을 제공 -> ExceptionHandlerExceptionResolver
     */
    @GetMapping("/api/default-handler-ex")
    public String defaultException(@RequestParam Integer data) {
        return "ok";
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
