package com.todaystock.api.entity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "alarm")
class Alarm(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    var email: String,
    @Column(nullable = false)
    var code: String,
    var price: Double,
    var condition: Condition = Condition.LTE,
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

enum class Condition {
    LTE,
    GTE,
}
