#!/bin/bash
# ─────────────────────────────────────────────────────────────────
# setup_fonts.sh — Descarga las fuentes desde Google Fonts
# Ejecutar UNA SOLA VEZ antes de compilar
# Requiere: curl o wget
# ─────────────────────────────────────────────────────────────────

FONT_DIR="app/src/main/res/font"
mkdir -p "$FONT_DIR"

echo "Descargando fuentes de Google Fonts..."

# Orbitron (usamos la API de Google Fonts para obtener TTF)
BASE_URL="https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;600;700;900&family=Space+Mono:wght@400;700&display=swap"

# Obtener CSS con User-Agent de browser para recibir woff2
CSS=$(curl -sA "Mozilla/5.0 (Linux; Android 10)" "$BASE_URL")

# Extraer URLs de woff2
URLS=$(echo "$CSS" | grep -oP "url\(\K[^)]+")

echo "URLs encontradas:"
echo "$URLS"

# Descargar cada fuente
i=1
for url in $URLS; do
    curl -sL "$url" -o "/tmp/font_${i}.woff2"
    echo "Descargado: $url -> font_${i}.woff2"
    i=$((i+1))
done

# Convertir woff2 a TTF usando fonttools (si está instalado)
if command -v fonttools &> /dev/null || python3 -c "import fonttools" 2>/dev/null; then
    echo "Convirtiendo woff2 -> TTF..."
    # ... conversión aquí
else
    echo ""
    echo "⚠️  Para convertir woff2 a TTF:"
    echo "    pip3 install fonttools brotli"
    echo "    python3 -c \"from fontTools.ttLib import TTFont; TTFont('/tmp/font_1.woff2').save('${FONT_DIR}/orbitron_regular.ttf')\""
fi

echo ""
echo "✅ Alternativa más fácil:"
echo "   1. Ve a https://fonts.google.com/specimen/Orbitron"
echo "   2. Download family → extrae los .ttf"
echo "   3. Renombra y copia a $FONT_DIR/"
echo ""
echo "   Nombres requeridos:"
echo "   - orbitron_regular.ttf   (weight 400)"
echo "   - orbitron_medium.ttf    (weight 500)"
echo "   - orbitron_semibold.ttf  (weight 600)"
echo "   - orbitron_bold.ttf      (weight 700)"
echo "   - orbitron_black.ttf     (weight 900)"
echo "   - space_mono_regular.ttf (weight 400)"
echo "   - space_mono_bold.ttf    (weight 700)"
