package hello.exception;

import hello.exception.filter.LogFilter;
import hello.exception.interceptor.LogInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

/**
 * DispatcherType
 - REQUEST -> 클라이언트 요청
 - ERROR -> 오류 요청
 - FORWARD -> 서블릿에서 다른 서블릿이나 JSP 를 호출할 때
 - INCLUDE -> 서블릿에서 다른 서블릿이나 JSP 결과를 포함할 때
 - ASYNC -> 서블릿 비동기 호출

 * setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR)
 - 클라이언트 요청, 오류 페이지 요청시 필터가 호출됨(default : DispatcherType.REQUEST)
 - 특별히 오류 페이지 경로도 필터를 적용할 것이 아니면, 기본 값을 그대로 사용
 - 오류 페이지 요청 전용 필터를 적용하고 싶으면 DispatcherType.ERROR 만 지정

 * 인터셉터는 스프링이 제공하는 기술로 DispatcherType 과 무관하게 항상 호출됨
 * DispatcherType 에 따라 기능 적용여부를 선택하는 필터와 달리 excludePathPatterns 을 이용하여 오류 페이지 경로를 제외하면 됨
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/error-page/**"); //오류 페이지 경로
    }

    //@Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);

        return filterRegistrationBean;
    }
}
