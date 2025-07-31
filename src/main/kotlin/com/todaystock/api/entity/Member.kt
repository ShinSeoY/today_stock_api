package com.todaystock.api.entity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
data class MemberId(
    val email: String,
    @Enumerated(EnumType.STRING)
    var provider: AuthProvider = AuthProvider.GOOGLE,
) : Serializable

@Entity
@Table(
    name = "member",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email", "provider"]),
    ],
)
class Member(
    @EmbeddedId
    val memberId: MemberId,
    var name: String? = null,
    var picture: String? = null,
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL])
    var alarms: MutableSet<Alarm> = mutableSetOf(),
)

enum class Role {
    USER,
    ADMIN,
}

enum class AuthProvider {
    GOOGLE,
    KAKAO,
}
