package main.model;

public class Jadwal {
    private int id;
    private int mahasiswaId;
    private String judul;
    private String tipe; // kuliah, ujian, nongki
    private String hari;
    private String jamMulai;
    private String jamSelesai;
    private String lokasi;
    private int prioritas; // 1=nongki, 2=kuliah, 3=ujian
    private int mataKuliahId;

    public Jadwal() {}

    public Jadwal(String judul, String tipe, String hari, String jamMulai, String jamSelesai, String lokasi) {
        this.judul = judul;
        this.tipe = tipe;
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.lokasi = lokasi;
        this.prioritas = switch (tipe) {
            case "ujian" -> 3;
            case "kuliah" -> 2;
            default -> 1;
        };
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMahasiswaId() { return mahasiswaId; }
    public void setMahasiswaId(int mahasiswaId) { this.mahasiswaId = mahasiswaId; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public String getHari() { return hari; }
    public void setHari(String hari) { this.hari = hari; }

    public String getJamMulai() { return jamMulai; }
    public void setJamMulai(String jamMulai) { this.jamMulai = jamMulai; }

    public String getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(String jamSelesai) { this.jamSelesai = jamSelesai; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public int getPrioritas() { return prioritas; }
    public void setPrioritas(int prioritas) { this.prioritas = prioritas; }

    public int getMataKuliahId() { return mataKuliahId; }
    public void setMataKuliahId(int mataKuliahId) { this.mataKuliahId = mataKuliahId; }

    public String getTipeLabel() {
        return switch (tipe) {
            case "kuliah" -> "📚 Kuliah";
            case "ujian" -> "📝 Ujian";
            case "nongki" -> "☕ Nongki";
            default -> tipe;
        };
    }

    @Override
    public String toString() {
        return judul + " (" + tipe + ") - " + hari + " " + jamMulai + "-" + jamSelesai;
    }
}