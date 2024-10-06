package com.vaadin.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.dom.Element;

import java.awt.*;

public class FarosUtils {

    public static String coalesce(JsonNode node) {
        if (node != null && !node.isNull()) return node.asText();
        else return null;
    }

    public static boolean empty(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Oscurece un color por el factor indicada. El parámetro debe estar en formato hexadecimal (ej: #FFDD55)
     * @param hexColor
     * @param factor
     * @return
     */
    public static String darkenColor(String hexColor, double factor) {
        // El factor debe estar entre 0 y 1, donde 1 no oscurece y 0 hace el color negro
        if (factor < 0 || factor > 1) {
            throw new IllegalArgumentException("El factor debe estar entre 0 y 1");
        }

        // Convertir el color hexadecimal a un objeto Color
        Color color = Color.decode(hexColor);

        // Oscurecer los componentes RGB
        int red = (int) (color.getRed() * factor);
        int green = (int) (color.getGreen() * factor);
        int blue = (int) (color.getBlue() * factor);

        // Crear el nuevo color oscurecido
        Color darkenedColor = new Color(red, green, blue);

        // Convertir el color oscurecido a formato hexadecimal y devolverlo como #RRGGBB
        return String.format("#%02X%02X%02X", darkenedColor.getRed(), darkenedColor.getGreen(), darkenedColor.getBlue());
    }

    // Método para aclarar un color dado en formato hexadecimal y devolverlo en la misma forma
    public static String lightenColor(String hexColor, double factor) {
        // El factor debe estar entre 0 y 1, donde 0 no aclara y 1 lo hace blanco
        if (factor < 0 || factor > 1) {
            throw new IllegalArgumentException("El factor debe estar entre 0 y 1");
        }

        // Convertir el color hexadecimal a un objeto Color
        Color color = Color.decode(hexColor);

        // Aclarar los componentes RGB, moviéndolos hacia 255
        int red = (int) (color.getRed() + (255 - color.getRed()) * factor);
        int green = (int) (color.getGreen() + (255 - color.getGreen()) * factor);
        int blue = (int) (color.getBlue() + (255 - color.getBlue()) * factor);

        // Crear el nuevo color aclarado
        Color lightenedColor = new Color(red, green, blue);

        // Convertir el color aclarado a formato hexadecimal y devolverlo como #RRGGBB
        return String.format("#%02X%02X%02X", lightenedColor.getRed(), lightenedColor.getGreen(), lightenedColor.getBlue());
    }

    public static void setTitle(Element element, String text) {
        if (element == null) return;
        element.setAttribute("title", text);
    }

}
