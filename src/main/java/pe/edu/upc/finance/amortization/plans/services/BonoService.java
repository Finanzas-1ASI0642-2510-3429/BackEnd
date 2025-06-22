package pe.edu.upc.finance.amortization.plans.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.finance.amortization.plans.dto.BonoRequestDTO;
import pe.edu.upc.finance.amortization.plans.entities.Bono;
import pe.edu.upc.finance.amortization.plans.entities.Pago;
import pe.edu.upc.finance.amortization.plans.repository.BonoRepository;

import java.util.List;

@Service
public class BonoService {
    @Autowired
    private BonoRepository bonoRepository;

    @Autowired
    private PagoService pagoService;

    public Bono guardarBono(BonoRequestDTO dto) {
        Bono bono = new Bono();

        bono.setNombre(dto.getNombre());
        bono.setMontoNominal(dto.getMontoNominal());
        bono.setPlazoAnios(dto.getPlazoAnios());
        bono.setFrecuenciaPago(dto.getFrecuenciaPago());
        bono.setTipoTasa(dto.getTipoTasa());
        bono.setCapitalizacion(dto.getCapitalizacion());
        bono.setTipoMoneda(dto.getTipoMoneda());
        bono.setPeriodoGracia(dto.getPeriodoGracia());
        bono.setTipoGracia(dto.getTipoGracia());

        // Nuevos campos
        bono.setTipoTasaBase(dto.getTipoTasaBase());
        bono.setTasaBase(dto.getTasaBase());

        // Convertir la tasa base a una tasa efectiva periódica (tasaConvertida)
        Double tasaConvertida = convertirTasa(dto.getTipoTasaBase(), dto.getTasaBase(), dto.getFrecuenciaPago(), dto.getCapitalizacion());
        bono.setTasaConvertida(tasaConvertida);

        // Generar pagos a partir del bono completo
        List<Pago> pagos = pagoService.generarPagos(bono);
        bono.setPagos(pagos);

        return bonoRepository.save(bono);
    }


    private Double convertirTasa(String tipoTasaBase, Double tasaBase, String frecuenciaPago, String capitalizacion) {
        // Determinar la cantidad de pagos al año según la frecuencia
        int nPagosAnuales = switch (frecuenciaPago.toLowerCase()) {
            case "mensual" -> 12;
            case "bimestral" -> 6;
            case "trimestral" -> 4;
            case "semestral" -> 2;
            case "anual" -> 1;
            default -> throw new IllegalArgumentException("Frecuencia de pago no válida: " + frecuenciaPago);
        };

        double tasaDecimal = tasaBase / 100.0;

        return switch (tipoTasaBase.toUpperCase()) {

            case "TEA" -> {
                // ✅ TEA: Tasa efectiva anual ya incluye capitalización anual
                // Por eso se transforma directamente a la tasa del periodo
                yield Math.pow(1 + tasaDecimal, 1.0 / nPagosAnuales) - 1;
            }

            case "TES" -> {
                // ✅ TES: Tasa efectiva semestral, ya incluye capitalización semestral
                // Por eso se transforma directamente sin aplicar capitalización adicional
                yield Math.pow(1 + tasaDecimal, 2.0 / nPagosAnuales) - 1;
            }

            case "TEM" -> {
                // ✅ TEM: Tasa efectiva mensual, ya incluye capitalización mensual
                // Se adapta al número de pagos anuales sin recalcular capitalización
                yield Math.pow(1 + tasaDecimal, 12.0 / nPagosAnuales) - 1;
            }

            case "TNA", "TNS", "TNM" -> {
                // ✅ TNA (anual), TNS (semestral), TNM (mensual) son tasas nominales
                // ❗ Estas tasas no incluyen capitalización por sí solas, así que debemos aplicarla
                int cap = switch (capitalizacion.toLowerCase()) {
                    case "mensual" -> 12;
                    case "bimestral" -> 6;
                    case "trimestral" -> 4;
                    case "semestral" -> 2;
                    case "anual" -> 1;
                    default -> throw new IllegalArgumentException("Capitalización no válida: " + capitalizacion);
                };

                // Conversión de tasa nominal con capitalización al número de pagos anuales
                yield Math.pow(1 + tasaDecimal / cap, (double) cap / nPagosAnuales) - 1;
            }

            default -> throw new IllegalArgumentException("Tipo de tasa base no reconocido: " + tipoTasaBase);
        };
    }

    public List<Bono> listarBonos() {
        return bonoRepository.findAll();
    }

    public Bono actualizarBono(Long bonoId, BonoRequestDTO dto) {
        Bono bonoExistente = bonoRepository.findById(bonoId)
                .orElseThrow(() -> new RuntimeException("Bono no encontrado"));

        // Actualizar campos
        bonoExistente.setNombre(dto.getNombre());
        bonoExistente.setMontoNominal(dto.getMontoNominal());
        bonoExistente.setPlazoAnios(dto.getPlazoAnios());
        bonoExistente.setFrecuenciaPago(dto.getFrecuenciaPago());
        bonoExistente.setTipoTasa(dto.getTipoTasa());
        bonoExistente.setCapitalizacion(dto.getCapitalizacion());
        bonoExistente.setTipoMoneda(dto.getTipoMoneda());
        bonoExistente.setPeriodoGracia(dto.getPeriodoGracia());
        bonoExistente.setTipoGracia(dto.getTipoGracia());
        bonoExistente.setTipoTasaBase(dto.getTipoTasaBase());
        bonoExistente.setTasaBase(dto.getTasaBase());

        // Recalcular tasaConvertida
        double tasaConvertida = convertirTasa(dto.getTipoTasaBase(), dto.getTasaBase(), dto.getFrecuenciaPago(), dto.getCapitalizacion());
        bonoExistente.setTasaConvertida(tasaConvertida);

        // Eliminar pagos anteriores y generar nuevos
        bonoExistente.getPagos().clear();
        List<Pago> nuevosPagos = pagoService.generarPagos(bonoExistente);
        nuevosPagos.forEach(p -> p.setBono(bonoExistente));
        bonoExistente.setPagos(nuevosPagos);

        return bonoRepository.save(bonoExistente);
    }


}
