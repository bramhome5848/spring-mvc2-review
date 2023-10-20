package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest request) {
        String data = request.getParameter("data"); //HTTP 요청 파라미터는 모두 문자로 처리됨
        Integer intValue = Integer.valueOf(data);         //숫자로 변환
        System.out.println("intValue = " + intValue);
        return "ok";
    }

    /**
     * @RequestParam 사용시 문자 10을 숫자 10으로 편리하게 받을 수 있음 -> 스프링이 중간에서 타입을 변환
     * @ModelAttribute , @PathVariable 또한 스프링이 중간에 타입을 변환
     * 스프링은 확장 가능한 컨버터 인터페이스를 제공 -> 해당 인터페이스 구현시 추가적인 타입 변환 기능 추가 가능
     */
    @GetMapping("/hello-v2")
    public String helloV2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
    }

    /**
     * 처리 과정
     * @RequestParam -> ArgumentResolver 인 RequestParamMethodArgumentResolver 에서 ConversionService 를 사용해서 타입을 변환
     */
    @GetMapping("/ip-port")
    public String ipPort(@RequestParam IpPort ipPort) {
        System.out.println("ipPort IP = " + ipPort.getIp());
        System.out.println("ipPort PORT = " + ipPort.getPort());
        return "ok";
    }
}
