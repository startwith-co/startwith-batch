package startwithco.tosssnapshot.snapshot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import startwithco.tosssnapshot.base.BaseTimeEntity;

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

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "method")
    private String method;

    @Column(name = "approved_at")
    private String approvedAt;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "interest_fee")
    private Long interestFee;

    @Column(name = "fee")
    private Long fee;

    @Column(name = "supply_amount")
    private Long supplyAmount;

    @Column(name = "vat")
    private Long vat;

    @Column(name = "pay_out_amount")
    private Long payOutAmount;
}
