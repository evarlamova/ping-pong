package ru.varlamova.controller.executor;

import ru.varlamova.controller.executor.exception.CommandLocationException;
import ru.varlamova.controller.executor.mapping.Mapping;
import ru.varlamova.controller.executor.mapping.UrlMapping;
import ru.varlamova.controller.meta.Controller;
import ru.varlamova.controller.meta.RequestMapping;
import ru.varlamova.http.enums.HttpMethod;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MappingFactory {
    private final Map<HttpMethod, Mapping> controllerMapping = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();
    private final String classPath;
    private static final Logger log = Logger.getLogger(MappingFactory.class.getName());

    private MappingFactory() {
        URL classPathResource = this.getClass().getClassLoader().getResource(".");
        this.classPath = classPathResource != null ? classPathResource.getPath() : "";
    }

    private static class MappingFactoryHolder {
        private final static MappingFactory factory = new MappingFactory();
    }

    public static MappingFactory getInstance() {
        return MappingFactoryHolder.factory;
    }

    Map<HttpMethod, Mapping> createMappings(String controllerLocationPackage) {
        File controllersDir = new File(classPath + controllerLocationPackage.replace(".", "/"));
        if (!controllersDir.isDirectory()) {
            throw new CommandLocationException("Controller mapping package doesn't exist");
        }
        for (String fileName : controllersDir.list()) {
            try {
                Class<?> clazz = Class.forName(controllerLocationPackage
                        + "." + fileName.replaceAll(".class", ""));
                if (clazz.getAnnotation(Controller.class) != null) {
                    for (Method method : clazz.getMethods()) {
                        Annotation annotation = method.getAnnotation(RequestMapping.class);
                        if (annotation != null) {
                            controllers.putIfAbsent(clazz.getName(), clazz.newInstance());
                            RequestMapping rmAnnotation = (RequestMapping) annotation;
                            Mapping mapping = controllerMapping.get(rmAnnotation.method());
                            if (mapping == null) {
                                mapping = new UrlMapping(null);
                                controllerMapping.put(rmAnnotation.method(), mapping);
                            }
                            mapping.addMapping(new ArrayDeque<>(Arrays.asList(rmAnnotation.path().split("/"))),
                                    method, controllers.get(clazz.getName()));
                            log.info("Controller " + clazz.getSimpleName() + "#" + method.getName()
                                    + " mapped to " + rmAnnotation.method() + " " + rmAnnotation.path());
                        }
                    }
                }
            } catch (ClassNotFoundException ignore) {
                log.warning("Can't find Controller class. Skipping.");
            } catch (InstantiationException | IllegalAccessException e) {
                log.warning("Can't instantiate controllers class. Skipping.");
            }
        }
        return new HashMap<>(controllerMapping);
    }
}
