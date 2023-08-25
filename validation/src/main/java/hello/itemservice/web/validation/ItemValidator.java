package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    /**
     * 해당 검증기를 지원하는 여부
     * 참고
     - instanceof - 특정 object 가 어떤 클래스/인터페이스를 상속/구현했는지를 체크
     - isAssignableFrom(clazz) - 특정 class(clazz)가 어떤 클래스/인터페이스를 상속/구현했는지 체크

     ex) Item, SubItem(Item 상속)
     -> Item.class.isAssignableFrom(Item.class);        //true
     -> Item.class.isAssignableFrom(SubItem.class);     //true
     -> Item a = new Item() -> a instanceof Item;       //true
     -> SubItem b = new SubItem() -> b instanceof Item; //true
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    /**
     * 검증 대상 객체와 BindingResult
     */
    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        if(!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
