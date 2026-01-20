/** Entitatea JPA pentru utilizatorii aplicației (Clienți, Agenți, Admini)
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "utilizatori")
public class Utilizator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilizator")
    private Long id;

    @NotBlank(message = "Numele este obligatoriu")
    @Size(min = 3, max = 20, message = "Numele trebuie să aibă între 3 și 20 de caractere")
    @Pattern(regexp = "^[a-zA-ZăâîșțĂÂÎȘȚ -]+$", message = "Numele poate conține doar litere")
    private String nume;

    @NotBlank(message = "Prenumele este obligatoriu")
    @Size(min = 3, max = 20, message = "Prenumele trebuie să aibă între 3 și 20 de caractere")
    @Pattern(regexp = "^[a-zA-ZăâîșțĂÂÎȘȚ -]+$", message = "Prenumele poate conține doar litere")
    private String prenume;

    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Formatul email-ului este invalid")
    @Size(min = 5, max = 50, message = "Email-ul trebuie să aibă maxim 50 de caractere")
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    // Validarea complexa de parola o facem si in frontend, aici verificam doar lungimea minima de siguranta
    @Size(min = 6, message = "Parola trebuie să aibă minim 6 caractere")
    private String parola;

    // Telefonul e optional, dar daca e introdus, trebuie sa fie valid (Format RO: 07xxxxxxxx)
    @Pattern(regexp = "^$|^07[0-9]{8}$", message = "Telefonul trebuie să fie valid (ex: 0722123123)")
    private String telefon;

    private String rol;

    @OneToMany(mappedBy = "utilizator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anunt> anunturi;

    public List<Anunt> getAnunturi() { return anunturi; }
    public void setAnunturi(List<Anunt> anunturi) { this.anunturi = anunturi; }

    public Utilizator() {}

    // --- GETTERS SI SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}