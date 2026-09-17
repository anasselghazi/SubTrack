package com.abonnements.ui;

import com.abonnements.dao.AbonnementDAO;
import com.abonnements.dao.PaiementDAO;
import com.abonnements.entity.Abonnement;
import com.abonnements.entity.Paiement;
import com.abonnements.service.AbonnementService;
import com.abonnements.service.PaiementService;
import com.abonnements.util.DateUtil;
import com.abonnements.util.FormatUtil;
import com.abonnements.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Point d'entrée de l'application : menu console de gestion des abonnements.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final AbonnementDAO abonnementDAO = new AbonnementDAO();
    private static final PaiementDAO paiementDAO = new PaiementDAO();
    private static final AbonnementService abonnementService = new AbonnementService(abonnementDAO, paiementDAO);
    private static final PaiementService paiementService = new PaiementService(paiementDAO, abonnementDAO);

    public static void main(String[] args) {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine().trim();
            try {
                continuer = traiterChoix(choix);
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Une erreur inattendue est survenue : " + e.getMessage());
            }
        }
        System.out.println("Au revoir !");
    }

    private static void afficherMenu() {
        System.out.println();
        System.out.println(FormatUtil.titre("GESTION DES ABONNEMENTS"));
        System.out.println(" 1. Créer un abonnement");
        System.out.println(" 2. Modifier un abonnement");
        System.out.println(" 3. Supprimer un abonnement");
        System.out.println(" 4. Consulter la liste des abonnements");
        System.out.println(" 5. Afficher les paiements d'un abonnement");
        System.out.println(" 6. Enregistrer un paiement");
        System.out.println(" 7. Modifier un paiement");
        System.out.println(" 8. Supprimer un paiement");
        System.out.println(" 9. Consulter les paiements manqués (avec engagement)");
        System.out.println("10. Afficher la somme payée d'un abonnement");
        System.out.println("11. Afficher les 5 derniers paiements");
        System.out.println("12. Générer un rapport mensuel");
        System.out.println("13. Générer un rapport annuel");
        System.out.println("14. Générer un rapport des impayés");
        System.out.println("15. Résilier un abonnement");
        System.out.println("16. Générer les échéances d'un abonnement");
        System.out.println(" 0. Quitter");
        System.out.print("Votre choix : ");
    }

    private static boolean traiterChoix(String choix) {
        switch (choix) {
            case "1": creerAbonnement(); break;
            case "2": modifierAbonnement(); break;
            case "3": supprimerAbonnement(); break;
            case "4": listerAbonnements(); break;
            case "5": afficherPaiementsAbonnement(); break;
            case "6": enregistrerPaiement(); break;
            case "7": modifierPaiement(); break;
            case "8": supprimerPaiement(); break;
            case "9": afficherImpayes(); break;
            case "10": afficherSommePayee(); break;
            case "11": afficherDerniersPaiements(); break;
            case "12": afficherRapportMensuel(); break;
            case "13": afficherRapportAnnuel(); break;
            case "14": afficherRapportImpayes(); break;
            case "15": resilierAbonnement(); break;
            case "16": genererEcheances(); break;
            case "0": return false;
            default: System.out.println("Choix invalide, réessayez.");
        }
        return true;
    }

    // ---------- Abonnements ----------

    private static void creerAbonnement() {
        System.out.print("Nom du service : ");
        String nom = scanner.nextLine();
        ValidationUtil.validerNonVide(nom, "nom du service");

        System.out.print("Montant mensuel : ");
        double montant = ValidationUtil.parseDouble(scanner.nextLine(), "montant mensuel");

        System.out.print("Date de début (jj/MM/aaaa) : ");
        LocalDate dateDebut = DateUtil.parse(scanner.nextLine());

        System.out.print("Date de fin (jj/MM/aaaa, laisser vide si aucune) : ");
        String dateFinStr = scanner.nextLine();
        LocalDate dateFin = dateFinStr.trim().isEmpty() ? null : DateUtil.parse(dateFinStr);

        System.out.print("Avec engagement ? (o/n) : ");
        String avecEngagement = scanner.nextLine().trim().toLowerCase();

        Abonnement abonnement;
        if (avecEngagement.equals("o")) {
            System.out.print("Durée d'engagement (en mois) : ");
            int duree = ValidationUtil.parseInt(scanner.nextLine(), "durée d'engagement");
            abonnement = abonnementService.creerAbonnementAvecEngagement(nom, montant, dateDebut, dateFin, duree);
        } else {
            abonnement = abonnementService.creerAbonnementSansEngagement(nom, montant, dateDebut, dateFin);
        }
        System.out.println("Abonnement créé avec succès : " + abonnement);
    }

    private static void modifierAbonnement() {
        System.out.print("ID de l'abonnement à modifier : ");
        String id = scanner.nextLine().trim();

        System.out.print("Nouveau nom du service (laisser vide pour ne pas changer) : ");
        String nom = scanner.nextLine();

        System.out.print("Nouveau montant mensuel (laisser vide pour ne pas changer) : ");
        String montantStr = scanner.nextLine();
        Double montant = montantStr.trim().isEmpty() ? null : ValidationUtil.parseDouble(montantStr, "montant mensuel");

        Abonnement abonnement = abonnementService.modifierAbonnement(id, nom.trim().isEmpty() ? null : nom, montant);
        System.out.println("Abonnement modifié : " + abonnement);
    }

    private static void supprimerAbonnement() {
        System.out.print("ID de l'abonnement à supprimer : ");
        String id = scanner.nextLine().trim();
        boolean supprime = abonnementService.supprimerAbonnement(id);
        System.out.println(supprime ? "Abonnement supprimé." : "Aucun abonnement trouvé avec cet ID.");
    }

    private static void resilierAbonnement() {
        System.out.print("ID de l'abonnement à résilier : ");
        String id = scanner.nextLine().trim();
        Abonnement abonnement = abonnementService.resilierAbonnement(id);
        System.out.println("Abonnement résilié : " + abonnement);
    }

    private static void listerAbonnements() {
        List<Abonnement> abonnements = abonnementService.findAll();
        if (abonnements.isEmpty()) {
            System.out.println("Aucun abonnement enregistré.");
            return;
        }
        abonnements.forEach(a -> System.out.println(
                "- [" + a.getId() + "] " + a.getNomService()
                        + " | " + a.getTypeAbonnement()
                        + " | " + FormatUtil.formatMontant(a.getMontantMensuel())
                        + " | statut=" + a.getStatut()
                        + " | début=" + DateUtil.format(a.getDateDebut())));
    }

    private static void genererEcheances() {
        System.out.print("ID de l'abonnement : ");
        String id = scanner.nextLine().trim();
        List<Paiement> echeances = abonnementService.genererEcheances(id);
        System.out.println(echeances.size() + " échéance(s) générée(s).");
    }

    // ---------- Paiements ----------

    private static void afficherPaiementsAbonnement() {
        System.out.print("ID de l'abonnement : ");
        String id = scanner.nextLine().trim();
        List<Paiement> paiements = paiementDAO.findByAbonnement(id);
        if (paiements.isEmpty()) {
            System.out.println("Aucun paiement pour cet abonnement.");
            return;
        }
        paiements.forEach(p -> System.out.println(
                "- [" + p.getIdPaiement() + "] échéance=" + DateUtil.format(p.getDateEcheance())
                        + " | statut=" + p.getStatut()
                        + " | payé le=" + DateUtil.format(p.getDatePaiement())));
    }

    private static void enregistrerPaiement() {
        System.out.print("ID du paiement : ");
        String id = scanner.nextLine().trim();
        System.out.print("Date de paiement (jj/MM/aaaa) : ");
        LocalDate date = DateUtil.parse(scanner.nextLine());
        Paiement paiement = paiementService.enregistrerPaiement(id, date);
        System.out.println("Paiement enregistré : " + paiement);
    }

    private static void modifierPaiement() {
        System.out.print("ID du paiement à modifier : ");
        String id = scanner.nextLine().trim();

        System.out.print("Nouvelle date d'échéance (jj/MM/aaaa, laisser vide pour ne pas changer) : ");
        String dateStr = scanner.nextLine();
        LocalDate nouvelleDate = dateStr.trim().isEmpty() ? null : DateUtil.parse(dateStr);

        System.out.print("Nouveau type de paiement (laisser vide pour ne pas changer) : ");
        String type = scanner.nextLine();

        Paiement paiement = paiementService.modifierPaiement(id, nouvelleDate, type.trim().isEmpty() ? null : type);
        System.out.println("Paiement modifié : " + paiement);
    }

    private static void supprimerPaiement() {
        System.out.print("ID du paiement à supprimer : ");
        String id = scanner.nextLine().trim();
        boolean supprime = paiementService.supprimerPaiement(id);
        System.out.println(supprime ? "Paiement supprimé." : "Aucun paiement trouvé avec cet ID.");
    }

    private static void afficherImpayes() {
        System.out.print("ID de l'abonnement (avec engagement) : ");
        String id = scanner.nextLine().trim();
        Map<String, Object> resultat = paiementService.getImpayesAvecMontant(id);

        @SuppressWarnings("unchecked")
        List<Paiement> impayes = (List<Paiement>) resultat.get("paiementsImpayes");
        double montantTotal = (double) resultat.get("montantTotalImpaye");

        if (impayes.isEmpty()) {
            System.out.println("Aucun paiement manqué pour cet abonnement.");
        } else {
            impayes.forEach(p -> System.out.println(
                    "- échéance=" + DateUtil.format(p.getDateEcheance()) + " | statut=" + p.getStatut()));
        }
        System.out.println("Montant total impayé : " + FormatUtil.formatMontant(montantTotal));
    }

    private static void afficherSommePayee() {
        System.out.print("ID de l'abonnement : ");
        String id = scanner.nextLine().trim();
        double somme = paiementService.getSommePayee(id);
        System.out.println("Somme totale payée : " + FormatUtil.formatMontant(somme));
    }

    private static void afficherDerniersPaiements() {
        List<Paiement> derniers = paiementService.getDerniersPaiements();
        if (derniers.isEmpty()) {
            System.out.println("Aucun paiement enregistré.");
            return;
        }
        derniers.forEach(p -> System.out.println(
                "- [" + p.getIdPaiement() + "] échéance=" + DateUtil.format(p.getDateEcheance())
                        + " | statut=" + p.getStatut()));
    }

    // ---------- Rapports ----------

    private static void afficherRapportMensuel() {
        Map<String, Double> rapport = paiementService.rapportMensuel();
        if (rapport.isEmpty()) {
            System.out.println("Aucune donnée pour le rapport mensuel.");
            return;
        }
        rapport.forEach((mois, montant) -> System.out.println(mois + " : " + FormatUtil.formatMontant(montant)));
    }

    private static void afficherRapportAnnuel() {
        Map<Integer, Double> rapport = paiementService.rapportAnnuel();
        if (rapport.isEmpty()) {
            System.out.println("Aucune donnée pour le rapport annuel.");
            return;
        }
        rapport.forEach((annee, montant) -> System.out.println(annee + " : " + FormatUtil.formatMontant(montant)));
    }

    private static void afficherRapportImpayes() {
        Map<String, List<Paiement>> rapport = paiementService.rapportImpayes();
        if (rapport.isEmpty()) {
            System.out.println("Aucun impayé détecté.");
            return;
        }
        rapport.forEach((idAbonnement, paiements) -> {
            System.out.println("Abonnement [" + idAbonnement + "] : " + paiements.size() + " impayé(s)");
            paiements.forEach(p -> System.out.println(
                    "   - échéance=" + DateUtil.format(p.getDateEcheance()) + " | statut=" + p.getStatut()));
        });
    }
}