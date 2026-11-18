package br.com.cardapio.server.dominio

class Categoria(
    val id: Long,
    val lojaId: Long,
    nome: String,
    ordem: Int,
) {
    var nome: String = nome; private set
    var ordem: Int = ordem; private set

    init {
        validarNome(nome)
    }

    fun renomear(nome: String, ordem: Int) {
        validarNome(nome)
        this.nome = nome.trim()
        this.ordem = ordem
    }

    companion object {
        fun nova(lojaId: Long, nome: String, ordem: Int) = Categoria(0, lojaId, nome.trim(), ordem)

        private fun validarNome(nome: String) = exigir(nome.isNotBlank() && nome.length <= 80) {
            "O nome da categoria é obrigatório e pode ter no máximo 80 caracteres."
        }
    }
}

class Produto(
    val id: Long,
    val lojaId: Long,
    categoriaId: Long?,
    nome: String,
    descricao: String,
    precoCentavos: Long,
    adicionais: String,
    destaque: Boolean,
    ativo: Boolean,
) {
    var categoriaId: Long? = categoriaId; private set
    var nome: String = nome; private set
    var descricao: String = descricao; private set
    var precoCentavos: Long = precoCentavos; private set
    var adicionais: String = adicionais; private set
    var destaque: Boolean = destaque; private set
    var ativo: Boolean = ativo; private set

    init {
        validar(nome, descricao, precoCentavos, adicionais)
    }

    fun atualizar(
        categoriaId: Long?,
        nome: String,
        descricao: String,
        precoCentavos: Long,
        adicionais: String,
        destaque: Boolean,
        ativo: Boolean,
    ) {
        validar(nome, descricao, precoCentavos, adicionais)
        this.categoriaId = categoriaId
        this.nome = nome.trim()
        this.descricao = descricao.trim()
        this.precoCentavos = precoCentavos
        this.adicionais = adicionais.trim()
        this.destaque = destaque
        this.ativo = ativo
    }

    fun marcarDestaque(destaque: Boolean) {
        this.destaque = destaque
    }

    companion object {
        fun novo(
            lojaId: Long,
            categoriaId: Long?,
            nome: String,
            descricao: String,
            precoCentavos: Long,
            adicionais: String,
            destaque: Boolean,
            ativo: Boolean,
        ) = Produto(0, lojaId, categoriaId, nome.trim(), descricao.trim(), precoCentavos, adicionais.trim(), destaque, ativo)

        private fun validar(nome: String, descricao: String, precoCentavos: Long, adicionais: String) {
            exigir(nome.isNotBlank() && nome.length <= 120) { "O nome do produto é obrigatório e pode ter no máximo 120 caracteres." }
            exigir(descricao.length <= 1000) { "A descrição pode ter no máximo 1000 caracteres." }
            exigir(precoCentavos > 0) { "O preço do produto deve ser maior que zero." }
            exigir(adicionais.length <= 1000) { "Os adicionais podem ter no máximo 1000 caracteres." }
        }
    }
}
