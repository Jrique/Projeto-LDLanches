package br.com.cardapio.server.infra.banco

import br.com.cardapio.server.dominio.Aparencia
import br.com.cardapio.server.dominio.Categoria
import br.com.cardapio.server.dominio.Loja
import br.com.cardapio.server.dominio.Pedido
import br.com.cardapio.server.dominio.Produto
import br.com.cardapio.server.dominio.RepositorioCatalogo
import br.com.cardapio.server.dominio.RepositorioLojas
import br.com.cardapio.server.dominio.RepositorioPedidos
import br.com.cardapio.server.dominio.RepositorioUsuarios
import br.com.cardapio.server.dominio.Usuario
import br.com.cardapio.shared.ItemPersonalizavel
import br.com.cardapio.shared.ModeloLayout
import br.com.cardapio.shared.Modulo
import br.com.cardapio.shared.Papel
import br.com.cardapio.shared.StatusPedido
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.ZoneOffset

// Implementações com Exposed (DSL). Devem ser chamadas dentro de Transacoes.executar { }.

class RepositorioLojasExposed : RepositorioLojas {
    override fun buscar(id: Long) =
        LojaTabela.selectAll().where { LojaTabela.id eq id }.singleOrNull()?.paraLoja()

    override fun buscarPorSlug(slug: String) =
        LojaTabela.selectAll().where { LojaTabela.slug eq slug }.singleOrNull()?.paraLoja()

    override fun listar() = LojaTabela.selectAll().orderBy(LojaTabela.nome).map { it.paraLoja() }

    override fun existeSlug(slug: String, exceto: Long?): Boolean =
        LojaTabela.selectAll().where {
            val base = LojaTabela.slug eq slug
            if (exceto != null) base and (LojaTabela.id neq exceto) else base
        }.any()

    override fun salvar(loja: Loja): Loja {
        val preencher: LojaTabela.(org.jetbrains.exposed.sql.statements.UpdateBuilder<*>) -> Unit = {
            it[slug] = loja.slug
            it[nome] = loja.nome
            it[ativa] = loja.ativa
            it[fusoHorario] = loja.fusoHorario
            it[corPrimaria] = loja.aparencia.corPrimaria
            it[corSecundaria] = loja.aparencia.corSecundaria
            it[corFundo] = loja.aparencia.corFundo
            it[logoUrl] = loja.aparencia.logoUrl
            it[bannerUrl] = loja.aparencia.bannerUrl
            it[modeloLayout] = loja.aparencia.modeloLayout.name
            it[mensagemBoasVindas] = loja.aparencia.mensagemBoasVindas
            it[aviso] = loja.aparencia.aviso
            it[cssExtra] = loja.aparencia.cssExtra
            it[modulos] = loja.modulos.map(Modulo::name).sorted()
            it[personalizacaoLiberada] = loja.personalizacaoLiberada.map(ItemPersonalizavel::name).sorted()
        }
        return if (loja.id == 0L) {
            val id = LojaTabela.insertAndGetId { preencher(it) }.value
            buscar(id)!!
        } else {
            LojaTabela.update({ LojaTabela.id eq loja.id }) { preencher(it) }
            loja
        }
    }

    private fun ResultRow.paraLoja() = Loja(
        id = this[LojaTabela.id].value,
        slug = this[LojaTabela.slug],
        nome = this[LojaTabela.nome],
        ativa = this[LojaTabela.ativa],
        fusoHorario = this[LojaTabela.fusoHorario],
        aparencia = Aparencia(
            corPrimaria = this[LojaTabela.corPrimaria],
            corSecundaria = this[LojaTabela.corSecundaria],
            corFundo = this[LojaTabela.corFundo],
            logoUrl = this[LojaTabela.logoUrl],
            bannerUrl = this[LojaTabela.bannerUrl],
            modeloLayout = enumOuPadrao(this[LojaTabela.modeloLayout], ModeloLayout.LISTA),
            mensagemBoasVindas = this[LojaTabela.mensagemBoasVindas],
            aviso = this[LojaTabela.aviso],
            cssExtra = this[LojaTabela.cssExtra],
        ),
        modulos = this[LojaTabela.modulos].mapNotNull { nome -> Modulo.entries.find { it.name == nome } }.toSet(),
        personalizacaoLiberada = this[LojaTabela.personalizacaoLiberada]
            .mapNotNull { nome -> ItemPersonalizavel.entries.find { it.name == nome } }.toSet(),
    )
}

class RepositorioUsuariosExposed : RepositorioUsuarios {
    override fun buscar(id: Long) =
        UsuarioTabela.selectAll().where { UsuarioTabela.id eq id }.singleOrNull()?.paraUsuario()

    override fun buscarDaLoja(lojaId: Long, id: Long) =
        UsuarioTabela.selectAll().where { (UsuarioTabela.id eq id) and (UsuarioTabela.lojaId eq lojaId) }
            .singleOrNull()?.paraUsuario()

    override fun buscarPorEmail(email: String) =
        UsuarioTabela.selectAll().where { UsuarioTabela.email.lowerCase() eq Usuario.normalizarEmail(email) }
            .singleOrNull()?.paraUsuario()

    override fun listarDaLoja(lojaId: Long) =
        UsuarioTabela.selectAll().where { UsuarioTabela.lojaId eq lojaId }.orderBy(UsuarioTabela.nome).map { it.paraUsuario() }

    override fun existeEmail(email: String, exceto: Long?): Boolean =
        UsuarioTabela.selectAll().where {
            val base = UsuarioTabela.email.lowerCase() eq Usuario.normalizarEmail(email)
            if (exceto != null) base and (UsuarioTabela.id neq exceto) else base
        }.any()

    override fun existeSuperAdmin(): Boolean =
        UsuarioTabela.selectAll().where { UsuarioTabela.papel eq Papel.SUPER_ADMIN.name }.any()

    override fun salvar(usuario: Usuario): Usuario {
        return if (usuario.id == 0L) {
            val id = UsuarioTabela.insertAndGetId {
                it[lojaId] = usuario.lojaId
                it[nome] = usuario.nome
                it[email] = usuario.email
                it[senhaHash] = usuario.senhaHash
                it[papel] = usuario.papel.name
                it[ativo] = usuario.ativo
            }.value
            buscar(id)!!
        } else {
            UsuarioTabela.update({ UsuarioTabela.id eq usuario.id }) {
                it[nome] = usuario.nome
                it[email] = usuario.email
                it[senhaHash] = usuario.senhaHash
                it[papel] = usuario.papel.name
                it[ativo] = usuario.ativo
            }
            usuario
        }
    }

    override fun remover(lojaId: Long, id: Long) {
        UsuarioTabela.deleteWhere { (UsuarioTabela.id eq id) and (UsuarioTabela.lojaId eq lojaId) }
    }

    private fun ResultRow.paraUsuario() = Usuario(
        id = this[UsuarioTabela.id].value,
        lojaId = this[UsuarioTabela.lojaId],
        nome = this[UsuarioTabela.nome],
        email = this[UsuarioTabela.email],
        senhaHash = this[UsuarioTabela.senhaHash],
        papel = Papel.valueOf(this[UsuarioTabela.papel]),
        ativo = this[UsuarioTabela.ativo],
    )
}

class RepositorioCatalogoExposed : RepositorioCatalogo {
    override fun listarCategorias(lojaId: Long) =
        CategoriaTabela.selectAll().where { CategoriaTabela.lojaId eq lojaId }
            .orderBy(CategoriaTabela.ordem to SortOrder.ASC, CategoriaTabela.nome to SortOrder.ASC)
            .map { it.paraCategoria() }

    override fun buscarCategoria(lojaId: Long, id: Long) =
        CategoriaTabela.selectAll().where { (CategoriaTabela.id eq id) and (CategoriaTabela.lojaId eq lojaId) }
            .singleOrNull()?.paraCategoria()

    override fun existeCategoriaComNome(lojaId: Long, nome: String, exceto: Long?): Boolean =
        CategoriaTabela.selectAll().where {
            val base = (CategoriaTabela.lojaId eq lojaId) and (CategoriaTabela.nome.lowerCase() eq nome.trim().lowercase())
            if (exceto != null) base and (CategoriaTabela.id neq exceto) else base
        }.any()

    override fun salvarCategoria(categoria: Categoria): Categoria {
        return if (categoria.id == 0L) {
            val id = CategoriaTabela.insertAndGetId {
                it[lojaId] = categoria.lojaId
                it[nome] = categoria.nome
                it[ordem] = categoria.ordem
            }.value
            buscarCategoria(categoria.lojaId, id)!!
        } else {
            CategoriaTabela.update({ (CategoriaTabela.id eq categoria.id) and (CategoriaTabela.lojaId eq categoria.lojaId) }) {
                it[nome] = categoria.nome
                it[ordem] = categoria.ordem
            }
            categoria
        }
    }

    override fun removerCategoria(lojaId: Long, id: Long) {
        CategoriaTabela.deleteWhere { (CategoriaTabela.id eq id) and (CategoriaTabela.lojaId eq lojaId) }
    }

    override fun listarProdutos(lojaId: Long, somenteAtivos: Boolean): List<Produto> =
        ProdutoTabela.selectAll().where {
            val base = ProdutoTabela.lojaId eq lojaId
            if (somenteAtivos) base and (ProdutoTabela.ativo eq true) else base
        }.orderBy(ProdutoTabela.nome).map { it.paraProduto() }

    override fun buscarProduto(lojaId: Long, id: Long) =
        ProdutoTabela.selectAll().where { (ProdutoTabela.id eq id) and (ProdutoTabela.lojaId eq lojaId) }
            .singleOrNull()?.paraProduto()

    override fun salvarProduto(produto: Produto): Produto {
        val preencher: ProdutoTabela.(org.jetbrains.exposed.sql.statements.UpdateBuilder<*>) -> Unit = {
            it[categoriaId] = produto.categoriaId
            it[nome] = produto.nome
            it[descricao] = produto.descricao
            it[precoCentavos] = produto.precoCentavos
            it[adicionais] = produto.adicionais
            it[destaque] = produto.destaque
            it[ativo] = produto.ativo
        }
        return if (produto.id == 0L) {
            val id = ProdutoTabela.insertAndGetId {
                it[lojaId] = produto.lojaId
                preencher(it)
            }.value
            buscarProduto(produto.lojaId, id)!!
        } else {
            ProdutoTabela.update({ (ProdutoTabela.id eq produto.id) and (ProdutoTabela.lojaId eq produto.lojaId) }) {
                preencher(it)
            }
            produto
        }
    }

    override fun removerProduto(lojaId: Long, id: Long) {
        ProdutoTabela.deleteWhere { (ProdutoTabela.id eq id) and (ProdutoTabela.lojaId eq lojaId) }
    }

    private fun ResultRow.paraCategoria() = Categoria(
        id = this[CategoriaTabela.id].value,
        lojaId = this[CategoriaTabela.lojaId],
        nome = this[CategoriaTabela.nome],
        ordem = this[CategoriaTabela.ordem],
    )

    private fun ResultRow.paraProduto() = Produto(
        id = this[ProdutoTabela.id].value,
        lojaId = this[ProdutoTabela.lojaId],
        categoriaId = this[ProdutoTabela.categoriaId],
        nome = this[ProdutoTabela.nome],
        descricao = this[ProdutoTabela.descricao],
        precoCentavos = this[ProdutoTabela.precoCentavos],
        adicionais = this[ProdutoTabela.adicionais],
        destaque = this[ProdutoTabela.destaque],
        ativo = this[ProdutoTabela.ativo],
    )
}

class RepositorioPedidosExposed : RepositorioPedidos {
    override fun buscar(lojaId: Long, id: Long) =
        PedidoTabela.selectAll().where { (PedidoTabela.id eq id) and (PedidoTabela.lojaId eq lojaId) }
            .singleOrNull()?.paraPedido()

    override fun buscarPorCodigo(lojaId: Long, codigo: String) =
        PedidoTabela.selectAll().where { (PedidoTabela.codigo eq codigo) and (PedidoTabela.lojaId eq lojaId) }
            .singleOrNull()?.paraPedido()

    override fun existeCodigo(lojaId: Long, codigo: String) =
        PedidoTabela.selectAll().where { (PedidoTabela.codigo eq codigo) and (PedidoTabela.lojaId eq lojaId) }.any()

    override fun listar(lojaId: Long, de: Instant, ate: Instant, limite: Int): List<Pedido> =
        PedidoTabela.selectAll().where {
            (PedidoTabela.lojaId eq lojaId) and
                (PedidoTabela.criadoEm greaterEq de.atOffset(ZoneOffset.UTC)) and
                (PedidoTabela.criadoEm less ate.atOffset(ZoneOffset.UTC))
        }.orderBy(PedidoTabela.criadoEm, SortOrder.DESC).limit(limite).map { it.paraPedido() }

    override fun salvar(pedido: Pedido): Pedido {
        return if (pedido.id == 0L) {
            val id = PedidoTabela.insertAndGetId {
                it[lojaId] = pedido.lojaId
                it[codigo] = pedido.codigo
                it[descricao] = pedido.descricao
                it[totalCentavos] = pedido.totalCentavos
                it[status] = pedido.status.name
                it[criadoEm] = pedido.criadoEm.atOffset(ZoneOffset.UTC)
                it[atualizadoEm] = pedido.atualizadoEm.atOffset(ZoneOffset.UTC)
            }.value
            buscar(pedido.lojaId, id)!!
        } else {
            PedidoTabela.update({ (PedidoTabela.id eq pedido.id) and (PedidoTabela.lojaId eq pedido.lojaId) }) {
                it[status] = pedido.status.name
                it[atualizadoEm] = pedido.atualizadoEm.atOffset(ZoneOffset.UTC)
            }
            pedido
        }
    }

    private fun ResultRow.paraPedido() = Pedido(
        id = this[PedidoTabela.id].value,
        lojaId = this[PedidoTabela.lojaId],
        codigo = this[PedidoTabela.codigo],
        descricao = this[PedidoTabela.descricao],
        totalCentavos = this[PedidoTabela.totalCentavos],
        status = StatusPedido.valueOf(this[PedidoTabela.status]),
        criadoEm = this[PedidoTabela.criadoEm].toInstant(),
        atualizadoEm = this[PedidoTabela.atualizadoEm].toInstant(),
    )
}

private inline fun <reified E : Enum<E>> enumOuPadrao(valor: String, padrao: E): E =
    enumValues<E>().find { it.name == valor } ?: padrao
