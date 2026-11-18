package br.com.cardapio.server.api

import br.com.cardapio.server.aplicacao.ServicoLojas
import br.com.cardapio.shared.Formatos
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.io.File

/**
 * Serve o cardápio web de cada loja por caminho: /ld-lanches/ → CardapioWeb/index.html.
 * O JavaScript descobre o slug pelo primeiro trecho da URL e chama /api/public/lojas/{slug}.
 */
fun Route.rotasCardapioWeb(diretorio: File, lojas: ServicoLojas) {
    val raiz = diretorio.canonicalFile

    get("/") { call.respondText("Plataforma de cardápio online. Acesse /{endereco-da-loja}/") }

    get("/{slug}") {
        val slug = call.parameters["slug"].orEmpty()
        if (!Formatos.slugValido(slug)) return@get call.respondText("Página não encontrada.", status = HttpStatusCode.NotFound)
        call.respondRedirect("/$slug/")
    }

    get("/{slug}/{caminho...}") {
        val slug = call.parameters["slug"].orEmpty()
        if (!Formatos.slugValido(slug)) return@get call.respondText("Página não encontrada.", status = HttpStatusCode.NotFound)

        val relativo = call.parameters.getAll("caminho").orEmpty().joinToString("/").ifEmpty { "index.html" }
        val arquivo = File(raiz, relativo).canonicalFile

        // Impede sair da pasta do cardápio (ex.: /loja/../../etc/passwd)
        if (!arquivo.path.startsWith(raiz.path + File.separator) || !arquivo.isFile) {
            return@get call.respondText("Página não encontrada.", status = HttpStatusCode.NotFound)
        }
        // Páginas só são servidas para lojas que existem e estão ativas
        if (arquivo.extension == "html") lojas.lojaPublica(slug)

        call.respondFile(arquivo)
    }
}
