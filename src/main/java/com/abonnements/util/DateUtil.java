package com.abonnements.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilitaires de gestion et de formatage des dates (format attendu : jj/MM/aaaa).
 */
public final class DateUtil {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtil() {
        // classe utilitaire, non instanciable
    }

    /**
     * Convertit une chaîne "jj/MM/aaaa" en LocalDate.
     * Lève une IllegalArgumentException avec message clair si le format est invalide.
     */
    public static LocalDate parse(String texte) {
        try {
            return LocalDate.parse(texte.trim(), FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Date invalide : '" + texte + "'. Format attendu : jj/MM/aaaa");
        }
    }

    /**
     * Formate une LocalDate en chaîne "jj/MM/aaaa". Retourne "-" si la date est nulle.
     */
    public static String format(LocalDate date) {
        return date == null ? "-" : date.format(FORMAT);
    }

    /**
     * Indique si une date est antérieure à aujourd'hui.
     */
    public static boolean estDansLePasse(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }
}