package com.abonnements.dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrat CRUD générique respecté par les DAO de l'application.
 *
 * @param <T>  type de l'entité gérée
 * @param <ID> type de l'identifiant de l'entité
 */
public interface Dao<T, ID> {

    T create(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    T update(T entity);

    boolean delete(ID id);
}