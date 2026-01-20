/** Controller pentru gestionarea anunțurilor (afișare, adăugare, editare, ștergere).
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */

package com.proiect.controller;

import com.proiect.model.*;
import com.proiect.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AnuntController {

    @Autowired private AnuntRepository anuntRepository;
    @Autowired private LocalitateRepository localitateRepository;
    @Autowired private FacilitateRepository facilitateRepository;

    // --- HOME & LISTARE ---
    @GetMapping("/")
    public String home() { return "redirect:/anunturi"; }

    @GetMapping("/anunturi")
    public String listaAnunturi(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long orasId,
            @RequestParam(required = false) Double minPret,
            @RequestParam(required = false) Double maxPret,
            @RequestParam(required = false) Double minMp,
            @RequestParam(required = false) Double maxMp,
            @RequestParam(required = false) Integer minAn,
            @RequestParam(required = false) Integer maxAn,
            @RequestParam(required = false) List<Long> facilitatiIds,
            @RequestParam(required = false, defaultValue = "noi") String sortare,
            Model model, HttpSession session) {

        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        model.addAttribute("userLogat", userLogat);

        // Validare input filtre (Sanitizare)
        if (keyword != null && (keyword.length() < 3 || keyword.length() > 20)) keyword = null;
        if (minPret != null && (minPret < 0 || minPret > 100000000)) minPret = null;
        if (minAn != null && (minAn < 1800 || minAn > 2030)) minAn = null;

        // Preluare toate active
        List<Anunt> lista = anuntRepository.findAll().stream()
                .filter(a -> "ACTIV".equals(a.getStatus()))
                .collect(Collectors.toList());

        // Aplicare filtre
        if (keyword != null) {
            String k = keyword.toLowerCase();
            lista = lista.stream().filter(a -> a.getTitlu().toLowerCase().contains(k) || a.getDescriere().toLowerCase().contains(k)).collect(Collectors.toList());
        }
        if (orasId != null) lista = lista.stream().filter(a -> a.getLocalitate().getId().equals(orasId)).collect(Collectors.toList());

        Double fMinPret = minPret; if (fMinPret != null) lista = lista.stream().filter(a -> a.getPret() >= fMinPret).collect(Collectors.toList());
        Double fMaxPret = maxPret; if (fMaxPret != null) lista = lista.stream().filter(a -> a.getPret() <= fMaxPret).collect(Collectors.toList());

        Double fMinMp = minMp; if (fMinMp != null) lista = lista.stream().filter(a -> a.getProprietate().getSuprafata() >= fMinMp).collect(Collectors.toList());
        Double fMaxMp = maxMp; if (fMaxMp != null) lista = lista.stream().filter(a -> a.getProprietate().getSuprafata() <= fMaxMp).collect(Collectors.toList());

        Integer fMinAn = minAn; if (fMinAn != null) lista = lista.stream().filter(a -> a.getProprietate().getAnConstruire() >= fMinAn).collect(Collectors.toList());
        Integer fMaxAn = maxAn; if (fMaxAn != null) lista = lista.stream().filter(a -> a.getProprietate().getAnConstruire() <= fMaxAn).collect(Collectors.toList());

        if (facilitatiIds != null && !facilitatiIds.isEmpty()) {
            lista = lista.stream().filter(a -> {
                List<Long> ids = a.getProprietate().getFacilitati().stream().map(Facilitate::getId).toList();
                return new HashSet<>(ids).containsAll(facilitatiIds);
            }).collect(Collectors.toList());
        }

        // --- SORTARE EXTINSA (Modificarea Ceruta) ---
        if (sortare != null) {
            switch (sortare) {
                // 1. DATA (NOI / VECHI)
                case "vechi":
                    lista.sort(Comparator.comparing(Anunt::getDataPublicarii));
                    break;
                case "noi":
                    lista.sort(Comparator.comparing(Anunt::getDataPublicarii).reversed());
                    break;

                // 2. PRET
                case "pret_cresc":
                    lista.sort(Comparator.comparing(Anunt::getPret));
                    break;
                case "pret_desc":
                    lista.sort(Comparator.comparing(Anunt::getPret).reversed());
                    break;

                // 3. SUPRAFATA (Navigam in obiectul Proprietate)
                case "suprafata_cresc":
                    lista.sort(Comparator.comparing(a -> a.getProprietate().getSuprafata()));
                    break;
                case "suprafata_desc":
                    lista.sort(Comparator.comparing((Anunt a) -> a.getProprietate().getSuprafata()).reversed());
                    break;

                // 4. AN CONSTRUCTIE
                case "an_cresc":
                    lista.sort(Comparator.comparing(a -> a.getProprietate().getAnConstruire()));
                    break;
                case "an_desc":
                    lista.sort(Comparator.comparing((Anunt a) -> a.getProprietate().getAnConstruire()).reversed());
                    break;

                // Default: Cele mai noi
                default:
                    lista.sort(Comparator.comparing(Anunt::getDataPublicarii).reversed());
                    break;
            }
        } else {
            // Default daca e null
            lista.sort(Comparator.comparing(Anunt::getDataPublicarii).reversed());
        }

        model.addAttribute("listaAnunturi", lista);
        model.addAttribute("toateOrasele", localitateRepository.findAll());
        model.addAttribute("toateFacilitatile", facilitateRepository.findAll());

        // Parametrii pt UI (sa ramana selectati dupa refresh)
        model.addAttribute("paramKeyword", keyword);
        model.addAttribute("paramMinPret", minPret);
        model.addAttribute("paramMaxPret", maxPret);
        model.addAttribute("paramMinMp", minMp);
        model.addAttribute("paramMaxMp", maxMp);
        model.addAttribute("paramMinAn", minAn);
        model.addAttribute("paramMaxAn", maxAn);
        model.addAttribute("paramSortare", sortare); // Adaugam si sortarea curenta

        return "anunturi";
    }

    // --- FORMULAR ADAUGARE ---
    @GetMapping("/anunturi/adauga")
    public String arataFormularAdaugare(HttpSession session, Model model) {
        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        if (userLogat == null) return "redirect:/login";

        Anunt anunt = new Anunt();
        anunt.setProprietate(new Proprietate());
        model.addAttribute("anunt", anunt);
        model.addAttribute("toateOrasele", localitateRepository.findAll());
        model.addAttribute("toateFacilitatile", facilitateRepository.findAll());

        return "anunt-form";
    }

    // --- SALVARE ANUNT (CU DEBUGGING) ---
    @PostMapping("/anunturi/salveaza")
    public String salveazaAnunt(@Valid @ModelAttribute Anunt anunt,
                                BindingResult bindingResult,
                                @RequestParam("fisierePoze") MultipartFile[] fisiere,
                                Model model, HttpSession session) {

        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        if (userLogat == null) return "redirect:/login";

        // 1. Validare erori standard (Java Bean Validation)
        if (bindingResult.hasErrors()) {
            System.out.println("--- EROARE VALIDARE ---");
            for (ObjectError error : bindingResult.getAllErrors()) {
                System.out.println(error.toString()); // Apare in consola IntelliJ
            }
            // REINCARCARE LISTE (CRITIC!)
            model.addAttribute("toateOrasele", localitateRepository.findAll());
            model.addAttribute("toateFacilitatile", facilitateRepository.findAll());
            return "anunt-form";
        }

        // 2. Validare Numar Poze
        long numarPoze = Arrays.stream(fisiere).filter(f -> !f.isEmpty()).count();
        if (numarPoze > 5) {
            model.addAttribute("eroareImagine", "Maxim 5 poze!");
            model.addAttribute("toateOrasele", localitateRepository.findAll());
            model.addAttribute("toateFacilitatile", facilitateRepository.findAll());
            return "anunt-form";
        }

        // 3. Salvare si Upload Imagini
        String uploadDir = "uploads/";
        try {
            // Verificam daca folderul exista, daca nu il cream
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception e) { e.printStackTrace(); }

        for (MultipartFile fisier : fisiere) {
            if (!fisier.isEmpty()) {
                // Verificare tip fisier
                if (!Arrays.asList("image/jpeg", "image/png", "image/jpg").contains(fisier.getContentType())) {
                    model.addAttribute("eroareImagine", "Fisier invalid (doar JPG/PNG): " + fisier.getOriginalFilename());
                    model.addAttribute("toateOrasele", localitateRepository.findAll());
                    model.addAttribute("toateFacilitatile", facilitateRepository.findAll());
                    return "anunt-form";
                }
                // Upload fizic
                try {
                    String numeUnic = UUID.randomUUID().toString() + "_" + fisier.getOriginalFilename();
                    Files.write(Paths.get(uploadDir + numeUnic), fisier.getBytes());
                    anunt.getImagini().add(new Imagine(numeUnic, anunt));
                } catch (IOException e) { e.printStackTrace(); }
            }
        }

        // 4. Salvare in Baza de Date (CU PROTECTIE LA EROARE)
        try {
            anunt.setUtilizator(userLogat);
            anunt.setDataPublicarii(LocalDate.now());
            anunt.setDataExpirare(LocalDate.now().plusYears(1));
            anunt.setStatus("ACTIV");

            anuntRepository.save(anunt);

            System.out.println("-> ANUNT SALVAT CU SUCCES! ID: " + anunt.getId());

        } catch (Exception e) {
            // Daca crapa aici, prindem eroarea si o afisam in formular, NU pagina alba
            e.printStackTrace(); // Apare in consola
            model.addAttribute("eroareImagine", "Eroare la salvarea în baza de date: " + e.getMessage());

            // Reincarcam listele ca sa nu dea eroare 500
            model.addAttribute("toateOrasele", localitateRepository.findAll());
            model.addAttribute("toateFacilitatile", facilitateRepository.findAll());
            return "anunt-form";
        }

        return "redirect:/anunturi";
    }

    // --- STERGERE SI STATUS (NESCHIMBATE) ---
    @GetMapping("/anunturi/sterge/{id}")
    public String stergeAnunt(@PathVariable Long id, HttpSession session) {
        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        Anunt anunt = anuntRepository.findById(id).orElse(null);
        if (anunt != null && userLogat != null && (userLogat.getRol().equals("ADMIN") || anunt.getUtilizator().getId().equals(userLogat.getId()))) {
            anuntRepository.deleteById(id);
        }
        return "redirect:/anunturi";
    }

    @GetMapping("/anunturi/status/{id}")
    public String schimbaStatus(@PathVariable Long id) {
        Anunt anunt = anuntRepository.findById(id).orElse(null);
        if (anunt != null) {
            anunt.setStatus("ACTIV".equals(anunt.getStatus()) ? "INACTIV" : "ACTIV");
            anuntRepository.save(anunt);
        }
        return "redirect:/profil";
    }

    // --- 1. AFISARE FORMULAR EDITARE ---
    @GetMapping("/anunturi/editeaza/{id}")
    public String arataFormularEditare(@PathVariable Long id, Model model, HttpSession session) {
        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        if (userLogat == null) return "redirect:/login";

        Anunt anuntexistent = anuntRepository.findById(id).orElse(null);

        // Verificam daca anuntul exista si daca userul e proprietarul (sau admin)
        if (anuntexistent == null ||
                (!anuntexistent.getUtilizator().getId().equals(userLogat.getId()) && !userLogat.getRol().equals("ADMIN"))) {
            return "redirect:/anunturi";
        }

        model.addAttribute("anunt", anuntexistent);
        model.addAttribute("toateOrasele", localitateRepository.findAll());
        model.addAttribute("toateFacilitatile", facilitateRepository.findAll());

        // Flag ca sa stim in HTML ca suntem pe modul de editare
        model.addAttribute("modEditare", true);

        return "anunt-form"; // Refolosim acelasi formular!
    }

    // --- 2. SALVARE MODIFICARI ---
    @PostMapping("/anunturi/update/{id}")
    public String updateAnunt(@PathVariable Long id,
                              @Valid @ModelAttribute Anunt anuntForm,
                              BindingResult bindingResult,
                              @RequestParam("fisierePoze") MultipartFile[] fisiere,
                              Model model, HttpSession session) {

        Utilizator userLogat = (Utilizator) session.getAttribute("userLogat");
        if (userLogat == null) return "redirect:/login";

        // Luam anuntul original din baza de date
        Anunt anuntDb = anuntRepository.findById(id).orElse(null);
        if (anuntDb == null) return "redirect:/anunturi";

        if (bindingResult.hasErrors()) {
            model.addAttribute("toateOrasele", localitateRepository.findAll());
            model.addAttribute("toateFacilitatile", facilitateRepository.findAll());
            model.addAttribute("modEditare", true);
            return "anunt-form";
        }

        // Actualizam campurile
        anuntDb.setTitlu(anuntForm.getTitlu());
        anuntDb.setPret(anuntForm.getPret());
        anuntDb.setDescriere(anuntForm.getDescriere());
        anuntDb.setLocalitate(anuntForm.getLocalitate());

        // Actualizam proprietatea (folosim ID-ul proprietatii vechi ca sa nu cream una noua)
        Proprietate propDb = anuntDb.getProprietate();
        propDb.setSuprafata(anuntForm.getProprietate().getSuprafata());
        propDb.setNrCamere(anuntForm.getProprietate().getNrCamere());
        propDb.setAnConstruire(anuntForm.getProprietate().getAnConstruire());
        propDb.setTipProprietate(anuntForm.getProprietate().getTipProprietate());
        propDb.setFacilitati(anuntForm.getProprietate().getFacilitati());

        // Upload Poze NOI (se adauga la cele vechi)
        String uploadDir = "uploads/";
        for (MultipartFile fisier : fisiere) {
            if (!fisier.isEmpty()) {
                try {
                    String numeUnic = UUID.randomUUID().toString() + "_" + fisier.getOriginalFilename();
                    Files.write(Paths.get(uploadDir + numeUnic), fisier.getBytes());
                    anuntDb.getImagini().add(new Imagine(numeUnic, anuntDb));
                } catch (IOException e) { e.printStackTrace(); }
            }
        }

        anuntRepository.save(anuntDb);
        return "redirect:/profil";
    }

    // --- VIZUALIZARE DETALII ANUNT ---
    @GetMapping("/anunturi/detalii/{id}")
    public String veziDetalii(@PathVariable Long id, Model model) {
        Anunt anunt = anuntRepository.findById(id).orElse(null);

        // Daca anuntul nu exista (cineva a scris ID gresit in URL), il trimitem la lista
        if (anunt == null) {
            return "redirect:/anunturi";
        }

        model.addAttribute("anunt", anunt);
        return "anunt-detalii"; // Aceasta va fi noua pagina HTML
    }
}