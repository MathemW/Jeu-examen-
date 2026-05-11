import java.util.Scanner;
import java.util.Random;

/**
 * Jeu de la Devinette
 *
 * Concepts illustres :
 *   - Variables (int, boolean, String)
 *   - Tableaux (historique des essais, scores)
 *   - Boucles (while, for imbriques pour tri a bulles)
 *   - Conditions (if/else if/else, switch)
 *   - Fonctions recursives (dejaEssaye, calculerBonus, trouverMeilleur)
 */
public class JeuDevinette {

    // ---------------------------------------------------------------
    // Constante globale
    // ---------------------------------------------------------------
    static final int MAX_TENTATIVES = 8;

    // ---------------------------------------------------------------
    // Point d'entree
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        afficherBanniere();

        int[] historiqueScores = new int[10]; // stocke jusqu'a 10 parties
        int nombreParties = 0;
        boolean continuer = true;

        // Boucle principale du jeu
        while (continuer && nombreParties < historiqueScores.length) {
            int niveau = choisirNiveau(scanner);
            int score  = jouerUnePartie(scanner, niveau);

            historiqueScores[nombreParties] = score;
            nombreParties++;

            afficherHistorique(historiqueScores, nombreParties);

            System.out.print("\nVoulez-vous rejouer ? (o/n) : ");
            String rep = scanner.nextLine().trim().toLowerCase();
            continuer = rep.equals("o") || rep.equals("oui");
        }

        // Meilleur score : fonction recursive
        int meilleur = trouverMeilleur(historiqueScores, nombreParties - 1, 0);
        System.out.println("\n=== FIN DU JEU ===");
        System.out.println("Parties jouees  : " + nombreParties);
        System.out.println("Meilleur score  : " + meilleur + " pts");

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Affichage
    // ---------------------------------------------------------------
    static void afficherBanniere() {
        System.out.println("+================================+");
        System.out.println("|      JEU DE LA DEVINETTE       |");
        System.out.println("|  Boucles * Recursion * Arrays  |");
        System.out.println("+================================+");
    }

    static void afficherHistorique(int[] scores, int nb) {
        System.out.println("\n+-- Historique des scores --------+");
        for (int i = 0; i < nb; i++) {
            System.out.printf("|  Partie %-2d : %5d pts         |%n", i + 1, scores[i]);
        }
        System.out.println("+---------------------------------+");
    }

    // ---------------------------------------------------------------
    // Choix du niveau (boucle + switch + conditions)
    // ---------------------------------------------------------------
    static int choisirNiveau(Scanner scanner) {
        System.out.println("\n+-- Choisissez un niveau ---------+");
        System.out.println("|  1. Facile    (1 a 50)          |");
        System.out.println("|  2. Moyen     (1 a 100)         |");
        System.out.println("|  3. Difficile (1 a 200)         |");
        System.out.println("+---------------------------------+");

        int niveau = 0;
        while (niveau < 1 || niveau > 3) {
            System.out.print("Votre choix (1-3) : ");
            try {
                niveau = Integer.parseInt(scanner.nextLine().trim());
                if (niveau < 1 || niveau > 3) {
                    System.out.println("Entrez 1, 2 ou 3 uniquement.");
                    niveau = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide, veuillez entrer un chiffre.");
            }
        }
        return niveau;
    }

    // ---------------------------------------------------------------
    // Deroulement d'une partie (boucle while + conditions + tableaux)
    // ---------------------------------------------------------------
    static int jouerUnePartie(Scanner scanner, int niveau) {
        int borneMax;
        switch (niveau) {
            case 1:  borneMax = 50;  break;
            case 2:  borneMax = 100; break;
            default: borneMax = 200; break;
        }

        int nombreSecret = new Random().nextInt(borneMax) + 1;
        int[] tentatives = new int[MAX_TENTATIVES]; // tableau des essais
        int nbTentatives  = 0;
        boolean trouve    = false;

        System.out.println("\nJ'ai choisi un nombre entre 1 et " + borneMax + ".");
        System.out.println("Vous avez " + MAX_TENTATIVES + " essais.");

        // Boucle de jeu
        while (nbTentatives < MAX_TENTATIVES && !trouve) {
            System.out.print("Essai " + (nbTentatives + 1) + "/" + MAX_TENTATIVES + " -> ");

            int guess = lireEntier(scanner, 1, borneMax);
            if (guess == -1) continue; // entree invalide, on relance

            // Verification par recursivite : deja essaye ?
            if (dejaEssaye(tentatives, guess, nbTentatives)) {
                System.out.println("Vous avez deja essaye " + guess + " !");
                continue;
            }

            // Enregistrement dans le tableau
            tentatives[nbTentatives] = guess;
            nbTentatives++;

            // Condition principale
            if (guess < nombreSecret) {
                System.out.println("Trop petit ! Le nombre est plus grand.");
            } else if (guess > nombreSecret) {
                System.out.println("Trop grand ! Le nombre est plus petit.");
            } else {
                System.out.println("Bravo ! Vous avez trouve " + nombreSecret + " en " + nbTentatives + " essai(s) !");
                trouve = true;
            }

            // Indice a mi-parcours
            if (!trouve && nbTentatives == MAX_TENTATIVES / 2) {
                String parite = (nombreSecret % 2 == 0) ? "pair" : "impair";
                System.out.println("  [Indice] Le nombre est " + parite + ".");
            }
        }

        // Resultat
        if (!trouve) {
            System.out.println("Perdu ! Le nombre etait " + nombreSecret + ".");
            afficherEssayesTries(tentatives, nbTentatives);
            return 0;
        }

        afficherEssayesTries(tentatives, nbTentatives);

        // Calcul du score : base + bonus recursif
        int tentativesRestantes = MAX_TENTATIVES - nbTentatives;
        int bonus     = calculerBonus(tentativesRestantes); // recursif
        int scoreBase = (tentativesRestantes + 1) * 10 * niveau;
        int scoreTotal = scoreBase + bonus;

        System.out.println("Score de base : " + scoreBase + " | Bonus : " + bonus + " | Total : " + scoreTotal);
        return scoreTotal;
    }

    // ---------------------------------------------------------------
    // Lecture securisee d'un entier
    // ---------------------------------------------------------------
    static int lireEntier(Scanner scanner, int min, int max) {
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            if (val < min || val > max) {
                System.out.println("Nombre hors plage [" + min + " - " + max + "].");
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide, entrez un nombre entier.");
            return -1;
        }
    }

    // ---------------------------------------------------------------
    // Affiche les essais tries (tri a bulles : boucles imbriquees)
    // ---------------------------------------------------------------
    static void afficherEssayesTries(int[] tentatives, int nb) {
        int[] copie = new int[nb];
        for (int i = 0; i < nb; i++) {
            copie[i] = tentatives[i];
        }

        // Tri a bulles (deux boucles for imbriquees)
        for (int i = 0; i < nb - 1; i++) {
            for (int j = 0; j < nb - 1 - i; j++) {
                if (copie[j] > copie[j + 1]) {
                    int tmp    = copie[j];
                    copie[j]   = copie[j + 1];
                    copie[j + 1] = tmp;
                }
            }
        }

        System.out.print("Vos essais (tries) : ");
        for (int i = 0; i < nb; i++) {
            System.out.print(copie[i]);
            if (i < nb - 1) System.out.print(", ");
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // FONCTIONS RECURSIVES
    // ---------------------------------------------------------------

    /**
     * Verifie recursivement si 'val' est deja dans les 'n' premiers elements.
     *   Cas de base  : n <= 0  -> false (tableau vide ou entierement parcouru)
     *   Cas recursif : compare le dernier element, puis rappelle sur n-1
     */
    static boolean dejaEssaye(int[] tableau, int val, int n) {
        if (n <= 0) return false;
        if (tableau[n - 1] == val) return true;
        return dejaEssaye(tableau, val, n - 1);
    }

    /**
     * Calcule un bonus exponentiel = 2^n - 1 pour n tentatives restantes.
     *   Cas de base  : n <= 0  -> 0
     *   Cas recursif : 2 * calculerBonus(n-1) + 1
     *
     *   Exemples : n=0 -> 0 | n=1 -> 1 | n=2 -> 3 | n=3 -> 7 | n=4 -> 15
     */
    static int calculerBonus(int n) {
        if (n <= 0) return 0;
        return 2 * calculerBonus(n - 1) + 1;
    }

    /**
     * Trouve recursivement le meilleur score parmi les 'index+1' premiers.
     *   Cas de base  : index < 0 -> renvoie le meilleur courant
     *   Cas recursif : compare scores[index] au meilleur, continue sur index-1
     */
    static int trouverMeilleur(int[] scores, int index, int meilleur) {
        if (index < 0) return meilleur;
        int nouveauMeilleur = (scores[index] > meilleur) ? scores[index] : meilleur;
        return trouverMeilleur(scores, index - 1, nouveauMeilleur);
    }
}
