# GymQuest — Context

> Snapshot atual do aplicativo: identidade, arquitetura e modelo de dados vigentes. Atualizar sempre após implementar ou corrigir algo — este documento reflete o estado real do app, não o backlog (backlog fica em `Gym_Quest_Planejamento.md`).

Aplicativo Android nativo (Kotlin) para controle de treinos de academia, inspirado no Hevy. Projeto de uso pessoal e de estudo: permite cadastrar exercícios, registrar séries (peso, repetições, tempo de execução e descanso) por dia de treino, e analisar a evolução dos dados salvos ao longo do tempo.

---

## 1. Identidade Visual e Estilo

Referência adotada a partir do projeto ScoreQuest (mesmo scaffold `android-compose-scaffold`), para manter consistência visual entre os projetos pessoais. Valores de cor são referência, não especificação pixel-perfect.

- **Paleta:** fundo `#121212` / superfície `#1E1E1E` (tema escuro, padrão), com alternativa clara. Cor de destaque: dourado `#D4AF37`.
- **Tema:** escuro por padrão, com opção de alternar para claro (ver tela de Configurações), persistido localmente.
- **Componentes:** cards com borda fina em gradiente dourado→branco para destaque; rótulos de campo em dourado; listas em grid de 2 colunas com cards quadrados (imagem/ícone + nome) quando aplicável.
- **Navegação:** bottom bar com o item central (Home) visualmente destacado — ícone dentro de um círculo dourado sólido; FAB dourado flutuante para a ação principal (iniciar/continuar treino).
- **Tabs:** texto da tab selecionada em dourado, com transição suave (fade/slide) ao trocar.

## 2. Visão Geral

- **Plataforma:** Android nativo, Kotlin com Jetpack Compose no V1.
- **Uso:** pessoal (usuário único), também serve como projeto de estudo.
- **Armazenamento:** local no dispositivo no V1 — sem conta, sem login, sem backend.
- **Inspiração:** Hevy, mas com escopo reduzido e evoluindo conforme a necessidade. Referência visual/UI segue a identidade descrita em §1 (paleta do ScoreQuest).
- **Idioma:** suporte a português e inglês.

## 3. Navegação

- Home / Treino do dia
- Cadastro e lista de exercícios
- Detalhe do exercício (histórico específico)
- Execução de treino (com timer)
- Histórico de treinos
- Detalhes de um treino passado
- Estatísticas (gráficos de evolução, PRs, volume, streak)
- Peso corporal (registro e evolução)
- Configurações (unidade de peso, tema, idioma, export/import)

## 4. Modelo de Dados

**Conceitos:**

- **Exercício:** um exercício específico (ex.: supino reto, agachamento) — catálogo reutilizável.
- **Série:** um exercício executado x vezes ou por x tempo/distância — a menor unidade registrada.
- **Treino:** uma sequência ordenada de N séries. A ordem é significativa e é definida série a série (não por bloco de exercício) — por exemplo, é possível intercalar 1 série de supino, 1 de agachamento, 1 de supino novamente. Na UI, séries consecutivas do mesmo exercício (adicionadas em sequência) são exibidas agrupadas; se não forem consecutivas, aparecem como blocos separados.

**Entidades principais (V1):**

- **Exercise**: `id`, `name`, `muscleGroupId`, `equipment` (opcional), `notes`, `exerciseType` (`WEIGHT_REPS` | `BODYWEIGHT_REPS` | `TIME` | `DISTANCE_TIME`).
- **MuscleGroup** (grupo muscular, gerenciado na aba Configurações): `id`, `name`.
- **Workout** (treino): `id`, `name` (opcional — nome da rotina, ex.: "Treino A - Peito/Ombro/Tríceps"; adicionado ao importar dados do Hevy, que nomeia rotinas), `date`, `startedAt`, `finishedAt` (opcional — preenchido ao clicar em "Finalizar treino"; duração = `finishedAt` − `startedAt`), `notes`, `status` (`IN_PROGRESS` | `COMPLETED`) — permite retomar um treino salvo automaticamente.
- **WorkoutSet** (série): `id`, `workoutId`, `exerciseId`, `order` (posição global da série dentro do treino), `setType` (`WARMUP` | `NORMAL` | `FAILURE` | `DROP_SET`), `weight` (opcional), `reps` (opcional), `durationSeconds` (opcional), `distanceMeters` (opcional), `restTimeSeconds`, `rpe` (opcional, escala 6-10, informado pelo usuário), `notes`, `supersetGroupId` (opcional — liga séries de exercícios diferentes que fazem parte da mesma superssérie/circuito, executadas em sequência com descanso compartilhado).
- **BodyWeightLog** (peso corporal do usuário): `id`, `date`, `weight`.

Os campos `weight`/`reps`/`durationSeconds`/`distanceMeters` em `WorkoutSet` são opcionais porque variam conforme o `exerciseType` do exercício (ex.: um exercício `TIME` preenche `durationSeconds` e deixa `weight`/`reps` vazios).

**Convenções:**

- **ID strategy:** autoincrement local (sem sync no V1). Reavaliar para UUID se uma versão futura trouxer sincronização multi-dispositivo.
- **Audit fields:** `createdAt` / `updatedAt` em todas as entidades.
- **Nomenclatura:** nomes de entidades, tabelas e colunas em inglês, camelCase — tanto nas classes Kotlin quanto nas colunas do Room/SQLite (sem snake_case).
- **Soft delete:** não usar no V1 (Android/local) — hard delete direto. Reavaliar soft delete quando houver backend, para suportar sync/histórico.

## 5. Pontos de Atenção Técnica

- **Stack:** Android nativo, Kotlin com Jetpack Compose.
- **Scaffolding do projeto:** criado a partir da skill `android-compose-scaffold`.
- **Persistência:** Room (SQLite) recomendado para o V1.
- **Arquitetura:** MVVM sugerido (padrão comum em apps Android e bom para fins de estudo).
- **Gráficos:** biblioteca [Vico](https://github.com/patrykandpatrick/vico) (nativa para Jetpack Compose). Nota: o projeto de referência ScoreQuest (mesmo ambiente de desenvolvimento, sem emulador Android disponível) abandonou o Vico em favor de componentes de gráfico feitos à mão em Compose, por não conseguir validar visualmente uma lib nova/em evolução rápida sem emulador. Decisão consciente de manter Vico no GymQuest mesmo assim — ajustar com testes no device físico se necessário.
- **Timer:** deve notificar (som/vibração) ao final do descanso mesmo com o app em segundo plano ou tela bloqueada — provavelmente via serviço em foreground ou WorkManager + notificação (a detalhar na implementação).
- **Estado do treino em andamento:** persistido via campo `status` em `Workout` (`IN_PROGRESS`/`COMPLETED`) — permite retomar de onde parou caso o app feche.

## 6. Estado de Implementação

Projeto criado em `C:\Rogue\GymQuest`, package `com.rogue.gymquest`, a partir da skill `android-compose-scaffold` (mesmo scaffold do ScoreQuest/ShopControl).

**Ajustes feitos no scaffold genérico para as necessidades do GymQuest:**

- Removidas as dependências de rede/câmera que o template traz por herança do ShopControl (Retrofit, OkHttp, CameraX, ML Kit Barcode, Jsoup) — o GymQuest não tem backend nem scanner, então `di/NetworkModule.kt` foi excluído. `kotlinx.serialization` foi mantido (vai servir para o export/import de JSON do §3).
- Adicionadas: DataStore Preferences (configurações), Vico `compose-m3` (gráficos de evolução — biblioteca já escolhida, ainda **não usada em nenhuma tela**), `material-icons-extended`.
- **Versão do Kotlin corrigida de 2.1.20 (o que o template da skill tinha) para 2.3.10**, pareando com o que o ShopControl de fato usa hoje — o template estava desatualizado e isso causava um erro real de compilação (stdlib incompatível). Mesmo ajuste replicado para os plugins `ksp` e `kotlin-compose` (`version.ref = "kotlin"` em vez de versão fixa).
- `material-icons-extended` precisa ser declarado **sem versão própria** (controlado pelo BOM do Compose, igual ao `material3`) — versão fixa quebra a resolução de dependências.

**Modelo de dados**: as 5 entidades do §4 estão implementadas em Room exatamente como especificado, com DAOs e repositories (`data/repository/*Repository.kt`) injetados via Koin. **Decisão deliberada**: todos os campos de data/hora (`date`, `startedAt`, `finishedAt`, `createdAt`, `updatedAt`) são `Long` (epoch millis), não `String` — evita de propósito o bug de ordenação por `ORDER BY` em string de data já documentado no histórico do ShopControl. Grupos musculares vêm com um seed padrão (10 nomes em PT-BR: Peito, Costas, Ombro, Bíceps, Tríceps, Antebraço, Perna, Glúteos, Panturrilha, Abdômen) inserido via `RoomDatabase.Callback.onCreate` em `di/DatabaseModule.kt` — não há tela de gerenciamento de grupos musculares ainda (§2.9, pendente).

**Arquitetura**: MVVM com Koin, mas **sem a camada de usecase-por-operação do ShopControl** — os ViewModels chamam os repositories diretamente (`data/repository/`), decisão de escopo para simplificar um app pessoal de um usuário só. Estado de cada tela como `data class` em `presentation/viewmodel/states/`, exposto como `StateFlow` (mesmo padrão de `MutableStateFlow` + `asStateFlow()` do ShopControl).

**Tema**: escuro (`#121212`/`#1E1E1E`) por padrão com destaque dourado (`#D4AF37`), alternativa clara, persistido via `SettingsRepository` (DataStore) e lido por um `ThemeViewModel` em `MainActivity`. Dynamic color (Android 12+) **desativado de propósito** para manter a identidade visual consistente entre dispositivos.

**Navegação**: bottom bar com 5 abas (Exercícios / Histórico / Início / Estatísticas / Config.), "Início" destacado num círculo dourado conforme §1; FAB dourado flutuante (ícone de play) presente em todas as abas, navega para a execução de treino. Rotas e telas implementadas até agora:

- ✅ **Home**: só mostra se há treino em andamento ou não (sem streak ainda).
- ✅ **Lista de exercícios**: busca por nome/grupo muscular (client-side, mesmo padrão do ShopControl), exclusão com bloqueio se o exercício já tem séries registradas.
- ✅ **Cadastro/edição de exercício**: nome, grupo muscular (dropdown), tipo de exercício (dropdown), equipamento e observações opcionais.
- ✅ **Detalhe do exercício**: grupo muscular, equipamento, observações, PR (maior carga) e histórico de séries em lista — **sem o gráfico de evolução ainda** (Vico não está wireado a nenhuma tela).
- ✅ **Configurações**: tema claro/escuro e unidade de peso (kg/lb) — **sem seletor de idioma** (ver nota de i18n abaixo).
- ⏳ **Placeholders** (compilam e navegam, mostram "Em construção"): Execução de treino, Histórico, Detalhe de treino, Estatísticas, Peso corporal.

**i18n**: ainda não iniciado — todos os textos de UI estão em português, hardcoded direto no Kotlin (não em `strings.xml`). Precisa de um passo de extração antes de dar suporte a EN.

**Build**: `.\gradlew.bat :app:compileDebugKotlin` passa limpo (sem warnings) nesta sessão. Ainda não instalado/testado em dispositivo físico.

**Grupo muscular "Cardio e Esporte"**: adicionado ao seed padrão (11 grupos agora) para cobrir exercícios que não têm grupo muscular de força associado (esteira, spinning, caminhada, alongamento, esportes como basquete).

**Export/Import de dados via JSON — implementado**: `domain/model/GymQuestBackup.kt` (schemaVersion, exportedAt, listas das 5 entidades — mesmo padrão do ShopControl de anotar as entidades Room diretamente com `@Serializable` em vez de DTOs paralelos) + `data/repository/BackupRepository.kt`.

- **Export**: snapshot de todas as DAOs (`.first()` em cada Flow), serializa com `kotlinx.serialization` (JSON legível, `prettyPrint`), grava em `cacheDir/backups/`, devolve um `content://` Uri via `FileProvider` (`res/xml/file_paths.xml` + `<provider>` no manifest, authority `${applicationId}.fileprovider`) para compartilhar via `Intent.ACTION_SEND` — mesmo fluxo do ShopControl.
- **Import**: lê o JSON de um Uri escolhido via `ActivityResultContracts.OpenDocument`, roda tudo dentro de `AppDatabase.withTransaction`. `MuscleGroup` e `Exercise` são dedup-ou-cria por nome (reaproveita se já existir); `Workout`, `WorkoutSet` e `BodyWeightLog` são sempre inseridos como novos (são registros históricos, não catálogo). IDs do JSON são tratados como locais-ao-arquivo e remapeados via `Map<idAntigo, idNovo>` — mesmo padrão do ShopControl. `supersetGroupId` é remapeado em uma segunda passada (já que pode referenciar o id de outra série do mesmo arquivo).
- UI em Configurações → seção "Dados": botões "Exportar dados (JSON)" / "Importar dados (JSON)", com `ConfirmDialog` (novo componente, `presentation/components/ConfirmDialog.kt`) antes de importar.
- **Usado para importar o histórico do Hevy** (ver `Gym_Quest_Planejamento.md` — a conversão do CSV do Hevy para esse formato JSON foi feita por um script PowerShell ad-hoc, fora do app, mesmo padrão da conversão de planilha pessoal que o ShopControl já fez para a Renda).
