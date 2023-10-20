package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * ConversionService
 - 타입 컨버터를 하나하나 직접 찾아서 타입 변환에 사용하는 것은 매우 불편
 - 스프링은 개별 컨버터를 모아두고 그것들을 묶어서 편리하게 사용할 수 있는 기능을 제공 -> ConversionService

 * 등록과 사용 분리
 - 컨버터를 등록시 StringToIntegerConverter 같은 타입 컨버터를 명확하게 알아야 함
 - 컨버터를 사용하는 입장에서는 타입 컨버터를 전혀 몰라도 됨
 - 타입 컨버터들은 모두 컨버전 서비스 내부에 숨어서 제공 -> 타입을 변환을 원할경우 컨버전 서비스 인터페이스에만 의존하면 됨
 - 물론 컨버전 서비스를 등록하는 부분과 사용하는 부분을 분리하고 의존관계 주입을 사용!!

 * 인터페이스 분리 원칙 - ISP(Interface Segregation Principal)
 -> 인터페이스 분리 원칙은 클라이언트가 자신이 이용하지 않는 메서드에 의존하지 않아야 함

 * DefaultConversionService 는 다음 두 인터페이스를 구현
 - ConversionService : 컨버터 사용에 초점
 - ConverterRegistry : 컨버터 등록에 초점

 - 컨버터를 사용하는 클라이언트와 컨버터를 등록하고 관리하는 클라이언트의 관심사를 명확하게 분리
 - 인터페이스를 분리하면 컨버터를 사용하는 클라이언트와 컨버터를 등록하고 관리하는 클라이언트의 관심사를 명확하게 분리
 - 컨버터를 사용하는 클라이언트는 ConversionService 만 의존하면 되므로 컨버터를 어떻게 등록하고 관리하는지는 전혀 몰라도 됨
 - 컨버터를 사용하는 클라이언트는 꼭 필요한 메서드만 알게됨
 -> 이러한 인터페이스 분리를 ISP

 * 스프링은 내부에서 ConversionService 를 사용해서 타입을 변환
 -> @RequestParam 같은 곳에서 이 기능을 사용해서 타입을 변환
 */
class ConversionServiceTest {

    @Test
    void conversionService() {
        //등록, DefaultConversionService -> ConversionService 인터페이스를 구현했는데, 추가로 컨버터를 등록하는 기능도 제공
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        //사용, (source, target Type)
        assertThat(conversionService.convert("10", Integer.class)).isEqualTo(10);
        assertThat(conversionService.convert(10, String.class)).isEqualTo("10");

        IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));

        String ipPortString = conversionService.convert(new IpPort("127.0.0.1", 8080), String.class);
        assertThat(ipPortString).isEqualTo("127.0.0.1:8080");
    }
}
