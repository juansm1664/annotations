package org.juandavid.annotations.procesador;

import org.juandavid.annotations.Init;
import org.juandavid.annotations.JsonAtributo;
import org.juandavid.annotations.procesador.exception.JsonSerializadorException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonSerializador {

    public static void inicializarObjeto(Object object){
        if(Objects.isNull(object)){
            throw new JsonSerializadorException("El objeto a serializar NO puede ser null");
        }

        Method[] metodos = object.getClass().getDeclaredMethods();
        Arrays.stream(metodos).filter(method -> method.isAnnotationPresent(Init.class))
                .forEach(method -> {
                    method.setAccessible(true);
                    try {
                        method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new JsonSerializadorException("Error al serializar objeto");
                    }

                });
    }

    public static  String convertirJson(Object object){

        if(Objects.isNull(object)){
            throw new JsonSerializadorException("El objeto a serializar NO puede ser null");
        }

        inicializarObjeto(object);

        Field[] atributos = object.getClass().getDeclaredFields();

            return Arrays.stream(atributos)
                .filter(f -> f.isAnnotationPresent(JsonAtributo.class))
                .map(f -> {
                    f.setAccessible(true);
                    String nombre = f.getAnnotation(JsonAtributo.class).nombre().isEmpty()
                            ? f.getName()
                            : f.getAnnotation(JsonAtributo.class).nombre();
                    try {
                        Object valor = f.get(object);
                        if(f.getAnnotation(JsonAtributo.class).capitalizar() &&
                                valor instanceof String){
                                String nuevoValor = (String) valor;

                               /* nuevoValor = nuevoValor.substring(0,1).toUpperCase()
                                        + nuevoValor.substring(1).toLowerCase();
                                f.set(object, nuevoValor);*/

                                nuevoValor = Arrays.stream(nuevoValor.split(" "))
                                        .map(palabra -> palabra.substring(0,1).toUpperCase()
                                        + palabra.substring(1).toLowerCase())
                                        .collect(Collectors.joining(" "));

                                f.set(object, nuevoValor);
                        }
                        return "\"" + nombre + "\":" + f.get(object) + "\"";
                    } catch (IllegalAccessException e) {

                        throw new JsonSerializadorException("Error al serializar a json :");
                    }
                })
                .reduce("{", (a, b) -> {
                    if ("{".equals(a)) {
                        return a + b;
                    }
                    return a + ", " + b;
                }).concat("}");
    }
}