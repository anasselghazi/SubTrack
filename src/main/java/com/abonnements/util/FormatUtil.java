package com.abonnements.util;

import java.util.Locale;

/**
 * Utilitaires de formatage pour l'affichage console (montants, séparateurs).
 */
public final class FormatUtil {

    private FormatUtil() {
        // classe utilitaire, non instanciable
    }

    /**
     * Formate un montant en devise (ex : 49.9 -> "49.90 MAD").
     */
    public static String formatMontant(double montant) {
        return String.format(Locale.forLanguageTag("fr"), "%.2f MAD", montant);
    }

    /**
     * Retourne une ligne de séparation pour l'affichage console.
     */
    public static String separateur() {
        return "----------------------------------------";
    }

    /**
     * Centre un titre entre deux séparateurs pour l'affichage console.
     */
    public static String titre(String texte) {
        return separateur() + System.lineSeparator()
                + texte + System.lineSeparator()
                + separateur();
    }
}