# 06 - Reflexao Final

## 6.1 Fase Critica do Projeto

A fase mais critica do projeto e a **migracao do controle atual (planilhas / papel) para o sistema**.

Isso porque:

1. **Pessoas** — atendentes e gerentes estao acostumados com o modo antigo. Mudanca gera resistencia.
2. **Dados existentes** — pode haver catalogo de produtos e estoque atual que precisa ser migrado.
3. **Operacao nao pode parar** — as filiais continuam vendendo durante a implantacao.

## 6.2 Como mitigar

- **Piloto em 1 filial** antes de expandir para as outras 2.
- **Periodo de convivencia** (1-2 semanas) com sistema antigo e novo rodando em paralelo.
- **Suporte presencial** no primeiro dia de uso em cada filial.
- **Canal direto** (WhatsApp grupo) para reportar duvidas e bugs.

## 6.3 Licoes aprendidas (previsão, a preencher ao final)

- _(a ser preenchido apos conclusao do projeto)_

## 6.4 Conclusao

O sistema proposto atende aos requisitos funcionais e nao funcionais usando uma stack simples e madura (Java + Spring Boot + Thymeleaf + H2). A escolha de H2 in-memory elimina a dependencia de instalacao de banco de dados, tornando o sistema portatil e facil de demonstrar. O escopo foi propositalmente limitado para garantir entrega em tempo habil, priorizando o caminho completo: login -> cadastrar produto -> consultar estoque -> registrar venda -> ver dashboard.