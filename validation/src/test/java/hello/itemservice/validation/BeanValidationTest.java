package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

class BeanValidationTest {

    @Test
    void beanValidation() {
        //검증기 생성, 스프링과 통합시 직접 해당 코드를 작성하지 않아도 됨
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setItemName(" ");
        item.setPrice(0);
        item.setQuantity(10000);

        //검증기 실행
        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        for(ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.message = " + violation.getMessage());
        }
    }
}
