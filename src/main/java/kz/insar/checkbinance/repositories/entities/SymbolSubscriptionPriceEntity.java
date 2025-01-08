package kz.insar.checkbinance.repositories.entities;

import kz.insar.checkbinance.domain.Symbol;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringExclude;

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

    @JoinColumn(name = "symbol_id")
    @OneToOne
    private SymbolEntity symbol;

    @Column(name = "subscription_status")
    private Boolean subscriptionStatus;
}
