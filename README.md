# Automates en Java : AFN → AFD et minimisation

Projet de **Grammaire et Langages** (Master 2 Génie logiciel, Université de Bordeaux), réalisé en binôme avec **Djibrine Moustapha Mahamat**.

Deux programmes Java en ligne de commande :

| Programme | Ce qu'il fait |
|---|---|
| `AFNtoAFD.java` | Lit un AFN dans `afn.txt` et le **déterminise** (construction par sous-ensembles) |
| `MinimiserAFD.java` | Lit un AFD dans `afd.txt` et calcule l'**AFD minimal** |

## Lancer

```bash
javac AFNtoAFD.java MinimiserAFD.java
java AFNtoAFD
java MinimiserAFD
```

Les fichiers `afn.txt` et `afd.txt` doivent être dans le dossier où on lance la commande.

## Format des fichiers

**afn.txt**
```
5        <- nombre d'états
0        <- état initial
4        <- état(s) final(aux)
a 0 0    <- transitions : lettre, état de départ, état d'arrivée
...
```

**afd.txt**
```
5        <- nombre d'états
1        <- état initial
0 4      <- états finaux
0 a 1    <- transitions : état de départ, lettre, état d'arrivée
...
```

## Exemple

Avec l'AFN fourni, l'AFD obtenu a 6 états. L'AFD d'exemple se minimise en 4 états.

## Limites connues

- L'alphabet est fixé à `{a, b}`.
- Les noms de fichiers sont fixes (`afn.txt`, `afd.txt`).

## Rapport

Le rapport du TD est dans [`docs/rapport-automates.pdf`](docs/rapport-automates.pdf).
