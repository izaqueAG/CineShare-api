# Migração visual do CineShare

Data: 26 de novembro de 2026.

## Objetivo

Atualizar a interface para o balcão retrô da referência, com lâmpadas de navegação, Home com prateleira e estojo Blu-ray com profundidade.

## Entregas

- A navegação usa lâmpadas com ícone, rótulo e feixe de luz. O rótulo aparece na seção atual, no foco ou ao passar o mouse.
- A Home autenticada apresenta saudação, atalhos, destaques e estojo. Usa filmes das listas quando existirem e cinco demonstrações quando a API estiver vazia ou indisponível.
- O seletor de tema também é uma lâmpada com estado `Light-OFF` ou `Light-ON`.
- O estojo fechado é vertical e compacto. O estojo aberto apresenta capa circular preenchendo o disco, anel central, ficha e botão de fechamento.
- O README documenta a execução no terminal integrado do VS Code em PowerShell, WSL, Linux e macOS.

## Critérios de aceite

Validar mouse, teclado e toque na navegação, alternância de tema, entrada e saída, prateleira com dados reais e demonstrações, estojo com capa e sem capa, além das larguras desktop e celular. Repetir `bash ./mvnw test` e `bash ./mvnw -q -DskipTests compile`.
