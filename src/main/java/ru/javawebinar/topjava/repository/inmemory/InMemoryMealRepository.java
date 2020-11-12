package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
   private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
 //   private Map<Integer, Meal> repository;
    private AtomicInteger counter = new AtomicInteger(0);
    private static final Logger log = getLogger(InMemoryMealRepository.class);

    {
        MealsUtil.MEALS.forEach(meal->save(meal,1));
    }


    @Override
    public Meal save(Meal meal, int userId) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        if(repository.containsKey(id)&&repository.get(id).getUserId()==userId)return repository.remove(id)!=null;
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal=repository.get(id);
        if(meal!=null&&meal.getUserId()==userId)return meal;
        return null;
    }

    @Override
    public List<Meal> getAll(int userId, LocalDate startDate, LocalDate endDate) {
        return repository.
                values().
                stream().
                filter(meal -> meal.getUserId()==userId&& DateTimeUtil.isBetween(meal.getDate(),startDate,endDate)).
                sorted((meal1,meal2)->meal2.getDate().compareTo(meal1.getDate())).
                collect(Collectors.toList());
    }
}

