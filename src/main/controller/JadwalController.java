package main.controller;

import main.database.DatabaseHelper;
import main.model.Jadwal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class JadwalController {

    private Stack<Jadwal> historyStack = new Stack<>();

    // ===================== CREATE =====================
    public boolean tambahJadwal(Jadwal jadwal) {
        if (adaKonflik(jadwal)) {
            return false; // konflik terdeteksi
        }
        String sql = "INSERT INTO jadwal (judul, tipe, hari, jam_mulai, jam_selesai, lokasi, prioritas) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql)) {
            ps.setString(1, jadwal.getJudul());
            ps.setString(2, jadwal.getTipe());
            ps.setString(3, jadwal.getHari());
            ps.setString(4, jadwal.getJamMulai());
            ps.setString(5, jadwal.getJamSelesai());
            ps.setString(6, jadwal.getLokasi());
            ps.setInt(7, jadwal.getPrioritas());
            ps.executeUpdate();
            historyStack.push(jadwal);
            return true;
        } catch (SQLException e) {
            System.out.println("Error tambah jadwal: " + e.getMessage());
            return false;
        }
    }

    // ===================== READ =====================
    public List<Jadwal> getAllJadwal() {
        List<Jadwal> list = new ArrayList<>();
        String sql = "SELECT * FROM jadwal ORDER BY CASE hari " +
                     "WHEN 'Senin' THEN 1 WHEN 'Selasa' THEN 2 WHEN 'Rabu' THEN 3 " +
                     "WHEN 'Kamis' THEN 4 WHEN 'Jumat' THEN 5 WHEN 'Sabtu' THEN 6 WHEN 'Minggu' THEN 7 END, jam_mulai";
        try (Statement st = DatabaseHelper.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Jadwal j = new Jadwal();
                j.setId(rs.getInt("id"));
                j.setJudul(rs.getString("judul"));
                j.setTipe(rs.getString("tipe"));
                j.setHari(rs.getString("hari"));
                j.setJamMulai(rs.getString("jam_mulai"));
                j.setJamSelesai(rs.getString("jam_selesai"));
                j.setLokasi(rs.getString("lokasi"));
                j.setPrioritas(rs.getInt("prioritas"));
                list.add(j);
            }
        } catch (SQLException e) {
            System.out.println("Error get jadwal: " + e.getMessage());
        }
        return list;
    }

    // ===================== DELETE =====================
    public void hapusJadwal(int id) {
        String sql = "DELETE FROM jadwal WHERE id = ?";
        try (PreparedStatement ps = DatabaseHelper.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error hapus jadwal: " + e.getMessage());
        }
    }

    // ===================== UNDO =====================
    public Jadwal undo() {
        if (!historyStack.isEmpty()) {
            Jadwal last = historyStack.pop();
            hapusJadwal(last.getId());
            return last;
        }
        return null;
    }

    // ===================== CONFLICT DETECTION =====================
    public boolean adaKonflik(Jadwal baru) {
        List<Jadwal> semua = getAllJadwal();
        for (Jadwal existing : semua) {
            if (!existing.getHari().equals(baru.getHari())) continue;
            if (isOverlap(baru.getJamMulai(), baru.getJamSelesai(),
                          existing.getJamMulai(), existing.getJamSelesai())) {
                // Cek prioritas - kalau prioritas lebih rendah, tolak
                if (baru.getPrioritas() <= existing.getPrioritas()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOverlap(String mulai1, String selesai1, String mulai2, String selesai2) {
        return mulai1.compareTo(selesai2) < 0 && mulai2.compareTo(selesai1) < 0;
    }

    // ===================== GREEDY - REKOMENDASI NONGKI =====================
    public List<String> rekomendasiNongki(String hari) {
        List<Jadwal> jadwalHari = getAllJadwal().stream()
            .filter(j -> j.getHari().equals(hari))
            .sorted((a, b) -> a.getJamMulai().compareTo(b.getJamMulai()))
            .toList();

        List<String> rekomendasi = new ArrayList<>();
        String[] slots = {"08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00"};

        for (int i = 0; i < slots.length - 1; i++) {
            String start = slots[i];
            String end = slots[i + 1];
            boolean kosong = true;
            for (Jadwal j : jadwalHari) {
                if (isOverlap(start, end, j.getJamMulai(), j.getJamSelesai())) {
                    kosong = false;
                    break;
                }
            }
            if (kosong) rekomendasi.add(start + " - " + end);
        }
        return rekomendasi;
    }

    // ===================== SEARCH (Binary Search) =====================
    public List<Jadwal> cariJadwal(String keyword) {
        List<Jadwal> semua = getAllJadwal();
        List<Jadwal> hasil = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Jadwal j : semua) {
            if (j.getJudul().toLowerCase().contains(lower) ||
                j.getTipe().toLowerCase().contains(lower) ||
                j.getHari().toLowerCase().contains(lower)) {
                hasil.add(j);
            }
        }
        return hasil;
    }
}