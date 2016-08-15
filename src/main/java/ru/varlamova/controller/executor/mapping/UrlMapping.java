package ru.varlamova.controller.executor.mapping;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.varlamova.controller.executor.exception.MappingException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class UrlMapping implements Mapping {

    private final Map<String, UrlMapping> mappings = new HashMap<>();
    private Pair<Method, Object> methodControllerMapping;
    private UrlMapping parent;

    public UrlMapping(UrlMapping parent) {
        this.parent = parent;
    }

    @Override
    public Triple<Method, Object, List<Object>> getMapping(Queue<String> urlContextQueue, List<Object> argList) {
        String context = urlContextQueue.poll();
        if (context == null && methodControllerMapping != null) {
            return Triple.of(methodControllerMapping.getLeft(), methodControllerMapping.getRight(), argList);
        }
        UrlMapping child = mappings.get(context);
        if (child != null) {
            return child.getMapping(urlContextQueue, argList);
        }
        List<UrlMapping> paramMappings =
                mappings.entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith("#"))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());

        return paramMappings.stream()
                .map(elems -> elems.getMapping(new ArrayDeque<>(urlContextQueue),
                        new LinkedList<Object>() {{
                            addAll(argList);
                            add(context);
                        }}))
                .filter(elem -> elem != null).findFirst().orElse(null);
    }

    @Override
    public void addMapping(Queue<String> urlContextQueue, Method method, Object controller) {
        String context = urlContextQueue.poll();
        if (context == null) {
            if (methodControllerMapping != null) {
                throw new MappingException("Duplicate mapping for url, check configuration");
            }
            methodControllerMapping = Pair.of(method, controller);
            return;
        }

        UrlMapping child = mappings.get(context);
        if (child != null) {
            child.addMapping(urlContextQueue, method, controller);
        } else {
            if (context.startsWith("#")) {
                long duplicateMappingCount =
                        mappings.entrySet().stream()
                                .filter(entry -> entry.getKey().startsWith("#"))
                                .map(entry -> entry.getValue().getMapping(new ArrayDeque<>(urlContextQueue),
                                        new LinkedList<>()))
                                .filter(elem -> elem != null)
                                .count();
                if (duplicateMappingCount > 0) {
                    throw new MappingException("Duplicate mapping for url, check configuration");
                }
            }

            UrlMapping mapping = new UrlMapping(this);
            mappings.put(context, mapping);
            mapping.addMapping(urlContextQueue, method, controller);
        }
    }

}
