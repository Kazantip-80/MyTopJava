package ru.javawebinar.topjava.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;


@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        Meal mealGet =service.get(2,USER_ID);
        assertMatch(mealGet,MEAL2);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() throws Exception {
        service.get(5,USER_ID);
    }


    @Test
    public void delete() {
        service.delete(4,ADMIN_ID);
        assertMatch(service.getAll(ADMIN_ID),MEAL5,MEAL6);
    }

    @Test(expected = NotFoundException.class)
    public void deletedNotFound() throws Exception {
        service.delete(4,USER_ID);
    }

    @Test
    public void getBetweenDates() {
        List<Meal>test=service.getBetweenDates(LocalDate.of(1, 1, 1)
                ,LocalDate.of(3000, 1, 1),USER_ID);
        assertMatch(test,MEAL2,MEAL3,MEAL1);
    }

//    @Test
//    public void getBetweenDateTimes() {
//        assertMatch(service.getBetweenDates(LocalDateTime.of(2015,Month.MAY,30,9,0,0)
//                ,LocalDateTime.of(2015,Month.MAY,30,11,0,0),USER_ID), MEAL1);
//    }

    @Test
    public void getAll() {
       int userId = 100000;
        List<Meal> meals = service.getAll(userId);
        assertMatch(meals,MEAL2,MEAL3,MEAL1);
    }

    @Test
    public void update() {
        Meal mealUpdate = new Meal(MEAL1);
        mealUpdate.setDescription("Update meal (test)");
        mealUpdate.setDateTime(LocalDateTime.of(2015, Month.MAY,30,23,0,0));
        service.update(mealUpdate,USER_ID);
        assertMatch(service.get(1,USER_ID),mealUpdate);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() {
        Meal mealUpdate = new Meal(MEAL4);
        mealUpdate.setDescription("UpdateNotFound meal (test)");
        mealUpdate.setDateTime(LocalDateTime.of(2015, Month.MAY,30,23,0,0));
        service.update(mealUpdate,USER_ID);
      //  assertMatch(service.get(1,USER_ID),mealUpdate);
    }

    @Test
    public void create() {

        Meal newMeal=new Meal(null, LocalDateTime.of(2015, Month.JUNE,1,11,0,0),"Тестовый  обед",1100);
        Meal created = service.create(newMeal,USER_ID);
        newMeal.setId(created.getId());
        assertMatch(service.getAll(USER_ID),newMeal,MEAL2,MEAL3,MEAL1);
}
}