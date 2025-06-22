package pe.edu.upc.finance.amortization.plans.services;

import org.springframework.stereotype.Service;
import pe.edu.upc.finance.amortization.plans.entities.Bono;
import pe.edu.upc.finance.amortization.plans.entities.Pago;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagoService {

//    public List<Pago> generarPagos(Bono bono) {
//        List<Pago> pagos = new ArrayList<>();
//
//        // ✅ Usamos la tasa periódica efectiva previamente calculada en BonoService.convertirTasa
//        // Esta tasa ya considera el tipo de tasa original (TEA, TNA, etc.) y su capitalización
//        double tasaPeriodo = bono.getTasaConvertida();
//
//        // Determinamos la cantidad de pagos por año según la frecuencia
//        int frecuenciaPorAnio = switch (bono.getFrecuenciaPago().toLowerCase()) {
//            case "mensual" -> 12;
//            case "bimestral" -> 6;
//            case "trimestral" -> 4;
//            case "semestral" -> 2;
//            case "anual" -> 1;
//            default -> throw new IllegalArgumentException("Frecuencia de pago no válida: " + bono.getFrecuenciaPago());
//        };
//
//        int totalPagos = bono.getPlazoAnios() * frecuenciaPorAnio;
//
//        // ✅ Formula clasica de cuota fija (metodo francés) con tasa periódica
//        double cuota = bono.getMontoNominal() * (tasaPeriodo / (1 - Math.pow(1 + tasaPeriodo, -totalPagos)));
//        cuota = redondear(cuota);
//
//        double saldo = bono.getMontoNominal();
//        LocalDate fechaInicio = LocalDate.now();
//        int mesesEntrePagos = 12 / frecuenciaPorAnio;
//
//        for (int i = 1; i <= totalPagos; i++) {
//            double interes = redondear(saldo * tasaPeriodo);
//            double amortizacion = redondear(cuota - interes);
//
//            Pago pago = new Pago();
//            pago.setNumeroCuota(i);
//            pago.setFechaPago(fechaInicio.plusMonths((long) mesesEntrePagos * i));
//            pago.setInteres(interes);
//            pago.setAmortizacion(amortizacion);
//            pago.setCuota(cuota);
//
//            saldo = redondear(saldo - amortizacion);
//            pago.setSaldo(Math.max(saldo, 0));
//            pago.setBono(bono);
//
//            pagos.add(pago);
//
//            // 🚨 Cortamos anticipadamente si el saldo es casi cero, por temas de redondeo
//            if (saldo <= 0.01) break;
//        }
//
//        return pagos;
//    }
//
//    private double redondear(double valor) {
//        // ✅ Redondeo a 2 decimales (centavos) típico en aplicaciones financieras
//        return Math.round(valor * 100.0) / 100.0;
//    }

    public List<Pago> generarPagos(Bono bono) {
        List<Pago> pagos = new ArrayList<>();

        double tasaPeriodo = bono.getTasaConvertida(); // ✅ Tasa efectiva del periodo ya convertida

        int frecuenciaPorAnio = switch (bono.getFrecuenciaPago().toLowerCase()) {
            case "mensual" -> 12;
            case "bimestral" -> 6;
            case "trimestral" -> 4;
            case "semestral" -> 2;
            case "anual" -> 1;
            default -> throw new IllegalArgumentException("Frecuencia de pago no válida: " + bono.getFrecuenciaPago());
        };

        int totalPagos = bono.getPlazoAnios() * frecuenciaPorAnio;
        int periodosGracia = bono.getPeriodoGracia() != null ? bono.getPeriodoGracia() : 0;

        // ✅ Cuota fija con metodo frances (solo para pagos regulares después de la gracia total/parcial)
        double cuota = bono.getMontoNominal() * (tasaPeriodo / (1 - Math.pow(1 + tasaPeriodo, -(totalPagos - periodosGracia))));
        cuota = redondear(cuota);

        double saldo = bono.getMontoNominal();
        LocalDate fechaInicio = LocalDate.now();
        int mesesEntrePagos = 12 / frecuenciaPorAnio;

        for (int i = 1; i <= totalPagos; i++) {
            Pago pago = new Pago();
            pago.setNumeroCuota(i);
            pago.setFechaPago(fechaInicio.plusMonths((long) mesesEntrePagos * i));

            if (i <= periodosGracia) {
                // 🟨 Periodo de gracia (total o parcial)
                switch (bono.getTipoGracia().toUpperCase()) {
                    case "TOTAL" -> {
                        // No se paga nada (cuota, interés ni amortización)
                        pago.setCuota(0.0);
                        pago.setInteres(0.0);
                        pago.setAmortizacion(0.0);
                    }
                    case "PARCIAL" -> {
                        // Se paga solo interés, no hay amortización
                        double interes = redondear(saldo * tasaPeriodo);
                        pago.setCuota(interes);
                        pago.setInteres(interes);
                        pago.setAmortizacion(0.0);
                    }
                    default -> throw new IllegalArgumentException("Tipo de gracia no válido: " + bono.getTipoGracia());
                }
                // El saldo permanece igual
                pago.setSaldo(saldo);
            } else {
                // 🟩 Periodo regular
                double interes = redondear(saldo * tasaPeriodo);
                double amortizacion = redondear(cuota - interes);
                pago.setCuota(cuota);
                pago.setInteres(interes);
                pago.setAmortizacion(amortizacion);

                saldo = redondear(saldo - amortizacion);
                pago.setSaldo(Math.max(saldo, 0));
            }

            pago.setBono(bono);
            pagos.add(pago);

            if (saldo <= 0.01) break;
        }

        return pagos;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

}
