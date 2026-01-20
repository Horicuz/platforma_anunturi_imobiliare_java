/** Interfață Repository pentru operațiile CRUD asupra anunțurilor.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.repository;

import com.proiect.model.Anunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnuntRepository extends JpaRepository<Anunt, Long> {

    // METODA NOUA: Cauta toate anunturile postate de un user specific
    List<Anunt> findAllByUtilizator_Id(Long idUtilizator);
}