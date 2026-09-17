package com.abonnements.dao;

import com.abonnements.entity.Abonnement;
import com.abonnements.entity.StatutAbonnement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * DAO gérant la persistance en mémoire des {@link Abonnement}.
 */
public class AbonnementDAO implements Dao<Abonnement, String> {

    private final Map<String, Abonnement> abonnements = new ConcurrentHashMap<>();

    @Override
    public Abonnement create(Abonnement abonnement) {
        abonnements.put(abonnement.getId(), abonnement);
        return abonnement;
    }

    @Override
    public Optional<Abonnement> findById(String id) {
        return Optional.ofNullable(abonnements.get(id));
    }

    @Override
    public List<Abonnement> findAll() {
        return new java.util.ArrayList<>(abonnements.values());
    }

    @Override
    public Abonnement update(Abonnement abonnement) {
        abonnements.put(abonnement.getId(), abonnement);
        return abonnement;
    }

    @Override
    public boolean delete(String id) {
        return abonnements.remove(id) != null;
    }

    /**
     * Retourne tous les abonnements dont le statut est ACTIVE.
     */
    public List<Abonnement> findActiveSubscriptions() {
        return abonnements.values().stream()
                .filter(a -> a.getStatut() == StatutAbonnement.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Retourne tous les abonnements d'un type donné
     * (ex : "AbonnementAvecEngagement" ou "AbonnementSansEngagement").
     */
    public List<Abonnement> findByType(String typeAbonnement) {
        return abonnements.values().stream()
                .filter(a -> a.getTypeAbonnement().equalsIgnoreCase(typeAbonnement))
                .collect(Collectors.toList());
    }
}