package com.fastcampus.springbatch.dto

class ApiOrder(
    var id: String = "",
    var customerId: Long? = null,
    var url: String = "",
    var status: Status = Status.SUCCESS,
    var createdAt: String = "",
)

enum class Status() {
    SUCCESS,
    FAIL,
}

enum class ServicePolicy(
    val id: Long,
    val url: String,
    val fee: Int,
) {
    A(1L, "/service/a", 100),
    B(2L, "/service/b", 200),
    C(3L, "/service/c", 300),
    D(4L, "/service/d", 400),
    E(5L, "/service/e", 500),
    F(6L, "/service/f", 600),
    G(7L, "/service/g", 700),
    H(8L, "/service/h", 800),
    I(9L, "/service/i", 900),
    J(10L, "/service/j", 1000),
    K(11L, "/service/k", 1100),
    L(12L, "/service/l", 1200),
    M(13L, "/service/m", 1300),
    N(14L, "/service/n", 1400),
    O(15L, "/service/o", 1500),
    P(16L, "/service/p", 1600),
    Q(17L, "/service/q", 1700),
    R(18L, "/service/r", 1800),
    S(19L, "/service/s", 1900),
    T(20L, "/service/t", 2000),
    U(21L, "/service/u", 2100),
    V(22L, "/service/v", 2200),
    W(23L, "/service/w", 2300),
    X(24L, "/service/x", 2400),
    Y(25L, "/service/y", 2500),
    Z(26L, "/service/z", 2600),
    ;

    companion object {
        fun findByUrl(url: String): ServicePolicy {
            return entries.firstOrNull { it.url == url }
                ?: throw IllegalArgumentException()
        }

        fun findById(id: Long): ServicePolicy {
            return entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException()
        }
    }
}