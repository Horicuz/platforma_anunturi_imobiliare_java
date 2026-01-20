/** Controller pentru gestionarea profilului utilizatorului curent.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */


package com.proiect.controller;

import com.proiect.model.Anunt;
import com.proiect.model.Utilizator;
import com.proiect.repository.AnuntRepository;
import com.proiect.repository.UtilizatorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProfilController {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @Autowired
    private AnuntRepository anuntRepository;

    // --- 1. VIZUALIZARE PROFIL ---
    @GetMapping("/profil")
    public String arataProfil(HttpSession session, Model model) {
        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        if (userLogat == null) return "redirect:/login";

        // Luam userul proaspat din DB
        Utilizator userDb = utilizatorRepository.findById(userLogat.getId()).orElse(null);
        model.addAttribute("utilizator", userDb);

        // Luam anunturile
        List<Anunt> anunturileMele = anuntRepository.findAllByUtilizator_Id(userLogat.getId());
        model.addAttribute("listaAnunturi", anunturileMele);

        return "profil";
    }

    // --- 2. PAGINA DE EDITARE ---
    @GetMapping("/profil/edit")
    public String arataFormularEditare(HttpSession session, Model model) {
        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        if (userLogat == null) return "redirect:/login";

        Utilizator userDb = utilizatorRepository.findById(userLogat.getId()).orElse(null);
        model.addAttribute("utilizator", userDb);

        return "profil-edit";
    }

    // --- 3. SALVARE DATE (LOGICA REPARATA) ---
    // --- 3. SALVARE DATE (CU VALIDARE STRICTA) ---
    @PostMapping("/profil/update")
    public String updateProfil(@ModelAttribute("utilizator") Utilizator userForm,
                               HttpSession session,
                               Model model) {

        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        if (userLogat == null) return "redirect:/login";

        // Reincarcam datele reale din baza de date
        Utilizator userDb = utilizatorRepository.findById(userLogat.getId()).orElse(null);
        if (userDb == null) return "redirect:/login";

        boolean areErori = false;

        // 1. Validare Nume (Litere, 3-20 caractere)
        if (userForm.getNume() == null || !userForm.getNume().matches("^[a-zA-ZăâîșțĂÂÎȘȚ -]{3,20}$")) {
            model.addAttribute("eroareNume", "Nume invalid (3-20 litere).");
            areErori = true;
        }

        // 2. Validare Prenume
        if (userForm.getPrenume() == null || !userForm.getPrenume().matches("^[a-zA-ZăâîșțĂÂÎȘȚ -]{3,20}$")) {
            model.addAttribute("eroarePrenume", "Prenume invalid (3-20 litere).");
            areErori = true;
        }

        // 3. Validare Telefon (Optional, Format + Unicitate)
        String telNou = userForm.getTelefon();
        if (telNou != null && !telNou.trim().isEmpty()) {
            // A. Format
            if (!telNou.matches("^07\\d{8}$")) { // Regex strict RO: incepe cu 07 si are 10 cifre total
                model.addAttribute("eroareTelefon", "Format invalid (Ex: 07xxxxxxxx).");
                areErori = true;
            } else {
                // B. Unicitate (Verificam daca numarul e luat de ALT user)
                Utilizator proprietarTelefon = utilizatorRepository.findByTelefon(telNou);
                if (proprietarTelefon != null && !proprietarTelefon.getId().equals(userDb.getId())) {
                    model.addAttribute("eroareTelefon", "Acest număr este deja folosit de altcineva.");
                    areErori = true;
                }
            }
        }

        // DACA AVEM ERORI -> Ramanem pe pagina
        if (areErori) {
            // Refacem datele care nu se editeaza (Email, Rol) ca sa nu apara goale
            userForm.setEmail(userDb.getEmail());
            userForm.setRol(userDb.getRol());
            return "profil-edit";
        }

        // DACA TOTUL E OK -> SALVAM
        userDb.setNume(userForm.getNume());
        userDb.setPrenume(userForm.getPrenume());
        userDb.setTelefon(userForm.getTelefon());

        // Rolul se schimba doar daca nu esti admin (adminul ramane admin)
        if (!"ADMIN".equals(userDb.getRol())) {
            userDb.setRol(userForm.getRol());
        }

        utilizatorRepository.save(userDb);
        session.setAttribute("userLogat", userDb); // Actualizam sesiunea

        return "redirect:/profil";
    }
}