package br.com.cardapio.app.api

import br.com.cardapio.shared.AlterarStatusDto
import br.com.cardapio.shared.AparenciaDto
import br.com.cardapio.shared.AtualizarLojaDto
import br.com.cardapio.shared.AtualizarUsuarioDto
import br.com.cardapio.shared.CategoriaDto
import br.com.cardapio.shared.LoginRequisicao
import br.com.cardapio.shared.LojaDto
import br.com.cardapio.shared.NovaLojaDto
import br.com.cardapio.shared.NovoUsuarioDto
import br.com.cardapio.shared.PedidoDto
import br.com.cardapio.shared.ProdutoDto
import br.com.cardapio.shared.ResumoDashboardDto
import br.com.cardapio.shared.SalvarCategoriaDto
import br.com.cardapio.shared.SalvarProdutoDto
import br.com.cardapio.shared.SessaoDto
import br.com.cardapio.shared.StatusPedido
import br.com.cardapio.shared.TrocarSenhaRequisicao
import br.com.cardapio.shared.UsuarioDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/** Cliente da API do servidor. Erros HTTP viram [ErroApi] (ver HttpClientManager). */
class PainelApi(private val http: HttpClient) {
    private val base = apiBaseUrl()

    // ---------- Autenticação ----------

    suspend fun login(email: String, senha: String): SessaoDto =
        http.post("$base/api/auth/login") { setBody(LoginRequisicao(email.trim(), senha)) }.body()

    suspend fun trocarSenha(senhaAtual: String, novaSenha: String) {
        http.put("$base/api/auth/eu/senha") { setBody(TrocarSenhaRequisicao(senhaAtual, novaSenha)) }
    }

    // ---------- Super admin: lojas ----------

    suspend fun listarLojas(): List<LojaDto> = http.get("$base/api/admin/lojas").body()

    suspend fun criarLoja(dto: NovaLojaDto): LojaDto = http.post("$base/api/admin/lojas") { setBody(dto) }.body()

    suspend fun atualizarLoja(id: Long, dto: AtualizarLojaDto): LojaDto =
        http.put("$base/api/admin/lojas/$id") { setBody(dto) }.body()

    // ---------- Painel: loja ----------

    suspend fun minhaLoja(): LojaDto = http.get("$base/api/painel/loja").body()

    suspend fun personalizarLoja(aparencia: AparenciaDto): LojaDto =
        http.put("$base/api/painel/loja/aparencia") { setBody(aparencia) }.body()

    // ---------- Painel: equipe ----------

    suspend fun listarUsuarios(): List<UsuarioDto> = http.get("$base/api/painel/usuarios").body()

    suspend fun criarUsuario(dto: NovoUsuarioDto): UsuarioDto =
        http.post("$base/api/painel/usuarios") { setBody(dto) }.body()

    suspend fun atualizarUsuario(id: Long, dto: AtualizarUsuarioDto): UsuarioDto =
        http.put("$base/api/painel/usuarios/$id") { setBody(dto) }.body()

    suspend fun removerUsuario(id: Long) {
        http.delete("$base/api/painel/usuarios/$id")
    }

    // ---------- Painel: categorias ----------

    suspend fun listarCategorias(): List<CategoriaDto> = http.get("$base/api/painel/categorias").body()

    suspend fun criarCategoria(nome: String): CategoriaDto =
        http.post("$base/api/painel/categorias") { setBody(SalvarCategoriaDto(nome)) }.body()

    suspend fun atualizarCategoria(id: Long, nome: String): CategoriaDto =
        http.put("$base/api/painel/categorias/$id") { setBody(SalvarCategoriaDto(nome)) }.body()

    suspend fun removerCategoria(id: Long) {
        http.delete("$base/api/painel/categorias/$id")
    }

    // ---------- Painel: produtos ----------

    suspend fun listarProdutos(): List<ProdutoDto> = http.get("$base/api/painel/produtos").body()

    suspend fun criarProduto(dto: SalvarProdutoDto): ProdutoDto =
        http.post("$base/api/painel/produtos") { setBody(dto) }.body()

    suspend fun atualizarProduto(id: Long, dto: SalvarProdutoDto): ProdutoDto =
        http.put("$base/api/painel/produtos/$id") { setBody(dto) }.body()

    suspend fun marcarDestaque(id: Long, destaque: Boolean): ProdutoDto =
        http.patch("$base/api/painel/produtos/$id/destaque") { parameter("valor", destaque) }.body()

    suspend fun removerProduto(id: Long) {
        http.delete("$base/api/painel/produtos/$id")
    }

    // ---------- Painel: pedidos ----------

    /** [inicio] e [fim] no formato AAAA-MM-DD. Sem datas: últimos 30 dias. */
    suspend fun listarPedidos(inicio: String? = null, fim: String? = null): List<PedidoDto> =
        http.get("$base/api/painel/pedidos") {
            if (inicio != null) parameter("inicio", inicio)
            if (fim != null) parameter("fim", fim)
        }.body()

    suspend fun alterarStatusPedido(id: Long, status: StatusPedido): PedidoDto =
        http.patch("$base/api/painel/pedidos/$id/status") { setBody(AlterarStatusDto(status)) }.body()

    // ---------- Painel: dashboard ----------

    suspend fun resumoDashboard(dias: Int? = 7, inicio: String? = null, fim: String? = null): ResumoDashboardDto =
        http.get("$base/api/painel/dashboard/resumo") {
            if (dias != null) parameter("dias", dias)
            if (inicio != null) parameter("inicio", inicio)
            if (fim != null) parameter("fim", fim)
        }.body()
}
