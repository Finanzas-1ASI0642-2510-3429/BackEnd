package pe.edu.upc.finance.amortization.plans.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.finance.amortization.plans.dto.BonoRequestDTO;
import pe.edu.upc.finance.amortization.plans.dto.BonoResumenDTO;
import pe.edu.upc.finance.amortization.plans.dto.ResultadosFinancierosDTO;
import pe.edu.upc.finance.amortization.plans.entities.Bono;
import pe.edu.upc.finance.amortization.plans.repository.BonoRepository;
import pe.edu.upc.finance.amortization.plans.services.BonoService;
import pe.edu.upc.finance.amortization.plans.services.IndicadoresFinancierosService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bonos")
@CrossOrigin
public class BonoController {

    @Autowired
    private BonoService bonoService;
    @Autowired
    private IndicadoresFinancierosService indicadoresService;
    @Autowired
    private BonoRepository bonoRepository;


    // POST /api/bonos/registrar
// 📥 Registra un nuevo bono con los datos proporcionados.
// 🔄 También genera automáticamente su tabla de pagos según el método francés.
// 📤 Devuelve el bono creado (con ID asignado).

    @PostMapping("/registrar")
    public Bono registrarBono(@RequestBody BonoRequestDTO bonoRequestDTO) {
        return bonoService.guardarBono(bonoRequestDTO);
    }
    // Trea el Bono solicitado x ID con sus pagos
    @GetMapping("/{bonoId}")
    public Bono obtenerBonoPorId(@PathVariable Long bonoId) {
        return bonoRepository.findById(bonoId)
                .orElseThrow(() -> new RuntimeException("Bono no encontrado"));
    }
    //trae los indicadores del Bono solicitado x ID del bono ingresado
    @GetMapping("/{bonoId}/indicadores")
    public ResultadosFinancierosDTO obtenerIndicadores(@PathVariable Long bonoId) {
        Bono bono = bonoRepository.findById(bonoId)
                .orElseThrow(() -> new RuntimeException("Bono no encontrado"));


        double valorRecibido = bono.getMontoNominal(); // Puedes ajustar si hay descuento
        double precioCompra = bono.getMontoNominal() * 0.95; // Este es el Valor de Precio Compra que asimilamos que lo compran ocon 5% de descuento

        var pagos = bono.getPagos();
        double tasaPeriodo = bono.getTasaConvertida();

        double tcea = indicadoresService.calcularTCEA(pagos, valorRecibido);
        double trea = indicadoresService.calcularTREA(pagos, precioCompra);
        double duracion = indicadoresService.calcularDuracion(pagos, tasaPeriodo);
        double durMod = indicadoresService.calcularDuracionModificada(duracion, tasaPeriodo);
        double convexidad = indicadoresService.calcularConvexidad(pagos, tasaPeriodo);

        ResultadosFinancierosDTO dto = new ResultadosFinancierosDTO();
        dto.setTcea(tcea);
        dto.setTrea(trea);
        dto.setDuracion(duracion);
        dto.setDuracionModificada(durMod);
        dto.setConvexidad(convexidad);

        return dto;
    }

    // GET /api/bonos – Lista todos los bonos
    @GetMapping
    public List<Bono> listarTodosLosBonos() {
        return bonoService.listarBonos();
    }

    @GetMapping("/resumen")
    public List<BonoResumenDTO> listarBonosResumen() {
        return bonoService.listarBonos().stream().map(bono -> {
            BonoResumenDTO dto = new BonoResumenDTO();
            dto.setId(bono.getId());
            dto.setNombre(bono.getNombre());
            dto.setMontoNominal(bono.getMontoNominal());
            dto.setPlazoAnios(bono.getPlazoAnios());
            dto.setTipoMoneda(bono.getTipoMoneda());
            return dto;
        }).collect(Collectors.toList());
    }

    @PutMapping("/{bonoId}")
    public Bono actualizarBono(@PathVariable Long bonoId, @RequestBody BonoRequestDTO bonoRequestDTO) {
        return bonoService.actualizarBono(bonoId, bonoRequestDTO);
    }

}
