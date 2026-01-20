/** Interfață Repository pentru gestionarea utilizatorilor și autentificare.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.repository;

import com.proiect.model.Utilizator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilizatorRepository extends JpaRepository<Utilizator, Long> {
    // Aceasta metoda magica ii spune lui Spring sa caute dupa coloana 'email'
    Utilizator findByEmail(String email);
    Utilizator findByTelefon(String telefon);
}