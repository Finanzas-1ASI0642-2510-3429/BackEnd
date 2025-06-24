package pe.edu.upc.finance.amortization.plans.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Bono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer montoNominal;
    private Integer plazoAnios;
    private String frecuenciaPago;
    private String tipoTasa;
    private String capitalizacion;
    private String tipoMoneda;
    private Integer periodoGracia;
    private String tipoGracia;

    // Nuevos campos
    private String tipoTasaBase;   // Ej: TEA, TNA, TES
    private Double tasaBase;       // Ej: 9.0
    private Double tasaConvertida; // Ej: 0.04403

    // 🔹 NUEVO CAMPO
    private String nombreCliente;

    @OneToMany(mappedBy = "bono", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pago> pagos = new ArrayList<>();

    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getMontoNominal() {
        return montoNominal;
    }

    public Integer getPlazoAnios() {
        return plazoAnios;
    }

    public String getFrecuenciaPago() {
        return frecuenciaPago;
    }

    public String getTipoTasa() {
        return tipoTasa;
    }

    public String getCapitalizacion() {
        return capitalizacion;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public Integer getPeriodoGracia() {
        return periodoGracia;
    }

    public String getTipoGracia() {
        return tipoGracia;
    }

    public String getTipoTasaBase() {
        return tipoTasaBase;
    }

    public Double getTasaBase() {
        return tasaBase;
    }

    public Double getTasaConvertida() {
        return tasaConvertida;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMontoNominal(Integer montoNominal) {
        this.montoNominal = montoNominal;
    }

    public void setPlazoAnios(Integer plazoAnios) {
        this.plazoAnios = plazoAnios;
    }

    public void setFrecuenciaPago(String frecuenciaPago) {
        this.frecuenciaPago = frecuenciaPago;
    }

    public void setTipoTasa(String tipoTasa) {
        this.tipoTasa = tipoTasa;
    }

    public void setCapitalizacion(String capitalizacion) {
        this.capitalizacion = capitalizacion;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public void setPeriodoGracia(Integer periodoGracia) {
        this.periodoGracia = periodoGracia;
    }

    public void setTipoGracia(String tipoGracia) {
        this.tipoGracia = tipoGracia;
    }

    public void setTipoTasaBase(String tipoTasaBase) {
        this.tipoTasaBase = tipoTasaBase;
    }

    public void setTasaBase(Double tasaBase) {
        this.tasaBase = tasaBase;
    }

    public void setTasaConvertida(Double tasaConvertida) {
        this.tasaConvertida = tasaConvertida;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}