package startwithco.tosssnapshot.snapshot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import startwithco.tosssnapshot.base.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "TOSS_PAYMENT_DAILY_SNAPSHOT_ENTITY")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class TossPaymentDailySnapshotEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toss_payment_daily_snapshot_seq")
    private Long tossPaymentDailySnapshotSeq;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "payment_key", nullable = false, unique = true)
    private String paymentKey;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "approved_at", nullable = false)
    private String approvedAt;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "fee", nullable = false)
    private Long fee;

    @Column(name = "pay_out_amount", nullable = false)
    private Long payOutAmount;

    @Column(name = "settlement_amount", nullable = false)
    private Long settlementAmount;

    @Column(name = "settlement_at", nullable = true)
    private LocalDateTime settlementAt;

    @Column(name = "is_settled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isSettled;
}
