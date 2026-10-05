# GymQuest — Planejamento

> Backlog de features já planejadas/refinadas mas ainda não implementadas. Ao implementar algo daqui: mover/atualizar a informação correspondente para `Gym_Quest_Context.md` e remover deste documento — este arquivo deve conter só o que falta fazer.

---

## 1. Roadmap por Versão

### V1 — MVP
- Seed inicial de exercícios pré-cadastrados (cadastro manual do exercício em si já implementado — ver `Gym_Quest_Context.md` §6).
- Atalho para cadastrar um novo exercício direto no fluxo de criação de treino (ao clicar em "adicionar exercício").
- Ordenação da lista de exercícios por grupo muscular ou por uso recente (hoje a lista já é alfabética por padrão e tem busca por nome/grupo — ver §6).
- Gráfico de evolução de carga na tela de detalhe do exercício (o histórico de séries e o PR já aparecem ali — ver §6; falta o gráfico, que entra junto da Feature de Estatísticas).
- Registro de treino por dia (sessão com data e exercícios realizados), com opção de repetir um treino anterior como ponto de partida.
- Edição e cancelamento de um treino em andamento ou já salvo, com confirmação antes de descartar.
- Treino em andamento fica salvo automaticamente; se o app fechar, ao reabrir oferece a opção de continuar ou descartar.
- Botão "Finalizar treino": marca o fim da sessão e calcula a duração automaticamente (do início ao fim).
- Superséries/circuitos: agrupar exercícios em sequência, com descanso controlado por grupo.
- Registro de série com suporte a diferentes tipos de exercício: peso + repetições, repetições sem carga, duração (ex.: prancha) ou distância + tempo (ex.: corrida).
- Tipos de série: aquecimento, normal, falha, drop set.
- Duplicar série anterior: ao adicionar uma nova série do mesmo exercício, pré-preencher com os valores da série anterior.
- RPE (percepção de esforço) opcional por série, escala 6-10 (preenchido pelo usuário, não calculado).
- Nota por treino e por série.
- Cronômetro/timer de descanso entre séries, com notificação/vibração ao final mesmo com o app em segundo plano.
- Busca/filtro na lista de exercícios.
- Histórico de treinos por dia (lista/calendário).
- Recordes pessoais (PRs) por exercício — maior carga registrada (sem cálculo de 1RM estimado).
- Gráficos de evolução de carga por exercício.
- Volume total de treino por período.
- Streak/frequência de treino (calendário de dias treinados).
- Log de peso corporal do usuário ao longo do tempo.
- Idioma PT/EN (tela de Configurações já existe com unidade de peso e tema claro/escuro — ver §6; falta a troca de idioma e a extração de strings para `strings.xml`/`strings-en.xml`, hoje os textos estão hardcoded em português no Kotlin).
- Export/Import de dados via JSON.

### V2
- Sincronização/backup em nuvem (conta + backend).
- Suporte a múltiplos dispositivos.
- Possível versão iOS (Swift), reaproveitando o modelo de dados e regras de negócio definidos no V1.

### V3
- Templates/rotinas de treino prontas.
- Integração com wearables (ex.: captura automática de tempo, frequência cardíaca).
- Gamificação / comparação social (opcional, avaliar se faz sentido para uso pessoal).

## 2. Telas — Descrição Detalhada

### 2.1 Home / Treino do dia
Ponto de entrada. Se houver um treino em andamento (salvo automaticamente), oferece continuar ou descartar (com confirmação). Caso contrário, exibe estado vazio com botão de destaque "Novo treino", com opção de repetir o último treino como base. Exibe também o streak/frequência de treino em destaque.

### 2.2 Cadastro de Exercício
Campos: nome, grupo muscular (selecionado a partir da lista gerenciada em Configurações — §2.9), equipamento (opcional), tipo de exercício (peso+reps, repetições sem carga, duração, distância+tempo), observações. Acessível tanto pela lista de exercícios quanto como atalho durante a criação de um treino. A lista de exercícios pode ser ordenada por ordem alfabética, grupo muscular ou uso recente.

### 2.3 Detalhe do Exercício
Acessada a partir da lista de exercícios. Mostra o histórico daquele exercício específico: séries anteriores, PR (maior carga registrada) e gráfico de evolução de carga.

### 2.4 Execução de Treino
Ao adicionar um exercício, se ele ainda não existir na lista, é possível cadastrá-lo ali mesmo (atalho para §2.2). Para cada exercício da sessão: adicionar séries com o tipo (aquecimento, normal, falha, drop set) e os campos correspondentes ao tipo de exercício (peso/repetições, duração ou distância/tempo); ao adicionar uma nova série do mesmo exercício, os valores da série anterior vêm pré-preenchidos. Cada série pode registrar RPE (escala 6-10) e uma nota; cada treino também pode ter uma nota geral. Timer de execução e timer de descanso entre séries, com notificação/vibração ao final mesmo com o app em segundo plano. Exercícios podem ser agrupados em superséries/circuitos, com descanso controlado por grupo. Um botão "Finalizar treino" encerra a sessão e calcula a duração automaticamente.

### 2.5 Histórico
Lista ou calendário dos treinos já realizados. Toque em um item abre o detalhe.

### 2.6 Detalhes do Treino
Exibe exercícios, séries, cargas, tempos e notas daquele dia específico, na ordem em que foram executados.

### 2.7 Estatísticas
Gráficos de evolução de carga por exercício, destaque de PRs (maior carga registrada), volume total de treino por período (semana/mês), e streak/calendário de frequência de treino.

### 2.8 Peso Corporal
Registro do peso corporal do usuário ao longo do tempo (log independente, não ligado a um treino específico), com gráfico de evolução.

### 2.9 Configurações
Unidade de peso (dropdown com kg ou lb), tema claro/escuro, idioma (PT/EN), gerenciamento de grupos musculares (criar/editar/remover, vinculados aos exercícios), e export/import de dados via JSON.

## 3. Itens em Aberto

- Mecanismo de export/import JSON: compartilhamento manual de arquivo ou tela dedicada com seleção de local?
- Qual fonte de seed de exercícios usar — [free-exercise-db](https://github.com/yuhonas/free-exercise-db) (~800 exercícios, domínio público) ou [wrkout/exercises.json](https://github.com/wrkout/exercises.json) — e como tratar a tradução para PT-BR (ambas as bases estão em inglês).
- Regra de descanso em superséries: o timer conta só entre exercícios do grupo, ou também dentro do mesmo exercício ao repetir a volta?
