package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.MealServlet;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    @Autowired
    private MealService service;


    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal, int userId) {
        return service.create(meal, userId);
    }

    public void doPostController(HttpServletRequest request, HttpServletResponse response)throws IOException {

        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        service.save(meal, SecurityUtil.authUserId());
        response.sendRedirect("meals");

    }

    public void doGetController(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                service.delete(id,SecurityUtil.authUserId());
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        service.get(getId(request),SecurityUtil.authUserId());
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filter":
                log.info("filter");
                List<String> listDateAndTime=new ArrayList<>();
                listDateAndTime.add(request.getParameter("afterDate"));
                listDateAndTime.add(request.getParameter("beforeDate"));
                listDateAndTime.add(request.getParameter("afterTime"));
                listDateAndTime.add(request.getParameter("beforeTime"));
                String line;
                line=listDateAndTime.get(0);
                LocalDate startDate=(line.isEmpty()?LocalDate.MIN:LocalDate.parse(line));
                line=listDateAndTime.get(1);
                LocalDate endDate=(line.isEmpty()?LocalDate.MAX:LocalDate.parse(line));
                line=listDateAndTime.get(2);
                LocalTime startTime=(line.isEmpty()?LocalTime.MIN:LocalTime.parse(line));
                line=listDateAndTime.get(3);
                LocalTime endTime=(line.isEmpty()?LocalTime.MAX:LocalTime.parse(line));

                request.setAttribute("meals",
                        MealsUtil.getTos(service.getAll(SecurityUtil.authUserId(),startDate,endDate), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                request.setAttribute("listDateAndTime",listDateAndTime);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;

            case "all":
            default:
                log.info("getAll");

                request.setAttribute("meals",
                        MealsUtil.getTos(service.getAll(SecurityUtil.authUserId(),LocalDate.MIN,LocalDate.MAX), MealsUtil.DEFAULT_CALORIES_PER_DAY));

                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }




    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

}