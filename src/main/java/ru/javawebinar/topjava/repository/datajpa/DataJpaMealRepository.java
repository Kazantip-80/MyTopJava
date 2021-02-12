package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private static final Sort SORT_MEAL_TIME = Sort.by(Sort.Direction.DESC,  "dateTime");

    @Autowired
    private CrudMealRepository crudRepository;

    @Autowired
    private CrudUserRepository crudUsRepository;


    @Override
    public Meal save(Meal meal, int userId) {

        meal.setUser(crudUsRepository.getOne(userId));
        if (!meal.isNew() && get(meal.getId(), userId) == null) {
            return null;
        }
        return (Meal) crudRepository.save(meal);

    }

    @Override
    public boolean delete(int id, int userId) {

        return crudRepository.delete(id,userId) !=0;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal=crudRepository.findById(id).orElse(null);
        if(meal!=null) return meal.getUser().getId()==userId?meal:null;
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.getAll(userId) ;
    }

    @Override
    public List<Meal> getBetweenInclusive(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return crudRepository.getBetween(startDate,endDate,userId);
    }

    @Override
    public Meal getByMealWithUser(int id, int userId) {
        return crudRepository.getByMealWithUser(id,userId);
    }
}
