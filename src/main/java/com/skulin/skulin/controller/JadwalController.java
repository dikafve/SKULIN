package com.skulin.skulin.controller;

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
        var semua = jadwalRepository.findAll();
        model.addAttribute("jadwalList", semua);
        model.addAttribute("jadwal", new Jadwal());
        model.addAttribute("totalJadwal", semua.size());
        model.addAttribute("totalKuliah", semua.stream().filter(j -> "kuliah".equals(j.getTipe())).count());
        model.addAttribute("totalUjian",  semua.stream().filter(j -> "ujian".equals(j.getTipe())).count());
        model.addAttribute("totalNongki", semua.stream().filter(j -> "nongki".equals(j.getTipe())).count());
        return "jadwal/index";
    }

    @PostMapping("/tambah")
    public String tambah(@ModelAttribute Jadwal jadwal) {
        jadwalRepository.save(jadwal);
        return "redirect:/jadwal?success=1";
    }

    @GetMapping("/hapus/{id}")
    public String hapus(@PathVariable Long id) {
        jadwalRepository.deleteById(id);
        return "redirect:/jadwal?hapus=1";
    }
}