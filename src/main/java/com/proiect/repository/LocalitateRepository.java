/** Interfață Repository pentru căutarea localităților.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.repository;

import com.proiect.model.Localitate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalitateRepository extends JpaRepository<Localitate, Long> {

    // Aceasta este metoda "magica" pe care o folosim in AnuntController
    // Spring genereaza automat SQL: SELECT * FROM localitate WHERE nume_oras = ?
    Localitate findByNumeOras(String numeOras);
}