/** Interfață Repository pentru datele proprietăților.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.repository;

import com.proiect.model.Proprietate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProprietateRepository extends JpaRepository<Proprietate, Long> {
    // Aici putem adauga pe viitor filtre: findByNrCamere(Integer nr);
}