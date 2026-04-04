package com.willwinder.universalgcodesender.services;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public class LookupService {

    private static final LinkedHashSet<Object> registry = new LinkedHashSet<>();

    public static synchronized void registerProviders(String... packageRoots) {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(packageRoots)
                .scan()) {

            scanResult.getClassesWithAnnotation(LookupServiceProvider.class.getName())
                    .loadClasses()
                    .stream()
                    .sorted(Comparator.comparingInt(LookupService::positionOf))
                    .forEach(LookupService::createAndRegister);
        }
    }

    public static void register(Object object) {
        if (lookup(object.getClass()).isPresent()) {
            return;
        }
        registry.add(object);
    }

    public static <T> Optional<T> lookup(Class<T> clazz) {
        return  lookupAll(clazz).stream().findFirst();
    }

    public static <T> List<T> lookupAll(Class<T> clazz) {
        return registry.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }

    private static int positionOf(Class<?> type) {
        LookupServiceProvider annotation = type.getAnnotation(LookupServiceProvider.class);
        return annotation != null ? annotation.position() : 0;
    }

    public static <T> T createAndRegister(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            register(instance);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to create service: " + type.getName(), e);
        }
    }
}