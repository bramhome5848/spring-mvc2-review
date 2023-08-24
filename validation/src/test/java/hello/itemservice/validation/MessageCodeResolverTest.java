package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DefaultMessageCodesResolver 의 기본 메시지 생성 규칙
 * 객체 오류 ***
 - 1.: code + "." + object name
 - 2.: code
 ex) 오류 코드: required, object name: item
 -> 1.: required.item
 -> 2.: required

 * 필드 오류 ***
 - 1.: code + "." + object name + "." + field
 - 2.: code + "." + field
 - 3.: code + "." + field type
 - 4.: code
 ex) 오류 코드: typeMismatch, object name "user", field "age", field type: int
 -> 1. "typeMismatch.user.age"
 -> 2. "typeMismatch.age"
 -> 3. "typeMismatch.int"
 -> 4. "typeMismatch"

 * 동작방식 ***
 - rejectValue() , reject() 는 내부에서 MessageCodesResolver 를 사용하여 메시지 코드를 생성
 - FieldError , ObjectError 의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있음

 * FieldError : rejectValue("itemName", "required") -> 4가지 오류 코드 자동 생성
 - required.item.itemName
 - required.itemName
 - required.java.lang.String
 - required

 * ObjectError : reject("totalPriceMin") -> 2가지 오류 코드 자동 생성
 - totalPriceMin.item
 - totalPriceMin

 * 오류 메시지 출력
 - 타임리프 화면을 렌더링 할 때 th:errors 가 실행
 - 이때 오류가 있다면 생성된 오류 메시지 코드를 순서대로 돌아가면서 메시지를 찾고 없는 경우 디폴트 메시지 출력
 */
class MessageCodeResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverFiled() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
