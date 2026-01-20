/** Clasa Service pentru logica de business legată de utilizatori.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */


package com.proiect.service;

import com.proiect.model.Utilizator;
import com.proiect.repository.UtilizatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilizatorService {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    // --- AUTENTIFICARE CU MESAJE EXPLICITE ---
    // Returneaza Utilizator daca e ok, sau arunca exceptie cu mesaj
    public Utilizator autentificare(String email, String parola) throws Exception {
        Utilizator user = utilizatorRepository.findByEmail(email);

        if (user == null) {
            throw new Exception("Acest cont nu există! Verifică email-ul sau înregistrează-te.");
        }

        if (!user.getParola().equals(parola)) {
            throw new Exception("Parola este greșită! Mai încearcă.");
        }

        return user;
    }

    // --- INREGISTRARE CU VALIDARE DE DUPLICAT ---
    public void inregistrare(Utilizator user) throws Exception {
        // 1. Verificam daca mailul exista deja
        Utilizator existent = utilizatorRepository.findByEmail(user.getEmail());
        if (existent != null) {
            throw new Exception("Există deja un cont cu acest email! Du-te la pagina de Login.");
        }

        // 2. Setam rolul default
        if (user.getRol() == null || user.getRol().isEmpty()) {
            user.setRol("CLIENT");
        }

        // 3. Salvam
        utilizatorRepository.save(user);
    }
}