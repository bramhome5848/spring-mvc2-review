package hello.typeconverter.formatter;

import hello.typeconverter.converter.IpPortToStringConverter;
import hello.typeconverter.converter.StringToIpPortConverter;
import hello.typeconverter.type.IpPort;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * 포맷터를 지원하는 컨버전 서비스
 - 컨버전 서비스에는 컨버터만 등록할 수 있고, 포맷터를 등록할 수 는 없음
 - 포맷터는 객체 문자, 문자 객체로 변환하는 특별한 컨버터
 - 포맷터를 지원하는 컨버전 서비스를 사용하면 컨버전 서비스에 포맷터 추가 가능
 - 내부에서 어댑터 패턴을 사용해서 Formatter 가 Converter 처럼 동작하도록 지원

 * FormattingConversionService
 - 포맷터를 지원하는 컨버전 서비스
 - DefaultFormattingConversionService 는 FormattingConversionService 에 기본적인 통화, 숫자 관련 몇가지 기본 포맷터를 추가해서 제공.

 * DefaultFormattingConversionService 상속 관계
 - FormattingConversionService 는 ConversionService 관련 기능을 상속받기 때문에 컨버터도 포맷터도 모두 등록 가능
 - 사용시에는 ConversionService 가 제공하는 convert 를 사용하면 됨
 - 추가로 스프링 부트는 DefaultFormattingConversionService 를 상속 받은 WebConversionService 를 내부에서 사용
 */
class FormattingConversionServiceTest {

    @Test
    void formattingConversionService() {

        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();

        //등록
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        //포맷터 등록
        conversionService.addFormatter(new MyNumberFormatter());

        //컨버터 사용
        IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));

        //포맷터 사용
        assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");
        assertThat(conversionService.convert("1,000", Long.class)).isEqualTo(1000L);
    }
}
