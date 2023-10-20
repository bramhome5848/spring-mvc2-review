package hello.typeconverter;

import hello.typeconverter.converter.IpPortToStringConverter;
import hello.typeconverter.converter.StringToIpPortConverter;
import hello.typeconverter.formatter.MyNumberFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 스프링은 내부에서 ConversionService 를 제공
 * WebMvcConfigurer 가 제공하는 addFormatters() 를 사용해서 추가하고 싶은 컨버터를 등록
 -> 스프링은 내부에서 사용하는 ConversionService 에 컨버터를 추가하게 됨
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 주석 처리 -> 우선순위
     * MyNumberFormatter 도 숫자 문자, 문자 숫자로 변경하기 때문에 둘의 기능이 겹침
     * 우선순위는 컨버터 우선하므로 포맷터가 적용되지 않고, 컨버터가 적용
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new StringToIntegerConverter());
//        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());

        //추가
        registry.addFormatter(new MyNumberFormatter());
    }
}
