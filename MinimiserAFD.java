import java.io.*; 
import java.util.*;

public class MinimiserAFD {

    public static void main(String[] args) throws IOException {
        // la création d'un objet fichier pour accéder au fichier contenant l'AFD
        File fichier = new File("afd.txt");
        // la vérification du fichier, si le fichier existe, sinon affiche un message d'erreur et termine le programme
        if (!fichier.exists()) {
            System.out.println("Le fichier afd.txt n'a pas été trouvé.");
            return; 
        }

        // le scanner pour lire le contenu du fichier ligne par ligne
        Scanner scanner = new Scanner(fichier);
        int nombreEtats = Integer.parseInt(scanner.nextLine());
        String etatInitial = scanner.nextLine();
        Set<String> etatsFinaux = new HashSet<>(Arrays.asList(scanner.nextLine().split(" ")));

        // l'initialisation de la structure de données pour stocker les transitions
        // les transitions sont organisées sous forme d'une map : état source -> (symbole -> état cible)
        Map<String, Map<Character, String>> transitions = new HashMap<>();
        while (scanner.hasNextLine()) {
            // ici Chaque ligne contient une transition sous la forme : étatSource symbole étatCible
            String[] ligne = scanner.nextLine().split(" ");
            String etatSource = ligne[0]; 
            char symbole = ligne[1].charAt(0); 
            String etatCible = ligne[2]; 
            transitions.putIfAbsent(etatSource, new HashMap<>());
            transitions.get(etatSource).put(symbole, etatCible);
        }
        scanner.close();

        // l'appel de la méthode pour minimiser l'AFD
        Automate afdMinimal = minimiserAFD(nombreEtats, etatInitial, etatsFinaux, transitions);

        // l'affichage des résultats de l'AFD minimal
        System.out.println("États de l'AFD minimal : " + afdMinimal.etats); 
        System.out.println("État initial : " + afdMinimal.etatInitial); 
        System.out.println("États finaux : " + afdMinimal.etatsFinaux); 
        System.out.println("Transitions : "); // Prépare l'affichage des transitions
        for (Map.Entry<String, Map<Character, String>> entree : afdMinimal.transitions.entrySet()) {
            String etatSource = entree.getKey(); // Récupère l'état source
            for (Map.Entry<Character, String> transition : entree.getValue().entrySet()) {
                // Affiche chaque transition sous la forme : étatSource symbole étatCible
                System.out.println(etatSource + " " + transition.getKey() + " " + transition.getValue());
            }
        }
    }

    // la méthode pour minimiser un AFD
    static Automate minimiserAFD(int nombreEtats, String etatInitial, Set<String> etatsFinaux,
                                 Map<String, Map<Character, String>> transitions) {
        Set<String> etatsNonFinaux = new HashSet<>(transitions.keySet()); 
        etatsNonFinaux.removeAll(etatsFinaux); 

        // l'initialisation des partitions avec deux groupes : états finaux et états non finaux
        List<Set<String>> partitions = new ArrayList<>();
        partitions.add(etatsFinaux);
        partitions.add(etatsNonFinaux); 

        boolean changement; 
              do {
            changement = false; 
            List<Set<String>> nouvellesPartitions = new ArrayList<>(); 

            // le parcours de chaque groupe dans les partitions actuelles
            for (Set<String> groupe : partitions) {
                // le Map pour regrouper les états selon leurs transitions
                Map<Map<Character, Integer>, Set<String>> separation = new HashMap<>();

                for (String etat : groupe) {
                    // la Clé pour identifier le groupe d'un état selon ses transitions
                    Map<Character, Integer> cle = new HashMap<>();
                    for (char symbole : new char[]{'a', 'b'}) { // 
                        String cible = transitions.get(etat).get(symbole); 
                        int indexPartition = trouverIndexPartition(partitions, cible); 
                        cle.put(symbole, indexPartition);
                     }
                    //l'ajout de l'état dans le groupe correspondant
                    separation.putIfAbsent(cle, new HashSet<>());
                    separation.get(cle).add(etat);
                }

                // l'ajout de tous les groupes créés à partir des séparations
                nouvellesPartitions.addAll(separation.values());
                if (separation.size() > 1) {
                    changement = true; 
                }
            }
         // Mise à jour des partitions pour le prochain tour
            partitions = nouvellesPartitions; 
        } while (changement); 

        // la construction de l'AFD minimal , le Map pour renommer les états
        Map<String, String> correspondanceEtats = new HashMap<>(); 
        for (int i = 0; i < partitions.size(); i++) {
            for (String etat : partitions.get(i)) {
                correspondanceEtats.put(etat, "S" + i); 
            }
        }

        // la création des ensembles d'états, d'états finaux et des transitions pour l'AFD minimal
        Set<String> etatsMinimaux = new HashSet<>(correspondanceEtats.values()); 
        String etatInitialMinimal = correspondanceEtats.get(etatInitial);
        Set<String> etatsFinauxMinimaux = new HashSet<>();
        for (String etat : etatsFinaux) {
            etatsFinauxMinimaux.add(correspondanceEtats.get(etat)); 
        }

        Map<String, Map<Character, String>> transitionsMinimales = new HashMap<>();
        for (Map.Entry<String, Map<Character, String>> entree : transitions.entrySet()) {
            String etatSource = correspondanceEtats.get(entree.getKey());
            transitionsMinimales.putIfAbsent(etatSource, new HashMap<>());
            for (Map.Entry<Character, String> transition : entree.getValue().entrySet()) {
                char symbole = transition.getKey(); 
                String etatCible = correspondanceEtats.get(transition.getValue());
                transitionsMinimales.get(etatSource).put(symbole, etatCible); 
            }
        }

        // le retour de l'AFD minimal
        return new Automate(etatsMinimaux, etatInitialMinimal, etatsFinauxMinimaux, transitionsMinimales);
    }

    // ici on trouve l'index de la partition contenant un état donné
    static int trouverIndexPartition(List<Set<String>> partitions, String etat) {
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).contains(etat)) {
                return i; 
            }
        }
        return -1; 
    }

    // la classe pour représenter un automate
    static class Automate {
        Set<String> etats; 
        String etatInitial; 
        Set<String> etatsFinaux; 
        Map<String, Map<Character, String>> transitions; 

        // le constructeur pour initialiser les attributs de l'automate
        Automate(Set<String> etats, String etatInitial, Set<String> etatsFinaux,
                 Map<String, Map<Character, String>> transitions) {
            this.etats = etats;
            this.etatInitial = etatInitial;
            this.etatsFinaux = etatsFinaux;
            this.transitions = transitions;
        }
    }
}
