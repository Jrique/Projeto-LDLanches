# Cardápio web (público)

Cardápio online que o cliente final usa para fazer pedidos. É servido pelo servidor Ktor em
`/{endereco-da-loja}/` (ex.: `http://localhost:8081/ld-lanches/`): o primeiro trecho do caminho
identifica a loja, e todos os dados vêm de `/api/public/lojas/{slug}`.

Sacola e histórico de pedidos ficam no `localStorage` separados por loja.

Integrantes do projeto original: José Henrique, Davi e David Braga.

Instruções de execução e visão geral: ver o [README da raiz](../README.md).

## Aparência por loja

O `script.js` aplica a aparência cadastrada (cores, logo, banner, textos, CSS extra). Se a cor de
fundo da loja for clara, o cardápio troca para o tema claro (`css/tema-loja.css`); a cor do texto
sobre botões é escolhida automaticamente para manter contraste.

`lojas/<slug>/` guarda arquivos provisórios de lojas (ex.: logo e banner recriados da Thai Cookies)
até existir o upload de imagens (Fase 1).
