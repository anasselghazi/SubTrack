package com.abonnements.entity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Représente un paiement (échéance) rattaché à un abonnement.
 * Relation : 1 Abonnement -> n Paiement.
 */
public class Paiement {

    private final String idPaiement;
    private final String idAbonnement;
    private LocalDate dateEcheance;
    private LocalDate datePaiement;
    private String typePaiement;
    private StatutPaiement statut;

    public Paiement(String idAbonnement, LocalDate dateEcheance, String typePaiement) {
        this.idPaiement = UUID.randomUUID().toString();
        this.idAbonnement = idAbonnement;
        this.dateEcheance = dateEcheance;
        this.typePaiement = typePaiement;
        this.statut = StatutPaiement.NON_PAYE;
        this.datePaiement = null;
    }

    public String getIdPaiement() {
        return idPaiement;
    }

    public String getIdAbonnement() {
        return idAbonnement;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(String typePaiement) {
        this.typePaiement = typePaiement;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    /**
     * Marque le paiement comme réglé à la date donnée.
     */
    public void enregistrerPaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
        this.statut = StatutPaiement.PAYE;
    }

    /**
     * Recalcule le statut EN_RETARD si l'échéance est dépassée et qu'aucun paiement n'a été enregistré.
     */
    public void actualiserStatut(LocalDate dateReference) {
        if (statut == StatutPaiement.PAYE) {
            return;
        }
        this.statut = dateReference.isAfter(dateEcheance) ? StatutPaiement.EN_RETARD : StatutPaiement.NON_PAYE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paiement)) return false;
        Paiement paiement = (Paiement) o;
        return Objects.equals(idPaiement, paiement.idPaiement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaiement);
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "idPaiement='" + idPaiement + '\'' +
                ", idAbonnement='" + idAbonnement + '\'' +
                ", dateEcheance=" + dateEcheance +
                ", datePaiement=" + datePaiement +
                ", typePaiement='" + typePaiement + '\'' +
                ", statut=" + statut +
                '}';
    }
}