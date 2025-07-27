package com.todaystock.api.entity
import jakarta.persistence.*

@Entity
@Table(name = "member")
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
)

enum class Role {
    USER,
    ADMIN,
}
