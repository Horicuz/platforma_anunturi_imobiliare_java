/** Clasa pentru gestionarea globală a erorilor (ex: fișiere prea mari).
 * @author Potop Horia-Ioan
 * @version 10 Decembrie 2025
 */


package com.proiect.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartError(MultipartException e, RedirectAttributes redirectAttributes) {

        // --- ACEASTA LINIE ESTE CRITICA PENTRU DEBUG ---
        System.out.println("--- EROARE UPLOAD PRINSĂ ---");
        e.printStackTrace(); // Ne va arata in consola DE CE a crapat
        // -----------------------------------------------

        redirectAttributes.addFlashAttribute("eroareImagine", "Eroare upload: " + e.getMessage());
        return "redirect:/anunturi/adauga";
    }
}