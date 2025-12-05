// ================== CONFIG GERAL ==================
let categorias = [];
const mockProdutos = [];

const ENDERECO_COMERCIO = {
    nome: "LD Lanches",
    logradouro: "Rua do Marechal Teodoro",
    numero: "578",
    bairro: "Itaim Paulista",
    cidade: "São Paulo",
    uf: "SP",
    complemento: "Proximo ao mercado",
    cep: "08441198",
    telefone: "(11) 98479-8725"
};

function parseExtras(extraStr) {
    if (!extraStr) return [];
    return extraStr
        .split(';')
        .map(e => e.trim())
        .filter(e => e.length > 0)
        .map(part => {
            const [nomeRaw, precoRaw] = part.split(':');
            const nome = (nomeRaw || '').trim();
            const preco = precoRaw ? parseFloat(precoRaw.replace(',', '.')) : 0;
            return { nome, preco: isNaN(preco) ? 0 : preco };
        });
}

async function carregarProdutosDoBackend() {
    const host = window.location.hostname || 'localhost';
    const baseUrl = `http://${host}:8081`;

    try {
        const resp = await fetch(`${baseUrl}/products`, {
            headers: { 'Accept': 'application/json' }
        });

        if (!resp.ok) {
            throw new Error(`Erro HTTP: ${resp.status}`);
        }

        const produtosApi = await resp.json();

        // esvazia a lista atual
        mockProdutos.length = 0;

        // converte o DTO da API para o formato usado no front
        produtosApi.forEach(p => {
            // tenta vários formatos possíveis vindo do backend
            const categoriaNome =
                (p.category && (p.category.name || p.category.categoryName)) ||
                p.categoryName ||
                p.type ||
                'Outros';
        
            mockProdutos.push({
                id: p.id,
                nome: p.name,
                descricao: p.description || '',
                preco: p.price,
                imagemUrl: 'images/logo.png',   // ou p.imageUrl se tiver depois
                categoria: categoriaNome,
                extras: parseExtras(p.extra),
                destaque: !!p.highlighted
            });
        });

    } catch (e) {
        console.error('Erro ao carregar produtos do backend:', e);
    }
}

function preencherBannersDestaque() {
    const track = document.getElementById('banners-track');
    if (!track) return;

    const destacados = mockProdutos.filter(p => p.destaque);
    const lista = destacados.length ? destacados : mockProdutos.slice(0, 3);

    track.innerHTML = '';

    lista.forEach(produto => {
        const slide = document.createElement('div');
        slide.className = 'banner-slide';
        slide.innerHTML = `
            <div class="banner-info">
                <div class="banner-label">${produto.destaque ? 'Destaque da casa': 'Mais pedido'}</div>
                <h2 class="banner-titulo banner-titulo-limitado">${produto.nome}</h2>
                <p class="banner-desc banner-desc-limitada">${produto.descricao || ''}</p>
                <div class="banner-preco">
                    <span class="preco-atual">R$ ${produto.preco.toFixed(2).replace('.', ',')}</span>
                </div>
                <button class="banner-btn" onclick="addToCartWithFeedback(${produto.id})">
                    <span>Adicionar à sacola</span>
                </button>
            </div>
            <div class="banner-img">
                <img src="${produto.imagemUrl}" alt="${produto.nome}">
            </div>
        `;
        track.appendChild(slide);
    });
}
async function carregarCategoriasDoBackend() {
    try {
      const host = window.location.hostname || 'localhost';
      const baseUrl = `http://${host}:8081`;
  
      const resp = await fetch(`${baseUrl}/category`, {
        headers: { 'Accept': 'application/json' }
      });
  
      if (!resp.ok) {
        console.error('Erro ao carregar categorias', resp.status);
        return;
      }
  
      categorias = await resp.json(); // [{ id, categoryName }]
    } catch (e) {
      console.error('Erro ao carregar categorias', e);
    }
  }

const taxaEntrega = 5.00;
const WHATSAPP_NUMERO = "5511999998888"; // altere para o número real

const API_BASE_URL = `http://${window.location.hostname || "localhost"}:8081`;


// ================== PERFIL / CADASTRO ==================
function getPerfil() {
    try {
        const data = localStorage.getItem('ldPerfil');
        return data ? JSON.parse(data) : null;
    } catch (e) {
        console.error('Erro lendo perfil:', e);
        return null;
    }
}

function salvarPerfil(dados) {
    try {
        localStorage.setItem('ldPerfil', JSON.stringify(dados));
    } catch (e) {
        console.error('Erro salvando perfil:', e);
    }
}

function apagarPerfil() {
    try {
        localStorage.removeItem('ldPerfil');
    } catch (e) {
        console.error('Erro removendo perfil:', e);
    }
}

function perfilIncompleto(perfil) {
    if (!perfil) return true;
    if (!perfil.nome) return true;
    if (!perfil.telefone) return true;
    if (!perfil.logradouro) return true;
    if (!perfil.numero) return true;
    if (!perfil.bairro) return true;
    if (!perfil.cidade) return true;
    return false;
}

function abrirModalPerfilGlobal() {
    const modal = document.getElementById('modal-perfil');
    if (!modal) {
        console.warn('modal-perfil não existe nesta página');
        return false;
    }
    preencherFormPerfilGlobal();
    registrarEventosCepPerfil();
    modal.style.display = 'flex';
    return true;
}

function fecharModalPerfilGlobal() {
    const modal = document.getElementById('modal-perfil');
    if (!modal) return;
    modal.style.display = 'none';
}

function preencherVisualPerfilGlobal() {
    const dados = getPerfil();

    // Mini perfil (header)
    const nomeEl = document.getElementById('perfil-nome');
    const endEl  = document.getElementById('perfil-endereco');

    if (nomeEl) nomeEl.textContent = dados?.nome || '-';

    const userLinha1 = dados
        ? [dados.logradouro, dados.numero].filter(Boolean).join(', ')
        : '';
    const userLinha2 = dados
        ? [
            dados.bairro,
            dados.cidade && dados.uf ? `${dados.cidade}/${dados.uf}` : (dados.cidade || dados.uf)
        ].filter(Boolean).join(' - ')
        : '';

    if (endEl) {
        if (!dados || perfilIncompleto(dados)) {
            endEl.innerHTML = 'Nenhum endereço cadastrado ainda.';
        } else {
            endEl.innerHTML = `
                ${userLinha1}<br>
                ${userLinha2}<br>
                ${dados.complemento ? dados.complemento + '<br>' : ''}
                ${dados.cep ? 'CEP: ' + dados.cep + '<br>' : ''}
                Tel: ${dados.telefone || '-'}
            `;
        }
    }

    // Bloco de endereço do CHECKOUT
    const entregaBox = document.getElementById('checkout-endereco-dados');
    if (entregaBox) {
        const tipoEntregaSelecionado =
            document.querySelector('input[name="tipoEntrega"]:checked')?.value || 'ENTREGA';

        if (tipoEntregaSelecionado === 'RETIRADA') {
            const loja = ENDERECO_COMERCIO;

            const lojaLinha1 = [loja.logradouro, loja.numero].filter(Boolean).join(', ');
            const lojaCidadeUf = (loja.cidade && loja.uf)
                ? `${loja.cidade}/${loja.uf}`
                : (loja.cidade || loja.uf || '');
            const lojaLinha2 = [loja.bairro, lojaCidadeUf].filter(Boolean).join(' - ');

            entregaBox.innerHTML = `
                <div class="endereco-linha"><strong>${loja.nome}</strong></div>
                ${lojaLinha1 ? `<div class="endereco-linha">${lojaLinha1}</div>` : ''}
                ${lojaLinha2 ? `<div class="endereco-linha">${lojaLinha2}</div>` : ''}
                ${loja.complemento ? `<div class="endereco-linha">${loja.complemento}</div>` : ''}
                <div class="endereco-linha">
                    ${loja.cep ? 'CEP: ' + loja.cep + ' - ' : ''}
                    Tel: ${loja.telefone || '-'}
                </div>
            `;
        } else {
        
            if (!dados || perfilIncompleto(dados)) {
                entregaBox.innerHTML = `
                    <div class="checkout-endereco-alerta">
                        Dados incompletos para entrega.<br>
                        Clique em "Editar dados de entrega" antes de finalizar.
                    </div>
                `;
            } else {
                entregaBox.innerHTML = `
                    <div class="endereco-linha"><strong>${dados.nome}</strong></div>
                    <div class="endereco-linha">${userLinha1}</div>
                    <div class="endereco-linha">${userLinha2}</div>
                    <div class="endereco-linha">${dados.complemento || ""}</div>
                    <div class="endereco-linha">
                        ${dados.cep ? 'CEP: ' + dados.cep + ' - ' : ''}
                        Tel: ${dados.telefone}
                    </div>
                `;
            }
        }
    }
}

function preencherFormPerfilGlobal() {
    const dados = getPerfil();

    const inputNome        = document.getElementById('perfilNome');
    const inputCep         = document.getElementById('perfilCep');
    const inputLogradouro  = document.getElementById('perfilLogradouro');
    const inputNumero      = document.getElementById('perfilNumero');
    const inputBairro      = document.getElementById('perfilBairro');
    const inputCidade      = document.getElementById('perfilCidade');
    const inputUf          = document.getElementById('perfilUf');
    const inputTelefone    = document.getElementById('perfilTelefone');
    const inputComplemento = document.getElementById('perfilComplemento');

    if (inputNome)        inputNome.value        = dados?.nome || '';
    if (inputCep)         inputCep.value         = dados?.cep || '';
    if (inputLogradouro)  inputLogradouro.value  = dados?.logradouro || '';
    if (inputNumero)      inputNumero.value      = dados?.numero || '';
    if (inputBairro)      inputBairro.value      = dados?.bairro || '';
    if (inputCidade)      inputCidade.value      = dados?.cidade || '';
    if (inputUf)          inputUf.value          = dados?.uf || '';
    if (inputTelefone)    inputTelefone.value    = dados?.telefone || '';
    if (inputComplemento) inputComplemento.value = dados?.complemento || '';
}

function atualizarResumoEntregaHome() {
    const dados   = getPerfil();
    const textoEl = document.getElementById('home-endereco-text');
    const acaoBtn = document.getElementById('home-endereco-acao');

    if (!textoEl || !acaoBtn) return;

    if (!dados || perfilIncompleto(dados)) {
        textoEl.textContent = "Informe seu endereço para ver o prazo e valor de entrega.";
        acaoBtn.textContent = "Adicionar endereço";
        return;
    }

    const linhaCidadeUf = dados.cidade && dados.uf
        ? `${dados.cidade}/${dados.uf}`
        : dados.cidade || dados.uf || '';

    const complementoLinha = dados.bairro && linhaCidadeUf
        ? `${dados.bairro} - ${linhaCidadeUf}`
        : dados.bairro || linhaCidadeUf || '';

    let texto = `Entregar em: ${dados.logradouro}, ${dados.numero}`;
    if (complementoLinha) {
        texto += ` • ${complementoLinha}`;
    }

    textoEl.textContent = texto;
    acaoBtn.textContent = "Alterar endereço";
}

function salvarPerfilDoModal() {
    const inputNome        = document.getElementById('perfilNome');
    const inputCep         = document.getElementById('perfilCep');
    const inputLogradouro  = document.getElementById('perfilLogradouro');
    const inputNumero      = document.getElementById('perfilNumero');
    const inputBairro      = document.getElementById('perfilBairro');
    const inputCidade      = document.getElementById('perfilCidade');
    const inputUf          = document.getElementById('perfilUf');
    const inputTelefone    = document.getElementById('perfilTelefone');
    const inputComplemento = document.getElementById('perfilComplemento');

    const novo = {
        nome:        (inputNome?.value || '').trim(),
        cep:         (inputCep?.value || '').trim(),
        logradouro:  (inputLogradouro?.value || '').trim(),
        numero:      (inputNumero?.value || '').trim(),
        bairro:      (inputBairro?.value || '').trim(),
        cidade:      (inputCidade?.value || '').trim(),
        uf:          (inputUf?.value || '').trim().toUpperCase(),
        telefone:    (inputTelefone?.value || '').trim(),
        complemento: (inputComplemento?.value || '').trim()
    };

    if (!novo.nome || !novo.telefone || !novo.logradouro || !novo.numero || !novo.bairro || !novo.cidade) {
        showActionFeedback(
            "Atenção",
            "Preencha nome, telefone, rua, número, bairro e cidade para continuar."
        );
        return;
    }

    salvarPerfil(novo);
    preencherVisualPerfilGlobal();
    atualizarResumoEntregaHome();
    fecharModalPerfilGlobal();

    showActionFeedback(
        "Dados salvos",
        "As informações do seu endereço foram salvas. Agora você já pode finalizar o pedido."
    );
}

function excluirPerfilFluxo() {
    showConfirmDelete(
        "Excluir conta",
        "Tem certeza que deseja excluir sua conta? Seus dados de entrega serão removidos.",
        () => {
            apagarPerfil();
            preencherVisualPerfilGlobal();
            preencherFormPerfilGlobal();
            atualizarResumoEntregaHome();
            showActionFeedback(
                "Conta excluída",
                "Seus dados foram removidos deste dispositivo."
            );
        }
    );
}

// ================== VIA CEP ==================
function registrarEventosCepPerfil() {
    const cepInput = document.getElementById('perfilCep');
    if (!cepInput) return;

    if (cepInput._viaCepReg) return;
    cepInput._viaCepReg = true;

    const acionarBusca = () => {
        const valor = cepInput.value.trim();
        if (!valor) return;
        buscarCepEPreencherEndereco(valor);
    };

    cepInput.addEventListener('blur', acionarBusca);
    cepInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            acionarBusca();
        }
    });
}

function buscarCepEPreencherEndereco(cepBruto) {
    const cepLimpo = cepBruto.replace(/\D/g, '');

    if (cepLimpo.length !== 8) {
        showActionFeedback(
            "CEP inválido",
            "Digite um CEP válido com 8 números."
        );
        return;
    }

    const url = `https://viacep.com.br/ws/${cepLimpo}/json/`;

    fetch(url)
        .then(r => r.json())
        .then(dados => {
            if (dados.erro) {
                showActionFeedback(
                    "CEP não encontrado",
                    "Não foi possível localizar o CEP informado. Verifique e tente novamente."
                );
                return;
            }

            const cepInput        = document.getElementById('perfilCep');
            const logradouroInput = document.getElementById('perfilLogradouro');
            const bairroInput     = document.getElementById('perfilBairro');
            const cidadeInput     = document.getElementById('perfilCidade');
            const ufInput         = document.getElementById('perfilUf');

            if (cepInput)        cepInput.value        = dados.cep || cepLimpo;
            if (logradouroInput) logradouroInput.value = dados.logradouro || '';
            if (bairroInput)     bairroInput.value     = dados.bairro || '';
            if (cidadeInput)     cidadeInput.value     = dados.localidade || '';
            if (ufInput)         ufInput.value         = (dados.uf || '').toUpperCase();

            showActionFeedback(
                "Endereço encontrado",
                "Preenchemos rua, bairro, cidade e UF com base no CEP. Confira se está tudo certo."
            );
        })
        .catch(() => {
            showActionFeedback(
                "Erro na consulta",
                "Não foi possível consultar o CEP no momento. Verifique sua conexão e tente novamente."
            );
        });
}

// ================== UI GLOBAL ==================
function configurarBotaoHeaderEntrega() {
    const btn = document.getElementById('btn-header-entrega');
    if (!btn) return;

    btn.addEventListener('click', () => {
        abrirModalPerfilGlobal();
    });
}

function atualizarStatusLoja() {
    const flagEl = document.getElementById('loja-status-flag');
    const descEl = document.getElementById('loja-status-desc');
    if (!flagEl || !descEl) return;

    const agora = new Date();
    const hora  = agora.getHours();
    const aberto = hora >= 18 && hora < 23;

    if (aberto) {
        flagEl.textContent = "Aberto";
        flagEl.classList.add('status-aberto');
        flagEl.classList.remove('status-fechado');
        descEl.textContent = "Entregas em ~40 min na sua região.";
    } else {
        flagEl.textContent = "Fechado";
        flagEl.classList.add('status-fechado');
        flagEl.classList.remove('status-aberto');
        descEl.textContent = "Fora do horário. Consulte nossos horários abaixo.";
    }
}

function configurarBotaoWhatsFlutuante() {
    const btn = document.getElementById('btn-whats-flutuante');
    if (!btn) return;

    btn.addEventListener('click', () => {
        const msg = encodeURIComponent("Olá, gostaria de fazer um pedido.");
        const link = "https://wa.me/" + WHATSAPP_NUMERO + "?text=" + msg;
        window.open(link, "_blank");
    });
}

function showActionFeedback(titulo, texto) {
    const overlay = document.getElementById('modal-msg');
    const titleEl = document.getElementById('modal-msg-title');
    const textEl  = document.getElementById('modal-msg-text');
    const okBtn   = document.getElementById('modal-msg-ok-btn');
    const closeBtn = document.getElementById('modal-msg-close-btn');

    if (!overlay || !titleEl || !textEl) {
        console.log('FEEDBACK:', titulo, texto);
        return;
    }

    titleEl.textContent = titulo;
    textEl.textContent  = texto;
    overlay.style.display = 'flex';

    function fechar() {
        overlay.style.display = 'none';
    }
    if (okBtn)   okBtn.onclick   = fechar;
    if (closeBtn) closeBtn.onclick = fechar;
}

function showConfirmDelete(titulo, texto, onConfirm) {
    const overlay   = document.getElementById('modal-confirm-delete');
    const titleEl   = document.getElementById('modal-confirm-delete-title');
    const textEl    = document.getElementById('modal-confirm-delete-text');
    const cancelBtn = document.getElementById('modal-confirm-delete-cancel-btn');
    const confirmBtn= document.getElementById('modal-confirm-delete-ok-btn');
    const closeBtn  = document.getElementById('modal-confirm-delete-close-btn');

    if (!overlay || !titleEl || !textEl || !cancelBtn || !confirmBtn) {
        console.warn("Modal de confirmação não encontrado na página.");
        return;
    }

    titleEl.textContent = titulo;
    textEl.textContent  = texto;
    overlay.style.display = 'flex';

    function fechar() {
        overlay.style.display = 'none';
        cancelBtn.onclick = null;
        confirmBtn.onclick = null;
        if (closeBtn) closeBtn.onclick = null;
    }

    cancelBtn.onclick = fechar;
    if (closeBtn) closeBtn.onclick = fechar;

    confirmBtn.onclick = () => {
        fechar();
        if (typeof onConfirm === 'function') onConfirm();
    };
}

// ================== CARRINHO / SACOLA ==================
function getCart() {
    try {
        const data = localStorage.getItem('ldLanchesCart');
        return data ? JSON.parse(data) : [];
    } catch (e) {
        console.error('Erro lendo carrinho:', e);
        return [];
    }
}

function saveCart(cart) {
    try {
        localStorage.setItem('ldLanchesCart', JSON.stringify(cart));
    } catch (e) {
        console.error('Erro salvando carrinho:', e);
    }
}

function clearCart() {
    try {
        localStorage.removeItem('ldLanchesCart');
    } catch (e) {
        console.error('Erro limpando carrinho:', e);
    }
}

/**
 * Adiciona um produto ao carrinho, com os extras selecionados.
 * produto: objeto de mockProdutos
 * extrasSelecionados: { "Carne extra": 1, "Cheddar extra": 2, ... }
 */
function addToCart(produto, extrasSelecionados = {}) {
    const cart = getCart();

    // monta a lista de extras com preço e quantidade
    const extrasDetalhados = (produto.extras || [])
        .map(extra => ({
            nome: extra.nome,
            preco: extra.preco,
            qtd: extrasSelecionados[extra.nome] || 0
        }))
        .filter(e => e.qtd > 0);

    // considera dois itens iguais se tiverem o mesmo produto + mesma combinação de extras
    const existente = cart.find(item =>
        item.id === produto.id &&
        JSON.stringify(item.extras || []) === JSON.stringify(extrasDetalhados)
    );

    if (existente) {
        existente.quantidade += 1;
    } else {
        cart.push({
            id: produto.id,
            nome: produto.nome,
            precoBase: produto.preco,
            imagemUrl: produto.imagemUrl,
            quantidade: 1,
            extras: extrasDetalhados   // [{nome, preco, qtd}]
        });
    }
    

    saveCart(cart);
}

/**
 * Versão com feedback visual (toast/modal)
 */
function addToCartWithFeedback(produtoId, extrasSelecionados = {}) {
    const produto = mockProdutos.find(p => p.id === produtoId);
    if (!produto) return;

    addToCart(produto, extrasSelecionados);

    const nome = produto.nome || "Item";
    showActionFeedback(
        "Adicionado à sacola",
        nome + " foi adicionado na sua sacola."
    );
    atualizarBadgeCarrinho();
}

function updateCartQuantity(produtoId, action) {
    const cart = getCart(); // lê do localStorage

    // acha o primeiro item com esse id
    const index = cart.findIndex(item => item.id === produtoId);
    if (index === -1) return;

    if (action === 'add') {
        cart[index].quantidade += 1;
    } else if (action === 'remove') {
        cart[index].quantidade -= 1;
        if (cart[index].quantidade <= 0) {
            cart.splice(index, 1); // remove item da sacola
        }
    }

    saveCart(cart);
}

function atualizarBadgeCarrinho() {
    const badge = document.getElementById('cart-badge');
    if (!badge) return;
    const cart = getCart();
    const qtdTotal = cart.reduce((acc, item) => acc + item.quantidade, 0);
    badge.textContent = qtdTotal;
}


// ================== LISTA DE PRODUTOS (HOME) ==================
let estadoCategoriaAtual = "Tudo";

function getTermoBusca() {
    const campo = document.getElementById('campo-busca');
    if (!campo) return "";
    return campo.value.trim().toLowerCase();
}

function filtrarProdutos() {
    const termo = getTermoBusca();
    return mockProdutos.filter(p => {
        if (estadoCategoriaAtual !== "Tudo" && p.categoria !== estadoCategoriaAtual) {
            return false;
        }
        if (!termo) return true;
        const alvo = (p.nome + " " + p.descricao).toLowerCase();
        return alvo.includes(termo);
    });
}

function renderizarProdutosNaTela(listaProdutos) {
    const container = document.getElementById('lista-produtos');
    if (!container) return;

    if (!listaProdutos || listaProdutos.length === 0) {
        container.innerHTML = `
            <div class="lista-vazia">
                Nenhum item encontrado.
            </div>
        `;
        return;
    }

    container.innerHTML = "";

    listaProdutos.forEach(prod => {
        const card = `
            <div class="produto-card" data-id="${prod.id}">
                <div class="produto-img">
                    <img src="${prod.imagemUrl}" alt="${prod.nome}" />
                </div>
                <div class="produto-info">
                    <div class="produto-nome">${prod.nome}</div>
                    <div class="produto-desc">${prod.descricao}</div>
                    <div class="produto-preco">R$ ${prod.preco.toFixed(2).replace('.', ',')}</div>
                </div>
                <div class="produto-acoes">
                    <button class="btn-add-carrinho" data-id="${prod.id}">
                        Adicionar
                    </button>
                    <a class="btn-detalhes" href="produto.html?id=${prod.id}">
                        Ver detalhes
                    </a>
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', card);
    });

    container.querySelectorAll('.btn-add-carrinho').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = parseInt(btn.getAttribute('data-id'), 10);
            addToCartWithFeedback(id);
        });
    });
}

function atualizarListaProdutosFiltrada() {
    const filtrados = filtrarProdutos();
    renderizarProdutosNaTela(filtrados);
}

function inicializarCategoriasELista() {
    const categoriasScroll = document.getElementById('categorias-scroll');
    if (categoriasScroll) {
        categoriasScroll.querySelectorAll('.cat-btn').forEach(botao => {
            botao.addEventListener('click', () => {
                categoriasScroll.querySelectorAll('.cat-btn').forEach(b => b.classList.remove('active'));
                botao.classList.add('active');

                estadoCategoriaAtual = botao.getAttribute('data-cat') || "Tudo";
                atualizarListaProdutosFiltrada();
            });
        });
    }

    atualizarListaProdutosFiltrada();
}

// ================== FINALIZAR PEDIDO / WHATSAPP ==================
function gerarCodigoPedido() {
    const agora = Date.now();
    const sequencia = (agora % 1000000).toString().padStart(6, "0");
    return "LD-" + sequencia; // ex: LD-083412
}

function showWhatsappPrompt(pedido) {
    const overlay     = document.getElementById('modal-whatsapp');
    const titleEl     = document.getElementById('modal-whatsapp-title');
    const textEl      = document.getElementById('modal-whatsapp-text');
    const btnAgoraNao = document.getElementById('modal-whatsapp-cancel-btn');
    const btnWhatsapp = document.getElementById('modal-whatsapp-ok-btn');
    const closeBtn    = document.getElementById('modal-whatsapp-close-btn');

    if (!overlay || !titleEl || !textEl || !btnAgoraNao || !btnWhatsapp) {
        console.warn("Modal whatsapp não encontrado.");
        return;
    }

    const perfil    = getPerfil() || {};
    const itens     = pedido.itens || [];
    const pedidoId  = pedido.id;
    const pagamento = pedido.pagamento;
    const total     = pedido.total || 0;

    titleEl.textContent = "Pedido confirmado";
    textEl.textContent =
        "Seu pedido " + pedidoId +
        " foi confirmado. Você quer acompanhar o status pelo WhatsApp?";

    overlay.style.display = 'flex';

    function encerrarFluxo() {
        clearCart();
        atualizarBadgeCarrinho();

        overlay.style.display = 'none';

        btnAgoraNao.onclick = null;
        btnWhatsapp.onclick = null;
        if (closeBtn) closeBtn.onclick = null;

        if (window.location.pathname.includes('checkout')) {
            window.location.href = 'index.html';
        }
    }

    btnAgoraNao.onclick = encerrarFluxo;
    if (closeBtn) {
        closeBtn.onclick = encerrarFluxo;
    }

    btnWhatsapp.onclick = () => {
        const linhasItens = itens.map(item => {
            const totalExtrasItem = (item.extras || []).reduce(
                (soma, extra) => soma + (extra.preco * extra.qtd),
                0
            );
            const totalItem = (item.precoBase * item.quantidade) + totalExtrasItem;
        
            return `${item.quantidade}x ${item.nome} - R$ ${totalItem.toFixed(2).replace('.', ',')}`;
        });

        const linhaEndereco1 = [perfil.logradouro, perfil.numero].filter(Boolean).join(', ');
        const linhaEndereco2 = [
            perfil.bairro,
            perfil.cidade && perfil.uf ? `${perfil.cidade}/${perfil.uf}` : perfil.cidade || perfil.uf
        ].filter(Boolean).join(' - ');

        const msg = encodeURIComponent(
            `Olá, gostaria de acompanhar meu pedido ${pedidoId}.\n\n` +
            `Pagamento: ${pagamento}\n` +
            `Total: R$ ${total.toFixed(2).replace('.', ',')}\n\n` +
            (perfil.nome ? `Nome: ${perfil.nome}\n` : "") +
            (linhaEndereco1 ? `Endereço: ${linhaEndereco1}\n` : "") +
            (linhaEndereco2 ? `Região: ${linhaEndereco2}\n` : "") +
            (perfil.complemento ? `Complemento: ${perfil.complemento}\n` : "") +
            (perfil.cep ? `CEP: ${perfil.cep}\n` : "") +
            (perfil.telefone ? `Telefone: ${perfil.telefone}\n\n` : "") +
            (linhasItens.length ? `Itens do pedido:\n${linhasItens.join('\n')}` : "")
        );

        const link = "https://wa.me/" + WHATSAPP_NUMERO + "?text=" + msg;
        window.open(link, "_blank");

        encerrarFluxo();
    };
}

// Monta a descrição do pedido que será enviada para o backend /order
function montarDescricaoParaBackend(pedido, perfil) {
    const itens = pedido.itens || [];
    const linhas = [];

    // --- produtos ---
    linhas.push("Produtos:");
    itens.forEach(item => {
        const totalExtrasItem = (item.extras || []).reduce(
            (soma, extra) => soma + (extra.preco * extra.qtd),
            0
        );
        const totalItem = (item.precoBase * item.quantidade) + totalExtrasItem;

        const extrasTexto = (item.extras || [])
            .filter(e => e.qtd > 0)
            .map(e => `${e.qtd}x ${e.nome}`)
            .join(", ");

        const extrasParte = extrasTexto ? ` | Extras: ${extrasTexto}` : "";

        linhas.push(
            `- ${item.quantidade}x ${item.nome} (R$ ${totalItem.toFixed(2).replace('.', ',')})${extrasParte}`
        );
    });

    // --- endereço / entrega ---
    if (perfil) {
        const linhaEndereco1 = [perfil.logradouro, perfil.numero]
            .filter(Boolean)
            .join(", ");

        const cidadeUf = (perfil.cidade && perfil.uf)
            ? `${perfil.cidade}/${perfil.uf}`
            : (perfil.cidade || perfil.uf || "");

        const linhaEndereco2 = [perfil.bairro, cidadeUf]
            .filter(Boolean)
            .join(" - ");

        if (linhaEndereco1 || linhaEndereco2 || perfil.complemento) {
            linhas.push("");
            linhas.push("Entrega:");
            if (linhaEndereco1) linhas.push(`- ${linhaEndereco1}`);
            if (linhaEndereco2) linhas.push(`- ${linhaEndereco2}`);
            if (perfil.complemento) linhas.push(`- ${perfil.complemento}`);
        }

        if (perfil.nome) {
            linhas.push("");
            linhas.push("Cliente:");
            linhas.push(`- ${perfil.nome}`);
        }

        if (perfil.telefone) {
            linhas.push("");
            linhas.push("Contato:");
            linhas.push(`- ${perfil.telefone}`);
        }
    }

    // --- pagamento ---
    linhas.push("");
    linhas.push("Pagamento:");
    linhas.push(`- ${pedido.pagamento || "Não informado"}`);

    // --- total ---
    linhas.push("");
    linhas.push("Total:");
    linhas.push(`- R$ ${(pedido.total || 0).toFixed(2).replace('.', ',')}`);

    return linhas.join("\n");
}

// Envia o pedido para o backend Spring (POST /order)
async function enviarPedidoParaBackend(pedido) {
    const perfil = getPerfil() || {};

    const descricao = montarDescricaoParaBackend(pedido, perfil);

    const body = {
        code: pedido.id,              // ex: "LDL-0001"
        description: descricao,       // texto completo
        total: Number((pedido.total || 0).toFixed(2))  // BigDecimal no backend
    };

    const url = (typeof API_BASE_URL !== "undefined"
        ? API_BASE_URL
        : `http://${window.location.hostname || 'localhost'}:8081`) + "/order";

    const resp = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: JSON.stringify(body)
    });

    if (!resp.ok) {
        const texto = await resp.text().catch(() => "");
        throw new Error(`Erro ao criar pedido no backend: ${resp.status} - ${texto}`);
    }

    // se quiser usar algo depois (id, status, etc)
    return await resp.json().catch(() => null);
}

function fmtBRL(n) { return Number(n || 0).toLocaleString('pt-BR',{style:'currency',currency:'BRL'}); }

// Monta a descrição no padrão do painel (Produtos / Entrega|Retirada / Cliente / Pagamento / Obs / Total)
function montarDescricaoParaBackend(pedido, perfil) {
  const itens = pedido.itens || [];
  const linhas = [];

  // Produtos
  linhas.push("Produtos:");
  itens.forEach(item => {
    const extrasValor = (item.extras || []).reduce((s,e)=> s + (e.preco * e.qtd), 0);
    const totalItem = (item.precoBase * item.quantidade) + extrasValor;
    const extrasTxt = (item.extras || []).filter(e=>e.qtd>0).map(e=>`${e.qtd}x ${e.nome}`).join(", ");
    const extrasParte = extrasTxt ? ` | Extras: ${extrasTxt}` : "";
    linhas.push(`- ${item.quantidade}x ${item.nome} (${fmtBRL(totalItem)})${extrasParte}`);
  });

  // Entrega / Retirada
  if (pedido.tipoEntrega === 'RETIRADA') {
    linhas.push("", "Retirada no balcão");
  } else {
    const l1 = [perfil?.logradouro, perfil?.numero].filter(Boolean).join(", ");
    const cidadeUf = (perfil?.cidade && perfil?.uf) ? `${perfil.cidade}/${perfil.uf}` : (perfil?.cidade || perfil?.uf || "");
    const l2 = [perfil?.bairro, cidadeUf].filter(Boolean).join(" - ");

    linhas.push("", "Entrega:");
    if (l1) linhas.push(`- ${l1}`);
    if (l2) linhas.push(`- ${l2}`);
    if (perfil?.complemento) linhas.push(`- ${perfil.complemento}`);
  }

  // Cliente
  if (perfil?.nome) {
    linhas.push("", "Cliente:", `- ${perfil.nome}`);
  }

  // Contato
  if (perfil?.telefone) {
    linhas.push("", "Contato:", `- ${perfil.telefone}`);
  }

  // Pagamento
  linhas.push("", "Pagamento:", `- ${pedido.pagamento || "Não informado"}`);

  // Obs
  if (pedido.obs) {
    linhas.push("", "Obs:", `- ${pedido.obs}`);
  }

  // Total
  linhas.push("", "Total:", `- ${fmtBRL(pedido.total)}`);

  return linhas.join("\n");
}

async function enviarPedidoParaBackend(pedido) {
    const perfil = (typeof getPerfil === 'function' ? getPerfil() : {}) || {};
    const descricao = montarDescricaoParaBackend(pedido, perfil);
  
    const body = {
      code: pedido.id,                 // Ex.: "LDL-0A1B2C"
      description: descricao,
      total: Number((pedido.total || 0).toFixed(2))
    };
  
    const resp = await fetch(`${API_BASE_URL}/order`, {
      method: "POST",
      headers: { "Content-Type": "application/json", "Accept": "application/json" },
      body: JSON.stringify(body)
    });
  
    if (!resp.ok) throw new Error(`Falha ao criar pedido: ${resp.status} ${await resp.text().catch(()=> '')}`);
    return await resp.json(); // OrderOutDTO {id, code, total, description, date, status}
  }

  function gerarCodigoPedido() {
    const segundos = Math.floor(Date.now() / 1000);
    return segundos.toString().slice(-4);
}
  
  async function finalizarPedidoFluxo(metodoPagamento) {
    const perfil = getPerfil && getPerfil();
    if (perfilIncompleto && perfilIncompleto(perfil)) {
      abrirModalPerfilGlobal && abrirModalPerfilGlobal();
      if (typeof registrarEventosCepPerfil === 'function') registrarEventosCepPerfil();
      return;
    }
  
    const cart = getCart && getCart();
    if (!cart || !cart.length) {
      showActionFeedback && showActionFeedback("Sua sacola está vazia", "Adicione itens antes de finalizar.");
      return;
    }
  
    if (!metodoPagamento) {
      showActionFeedback && showActionFeedback("Selecione a forma de pagamento", "Escolha Pix, Cartão ou Dinheiro.");
      return;
    }
  
    // → tipo de entrega + obs do checkout
    const tipoEntrega = document.querySelector('input[name="tipoEntrega"]:checked')?.value || 'ENTREGA';
    const obs = document.querySelector('#observacoes')?.value?.trim() || "";
  
    // Subtotal
    let subtotal = 0;
    cart.forEach(item => {
      const extrasValor = (item.extras || []).reduce((s,e)=> s + (e.preco * e.qtd), 0);
      subtotal += (item.precoBase * item.quantidade) + extrasValor;
    });
  
    // Taxa de entrega (0 para RETIRADA)
    const taxa = (tipoEntrega === 'RETIRADA') ? 0 : (typeof taxaEntrega !== 'undefined' ? taxaEntrega : 0);
    const total = subtotal + taxa;
    
    const agora = new Date();

    const pedido = {
        id: gerarCodigoPedido(),
        itens: cart,
        subtotal,
        entrega: taxa,
        total,
        pagamento: metodoPagamento,
        tipoEntrega,
        obs,
        status: 'PENDENTE', // pode ser 'pendente' se preferir, o importante é a consistência
        dataIso: agora.toISOString(),
        dataDisplay: agora.toLocaleString('pt-BR'),
        // hora da última mudança de status (começa agora, na criação)
        statusUpdatedAt: agora.toISOString()
      };
  
    // registra no backend
        try {
        const created = await enviarPedidoParaBackend(pedido);
        if (created) {
            pedido.backendId = created.id;
            pedido.code      = created.code;

            const novoStatus = created.status || 'NOVO';

            // data/hora que o backend mandou (ex: "Hoje, 15:30")
            pedido.date             = created.date;          // "Hoje, 15:30"
            pedido.descricaoBackend = created.description;  // se quiser visualizar

            // se o status vindo do backend for diferente do que estava,
            // considera que acabou de mudar e atualiza a hora
            if (novoStatus !== pedido.status) {
            pedido.statusUpdatedAt = new Date().toISOString();
            }

            pedido.status = novoStatus;
        }
        } catch (e) {
        console.error(e);
        showActionFeedback && showActionFeedback("Erro ao registrar pedido", "Tente novamente em instantes.");
        return;
        }
  
    // histórico local do cliente (pedidos.html)
    const pedidos = (getPedidos && getPedidos()) || [];
    pedidos.unshift(pedido);
    savePedidos && savePedidos(pedidos);
  
    // limpa sacola e segue o fluxo normal
    saveCart && saveCart([]);
    showWhatsappPrompt && showWhatsappPrompt(pedido);
  }




// ================== HISTÓRICO DE PEDIDOS ==================
function getPedidos() {
    try {
        const data = localStorage.getItem('ldPedidos');
        return data ? JSON.parse(data) : [];
    } catch (e) {
        console.error('Erro lendo pedidos:', e);
        return [];
    }
}

function savePedidos(lista) {
    try {
        localStorage.setItem('ldPedidos', JSON.stringify(lista));
    } catch (e) {
        console.error('Erro salvando pedidos:', e);
    }
}

function isPaginaPedidos() {
    return /pedidos\.html(\?|$)/.test(location.pathname) || document.body.dataset.page === 'pedidos';
  }
  
  // mapeia enum do backend para label do site (se você usa rótulos diferentes)
  function mapStatusLabel(s) {
    switch ((s || "").toUpperCase()) {
      case "NOVO": return "Novo";
      case "VISTO": return "Visto";
      case "EM_PREPARO": return "Em preparo";
      case "SAIU_PARA_ENTREGA": return "Saiu para entrega";
      case "FINALIZADO": return "Finalizado";
      default: return s || "—";
    }
  }
  
  async function atualizarPedidosComBackendSePossivel() {
    let pedidos = (typeof getPedidos === "function" ? getPedidos() : []) || [];
    if (!pedidos.length) return;
  
    const atualizados = await Promise.all(
      pedidos.map(async (p) => {
        // pedidos antigos (antes do backend) não têm backendId → deixa como está
        if (!p.backendId) return p;
  
        try {
          const r = await fetch(`${API_BASE_URL}/order/${p.backendId}`, {
            headers: { "Accept": "application/json" }
          });
  
          if (!r.ok) return p;
  
          const dto = await r.json();
  
          const statusAnterior = p.status;
          const novoStatus = dto.status || statusAnterior;
  
          // por padrão, mantém a hora antiga (se existir)
          let statusUpdatedAt = p.statusUpdatedAt;
  
          // se o backend mudou o status, registra a hora dessa mudança
          if (novoStatus && statusAnterior && novoStatus !== statusAnterior) {
            statusUpdatedAt = new Date().toISOString();
          }
  
          return {
            ...p,
            code: dto.code || p.code,
            total: dto.total ?? p.total,
  
            // status "cru" vindo do backend (NOVO, VISTO, EM_PREPARO, .)
            status: novoStatus,
  
            // status "bonito" para exibir, se você quiser usar no futuro
            statusLabel: mapStatusLabel(dto.status) || p.statusLabel || p.status,
  
            // hora da última alteração de status
            statusUpdatedAt,
  
            date: dto.date || p.date,
            descricaoBackend: dto.description || p.descricaoBackend
          };
        } catch (e) {
          console.error("Erro ao atualizar pedido com backend", e);
          return p;
        }
      })
    );
  
    if (typeof savePedidos === "function") {
      savePedidos(atualizados);
    }
  
    // Página pedidos.html usa preencherListaPedidos()
    if (typeof preencherListaPedidos === "function") {
      preencherListaPedidos();
    }
  }

  document.addEventListener("DOMContentLoaded", () => {
    if (typeof isPaginaPedidos === "function" && isPaginaPedidos()) {
      // primeira carga
      atualizarPedidosComBackendSePossivel();
      // atualiza a cada 15s
      setInterval(atualizarPedidosComBackendSePossivel, 15000);
    }
  });  