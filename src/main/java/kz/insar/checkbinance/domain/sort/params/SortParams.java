package kz.insar.checkbinance.domain.sort.params;

import lombok.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class SortParams<T> {

    @NonNull
    private final T column;

    @NonNull
    private final SortDirection dir;

}
