package com.skulin.skulin.controller;

import com.skulin.skulin.model.Jadwal;
import com.skulin.skulin.repository.JadwalRepository;

import com.skulin.skulin.model.Jadwal;
import com.skulin.skulin.repository.JadwalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jadwal")
public class JadwalController {

    @Autowired
    private JadwalRepository jadwalRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("jadwalList", jadwalRepository.findAll());
        model.addAttribute("jadwal", new Jadwal());
        return "jadwal/index";
    }

    @PostMapping("/tambah")
    public String tambah(@ModelAttribute Jadwal jadwal) {
        jadwalRepository.save(jadwal);
        return "redirect:/jadwal";
    }

    @GetMapping("/hapus/{id}")
    public String hapus(@PathVariable Long id) {
        jadwalRepository.deleteById(id);
        return "redirect:/jadwal";
    }
}
