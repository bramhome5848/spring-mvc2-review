package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    /**
     * 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용
     * @InitBinder 해당 컨트롤러에만 영향을 준다.
     */
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        log.info("init binder {}", dataBinder);
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /**
     * 주의
     * BindingResult 는 검증할 대상 바로 다음에 와야함
     -> BindingResult 파라미터의 위치는 @ModelAttribute Item 다음에 위치해야함
     -> item 객체의 binding 결과를 담고 있기 때문에 순서가 중요함..
     * BindingResult 는 Model 에 자동으로 포함

     * BindingResult 정리
     * BindingResult 가 있으면 @ModelAttribute 에 데이터 바인딩 시 오류가 발생해도 컨트롤러 호출됨
     * @ModelAttribute 에 바인딩시 타입 오류가 발생할 경우?
     -> BindingResult 가 없으면 -> 400 오류가 발생하면서 컨트롤러가 호출되지 않고, 오류 페이지로 이동
     -> BindingResult 가 있으면 -> 오류 정보를 BindingResult 에 담아서 컨트롤러를 정상 호출

     * BindingResult 에 검증 오류를 적용하는 3가지 방법
     - 1. @ModelAttribute 의 객체에 타입 오류 등으로 바인딩이 실패하는 경우 -> 스프링이 FieldError 생성후 BindingResult 에 넣어줌
     - 2. 개발자가 직접 넣어줌 -> 아래 검증로직에서 직접 넣어주는 경우
     - 3. Validator 사용
     */
    //@PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {

        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }

        /**
         * 특정 필드가 아닌 복합 룰 검증
         - 특정 필드를 넘어서는 오류가 있으면 ObjectError 객체를 생성해서 bindingResult 에 add
         */
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //bindingResult 는 View 에 같이 넘어감
        if(bindingResult.hasErrors()) {
            log.info("errors =  {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * filed error 파라미터 목록
     - objectName : 오류가 발생한 객체 이름
     - field : 오류 필드
     - rejectedValue : 사용자가 입력한 값(거절된 값)
     - bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
     - codes : 메시지 코드
     - arguments : 메시지에서 사용하는 인자
     - defaultMessage : 기본 오류 메시지

     * 사용자의 입력 데이터가 컨트롤러의 @ModelAttribute 에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어려움
     -> 가격에 숫자가 아닌 문자가 입력된다면 가격은 Integer 타입이므로 문자를 보관할 수 있는 방법이 없음
     * FieldError 는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공
     -> 오류가 발생한 사용자 입력값을 보관하여 화면에 다시 출력

     * rejectedValue 는 오류 발생시 사용자 입력 값을 저장하는 필드
     * bindingFailure 는 타입 오류 같은 바인딩이 실패했는지 여부를 적어주면 됨 -> 바인딩이 실패한 것은 아니기 때문에 예제에서는 false 사용

     * binding error 시에는(가격에 문자 입력시) 스프링이 FieldError 생성후 BindingResult 에 넣어줌
     -> bindingResult.addError(new FiledError("item", "itemName", "qqqq", true, null, null, "상품 이름은 필수 입니다."));
     -> 해당 내용을 bindingResult 에 담고 Controller 를 호출

     * 타임리프의 사용자 입력값 유지
     - 타임리프의 th:field 는 정상 상황에는 모델 객체의 값을 사용, 오류가 발생시 FieldError 에서 보관한 값을 사용해서 값을 출력

     * 스프링의 바인딩 오류 처리
     - 타입 오류로 바인딩 실패시 스프링은 FieldError 를 생성하면서 사용자가 입력한 값을 넣어두고 해당 오류를 BindingResult 에 담아 컨트롤러를 호출
     - 타입 오류 같은 바인딩 실패시에도 사용자의 오류 메시지를 정상 출력
     */
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes, Model model) {

        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
                    false, null, null, "상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
                    false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null,
                        "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors =  {}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
                    false, new String[]{"required.item.itemName"}, null, null));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
                    false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},
                        new Object[]{10000, resultPrice}, null));
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * rejectValue() , reject() 를 사용해서 기존 코드 단순화
     * 컨트롤러에서 BindingResult 는 검증해야 할 객체인 target 바로 다음 위치
     -> BindingResult 는 이미 본인이 검증해야 할 객체인 target 을 알고 있음

     * BindingResult 가 제공하는 rejectValue() , reject() 사용시
     -> FieldError , ObjectError 를 직접 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있음

     * rejectValue(), reject()
     - field : 오류 필드명
     - errorCode : 오류 코드(해당 코드는 메시지에 등록된 코드가 아님, messageResolver 를 위한 오류 코드)
     - errorArgs : 오류 메시지에서 {0} 을 치환하기 위한 값
     - defaultMessage : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지

     * 축약된 오류 코드
     - FieldError() 를 직접 다룰 떄와 달리 오류코드를 간단히 입력 -> 그래도 오류 메시지를 찾음
     - 해당 부분을 이해하기 위해서는 MessageCodesResolver 이해가 필요
     */
    //@PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //첫 if 문과 동일한 기능 -> empty, 공백 같은 단순한 기능만 제공
        //ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");

        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        itemValidator.validate(item, bindingResult);

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * @Validated
     - 검증기를 실행하는 어노테이션으로 해당 어노테이션 적용시 WebDataBinder 에 등록한 검증기를 찾아서 실행
     - 여러 검증기를 등록할 경우 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요 -> 이 때 supports() 가 사용

     * 참고
     - 검증시 @Validated @Valid 둘다 사용가능하다.
     - javax.validation.@Valid 를 사용하려면 build.gradle 의존관계 추가가 필요 -
     -> implementation 'org.springframework.boot:spring-boot-starter-validation'
     -> @Validated 는 스프링 전용 검증 어노테이션, @Valid 는 자바 표준 검증 애노테이션
     */
    @PostMapping("/add")
    public String addItem6(@Validated @ModelAttribute Item item, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }
}

