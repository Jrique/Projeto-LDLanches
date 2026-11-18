package br.com.cardapio.server.aplicacao

import br.com.cardapio.server.dominio.Aparencia
import br.com.cardapio.server.dominio.Categoria
import br.com.cardapio.server.dominio.Loja
import br.com.cardapio.server.dominio.Pedido
import br.com.cardapio.server.dominio.Produto
import br.com.cardapio.server.dominio.Usuario
import br.com.cardapio.shared.AparenciaDto
import br.com.cardapio.shared.CategoriaDto
import br.com.cardapio.shared.LojaDto
import br.com.cardapio.shared.PedidoDto
import br.com.cardapio.shared.PedidoPublicoDto
import br.com.cardapio.shared.ProdutoDto
import br.com.cardapio.shared.UsuarioDto
import br.com.cardapio.shared.VitrineLojaDto

// Conversões entre o domínio e os DTOs de transporte (módulo shared).

fun Aparencia.paraDto() = AparenciaDto(
    corPrimaria, corSecundaria, corFundo, logoUrl, bannerUrl, modeloLayout, mensagemBoasVindas, aviso, cssExtra,
)

fun AparenciaDto.paraDominio() = Aparencia(
    corPrimaria = corPrimaria.uppercase(),
    corSecundaria = corSecundaria.uppercase(),
    corFundo = corFundo.uppercase(),
    logoUrl = logoUrl?.trim()?.ifEmpty { null },
    bannerUrl = bannerUrl?.trim()?.ifEmpty { null },
    modeloLayout = modeloLayout,
    mensagemBoasVindas = mensagemBoasVindas?.trim()?.ifEmpty { null },
    aviso = aviso?.trim()?.ifEmpty { null },
    cssExtra = cssExtra?.ifBlank { null },
)

fun Loja.paraDto() = LojaDto(id, slug, nome, ativa, fusoHorario, aparencia.paraDto(), modulos, personalizacaoLiberada)

fun Loja.paraVitrine() = VitrineLojaDto(slug, nome, aparencia.paraDto(), modulos)

fun Usuario.paraDto(lojaSlug: String? = null) = UsuarioDto(id, nome, email, papel, lojaId, lojaSlug, ativo)

fun Categoria.paraDto() = CategoriaDto(id, nome, ordem)

fun Produto.paraDto(categoriaNome: String?) =
    ProdutoDto(id, categoriaId, categoriaNome, nome, descricao, precoCentavos, adicionais, destaque, ativo)

fun Pedido.paraDto() = PedidoDto(id, codigo, descricao, totalCentavos, status, criadoEm.toString())

fun Pedido.paraPublico() = PedidoPublicoDto(codigo, status, totalCentavos, criadoEm.toString())
