package org.juandavid.annotations;

import org.juandavid.annotations.models.Producto;
import org.juandavid.annotations.procesador.JsonSerializador;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class EjemploAnotations {

    public static void main(String[] args) {
        Producto p = new Producto();
        p.setFecha(LocalDate.now());
        p.setNombre("mesa de centro");
        p.setPrecio(1000L);
        Field[] atributos = p.getClass().getDeclaredFields();

        System.out.println("json: " + JsonSerializador.convertirJson(p));

    }
}
