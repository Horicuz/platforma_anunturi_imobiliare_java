/** Entitatea JPA pentru facilitățile proprietăților (Many-to-Many).
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "facilitati")
public class Facilitate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facilitate")
    private Long id;

    @Column(name = "nume_facilitate")
    private String numeFacilitate;

    // Relatia inversa (optionala, dar buna pentru JPA)
    @ManyToMany(mappedBy = "facilitati")
    private List<Proprietate> proprietati;

    public Facilitate() {}

    // Getters si Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeFacilitate() { return numeFacilitate; }
    public void setNumeFacilitate(String numeFacilitate) { this.numeFacilitate = numeFacilitate; }
    public List<Proprietate> getProprietati() { return proprietati; }
    public void setProprietati(List<Proprietate> proprietati) { this.proprietati = proprietati; }
}