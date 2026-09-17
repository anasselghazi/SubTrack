package com.abonnements.dao;

import com.abonnements.entity.Paiement;
import com.abonnements.entity.StatutPaiement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * DAO gérant la persistance en mémoire des {@link Paiement}.
 */
public class PaiementDAO implements Dao<Paiement, String> {

    private final Map<String, Paiement> paiements = new ConcurrentHashMap<>();

    @Override
    public Paiement create(Paiement paiement) {
        paiements.put(paiement.getIdPaiement(), paiement);
        return paiement;
    }

    @Override
    public Optional<Paiement> findById(String idPaiement) {
        return Optional.ofNullable(paiements.get(idPaiement));
    }

    @Override
    public List<Paiement> findAll() {
        return new ArrayList<>(paiements.values());
    }

    @Override
    public Paiement update(Paiement paiement) {
        paiements.put(paiement.getIdPaiement(), paiement);
        return paiement;
    }

    @Override
    public boolean delete(String idPaiement) {
        return paiements.remove(idPaiement) != null;
    }

    /**
     * Retourne tous les paiements liés à un abonnement donné.
     */
    public List<Paiement> findByAbonnement(String idAbonnement) {
        return paiements.values().stream()
                .filter(p -> p.getIdAbonnement().equals(idAbonnement))
                .collect(Collectors.toList());
    }

    /**
     * Retourne les paiements non payés (NON_PAYE ou EN_RETARD) d'un abonnement donné.
     */
    public List<Paiement> findUnpaidByAbonnement(String idAbonnement) {
        return paiements.values().stream()
                .filter(p -> p.getIdAbonnement().equals(idAbonnement))
                .filter(p -> p.getStatut() != StatutPaiement.PAYE)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les 5 derniers paiements (les plus récents en date d'échéance), tous abonnements confondus.
     */
    public List<Paiement> findLastPayments() {
        return paiements.values().stream()
                .sorted(Comparator.comparing(Paiement::getDateEcheance).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}