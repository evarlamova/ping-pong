package ru.varlamova.controller.commands;


import ru.varlamova.controller.meta.Controller;
import ru.varlamova.controller.meta.RequestMapping;
import ru.varlamova.dao.PingRequestDAO;
import ru.varlamova.dao.PingRequestDAOImpl;
import ru.varlamova.entity.PingRequest;
import ru.varlamova.http.enums.HttpMethod;

@Controller
public class PingController {

    private PingRequestDAO requestDAO = new PingRequestDAOImpl();

    @RequestMapping(path = "/ping/#id", method = HttpMethod.GET)
    public String pingId(String id) {
        PingRequest pingRequest = new PingRequest().setId(id);
        requestDAO.saveOrUpdate(pingRequest);
        return "PONG " + requestDAO.getCountByUser(pingRequest);
    }

 /*   @RequestMapping(path = "/error", method = HttpMethod.GET)
    public String error() {
        throw new RuntimeException("Status 500");
    }

    @RequestMapping(path = "/long/long/long/long/path", method = HttpMethod.GET)
    public String longPath() {
        return "It was a long long long long path";
    }

    @RequestMapping(path = "/ping/#id/pong", method = HttpMethod.GET)
    public String pingIdPong(String id) {
        return "id = " + id;
    }

    @RequestMapping(path = "/ping/#id/#pathVar", method = HttpMethod.GET)
    public String pingIdPathVar(String id, String pathVar) {
        return "id = " + id + "pathVar = " + pathVar;
    }*/
}
