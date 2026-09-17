package com.abonnements.service;

import com.abonnements.dao.AbonnementDAO;
import com.abonnements.dao.PaiementDAO;
import com.abonnements.entity.Abonnement;
import com.abonnements.entity.AbonnementAvecEngagement;
import com.abonnements.entity.Paiement;
import com.abonnements.entity.StatutPaiement;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des paiements :
 * enregistrement, modification, suppression, détection des impayés,
 * génération de rapports financiers (mensuels, annuels, impayés).
 */
public class PaiementService {

    private final PaiementDAO paiementDAO;
    private final AbonnementDAO abonnementDAO;

    public PaiementService(PaiementDAO paiementDAO, AbonnementDAO abonnementDAO) {
        this.paiementDAO = paiementDAO;
        this.abonnementDAO = abonnementDAO;
    }

    /**
     * Enregistre un paiement (le marque comme payé à la date donnée).
     */
    public Paiement enregistrerPaiement(String idPaiement, LocalDate datePaiement) {
        Paiement paiement = paiementDAO.findById(idPaiement)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable : " + idPaiement));
        paiement.enregistrerPaiement(datePaiement);
        return paiementDAO.update(paiement);
    }

    /**
     * Modifie le type et/ou la date d'échéance d'un paiement existant.
     */
    public Paiement modifierPaiement(String idPaiement, LocalDate nouvelleDateEcheance, String nouveauType) {
        Paiement paiement = paiementDAO.findById(idPaiement)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable : " + idPaiement));

        if (nouvelleDateEcheance != null) {
            paiement.setDateEcheance(nouvelleDateEcheance);
        }
        if (nouveauType != null && !nouveauType.isEmpty()) {
            paiement.setTypePaiement(nouveauType);
        }
        return paiementDAO.update(paiement);
    }

    /**
     * Supprime définitivement un paiement.
     */
    public boolean supprimerPaiement(String idPaiement) {
        return paiementDAO.delete(idPaiement);
    }

    /**
     * Recalcule le statut (NON_PAYE / EN_RETARD) de tous les paiements non réglés
     * en fonction de la date du jour. À appeler avant toute détection d'impayés.
     */
    public void detecterImpayes() {
        LocalDate aujourdHui = LocalDate.now();
        paiementDAO.findAll().stream()
                .filter(p -> p.getStatut() != StatutPaiement.PAYE)
                .forEach(p -> {
                    p.actualiserStatut(aujourdHui);
                    paiementDAO.update(p);
                });
    }

    /**
     * Retourne les paiements manqués (non payés ou en retard) d'un abonnement,
     * avec le montant total impayé. Ne s'applique qu'aux abonnements avec engagement,
     * conformément au cahier des charges.
     */
    public Map<String, Object> getImpayesAvecMontant(String idAbonnement) {
        Abonnement abonnement = abonnementDAO.findById(idAbonnement)
                .orElseThrow(() -> new IllegalArgumentException("Abonnement introuvable : " + idAbonnement));

        if (!(abonnement instanceof AbonnementAvecEngagement)) {
            throw new IllegalStateException(
                    "Le suivi des impayés avec montant total ne s'applique qu'aux abonnements avec engagement.");
        }

        List<Paiement> impayes = paiementDAO.findUnpaidByAbonnement(idAbonnement);
        double montantTotal = impayes.size() * abonnement.getMontantMensuel();

        Map<String, Object> resultat = new java.util.HashMap<>();
        resultat.put("paiementsImpayes", impayes);
        resultat.put("montantTotalImpaye", montantTotal);
        return resultat;
    }

    /**
     * Calcule la somme totale payée pour un abonnement donné.
     */
    public double getSommePayee(String idAbonnement) {
        return paiementDAO.findByAbonnement(idAbonnement).stream()
                .filter(p -> p.getStatut() == StatutPaiement.PAYE)
                .mapToDouble(p -> abonnementDAO.findById(idAbonnement)
                        .map(Abonnement::getMontantMensuel)
                        .orElse(0.0))
                .sum();
    }

    /**
     * Retourne les 5 derniers paiements enregistrés (toutes souscriptions confondues).
     */
    public List<Paiement> getDerniersPaiements() {
        return paiementDAO.findLastPayments();
    }

    /**
     * Génère un rapport financier mensuel : montant total payé par mois (yyyy-MM),
     * en utilisant Stream API et Collectors.groupingBy.
     */
    public Map<String, Double> rapportMensuel() {
        return paiementDAO.findAll().stream()
                .filter(p -> p.getStatut() == StatutPaiement.PAYE)
                .collect(Collectors.groupingBy(
                        p -> p.getDatePaiement().getYear() + "-" + String.format("%02d", p.getDatePaiement().getMonthValue()),
                        Collectors.summingDouble(p -> abonnementDAO.findById(p.getIdAbonnement())
                                .map(Abonnement::getMontantMensuel)
                                .orElse(0.0))
                ));
    }

    /**
     * Génère un rapport financier annuel : montant total payé par année,
     * en utilisant Stream API et Collectors.groupingBy.
     */
    public Map<Integer, Double> rapportAnnuel() {
        return paiementDAO.findAll().stream()
                .filter(p -> p.getStatut() == StatutPaiement.PAYE)
                .collect(Collectors.groupingBy(
                        p -> p.getDatePaiement().getYear(),
                        Collectors.summingDouble(p -> abonnementDAO.findById(p.getIdAbonnement())
                                .map(Abonnement::getMontantMensuel)
                                .orElse(0.0))
                ));
    }

    /**
     * Génère un rapport des impayés : liste de tous les paiements NON_PAYE ou EN_RETARD,
     * groupés par idAbonnement.
     */
    public Map<String, List<Paiement>> rapportImpayes() {
        detecterImpayes();
        return paiementDAO.findAll().stream()
                .filter(p -> p.getStatut() != StatutPaiement.PAYE)
                .collect(Collectors.groupingBy(Paiement::getIdAbonnement));
    }
}
