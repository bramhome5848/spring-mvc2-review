package hello.exception.api;

import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * API 예외 처리(@ExceptionHandler)
 * HTML 화면 오류 vs API 오류
 - 웹 브라우저에 HTML 화면을 제공할 때는 오류가 발생하면 BasicErrorController 를 사용하는게 편리함
 - 단순히 5xx, 4xx 관련된 오류 화면을 보여주면 되고 BasicErrorController 는 이런 메커니즘을 모두 구현

 - API 는 각 시스템 마다 응답의 모양도 다르고, 스펙도 모두 다름
 - 예외 상황에 단순히 오류 화면을 보여주는 것이 아니라, 예외에 따라서 각각 다른 데이터를 출력해야 할 수도 있음
 - 같은 예외라고 해도 어떤 컨트롤러에서 발생했는가에 따라서 다른 예외 응답을 내려주어야 할 수 있기 때문에 세밀한 제어가 필요
 - 결국 BasicErrorController 를 사용하거나 HandlerExceptionResolver 를 직접 구현하는 방식으로 API 예외를 다루기는 쉽지 않음

 * API 예외처리의 어려운 점
 - HandlerExceptionResolver 를 떠올려 보면 ModelAndView 를 반환해야 하지만 API 응답에는 필요하지 않음
 - API 응답을 위해서 HttpServletResponse 에 직접 응답 데이터를 넣어주는 것이 필요

 * @ExceptionHandler -> ExceptionHandlerExceptionResolver
 - 스프링은 API 예외 처리 문제를 해결하기 위해 @ExceptionHandler 라는 애노테이션을 사용하는 매우 편리한 예외 처리 기능을 제공
 - 스프링은 ExceptionHandlerExceptionResolver 를 기본으로 제공하고, 기본으로 제공하는 ExceptionResolver 중에 우선순위도 가장 높음
 - 실무에서 API 예외 처리는 대부분 이 기능을 사용

 * Exception 우선 순위
 - 스프링의 우선순위는 항상 자세한 것이 우선권
 - 자식예외가 발생하면 부모예외처리(), 자식예외처리() 둘다 호출 대상
 - 둘 중 더 자세한 것이 우선권을 가지므로 자식예외처리() 가 호출
 - 물론 부모예외가 호출되면 부모예외처리()만 호출 대상이 되므로 부모예외처리()가 호출

 * 참고
 - ModelAndView 를 사용해서 오류화면을 응답하는데 사용가능 -> 거의 사용하지 않음!
 */
@Slf4j
@RestController
public class ApiExceptionV2Controller {
    /**
     * 해당 컨트롤러에서 처리하고 싶은 예외를 지정, 해당 컨트롤러에서 예외가 발생하면 메서드 호출
     -> 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다.

     * 실행 흐름
     1. 컨트롤러를 호출한 결과 IllegalArgumentException 예외가 컨트롤러 밖으로 던져짐
     2. 예외가 발생했으로 ExceptionResolver 가 작동
     3. 가장 우선순위가 높은 ExceptionHandlerExceptionResolver 가 실행
     4. ExceptionHandlerExceptionResolver 는 해당 컨트롤러에 IllegalArgumentException 을 처리할 수 있는
     5. @ExceptionHandler 가 있는지 확인후 해당 메서드 실행
     6. @RestController 이므로 illegalExHandle() 에도 @ResponseBody 가 적용
     -> HTTP 컨버터가 사용되고, 응답이 JSON 으로 반환
     -> HttpStatus.BAD_REQUEST 를 지정했으므로 HTTP 상태 코드 400으로 응답

     * ResponseStatus 가 없는 경우
     -> 예외가 발생했지만 처리하여 정상 흐름으로 처리한 것이기 때문에 요청에 대한 성공으로 200 OK 가 반환됨

     * 다양한 예외 처리 형태 가능
     * ExceptionHandler({AException.class, BException.class})
     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ErrorResult illegalExHandle(IllegalArgumentException e) {
//        log.error("[exceptionHandle] ex", e);
//        return new ErrorResult("BAD", e.getMessage());
//    }

    /**
     * @ExceptionHandler 에 예외를 생략 가능 -> 생략시 메서드 파라미터의 예외가 지정
     */
//    @ExceptionHandler
//    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
//        log.error("[exceptionHandle] ex", e);
//        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
//        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
//    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler
//    public ErrorResult exHandle(Exception e) {
//        log.error("[exceptionHandle] ex", e);
//        return new ErrorResult("EX", "내부 오류");
//    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if(id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if(id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        if(id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
