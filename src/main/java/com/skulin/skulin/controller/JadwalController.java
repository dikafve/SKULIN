package com.skulin.skulin.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skulin.skulin.model.Jadwal;
import com.skulin.skulin.repository.JadwalRepository;

@Controller
@RequestMapping("/jadwal")
public class JadwalController {

    @Autowired
    private JadwalRepository jadwalRepository;

    @GetMapping
    public String index(Model model, @RequestParam(defaultValue = "hari") String sort) {
        var semua = jadwalRepository.findAll();
        
        // Urutan hari
        List<String> urutan = List.of("Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu");
        
        semua.sort((a, b) -> {
            if (sort.equals("hari")) {
                int hariA = urutan.indexOf(a.getHari());
                int hariB = urutan.indexOf(b.getHari());
                if (hariA != hariB) return hariA - hariB;
                return a.getJamMulai().compareTo(b.getJamMulai());
            } else if (sort.equals("jam")) {
                return a.getJamMulai().compareTo(b.getJamMulai());
            } else if (sort.equals("matkul")) {
                return a.getMataPelajaran().compareToIgnoreCase(b.getMataPelajaran());
            }
            return 0;
        });
    
        model.addAttribute("jadwalList", semua);
        model.addAttribute("jadwal", new Jadwal());
        model.addAttribute("totalJadwal", semua.size());
        model.addAttribute("totalKuliah", semua.stream().filter(j -> "kuliah".equals(j.getTipe())).count());
        model.addAttribute("totalUjian",  semua.stream().filter(j -> "ujian".equals(j.getTipe())).count());
        model.addAttribute("totalNongki", semua.stream().filter(j -> "nongki".equals(j.getTipe())).count());
        model.addAttribute("currentSort", sort);
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

    @GetMapping("/edit/{id}")
public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("jadwal", jadwalRepository.findById(id).orElseThrow());
    return "jadwal/edit";
}

@PostMapping("/edit/{id}")
public String editSave(@PathVariable Long id, @ModelAttribute Jadwal jadwal) {
    jadwal.setId(id);
    jadwalRepository.save(jadwal);
    return "redirect:/jadwal?success=1";
  }
}