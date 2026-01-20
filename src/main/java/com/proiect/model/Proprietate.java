/** Entitatea JPA care stochează detaliile tehnice ale proprietății.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */


package com.proiect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proprietate")
public class Proprietate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proprietate") // <--- ESTE OBLIGATORIU SA AI ASTA
    private Long id;

    // --- LIMITELE CERUTE DE TINE ---

    @NotNull(message = "Suprafața este obligatorie")
    @Min(value = 1, message = "Minim 1 mp")
    @Max(value = 100000, message = "Maxim 100.000 mp")
    private Double suprafata;

    @NotNull(message = "Numărul de camere este obligatoriu")
    @Min(value = 1, message = "Minim o cameră")
    @Max(value = 100, message = "Maxim 100 camere")
    @Column(name = "nr_camere") // E bine sa fii explicit si aici daca in DB e cu underscore
    private Integer nrCamere;

    @NotNull(message = "Anul construcției este obligatoriu")
    @Min(value = 1800, message = "Anul trebuie să fie după 1800")
    @Max(value = 2030, message = "Anul nu poate depăși 2030")
    @Column(name = "an_construire")
    private Integer anConstruire;

    @Column(name = "tip_proprietate")
    private String tipProprietate;

    // Relatie ManyToMany cu Facilitati
    @ManyToMany
    @JoinTable(
            name = "proprietate_facilitati",
            joinColumns = @JoinColumn(name = "id_proprietate"),
            inverseJoinColumns = @JoinColumn(name = "id_facilitate")
    )
    private List<Facilitate> facilitati = new ArrayList<>();

    // Constructor gol
    public Proprietate() {}

    // --- GETTERS SI SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getSuprafata() { return suprafata; }
    public void setSuprafata(Double suprafata) { this.suprafata = suprafata; }

    public Integer getNrCamere() { return nrCamere; }
    public void setNrCamere(Integer nrCamere) { this.nrCamere = nrCamere; }

    public Integer getAnConstruire() { return anConstruire; }
    public void setAnConstruire(Integer anConstruire) { this.anConstruire = anConstruire; }

    public String getTipProprietate() { return tipProprietate; }
    public void setTipProprietate(String tipProprietate) { this.tipProprietate = tipProprietate; }

    public List<Facilitate> getFacilitati() { return facilitati; }
    public void setFacilitati(List<Facilitate> facilitati) { this.facilitati = facilitati; }
}