package com.abonnements.entity;

import java.time.LocalDate;

/**
 * Abonnement résiliable à tout moment, sans durée d'engagement minimale
 * (ex : abonnement streaming mensuel sans engagement).
 */
public class AbonnementSansEngagement extends Abonnement {

    public AbonnementSansEngagement(String nomService, double montantMensuel,
                                     LocalDate dateDebut, LocalDate dateFin) {
        super(nomService, montantMensuel, dateDebut, dateFin);
    }

    @Override
    public String getTypeAbonnement() {
        return "AbonnementSansEngagement";
    }
}