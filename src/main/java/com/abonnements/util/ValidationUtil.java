package com.abonnements.util;

/**
 * Utilitaires de validation des données saisies par l'utilisateur.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        // classe utilitaire, non instanciable
    }

    /**
     * Vérifie qu'une chaîne n'est ni nulle ni vide. Lève une IllegalArgumentException sinon.
     */
    public static void validerNonVide(String valeur, String nomChamp) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est obligatoire.");
        }
    }

    /**
     * Vérifie qu'un montant est positif ou nul. Lève une IllegalArgumentException sinon.
     */
    public static void validerMontantPositif(double montant, String nomChamp) {
        if (montant < 0) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' ne peut pas être négatif.");
        }
    }

    /**
     * Vérifie qu'un entier est strictement positif. Lève une IllegalArgumentException sinon.
     */
    public static void validerEntierPositif(int valeur, String nomChamp) {
        if (valeur <= 0) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' doit être strictement positif.");
        }
    }

    /**
     * Convertit une chaîne en double, avec message d'erreur clair en cas d'échec.
     */
    public static double parseDouble(String valeur, String nomChamp) {
        try {
            return Double.parseDouble(valeur.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Le champ '" + nomChamp + "' doit être un nombre valide (ex : 9.99).");
        }
    }

    /**
     * Convertit une chaîne en entier, avec message d'erreur clair en cas d'échec.
     */
    public static int parseInt(String valeur, String nomChamp) {
        try {
            return Integer.parseInt(valeur.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Le champ '" + nomChamp + "' doit être un nombre entier valide.");
        }
    }
}
