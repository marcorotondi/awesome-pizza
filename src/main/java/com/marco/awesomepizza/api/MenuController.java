package com.marco.awesomepizza.api;

import com.marco.awesomepizza.api.model.Pizza;
import com.marco.awesomepizza.api.service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/pizzas")
    public Flux<Pizza> getPizzas() {
        return menuService.getPizzas();
    }
}