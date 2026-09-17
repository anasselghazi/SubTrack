package com.abonnements.entity;

import java.time.LocalDate;

/**
 * Abonnement soumis à une durée d'engagement minimale (ex : forfait mobile 12 mois).
 */
public class AbonnementAvecEngagement extends Abonnement {

    private int dureeEngagementMois;

    public AbonnementAvecEngagement(String nomService, double montantMensuel,
                                     LocalDate dateDebut, LocalDate dateFin,
                                     int dureeEngagementMois) {
        super(nomService, montantMensuel, dateDebut, dateFin);
        this.dureeEngagementMois = dureeEngagementMois;
    }

    public int getDureeEngagementMois() {
        return dureeEngagementMois;
    }

    public void setDureeEngagementMois(int dureeEngagementMois) {
        this.dureeEngagementMois = dureeEngagementMois;
    }

    /**
     * Calcule la date de fin d'engagement à partir de la date de début.
     */
    public LocalDate getDateFinEngagement() {
        return getDateDebut().plusMonths(dureeEngagementMois);
    }

    /**
     * Indique si l'abonnement est encore sous engagement à la date donnée.
     */
    public boolean estSousEngagement(LocalDate date) {
        return date.isBefore(getDateFinEngagement());
    }

    @Override
    public String getTypeAbonnement() {
        return "AbonnementAvecEngagement";
    }
}