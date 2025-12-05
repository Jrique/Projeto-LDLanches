package org.example.project.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.example.project.data.Categoria
import io.ktor.client.statement.bodyAsText

// DTOs auxiliares
@Serializable
data class ProductDTO(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: Double,
    val extra: String = "",
    val type: String = "",
    val highlighted: Boolean = false
)

@Serializable
data class OrderDTO(
    val id: Long? = null,
    val code: String,
    val total: Double,
    val description: String = "",
    val date: String,
    val status: String
)

@Serializable
data class OrderStatusUpdateDTOFront(val status: String)

@Serializable
data class CategoryDTO(val id: Long, val categoryName: String)

@Serializable
data class CategoryCreateDTO(val categoryName: String)

fun CategoryDTO.toDomain() = Categoria(id = id, nome = categoryName)
fun Categoria.toCreateDto() = CategoryCreateDTO(categoryName = nome)


@Serializable
data class AuthDTO(
    val username: String,
    val password: String,
    val name: String? = null
)

@Serializable
data class LoginResponse(
    val token: String,
    val username: String,
    val name: String,
    val id: Long
)

@Serializable
data class UserResponse(val id: Long, val name: String, val email: String)

@Serializable
data class SensorDTO(
    val temp: Double,
    val time: String
)

@Serializable
data class DashboardDTO(
    val faturamentoDiario: List<Double>,
    val vendasDiarias: List<Int>,
    val diasDaSemana: List<String>,
    val totalFaturamento: Double,
    val totalVendas: Int
)



class LanchesApi(private val http: HttpClient) {
    private val base = apiBaseUrl()

    // PRODUTOS
    suspend fun setHighlight(id: Long, highlighted: Boolean): ProductDTO =
        http.patch("$base/products/$id/highlight") { parameter("highlighted", highlighted) }.body()

    suspend fun listarProdutos(): List<ProductDTO> =
        http.get("$base/products").body()

    suspend fun criarProduto(dto: ProductDTO): ProductDTO =
        http.post("$base/products") { setBody(dto) }.body()

    suspend fun atualizarProduto(id: Long, dto: ProductDTO): ProductDTO =
        http.put("$base/products/$id") { setBody(dto) }.body()

    suspend fun deletarProduto(id: Long) {
        http.delete("$base/products/$id")
    }

    // PEDIDOS
    suspend fun listarPedidos(inicio: String? = null, fim: String? = null): List<OrderDTO> =
        http.get("$base/order") {
            if (inicio != null) parameter("inicio", inicio)
            if (fim != null) parameter("fim", fim)
        }.body()

    suspend fun criarPedido(dto: OrderDTO): OrderDTO =
        http.post("$base/order") { setBody(dto) }.body()

    suspend fun atualizarPedido(id: Long, dto: OrderDTO): OrderDTO =
        http.put("$base/order/$id") { setBody(dto) }.body()

    suspend fun deletarPedido(id: Long) {
        http.delete("$base/order/$id")
    }

    suspend fun atualizarStatusPedido(id: Long, statusCode: String): OrderDTO =
        http.request("$base/order/$id/status") {
            method = HttpMethod.Patch
            setBody(OrderStatusUpdateDTOFront(statusCode))
        }.body()

    // CATEGORIAS
    suspend fun listarCategorias(): List<CategoryDTO> = http.get("$base/category").body()

    suspend fun criarCategoria(nome: String): CategoryDTO =
        http.post("$base/category") { setBody(CategoryCreateDTO(categoryName = nome)) }.body()

    suspend fun atualizarCategoria(id: Long, nome: String): CategoryDTO =
        http.put("$base/category/$id") { setBody(CategoryCreateDTO(categoryName = nome)) }.body()

    suspend fun deletarCategoria(id: Long) {
        http.delete("$base/category/$id")
    }

    // AUTENTICAÇÃO

    suspend fun login(dto: AuthDTO): LoginResponse {
        val response = http.post("$base/user/signin") {
            setBody(dto)
        }
        if (response.status != HttpStatusCode.OK) {
            val msg = response.bodyAsText()
            throw Exception("Erro API (${response.status.value}): $msg")
        }
        return response.body()
    }

    suspend fun cadastrar(dto: AuthDTO): LoginResponse {
        return http.post("$base/user/signup") {
            setBody(dto)
        }.body()
    }

    suspend fun atualizarUsuario(id: Long, dto: AuthDTO) {
        http.put("$base/user/$id") { setBody(dto) }
    }

    suspend fun listarUsuarios(): List<UserResponse> = http.get("$base/user").body()

    suspend fun deletarUsuario(id: Long) {
        http.delete("$base/user/$id")
    }

    // Sensor
    suspend fun getTemperaturaFreezer(): SensorDTO = http.get("$base/sensors/freezer").body()

    //Grafico
    suspend fun getDashboardResumo(
        dias: Int = 7,
        inicio: String? = null,
        fim: String? = null
    ): DashboardDTO =
        http.get("$base/dashboard/resumo") {
            parameter("dias", dias)
            if (inicio != null) parameter("inicio", inicio)
            if (fim != null) parameter("fim", fim)
        }.body()
}