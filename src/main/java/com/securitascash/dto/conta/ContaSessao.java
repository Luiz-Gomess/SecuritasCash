package com.securitascash.dto.conta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaSessao {
    private Long id;
    private String numero;
    private String tipo;
}
