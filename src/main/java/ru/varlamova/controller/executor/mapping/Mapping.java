package ru.varlamova.controller.executor.mapping;

import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Queue;

public interface Mapping {

    Triple<Method, Object, List<Object>> getMapping(Queue<String> urlContextQueue, List<Object> argList);

    void addMapping(Queue<String> urlContextQueue, Method method, Object controller);

}
