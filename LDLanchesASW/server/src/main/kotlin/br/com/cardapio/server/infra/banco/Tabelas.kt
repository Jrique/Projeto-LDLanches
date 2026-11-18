package br.com.cardapio.server.infra.banco

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

// Mapeamento das tabelas criadas pelas migrações em resources/db/migracao.
// O schema é responsabilidade do Flyway; o Exposed só lê e grava.

object LojaTabela : LongIdTable("loja") {
    val slug = varchar("slug", 50)
    val nome = varchar("nome", 120)
    val ativa = bool("ativa")
    val fusoHorario = varchar("fuso_horario", 50)
    val corPrimaria = varchar("cor_primaria", 7)
    val corSecundaria = varchar("cor_secundaria", 7)
    val corFundo = varchar("cor_fundo", 7)
    val logoUrl = varchar("logo_url", 500).nullable()
    val bannerUrl = varchar("banner_url", 500).nullable()
    val modeloLayout = varchar("modelo_layout", 20)
    val mensagemBoasVindas = varchar("mensagem_boas_vindas", 300).nullable()
    val aviso = varchar("aviso", 300).nullable()
    val cssExtra = text("css_extra").nullable()
    val modulos = array<String>("modulos")
    val personalizacaoLiberada = array<String>("personalizacao_liberada")
}

object UsuarioTabela : LongIdTable("usuario") {
    val lojaId = long("loja_id").nullable()
    val nome = varchar("nome", 120)
    val email = varchar("email", 160)
    val senhaHash = varchar("senha_hash", 100)
    val papel = varchar("papel", 20)
    val ativo = bool("ativo")
}

object CategoriaTabela : LongIdTable("categoria") {
    val lojaId = long("loja_id")
    val nome = varchar("nome", 80)
    val ordem = integer("ordem")
}

object ProdutoTabela : LongIdTable("produto") {
    val lojaId = long("loja_id")
    val categoriaId = long("categoria_id").nullable()
    val nome = varchar("nome", 120)
    val descricao = varchar("descricao", 1000)
    val precoCentavos = long("preco_centavos")
    val adicionais = varchar("adicionais", 1000)
    val destaque = bool("destaque")
    val ativo = bool("ativo")
}

object PedidoTabela : LongIdTable("pedido") {
    val lojaId = long("loja_id")
    val codigo = varchar("codigo", 20)
    val descricao = text("descricao")
    val totalCentavos = long("total_centavos")
    val status = varchar("status", 30)
    val criadoEm = timestampWithTimeZone("criado_em")
    val atualizadoEm = timestampWithTimeZone("atualizado_em")
}
