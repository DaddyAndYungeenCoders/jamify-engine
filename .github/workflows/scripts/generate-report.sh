# Emplacement du fichier CSV généré par JaCoCo
JACOCO_CSV="target/site/jacoco/jacoco.csv"

# Vérifiez si le fichier existe
if [[ ! -f "$JACOCO_CSV" ]]; then
  echo "Fichier JaCoCo introuvable à l'emplacement : $JACOCO_CSV"
  exit 1
fi

# Extraire la couverture globale, des lignes et des branches
GLOBAL_COVERAGE=$(awk -F',' 'NR>1 {total+=$8} END {print total/NR"%"}' "$JACOCO_CSV")
LINE_COVERAGE=$(awk -F',' 'NR>1 {total+=$6} END {print total/NR"%"}' "$JACOCO_CSV")
BRANCH_COVERAGE=$(awk -F',' 'NR>1 {total+=$7} END {print total/NR"%"}' "$JACOCO_CSV")

# Afficher les métriques extraites
echo "Couverture globale : $GLOBAL_COVERAGE"
echo "Couverture des lignes : $LINE_COVERAGE"
echo "Couverture des branches : $BRANCH_COVERAGE"

# Optionnel : Générer un rapport Markdown
cat << EOF > .github/test-coverage/coverage.md
# 📊 Rapport de Couverture des Tests

## Vue d'ensemble
| Métrique            | Couverture | Statut |
|---------------------|------------|--------|
| Couverture Globale  | $GLOBAL_COVERAGE | $([ "${GLOBAL_COVERAGE//%/}" -ge 80 ] && echo "✅" || echo "❌") |
| Lignes de Code      | $LINE_COVERAGE | $([ "${LINE_COVERAGE//%/}" -ge 80 ] && echo "✅" || echo "❌") |
| Branches            | $BRANCH_COVERAGE | $([ "${BRANCH_COVERAGE//%/}" -ge 80 ] && echo "✅" || echo "❌") |

## ℹ️ Informations
- Dernière mise à jour : $(date '+%Y-%m-%d %H:%M:%S')
- Seuil minimal : 80%
EOF
