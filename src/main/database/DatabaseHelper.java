package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:skulin.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("✅ Koneksi database berhasil!");
            }
        } catch (Exception e) {
            System.out.println("❌ Koneksi database gagal: " + e.getMessage());
        }
        return connection;
    }

    public static void initDatabase() {
        String mahasiswa = """
            CREATE TABLE IF NOT EXISTS mahasiswa (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                nim TEXT UNIQUE NOT NULL
            );
        """;

        String mataKuliah = """
            CREATE TABLE IF NOT EXISTS mata_kuliah (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kode TEXT UNIQUE NOT NULL,
                nama TEXT NOT NULL,
                sks INTEGER,
                dosen TEXT
            );
        """;

        String jadwal = """
            CREATE TABLE IF NOT EXISTS jadwal (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                mahasiswa_id INTEGER,
                judul TEXT NOT NULL,
                tipe TEXT CHECK(tipe IN ('kuliah', 'ujian', 'nongki')) NOT NULL,
                hari TEXT NOT NULL,
                jam_mulai TEXT NOT NULL,
                jam_selesai TEXT NOT NULL,
                lokasi TEXT,
                prioritas INTEGER DEFAULT 1,
                mata_kuliah_id INTEGER,
                FOREIGN KEY (mahasiswa_id) REFERENCES mahasiswa(id),
                FOREIGN KEY (mata_kuliah_id) REFERENCES mata_kuliah(id)
            );
        """;

        String history = """
            CREATE TABLE IF NOT EXISTS history_perubahan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                jadwal_id INTEGER,
                aksi TEXT CHECK(aksi IN ('tambah', 'ubah', 'hapus')),
                data_lama TEXT,
                waktu_perubahan TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (jadwal_id) REFERENCES jadwal(id)
            );
        """;

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(mahasiswa);
            stmt.execute(mataKuliah);
            stmt.execute(jadwal);
            stmt.execute(history);
            System.out.println("✅ Tabel database berhasil dibuat!");
        } catch (Exception e) {
            System.out.println("❌ Gagal membuat tabel: " + e.getMessage());
        }
    }
}