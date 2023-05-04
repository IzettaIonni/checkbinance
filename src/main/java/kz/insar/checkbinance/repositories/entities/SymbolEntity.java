package kz.insar.checkbinance.repositories.entities;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import kz.insar.checkbinance.client.SymbolStatus;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table(name = "symbols")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@TypeDef(
        name = "symbol_status_type",
        typeClass = PostgreSQLEnumType.class
)
public class SymbolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "symbol_id")
    private Integer symbolId;

    @Column(name = "symbol_name")
    private String symbolName;

    @Column(name = "symbol_status")
    @Enumerated(EnumType.STRING)
    @Type(type = "symbol_status_type")
    private SymbolStatus symbolStatus;

    @Column(name = "base_asset")
    private String baseAsset;

    @Column(name = "base_asset_precision")
    private Integer baseAssetPrecision;

    @Column(name = "quote_asset")
    private String quoteAsset;

    @Column(name = "quote_precision")
    private Integer quotePrecision;

    @Column(name = "quote_asset_precision")
    private Integer quoteAssetPrecision;

}
