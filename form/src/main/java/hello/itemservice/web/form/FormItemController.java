package hello.itemservice.web.form;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {

    private final ItemRepository itemRepository;

    /**
     * @ModelAttribute 는 컨트롤러에 있는 별도의 메서드에 적용 가능
     * 해당 컨트롤러(모든 mapping)를 요청할 때 regions 에서 반환한 값이 자동으로 모델(model)에 담김 -> 특수한 사용법!!
     */
    @ModelAttribute("regions")
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/item";
    }

    /**
     * th:object 를 적용하기 위해 해당 오브젝트 정보를 전달
     * 등록 폼이기 때문에 데이터가 비어있는 빈 오브젝트를 뷰에 전달
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "form/addForm";
    }

    /**
     * 체크박스
     -> 체크 박스를 선택한 경우 -> item.open=true
     -> 체크 박스를 선택하지 않는 경우 -> item.open=null
     * 체크 박스 선택시 HTML Form 에서 open=on 이라는 값을 전달 -> 스프링은 on 이라는 문자를 true 타입으로 변환
     * 체크 박스 미선택시 open 이라는 필드 자체가 서버로 전송되지 않음

     * 수정 시 문제 발생 가능성
     -> 체크를 해제 후 저장시 아무 값도 전송되지 않음(open 필드 자체가 전송되지 않음)
     -> 서버 구현에 따라서 값이 전송되지 않은 것으로 판단하여 값을 변경하지 않음

     * 해결방법
     -> 히든 필드를 만들어(_open) 기존 체크 박스 이름 앞에 언더스코어(_)를 붙여 전송하면 체크를 해제했다고 인식 할 수 있음
     -> 히든 필드는 항상 전송되기 때문에 체크를 해제한 경우 open 은 전송되지 않고 _open 만 전송됨
     -> 이 경우 스프링 MVC 는 체크를 해제했다고 판단

     * 체크를 한 경우 : item.open == true
     -> 체크 박스를 체크시 스프링 MVC 가 open 에 값이 있는 것을 확인하고 사용(이 때 _open 은 무시)

     * 체크 박스 미체크 : item.open == false
     -> 체크 박스 미체크시 스프링 MVC 가 _open 만 있는 것을 확인하고 open 의 값이 체크되지 않았다고 인식
     */
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        log.info("item.open = {}", item.getOpen());
        log.info("item.regions = {}", item.getRegions());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/form/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/form/items/{itemId}";
    }
}

