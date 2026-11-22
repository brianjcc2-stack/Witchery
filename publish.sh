#!/bin/zsh
set -e

cd "$(dirname "$0")"

export JAVA_HOME="$(pwd)/zulu8.94.0.17-ca-jdk8.0.492-macosx_aarch64/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "======================================================"
echo " 1/3 Compilando Witchery y generando hash SHA-256..."
echo "======================================================"
./gradle-4.4.1/bin/gradle build

echo ""
echo "======================================================"
echo " 2/3 Verificando archivos en release/..."
echo "======================================================"
cat release/witchery.sha256
ls -lh release/witchery-1.7.10-0.24.1.jar

if [[ "$1" == "--no-push" ]]; then
  echo ""
  echo "Compilacion lista en release/ (modo --no-push)."
  exit 0
fi

COMMIT_MSG="${1:-chore: auto-update witchery release build}"

echo ""
echo "======================================================"
echo " 3/3 Subiendo actualizacion a GitHub..."
echo "======================================================"
git add release/witchery-1.7.10-0.24.1.jar release/witchery.sha256
git commit -m "$COMMIT_MSG" || echo "Sin cambios pendientes en release/"
git push

echo ""
echo "✅ ¡Listo! Todos los jugadores y el servidor recibiran esta actualizacion automaticamente al iniciar."
