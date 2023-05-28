package kz.insar.checkbinance.repositories.entities;

import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "subscriptions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class SymbolSubscriptionPriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "symbol_subscription_price_id")
    private Integer symbolSubscriptionPriceId;

    @Column(name = "symbol_id")
    private Integer symbol;

    @Column(name = "subscription_status")
    private Boolean subscriptionStatus;
}
