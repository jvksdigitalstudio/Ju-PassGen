# 🚀 JU Password Generator Android — Setup Completo

## 📋 Tabla de contenidos
1. [Subir a GitHub](#1-subir-a-github)
2. [Configurar GitHub Secrets](#2-configurar-github-secrets)
3. [Generar Keystore](#3-generar-keystore)
4. [Obtener AdMob IDs](#4-obtener-admob-ids)
5. [Primer Build](#5-primer-build)
6. [Publicar en Play Store](#6-publicar-en-play-store)

---

## 1. Subir a GitHub

```bash
git init
git add .
git commit -m "JU Password Generator Android — Fase 4 completa"
git remote add origin https://github.com/TU_USUARIO/ju-passgen-android.git
git push -u origin main
```

GitHub Actions arranca automáticamente. El **Debug APK** se genera en ~5 minutos.

---

## 2. Configurar GitHub Secrets

Ve a tu repo → **Settings → Secrets and variables → Actions → New repository secret**

| Secret | Descripción | Cuándo se necesita |
|---|---|---|
| `KEYSTORE_BASE64` | Keystore en base64 | Release firmado |
| `KEY_ALIAS` | Alias de tu clave | Release firmado |
| `KEY_PASSWORD` | Contraseña de la clave | Release firmado |
| `STORE_PASSWORD` | Contraseña del keystore | Release firmado |
| `ADMOB_APP_ID` | App ID de AdMob | Release (AdMob real) |
| `ADMOB_BANNER_ID` | Banner Ad Unit ID | Release (AdMob real) |
| `ADMOB_INTERSTITIAL_ID` | Interstitial Ad Unit ID | Release (AdMob real) |

> **Sin secrets** la app compila igual en debug con Test IDs de Google.

---

## 3. Generar Keystore

Ejecuta esto **una sola vez** y guarda el .jks en lugar seguro:

```bash
# Generar keystore
keytool -genkey -v \
  -keystore ju_passgen_release.jks \
  -alias ju_passgen \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=JU PassGen, OU=Android, O=JU, L=Ciudad, S=Estado, C=ES"

# Convertir a base64 para el secret KEYSTORE_BASE64
# macOS/Linux:
base64 -i ju_passgen_release.jks | tr -d '\n'

# Windows (PowerShell):
[Convert]::ToBase64String([IO.File]::ReadAllBytes("ju_passgen_release.jks"))
```

⚠️ **Guarda el .jks en lugar seguro.** Si lo pierdes, no puedes actualizar la app en Play Store.

---

## 4. Obtener AdMob IDs

1. Ve a [admob.google.com](https://admob.google.com)
2. Crea una app Android: "JU Password Generator"
3. Crea 2 unidades de anuncios:
   - **Banner** → copia el Ad Unit ID → secret `ADMOB_BANNER_ID`
   - **Interstitial** → copia el Ad Unit ID → secret `ADMOB_INTERSTITIAL_ID`
4. Copia el **App ID** (formato: `ca-app-pub-XXXX~XXXX`) → secret `ADMOB_APP_ID`

Durante desarrollo, los Test IDs están activos automáticamente.

---

## 5. Primer Build

```
main push → GitHub Actions corre →
  Job 1: Debug APK (siempre) ~4min
  Job 2: Release AAB firmado (solo main con secrets) ~6min
```

**Descargar APK:**  
Repo → Actions → Build JU Password Generator → Artifacts → `JU-PassGen-Debug-*`

**Build local:**
```bash
./gradlew assembleDebug
# APK en: app/build/outputs/apk/debug/
```

---

## 6. Publicar en Play Store

### Assets ya listos (del proyecto web)
| Archivo | Uso |
|---|---|
| `play_store_assets/screenshot-01.png` | Screenshot 1 |
| `play_store_assets/screenshot-02.png` | Screenshot 2 |
| `play_store_assets/screenshot-03.png` | Screenshot 3 |
| `play_store_assets/feature_graphic.png` | Feature graphic 1024×500 |
| `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` | Ícono 192×192 (subir el 512×512 del web) |

### Pasos en Play Console
1. [play.google.com/console](https://play.google.com/console) → Crear app
2. **Ficha de Play Store:**
   - Título: `JU Password Generator`
   - Subtítulo: `Seguro · Local · Sin servidores`
   - Categoría: Herramientas
3. **Clasificación de contenido:** Todos (no tiene contenido sensible)
4. **Política de privacidad:** URL de tu `privacy.html` en Netlify
5. **Declaraciones de seguridad de datos:**
   - Sin datos de usuarios recopilados
   - Usa publicidad (AdMob) ✓
   - Sin cuenta requerida ✓
6. **Subir AAB** → Revisión (1-3 días) → Publicar

---

## 🔑 Resumen de Secrets por Job

```
Debug build  → No necesita ningún secret (Test IDs automáticos)
Release build → KEYSTORE_BASE64 + KEY_ALIAS + KEY_PASSWORD + STORE_PASSWORD
AdMob real   → ADMOB_APP_ID + ADMOB_BANNER_ID + ADMOB_INTERSTITIAL_ID
```

---

## ✅ Estado de Fases

- [x] **Fase 1** — Estructura base + GitHub Actions
- [x] **Fase 2** — Diseño visual (colores web, fuentes, componentes)
- [x] **Fase 3** — Motor de generación premium + Compresión ZIP/7Z/TAR.GZ
- [x] **Fase 4** — AdMob (Banner + Interstitial) + Firma release + CI/CD final
