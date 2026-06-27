package com.skulin.skulin.model;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jadwal")
public class Jadwal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mataPelajaran;
    private String tipe;
    private LocalDate tanggal;
    private String jamMulai;
    private String jamSelesai;
    private String kelas;

    public Jadwal() {}

    // Otomatis ambil nama hari dari tanggal
    public String getHari() {
        if (tanggal == null) return "-";
        return tanggal.getDayOfWeek()
               .getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMataPelajaran() { return mataPelajaran; }
    public void setMataPelajaran(String mataPelajaran) { this.mataPelajaran = mataPelajaran; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public String getJamMulai() { return jamMulai; }
    public void setJamMulai(String jamMulai) { this.jamMulai = jamMulai; }

    public String getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(String jamSelesai) { this.jamSelesai = jamSelesai; }

    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }
}