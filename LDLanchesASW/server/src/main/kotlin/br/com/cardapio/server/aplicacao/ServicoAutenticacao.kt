package br.com.cardapio.server.aplicacao

import br.com.cardapio.server.dominio.Ator
import br.com.cardapio.server.dominio.CredenciaisInvalidas
import br.com.cardapio.server.dominio.NaoEncontrado
import br.com.cardapio.server.dominio.RepositorioLojas
import br.com.cardapio.server.dominio.RepositorioUsuarios
import br.com.cardapio.server.dominio.Transacoes
import br.com.cardapio.server.dominio.Usuario
import br.com.cardapio.server.infra.seguranca.Senhas
import br.com.cardapio.server.infra.seguranca.Tokens
import br.com.cardapio.shared.LoginRequisicao
import br.com.cardapio.shared.Papel
import br.com.cardapio.shared.SessaoDto
import br.com.cardapio.shared.TrocarSenhaRequisicao
import br.com.cardapio.shared.UsuarioDto
import org.slf4j.LoggerFactory

class ServicoAutenticacao(
    private val tx: Transacoes,
    private val usuarios: RepositorioUsuarios,
    private val lojas: RepositorioLojas,
    private val senhas: Senhas,
    private val tokens: Tokens,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun entrar(req: LoginRequisicao): SessaoDto {
        // Buscar
        val (usuario, loja) = tx.executar {
            val u = usuarios.buscarPorEmail(req.email)
            u to u?.lojaId?.let { lojas.buscar(it) }
        }
        // Trabalhar: confere a senha mesmo quando o usuário não existe (tempo constante)
        val senhaConfere = senhas.confere(req.senha, usuario?.senhaHash ?: senhas.hashFalso)
        if (usuario == null || !senhaConfere || !usuario.podeEntrar()) throw CredenciaisInvalidas()
        if (loja != null && !loja.ativa) throw CredenciaisInvalidas()

        return SessaoDto(tokens.gerar(usuario), usuario.paraDto(loja?.slug))
    }

    suspend fun eu(ator: Ator): UsuarioDto = tx.executar {
        val usuario = usuarios.buscar(ator.usuarioId) ?: throw NaoEncontrado("Usuário não encontrado.")
        usuario.paraDto(usuario.lojaId?.let { lojas.buscar(it)?.slug })
    }

    suspend fun trocarSenha(ator: Ator, req: TrocarSenhaRequisicao) {
        val usuario = tx.executar { usuarios.buscar(ator.usuarioId) } ?: throw NaoEncontrado("Usuário não encontrado.")
        if (!senhas.confere(req.senhaAtual, usuario.senhaHash)) throw CredenciaisInvalidas()
        Usuario.validarSenhaNova(req.novaSenha)
        usuario.trocarSenha(senhas.gerarHash(req.novaSenha))
        tx.executar { usuarios.salvar(usuario) }
    }

    /** Cria o super admin na primeira subida, a partir das variáveis de ambiente. Não faz nada se já existir. */
    suspend fun garantirSuperAdmin(email: String?, senha: String?) {
        if (tx.executar { usuarios.existeSuperAdmin() }) return
        if (email.isNullOrBlank() || senha.isNullOrBlank()) {
            log.warn("Nenhum super admin cadastrado. Defina SUPERADMIN_EMAIL e SUPERADMIN_SENHA para criar o primeiro.")
            return
        }
        Usuario.validarSenhaNova(senha)
        val novo = Usuario.novo(null, "Super admin", email, senhas.gerarHash(senha), Papel.SUPER_ADMIN)
        tx.executar { usuarios.salvar(novo) }
        log.info("Super admin criado: {}", novo.email)
    }
}
