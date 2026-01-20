/** Entitatea JPA pentru stocarea căilor către imaginile anunțurilor.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */


package com.proiect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "imagini")
public class Imagine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagine") // <--- ASTA ERA PROBLEMA! Mapeaza pe coloana existenta
    private Long id;

    @Column(name = "url_imagine") // Mapeaza pe coloana corecta din SQL
    private String url;

    @ManyToOne
    @JoinColumn(name = "id_anunt") // Mapeaza pe cheia straina corecta
    private Anunt anunt;

    public Imagine() {}

    // Constructor util pentru salvare rapida
    public Imagine(String url, Anunt anunt) {
        this.url = url;
        this.anunt = anunt;
    }

    // --- Getters si Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Anunt getAnunt() { return anunt; }
    public void setAnunt(Anunt anunt) { this.anunt = anunt; }
}