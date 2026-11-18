package br.com.cardapio.shared

import kotlinx.serialization.Serializable

// Contratos da API (JSON) compartilhados entre servidor, app e testes.
// Valores em dinheiro sempre em CENTAVOS (Long) para evitar erro de arredondamento.
// Datas/horas sempre em ISO-8601 (UTC) como String; quem exibe converte para o fuso local.

// ---------- Erros ----------

@Serializable
data class ErroDto(val mensagem: String, val codigo: String)

// ---------- Autenticação ----------

@Serializable
data class LoginRequisicao(val email: String, val senha: String)

@Serializable
data class SessaoDto(val token: String, val usuario: UsuarioDto)

@Serializable
data class TrocarSenhaRequisicao(val senhaAtual: String, val novaSenha: String)

// ---------- Usuários ----------

@Serializable
data class UsuarioDto(
    val id: Long,
    val nome: String,
    val email: String,
    val papel: Papel,
    val lojaId: Long? = null,
    val lojaSlug: String? = null,
    val ativo: Boolean = true,
)

@Serializable
data class NovoUsuarioDto(
    val nome: String,
    val email: String,
    val senha: String,
    val papel: Papel,
)

/** Atualização de usuário. [senha] nula ou vazia mantém a senha atual. */
@Serializable
data class AtualizarUsuarioDto(
    val nome: String,
    val email: String,
    val papel: Papel,
    val ativo: Boolean = true,
    val senha: String? = null,
)

// ---------- Lojas ----------

@Serializable
data class AparenciaDto(
    val corPrimaria: String = "#790001",
    val corSecundaria: String = "#FFC107",
    val corFundo: String = "#FFF8F6",
    val logoUrl: String? = null,
    val bannerUrl: String? = null,
    val modeloLayout: ModeloLayout = ModeloLayout.LISTA,
    val mensagemBoasVindas: String? = null,
    val aviso: String? = null,
    val cssExtra: String? = null,
)

@Serializable
data class LojaDto(
    val id: Long,
    val slug: String,
    val nome: String,
    val ativa: Boolean,
    val fusoHorario: String,
    val aparencia: AparenciaDto,
    val modulos: Set<Modulo>,
    val personalizacaoLiberada: Set<ItemPersonalizavel>,
)

@Serializable
data class NovaLojaDto(
    val slug: String,
    val nome: String,
    val donoNome: String,
    val donoEmail: String,
    val donoSenha: String,
    val fusoHorario: String = "America/Sao_Paulo",
    val modulos: Set<Modulo> = Modulo.PADRAO,
    val personalizacaoLiberada: Set<ItemPersonalizavel> = ItemPersonalizavel.PADRAO,
    val aparencia: AparenciaDto = AparenciaDto(),
)

/** Atualização completa feita pelo super admin. */
@Serializable
data class AtualizarLojaDto(
    val slug: String,
    val nome: String,
    val ativa: Boolean,
    val fusoHorario: String,
    val aparencia: AparenciaDto,
    val modulos: Set<Modulo>,
    val personalizacaoLiberada: Set<ItemPersonalizavel>,
)

/** O que o cliente final vê da loja no cardápio público. */
@Serializable
data class VitrineLojaDto(
    val slug: String,
    val nome: String,
    val aparencia: AparenciaDto,
    val modulos: Set<Modulo>,
)

// ---------- Catálogo ----------

@Serializable
data class CategoriaDto(val id: Long, val nome: String, val ordem: Int = 0)

@Serializable
data class SalvarCategoriaDto(val nome: String, val ordem: Int = 0)

@Serializable
data class ProdutoDto(
    val id: Long,
    val categoriaId: Long? = null,
    val categoriaNome: String? = null,
    val nome: String,
    val descricao: String = "",
    val precoCentavos: Long,
    /** Provisório até a Fase 1 (grupos de complementos). Formato: "Bacon:2,00; Ovo:1,50". */
    val adicionais: String = "",
    val destaque: Boolean = false,
    val ativo: Boolean = true,
)

@Serializable
data class SalvarProdutoDto(
    val categoriaId: Long? = null,
    val nome: String,
    val descricao: String = "",
    val precoCentavos: Long,
    val adicionais: String = "",
    val destaque: Boolean = false,
    val ativo: Boolean = true,
)

// ---------- Pedidos ----------

@Serializable
data class PedidoDto(
    val id: Long,
    val codigo: String,
    val descricao: String,
    val totalCentavos: Long,
    val status: StatusPedido,
    val criadoEm: String,
)

/**
 * Pedido enviado pelo cardápio público.
 * ATENÇÃO (Fase 2): ainda é texto livre com total informado pelo cliente; será trocado por itens
 * estruturados com o preço calculado no servidor.
 */
@Serializable
data class NovoPedidoPublicoDto(val descricao: String, val totalCentavos: Long)

/** O que o cliente consegue consultar do próprio pedido (sem dados pessoais). */
@Serializable
data class PedidoPublicoDto(
    val codigo: String,
    val status: StatusPedido,
    val totalCentavos: Long,
    val criadoEm: String,
)

@Serializable
data class AlterarStatusDto(val status: StatusPedido)

// ---------- Dashboard ----------

@Serializable
data class ResumoDashboardDto(
    val faturamentoDiarioCentavos: List<Long>,
    val vendasDiarias: List<Int>,
    val rotulosDias: List<String>,
    val totalFaturamentoCentavos: Long,
    val totalVendas: Int,
)
