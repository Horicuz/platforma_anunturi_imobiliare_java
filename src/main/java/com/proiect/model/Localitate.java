/** Entitatea JPA pentru localități (orașe).
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */


package com.proiect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "localitate")
public class Localitate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_localitate")
    private Long id;

    @Column(name = "nume_oras")
    private String numeOras;

    @Column(name = "nume_judet")
    private String numeJudet;

    @Column(name = "nume_tara")
    private String numeTara;

    // Constructor gol
    public Localitate() {}

    // Constructor cu parametri (util cand cream localitati noi)
    public Localitate(String numeOras, String numeJudet, String numeTara) {
        this.numeOras = numeOras;
        this.numeJudet = numeJudet;
        this.numeTara = numeTara;
    }

    // --- GETTERS SI SETTERS COMPLET ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeOras() { return numeOras; }
    public void setNumeOras(String numeOras) { this.numeOras = numeOras; }

    public String getNumeJudet() { return numeJudet; }
    public void setNumeJudet(String numeJudet) { this.numeJudet = numeJudet; }

    public String getNumeTara() { return numeTara; }
    public void setNumeTara(String numeTara) { this.numeTara = numeTara; }
}