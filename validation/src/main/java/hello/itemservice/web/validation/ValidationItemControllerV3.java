
package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    /**
     * 스프링 MVC 의 Bean Validator 를 사용
     - spring-boot-starter-validation 라이브러리 적용시 자동으로 Bean Validator 를 인지하고 스프링에 통합

     * 스프링 부트는 자동으로 글로벌 Validator 로 등록
     - LocalValidatorFactoryBean 을 글로벌 Validator 로 등록하여 @Notnull 과 같은 어노테이션을 보고 검증을 수행
     - Validator 가 적용되어 있기 때문에, @Valid, @Validated 만 적용하면 사용 가능
     - 검증 오류 발생시, FieldError, ObjectError 를 생성하여 BindingResult 에 담아줌

     * 검증 순서
     1. @ModelAttribute 각각의 필드에 타입 변환 시도
     -> 성공하면 다음으로
     -> 실패하면 typeMismatch 로 FieldError 추가
     2. Validator 적용
     -> 바인딩에 성공한 필드만 Bean Validation 적용
     -> BeanValidator 는 바인딩에 실패한 필드는 BeanValidation 을 적용하지 않음
     -> 타입 변환에 성공하고 바인딩에 성공한 필드여야 BeanValidation 적용이 의미가 있다

     * @ModelAttribute -> 각각의 필드 타입 변환시도  -> 변환에 성공하여 바인딩에 성공한 필드만 BeanValidation 적용
     */
    //@PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    /**
     * Bean Validation - groups 적용
     */
    @PostMapping("/add")
    public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    //@PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute Item item,
                       BindingResult bindingResult) {

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

    /**
     * Bean Validation - groups 적용
     - groups 기능을 사용하여 등록과 수정시에 각각 다르게 검증 기능을 적용시키는 것이 가능
     - groups 기능 사용시 개발의 복잡도가 올라가게 됨
     - groups 기능은 실제 잘 사용되지는 않는데, 실무에서는 등록용 폼 객체와 수정용 폼 객체를 분리해서 사용하기 때문
     */
    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item,
                         BindingResult bindingResult) {

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }
}

