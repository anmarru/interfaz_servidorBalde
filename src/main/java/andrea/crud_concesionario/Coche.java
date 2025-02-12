package andrea.crud_concesionario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Coche {
    private Long id;
    private String matricula;
    private String marca;
    private String modelo;
    private Date fechaMatriculacion;

}
