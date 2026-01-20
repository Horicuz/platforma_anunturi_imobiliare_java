/** Entitatea JPA care definește tabelul Anunțuri în baza de date.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */
package com.proiect.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "anunt")
public class Anunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anunt")
    private Long id;

    @NotBlank(message = "Titlul este obligatoriu")
    @Size(min = 10, max = 100, message = "Titlul trebuie să aibă între 10 și 100 caractere")
    private String titlu;

    @NotBlank(message = "Descrierea este obligatorie")
    @Size(max = 75, message = "Descrierea trebuie să fie scurtă (max 75 caractere)") // <--- MODIFICAT
    private String descriere;

    @NotNull(message = "Prețul este obligatoriu")
    @Min(value = 100, message = "Prețul minim este 100 €") // <--- LIMITA NOUA
    @Max(value = 100000000, message = "Prețul depășește limita maximă (100 Mil €)")
    private Double pret;


    private String status; // "ACTIV" sau "INACTIV"

    @Column(name = "data_publicarii")
    private LocalDate dataPublicarii;

    @Column(name = "data_expirare")
    private LocalDate dataExpirare;

    // --- RELATII (Foreign Keys) ---

    @ManyToOne
    @JoinColumn(name = "id_utilizator")
    private Utilizator utilizator;

    @NotNull(message = "Trebuie să selectezi un oraș")
    @ManyToOne
    @JoinColumn(name = "id_localitate")
    private Localitate localitate;

    // RELATIA 1:N cu IMAGINI
    // mappedBy = "anunt" inseamna ca legatura e gestionata de campul "anunt" din clasa Imagine
    @OneToMany(mappedBy = "anunt", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Imagine> imagini = new java.util.ArrayList<>();

    // Getter si Setter pentru lista
    public java.util.List<Imagine> getImagini() { return imagini; }
    public void setImagini(java.util.List<Imagine> imagini) { this.imagini = imagini; }

    @Valid
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_proprietate")
    private Proprietate proprietate;

    // Camp temporar (nefolosit in DB, dar util pentru formularul HTML simplu daca vrei sa scrii doar orasul)
    @Transient
    private String numeLocalitateTemp;

    public Anunt() {}

    // --- GETTERS SI SETTERS COMPLET ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public Double getPret() { return pret; }
    public void setPret(Double pret) { this.pret = pret; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataPublicarii() { return dataPublicarii; }
    public void setDataPublicarii(LocalDate dataPublicarii) { this.dataPublicarii = dataPublicarii; }

    public LocalDate getDataExpirare() { return dataExpirare; }
    public void setDataExpirare(LocalDate dataExpirare) { this.dataExpirare = dataExpirare; }

    public Utilizator getUtilizator() { return utilizator; }
    public void setUtilizator(Utilizator utilizator) { this.utilizator = utilizator; }

    public Localitate getLocalitate() { return localitate; }
    public void setLocalitate(Localitate localitate) { this.localitate = localitate; }

    public Proprietate getProprietate() { return proprietate; }
    public void setProprietate(Proprietate proprietate) { this.proprietate = proprietate; }

    public String getNumeLocalitateTemp() { return numeLocalitateTemp; }
    public void setNumeLocalitateTemp(String numeLocalitateTemp) { this.numeLocalitateTemp = numeLocalitateTemp; }
}