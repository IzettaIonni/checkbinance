package kz.insar.checkbinance.domain;

import lombok.Getter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class SubscribeValidation {

    @Size(min = 1, max = 8)
    private Integer id;

    @Pattern(regexp = "^[A-Za-z]{2,32}$")
    private String name;

}
