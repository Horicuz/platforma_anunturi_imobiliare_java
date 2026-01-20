/** Controller pentru autentificare și înregistrare utilizatori.
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */


package com.proiect.controller;

import com.proiect.model.Utilizator;
import com.proiect.repository.UtilizatorRepository; // <--- IMPORT NOU
import com.proiect.service.UtilizatorService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; // <--- IMPORT NOU
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // <--- IMPORT NOU
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UtilizatorService utilizatorService;

    // --- CRITIC: Adaugam si Repository-ul pentru verificarile de unicitate ---
    @Autowired
    private UtilizatorRepository utilizatorRepository;

    // --- LOGIN (Logica ta existenta) ---
    @GetMapping("/login")
    public String arataLogin() { return "login"; }

    @PostMapping("/login")
    public String proceseazaLogin(@RequestParam String email,
                                  @RequestParam String parola,
                                  HttpSession session,
                                  Model model) {
        try {
            Utilizator user = utilizatorService.autentificare(email, parola);
            session.setAttribute("userLogat", user);
            return "redirect:/anunturi";
        } catch (Exception e) {
            model.addAttribute("eroare", e.getMessage());
            return "login";
        }
    }

    // --- REGISTER (Logica noua cu validari) ---
    @GetMapping("/register")
    public String arataRegister(Model model) {
        // IMPORTANT: Numele atributului "user" trebuie sa coincida cu cel din POST si din HTML
        model.addAttribute("user", new Utilizator());
        return "register";
    }

    @PostMapping("/register")
    public String inregistrare(@Valid @ModelAttribute("user") Utilizator user,
                               BindingResult bindingResult,
                               Model model) {

        // 1. Verificare Email Unic
        if (utilizatorRepository.findByEmail(user.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.user", "Acest email este deja înregistrat.");
        }

        // --- 2. VERIFICARE TELEFON (RESTRICTII CERUTE) ---
        String tel = user.getTelefon();

        // Verificam DOAR daca userul a scris ceva (fiind optional)
        if (tel != null && !tel.trim().isEmpty()) {

            // A. Verificare Format (Exact 10 cifre)
            if (!tel.matches("^\\d{10}$")) {
                bindingResult.rejectValue("telefon", "error.user", "Telefonul trebuie să conțină exact 10 cifre.");
            }
            // B. Verificare Unicitate (doar daca formatul e bun)
            else {
                Utilizator userExistent = utilizatorRepository.findByTelefon(tel);
                if (userExistent != null) {
                    bindingResult.rejectValue("telefon", "error.user", "Număr de telefon deja utilizat.");
                }
            }
        }

        // 3. Daca sunt erori, reincarcam pagina register
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 4. Salvare User
        if (user.getRol() == null) {
            user.setRol("CLIENT");
        }

        // Salvam direct prin repo (sau poti face o metoda in service, dar e ok si asa)
        utilizatorRepository.save(user);

        return "redirect:/login?success";
    }

    // --- LOGOUT ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}