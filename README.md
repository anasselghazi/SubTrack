# Gestion des Abonnements

Application console Java 8 permettant de centraliser la gestion d'abonnements personnels et professionnels : suivi des échéances, détection des impayés, et génération de rapports financiers.

Projet réalisé dans le cadre du Sprint 1 — Brief 2.

## Contexte

La gestion des abonnements (streaming, musique, assurances, logiciels, outils collaboratifs, services cloud...) est devenue complexe à suivre. Cette application centralise ce suivi pour apporter plus de visibilité et de contrôle sur les échéances et le coût réel/prévisionnel des abonnements.

## Fonctionnalités

- Création d'abonnements **avec** ou **sans engagement**
- Modification, suppression et résiliation d'un abonnement
- Consultation de la liste des abonnements
- Génération automatique des échéances de paiement
- Enregistrement, modification et suppression de paiements
- Détection des paiements manqués et calcul du montant total impayé (abonnements avec engagement)
- Affichage de la somme totale payée pour un abonnement
- Affichage des 5 derniers paiements
- Génération de rapports financiers : **mensuel**, **annuel**, **impayés**
- Gestion des exceptions avec messages clairs

## Architecture

L'application respecte une architecture en couches :