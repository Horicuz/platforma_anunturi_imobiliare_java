/** Controller pentru funcționalitățile de administrator (gestionare utilizatori).
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.controller;

import com.proiect.model.Utilizator;
import com.proiect.repository.UtilizatorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Controller dedicat funcționalităților de Administrator.
 * Gestionare utilizatori.
 */
@Controller
public class AdminController {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    // --- 1. LISTA TUTUROR UTILIZATORILOR ---
    @GetMapping("/admin/utilizatori")
    public String listaUtilizatori(Model model, HttpSession session) {
        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");

        // Securitate: Doar ADMIN are voie aici
        if (userLogat == null || !userLogat.getRol().equals("ADMIN")) {
            return "redirect:/anunturi";
        }

        List<Utilizator> totiUserii = utilizatorRepository.findAll();
        model.addAttribute("listaUseri", totiUserii);

        return "admin-utilizatori"; // Pagina HTML noua
    }

    // --- 2. STERGE UTILIZATOR ---
    @GetMapping("/admin/sterge-user/{id}")
    public String stergeUtilizator(@PathVariable Long id, HttpSession session) {
        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");

        if (userLogat != null && userLogat.getRol().equals("ADMIN")) {
            // Protectie: Adminul nu se poate sterge pe el insusi
            if (!userLogat.getId().equals(id)) {
                utilizatorRepository.deleteById(id);
            }
        }
        return "redirect:/admin/utilizatori";
    }
}