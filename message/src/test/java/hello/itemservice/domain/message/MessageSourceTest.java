package hello.itemservice.domain.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 테스트가 한글 때문에 깨질 경우
 -> Settings -> Editor -> File Encodings > default encoding > utf-8 변경
 */
@SpringBootTest
class MessageSourceTest {

    @Autowired
    MessageSource ms;

    /**
     * code, args, locale
     * locale 정보가 없으면 basename 에서 설정한 기본 이름 메시지 파일을 조회
     * basename 으로 messages 를 지정 했으므로 messages.properties 파일에서 데이터 조회
     */
    @Test
    void helloMessage() {
        String result = ms.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }

    /**
     * 메시지가 없는 경우에는 NoSuchMessageException 이 발생
     */
    @Test
    void notFoundMessageCode() {
        assertThatThrownBy(() -> ms.getMessage("no_code", null, null))
                        .isInstanceOf(NoSuchMessageException.class);
    }

    /**
     * 메시지가 없어도 기본 메시지( defaultMessage )를 사용하면 기본 메시지가 반환
     */
    @Test
    void notFoundMessageCodeDefaultMessage() {
        String result = ms.getMessage("no_code", null, "기본 메시지", null);
        assertThat(result).isEqualTo("기본 메시지");
    }

    /**
     * 매개 변수 사용
     */
    @Test
    void argumentMessage() {
        String result = ms.getMessage("hello.name", new Object[]{"Spring"}, null);
        assertThat(result).isEqualTo("안녕 Spring");
    }

    /**
     * 국제화 파일 선택
     * locale 정보를 기반으로 국제화 파일을 선택
     * Locale 이 en_US 의 경우 messages_en_US -> messages_en -> messages 순서
     * Locale 에 맞추어 구체적인 것이 있으면 구체적인 것을 찾고, 없으면 디폴트를 찾는다고 이해
     */
    @Test
    void defaultLang() {
        // locale 정보가 없으므로 messages 를 사용
        assertThat(ms.getMessage("hello", null, null)).isEqualTo("안녕");

        // locale 정보가 있지만, message_ko 가 없으므로 messages
        assertThat(ms.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
    }

    /**
     * 국제화 파일 선택2
     * locale 정보가 Locale.ENGLISH 이므로 messages_en 을 찾아서 사용
     */
    @Test
    void enLang() {
        assertThat(ms.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
    }
}
