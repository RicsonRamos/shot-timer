# Shot Timer (Android)

App minimalista de shot timer. A interface é uma página HTML offline (`app/src/main/assets/index.html`) dentro de um WebView. O app não usa internet.

## Gerar o APK

**Opção A: Android Studio**
1. Abra esta pasta em *File > Open*.
2. Aguarde a sincronização do Gradle (o Android Studio baixa tudo sozinho).
3. *Build > Generate Signed Bundle / APK* ou *Build APK(s)*.
4. O arquivo fica em `app/build/outputs/apk/release/app-release.apk` (assinado) ou `app-debug.apk`.

**Opção B: GitHub (sem instalar nada)**
1. Envie as alterações para o repositório (`git push`).
2. Abra a aba *Actions*, rode **Gerar APK** e baixe `shot-timer-release-apk` nos artefatos.

## Instalar no celular
Copie o `app-release.apk` para o aparelho, abra e permita "instalar apps desconhecidos" quando o Android pedir. Na primeira detecção por microfone, aceite a permissão.
> **Dica Xiaomi (MIUI/HyperOS):** Para instalar via cabo USB no modo depuração, habilite a opção **"Instalar via USB"** nas *Opções do Desenvolvedor*.

## Como usar
- **Iniciar**: toca o sinal depois do atraso e começa a contar. O microfone marca cada tiro e calcula os splits.
- **Microfone / Toque**: no modo Toque você registra cada tiro tocando na tela (treino a seco).
- **Par**: ative, ajuste (− / + ou digitando) e o app toca um segundo sinal no tempo do par.
- **Revisão**: ao parar, use ‹ › ou toque num tiro da lista. Séries com tiros são salvas no Histórico.
- **Ajustes**: sensibilidade com medidor ao vivo, intervalo mínimo entre tiros, atraso (aleatório, fixo ou nenhum), voz "Preparar", frequência/duração/volume do sinal, parar após N tiros, tempo máximo, tela ligada, tema.
- **Histórico**: reabrir, excluir e copiar tudo como CSV.

## Precisão
Splits (tempo entre tiros) são precisos, pois vêm do relógio de áudio do aparelho. O tempo do primeiro tiro inclui a latência de áudio do celular (alguns milissegundos a algumas dezenas), então compare com um timer de referência antes de usar em competição.
