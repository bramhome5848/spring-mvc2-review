package hello.typeconverter.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Formatter
 - 일반적인 웹 애플리케이션 환경에서 개발자는 문자를 다른 타입으로 변환하거나, 다른 타입을 문자로 변환하는 상황이 대부분
 -> 화면에 숫자를 출력시, Integer -> String 출력 시점에 숫자 1000 -> 문자 "1,000" 이렇게 1000 단위에 쉼표를 넣어서 출력
 -> 날짜 객체를 문자인 "2021-01-01 10:50:11" 와 같이 출력 또는 그 반대의 상황

 * 객체를 특정한 포멧에 맞추어 문자로 출력하거나 또는 그 반대의 역할을 하는 것에 특화된 기능 -> 포맷터, 컨버터의 특별한 버전
 * Converter -> 범용(객체 객체)
 * Formatter -> 문자에 특화(객체 문자, 문자 객체) + 현지화(Locale)
 */
@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

    /**
     - "1,000" 처럼 숫자 중간의 쉼표를 적용하려면 자바가 기본으로 제공하는 NumberFormat 객체를 사용
     - 해당 객체는 Locale 정보를 활용해서 나라별로 다른 숫자 포맷을 만들 수 있음
     - parse() 를 사용해서 문자를 숫자로 변환
     - Number 타입은 Integer , Long 과 같은 숫자 타입의 부모 클래스
     - print() 를 사용해서 객체를 문자로 변환

     * 참고 - 스프링은 용도에 따라 다양한 방식의 포맷터를 제공
     -> Formatter 포맷터
     -> AnnotationFormatterFactory 필드의 타입이나 애노테이션 정보를 활용할 수 있는 포맷터
     */
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("text = {}, locale = {}", text, locale);
        NumberFormat format = NumberFormat.getInstance(locale);

        return format.parse(text);
    }

    @Override
    public String print(Number object, Locale locale) {
        log.info("object = {}, locale = {}", object, locale);

        return NumberFormat.getInstance(locale).format(object);
    }
}
