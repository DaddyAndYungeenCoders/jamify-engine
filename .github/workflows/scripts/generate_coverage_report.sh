#!/bin/bash

# Création du fichier de rapport avec un en-tête Markdown
cat << EOF > .github/test-coverage/coverage.md
# Rapport de Couverture de Tests

Ce rapport est généré automatiquement à chaque exécution des tests.

| Catégorie | Couverture | Statut |
|-----------|------------|---------|
EOF

# Extraction des données de couverture du rapport JaCoCo
COVERAGE=$(cat target/site/jacoco/index.html | grep -o 'Total[^%]*%' | grep -o '[0-9]*%' | head -1)
LINE_COVERAGE=$(cat target/site/jacoco/index.html | grep -o 'Lines[^%]*%' | grep -o '[0-9]*%' | head -1)
BRANCH_COVERAGE=$(cat target/site/jacoco/index.html | grep -o 'Branches[^%]*%' | grep -o '[0-9]*%' | head -1)

# Fonction pour déterminer le statut basé sur un seuil
get_status() {
    local coverage=$1
    local threshold=80
    if [ "${coverage//%/}" -ge $threshold ]; then
        echo "✅"
    else
        echo "❌"
    fi
}

# Ajout des lignes de données au rapport
cat << EOF >> .github/test-coverage/coverage.md
| Global | $COVERAGE | $(get_status $COVERAGE) |
| Lignes | $LINE_COVERAGE | $(get_status $LINE_COVERAGE) |
| Branches | $BRANCH_COVERAGE | $(get_status $BRANCH_COVERAGE) |

*Dernière mise à jour : $(date '+%Y-%m-%d %H:%M:%S')*
EOF