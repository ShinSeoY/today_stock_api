package com.todaystock.api.entity
import jakarta.persistence.*

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email", "provider"]),
    ],
)
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    var email: String,
    var name: String? = null,
    var picture: String? = null,
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: AuthProvider = AuthProvider.GOOGLE,
)

enum class Role {
    USER,
    ADMIN,
}

enum class AuthProvider {
    GOOGLE,
    KAKAO,
}
