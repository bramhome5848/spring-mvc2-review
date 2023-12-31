package hello.itemservice.domain.item;

import lombok.Data;

/**
 * 검증 어노테이션
 - @NotBlank : 빈값 + 공백만 있는 경우를 허용하지 않음
 - @NotNull : null 을 허용하지 않음
 - @Range(min = 1000, max = 1000000) : 범위 안의 값
 - @Max(9999) : 최대 9999까지만 허용

 * 참고
 - javax.validation 으로 시작하면 특정 구현에 관계없이 제공되는 표준 인터페이스
 - org.hibernate.validator 로 시작하면 하이버네이트 validator 구현체를 사용할 때만 제공되는 검증 기능
 - 실무에서 대부분 하이버네이트 validator 를 사용하므로 자유롭게 사용 가능

 * @NotNull, @NotEmpty, @NotBlank 정리
 - @NotNull -> Null 만 허용하지 않음, "" 이나 " " 은 허용,
 - @NotEmpty -> null 과 "" 둘 다 허용하지 않음, " " 은 허용
 - @NotBlank -> null 과 "" 과 " " 모두 허용하지 않음, 3개 중 validation 강도가 높음

 * Bean Validation 에러 코드 예시(생성된 메시지 코드 순서)
 - @NotBlank
 -> NotBlank.item.itemName
 -> NotBlank.itemName
 -> NotBlank.java.lang.String
 -> NotBlank

 - @Range
 -> Range.item.price
 -> Range.price
 -> Range.java.lang.Integer
 -> Range

 * Object Error -> @ScriptAssert() 를 사용
 - ScriptAssert.item
 - ScriptAssert
 */
@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
public class Item {

    //@NotNull(groups = UpdateCheck.class)
    private Long id;
    //@NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    //@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    //@Range(min = 1000, max = 100000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    //@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    //@Max(value = 9999, groups = SaveCheck.class)
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
