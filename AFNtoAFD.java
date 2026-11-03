import java.io.*;
import java.util.*;

public class AFNtoAFD {
    public static void main(String[] args) throws IOException {
        // J'ai décidé de commencer par vérifier si le fichier contenant l'AFN existe.
        // Cela permet d'éviter des erreurs inutiles si le fichier est absent.
        File fichier = new File("afn.txt");
        if (!fichier.exists()) {
            System.out.println("Erreur : Le fichier afn.txt est introuvable.");
            return; // Arrêter le programme si le fichier n'est pas trouvé
        }
        // la lecture des données de l'AFN
        System.out.println("Lecture du fichier afn.txt...");
        Scanner scanner = new Scanner(fichier);

        // a lecture du nombre d'états
        int nbEtats = Integer.parseInt(scanner.nextLine()); 
        System.out.println("Nombre d'états : " + nbEtats);

        // la lecture de l'état initial
        int etatInit = Integer.parseInt(scanner.nextLine()); 
        System.out.println("État initial : " + etatInit);

        // la lecture des états finaux
        // J'ai utilisé un Set pour stocker les états finaux afin d'éviter les doublons
        Set<Integer> etatsFinaux = new HashSet<>();
        for (String etat : scanner.nextLine().split(" ")) {
            etatsFinaux.add(Integer.parseInt(etat));
        }
        System.out.println("États finaux : " + etatsFinaux);

        //la table de transitions de l'AFN , ici J'ai utilisé une structure imbriquée (Map -> Map -> Set) pour gérer les transitions
           Map<Integer, Map<Character, Set<Integer>>> transitionsAFN = new HashMap<>();
        for (int i = 0; i < nbEtats; i++) {
            transitionsAFN.put(i, new HashMap<>());
            transitionsAFN.get(i).put('a', new HashSet<>());
            transitionsAFN.get(i).put('b', new HashSet<>()); 
        }

        // la lecture des transitions depuis le fichier
        while (scanner.hasNextLine()) {
            String[] ligne = scanner.nextLine().split(" ");
            char symbole = ligne[0].charAt(0); 
            int source = Integer.parseInt(ligne[1]); 
            int destination = Integer.parseInt(ligne[2]); 
            // Ajout de la transition dans la structure
            transitionsAFN.get(source).get(symbole).add(destination);
        }
        scanner.close(); 
        System.out.println("Transitions de l'AFN : " + transitionsAFN);

        // la conversion de l'AFN en AFD
        // J'ai structuré cette partie pour suivre méthodiquement chaque étape de la conversion.
        Map<Set<Integer>, Map<Character, Set<Integer>>> transitionsAFD = new HashMap<>();
        Set<Set<Integer>> etatsAFD = new HashSet<>();
        Set<Set<Integer>> etatsFinauxAFD = new HashSet<>();
        Queue<Set<Integer>> file = new LinkedList<>();

        // l'initialisation de l'état initial de l'AFD
        Set<Integer> etatInitialAFD = new HashSet<>();
        etatInitialAFD.add(etatInit); 
        file.add(etatInitialAFD); 
        etatsAFD.add(etatInitialAFD); 

        while (!file.isEmpty()) {
            Set<Integer> etatCourant = file.poll(); 
            Map<Character, Set<Integer>> transCourantes = new HashMap<>(); 
            // calcul des états atteignables de ('a' ou 'b')
            for (char symbole : new char[]{'a', 'b'}) {
                Set<Integer> nouvelEtat = new HashSet<>();
                for (int etat : etatCourant) {
                    nouvelEtat.addAll(transitionsAFN.get(etat).get(symbole));
                }

                if (!nouvelEtat.isEmpty()) {
                    transCourantes.put(symbole, nouvelEtat);

                    // Si le nouvel état n'a pas encore été traité, on l'ajouter à la file
                    if (!etatsAFD.contains(nouvelEtat)) {
                        etatsAFD.add(nouvelEtat); 
                        file.add(nouvelEtat); 
                    }
                }
            }

            transitionsAFD.put(etatCourant, transCourantes);

            // il vérifie si l'état courant contient un état final de l'AFN
            for (int etat : etatCourant) {
                if (etatsFinaux.contains(etat)) {
                    etatsFinauxAFD.add(etatCourant); 
                    break; 
                }
            }
        }

        // J'ai ajouté cette partie pour afficher les résultats de manière structurée et lisible.
        System.out.println("États de l'AFD : " + etatsAFD);
        System.out.println("État initial de l'AFD : " + etatInitialAFD);
        System.out.println("États finaux de l'AFD : " + etatsFinauxAFD);
        System.out.println("Transitions de l'AFD : ");
        for (Map.Entry<Set<Integer>, Map<Character, Set<Integer>>> entry : transitionsAFD.entrySet()) {
            System.out.print("Depuis " + entry.getKey() + " : ");
            for (Map.Entry<Character, Set<Integer>> trans : entry.getValue().entrySet()) {
                System.out.print(trans.getKey() + " -> " + trans.getValue() + " ");
            }
            System.out.println();
        }
    }
}
