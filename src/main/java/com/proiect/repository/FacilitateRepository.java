/** Interfață Repository pentru gestionarea facilităților.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.repository;
import com.proiect.model.Facilitate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilitateRepository extends JpaRepository<Facilitate, Long> {}