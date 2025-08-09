package com.todaystock.api.entity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
data class AlarmId(
    val memberEmail: String,
    @Enumerated(EnumType.STRING)
    var memberProvider: AuthProvider,
    val code: String,
) : Serializable

@Entity
@Table(name = "alarm")
class Alarm(
    @EmbeddedId
    val alarmId: AlarmId,
    val name: String,
    val currencyCode: String,
    @Column(nullable = false)
    var email: String,
    var price: Double,
    var conditionType: ConditionType = ConditionType.LTE,
    var url: String,
    var enable: Boolean = false,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @ManyToOne
    @JoinColumns(
        JoinColumn(name = "memberEmail", referencedColumnName = "email", insertable = false, updatable = false),
        JoinColumn(name = "memberProvider", referencedColumnName = "provider", insertable = false, updatable = false),
    )
    val member: Member,
)

enum class ConditionType {
    LTE,
    GTE,
}
