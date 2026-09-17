package com.abonnements.service;

import com.abonnements.dao.AbonnementDAO;
import com.abonnements.dao.PaiementDAO;
import com.abonnements.entity.Abonnement;
import com.abonnements.entity.AbonnementAvecEngagement;
import com.abonnements.entity.Paiement;
import com.abonnements.entity.StatutAbonnement;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service métier pour la gestion des abonnements :
 * création, modification, suppression, résiliation, génération d'échéances.
 */
public class AbonnementService {

    private final AbonnementDAO abonnementDAO;
    private final PaiementDAO paiementDAO;

    public AbonnementService(AbonnementDAO abonnementDAO, PaiementDAO paiementDAO) {
        this.abonnementDAO = abonnementDAO;
        this.paiementDAO = paiementDAO;
    }

    /**
     * Crée un abonnement sans engagement.
     */
    public Abonnement creerAbonnementSansEngagement(String nomService, double montantMensuel,
                                                     LocalDate dateDebut, LocalDate dateFin) {
        validerDonneesAbonnement(nomService, montantMensuel, dateDebut);
        Abonnement abonnement = new com.abonnements.entity.AbonnementSansEngagement(
                nomService, montantMensuel, dateDebut, dateFin);
        return abonnementDAO.create(abonnement);
    }

    /**
     * Crée un abonnement avec engagement.
     */
    public Abonnement creerAbonnementAvecEngagement(String nomService, double montantMensuel,
                                                     LocalDate dateDebut, LocalDate dateFin,
                                                     int dureeEngagementMois) {
        validerDonneesAbonnement(nomService, montantMensuel, dateDebut);
        if (dureeEngagementMois <= 0) {
            throw new IllegalArgumentException("La durée d'engagement doit être positive.");
        }
        Abonnement abonnement = new AbonnementAvecEngagement(
                nomService, montantMensuel, dateDebut, dateFin, dureeEngagementMois);
        return abonnementDAO.create(abonnement);
    }

    /**
     * Modifie le nom du service et/ou le montant mensuel d'un abonnement existant.
     */
    public Abonnement modifierAbonnement(String id, String nouveauNomService, Double nouveauMontant) {
        Abonnement abonnement = abonnementDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Abonnement introuvable : " + id));

        if (nouveauNomService != null && !nouveauNomService.isEmpty()) {
            abonnement.setNomService(nouveauNomService);
        }
        if (nouveauMontant != null) {
            if (nouveauMontant < 0) {
                throw new IllegalArgumentException("Le montant mensuel ne peut pas être négatif.");
            }
            abonnement.setMontantMensuel(nouveauMontant);
        }
        return abonnementDAO.update(abonnement);
    }

    /**
     * Supprime définitivement un abonnement.
     */
    public boolean supprimerAbonnement(String id) {
        return abonnementDAO.delete(id);
    }

    /**
     * Résilie un abonnement (le statut passe à RESILIE).
     */
    public Abonnement resilierAbonnement(String id) {
        Abonnement abonnement = abonnementDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Abonnement introuvable : " + id));
        abonnement.resilier();
        return abonnementDAO.update(abonnement);
    }

    /**
     * Génère les échéances de paiement mensuelles d'un abonnement, de sa date de début
     * jusqu'à aujourd'hui (ou jusqu'à sa date de fin si elle est antérieure).
     * Utilise Stream API pour construire la liste des échéances.
     */
    public List<Paiement> genererEcheances(String idAbonnement) {
        Abonnement abonnement = abonnementDAO.findById(idAbonnement)
                .orElseThrow(() -> new IllegalArgumentException("Abonnement introuvable : " + idAbonnement));

        LocalDate debut = abonnement.getDateDebut();
        LocalDate limite = abonnement.getDateFin() != null && abonnement.getDateFin().isBefore(LocalDate.now())
                ? abonnement.getDateFin()
                : LocalDate.now();

        long nombreMois = ChronoUnit.MONTHS.between(
                debut.withDayOfMonth(1), limite.withDayOfMonth(1));

        return Stream.iterate(debut, d -> d.plusMonths(1))
                .limit(nombreMois + 1)
                .map(dateEcheance -> paiementDAO.create(
                        new Paiement(abonnement.getId(), dateEcheance, "Mensuel")))
                .collect(Collectors.toList());
    }

    public Optional<Abonnement> findById(String id) {
        return abonnementDAO.findById(id);
    }

    public List<Abonnement> findAll() {
        return abonnementDAO.findAll();
    }

    public List<Abonnement> findActiveSubscriptions() {
        return abonnementDAO.findActiveSubscriptions();
    }

    private void validerDonneesAbonnement(String nomService, double montantMensuel, LocalDate dateDebut) {
        if (nomService == null || nomService.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du service est obligatoire.");
        }
        if (montantMensuel < 0) {
            throw new IllegalArgumentException("Le montant mensuel ne peut pas être négatif.");
        }
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début est obligatoire.");
        }
    }
}