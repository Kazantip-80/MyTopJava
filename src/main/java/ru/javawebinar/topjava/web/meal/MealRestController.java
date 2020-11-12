package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

@Controller
public class MealRestController {
    @Autowired
    private MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal, int userId) {
        return service.create(meal, userId);
    }

}