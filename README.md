# Skulin — Smart Kuliah Utility for Learning & Integrated Scheduling

Skulin adalah aplikasi web manajemen jadwal untuk mahasiswa yang dibangun menggunakan Java Spring Boot. Aplikasi ini memungkinkan mahasiswa mengelola jadwal kuliah, ujian, dan kegiatan santai (nongki) dalam satu tampilan terpadu, dilengkapi dengan fitur deteksi konflik jadwal otomatis dan rekomendasi waktu kosong.

---

## Daftar Isi

1. [Prasyarat](#1-prasyarat)
2. [Instalasi Java](#2-instalasi-java)
3. [Setup Project Spring Boot](#3-setup-project-spring-boot)
4. [Struktur Project](#4-struktur-project)
5. [Konfigurasi Database](#5-konfigurasi-database)
6. [Membuat Model](#6-membuat-model-jadwaljava)
7. [Membuat Repository](#7-membuat-repository)
8. [Membuat Controller](#8-membuat-controller)
9. [Membuat Tampilan (Frontend)](#9-membuat-tampilan-frontend)
10. [Menjalankan Aplikasi](#10-menjalankan-aplikasi)
11. [Fitur Aplikasi](#11-fitur-aplikasi)
12. [Push ke GitHub](#12-push-ke-github)
13. [Migrasi Database ke Neon (PostgreSQL)](#13-migrasi-database-ke-neon-postgresql)
14. [Deploy Publik dengan ngrok](#14-deploy-publik-dengan-ngrok)

---

## 1. Prasyarat

Sebelum memulai, pastikan perangkat kamu sudah terinstall:

| Kebutuhan | Versi | Keterangan |
|---|---|---|
| Java JDK | 21 atau lebih | Runtime untuk menjalankan Java |
| VS Code | Terbaru | Editor kode |
| Extension Pack for Java | Terbaru | Plugin Java di VS Code |
| Browser | Chrome/Firefox | Untuk membuka aplikasi |
| Git | Terbaru | Untuk push ke GitHub |

---

## 2. Instalasi Java

### Download Java JDK

1. Buka https://www.oracle.com/java/technologies/downloads/
2. Pilih versi **JDK 21** (atau yang terbaru)
3. Pilih sistem operasi sesuai perangkat kamu (Windows/Mac/Linux)
4. Download dan install

### Verifikasi Instalasi

Buka terminal atau Command Prompt, lalu ketik:

```bash
java -version
```

Jika berhasil, akan muncul output seperti:
```
java version "21.0.x" ...
```

### Set JAVA_HOME (Windows)

Jika perintah `mvnw` tidak bisa berjalan, set JAVA_HOME di terminal:

```powershell
$env:JAVA_HOME = "C:\path\ke\jdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

Untuk menyimpan permanen:

```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\path\ke\jdk", "User")
```

---

## 3. Setup Project Spring Boot

### Buat Project via Spring Initializr

1. Buka https://start.spring.io di browser
2. Isi konfigurasi berikut:

| Field | Nilai |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.5.x (versi stabil terbaru) |
| Group | com.skulin |
| Artifact | skulin |
| Packaging | Jar |
| Java | 21 |

3. Klik **ADD DEPENDENCIES**, tambahkan:
   - **Spring Web** — untuk membuat web server
   - **Thymeleaf** — untuk template HTML

4. Klik **GENERATE** → download file ZIP
5. Extract ZIP ke folder project kamu, misalnya: `C:\Algoritma\Skulin\`
6. Buka folder tersebut di VS Code: **File → Open Folder**

### Jalankan Pertama Kali

Buka terminal di VS Code (`Ctrl + \``) lalu ketik:

```powershell
.\mvnw spring-boot:run
```

Tunggu hingga muncul:
```
Started SkulinApplication in X.XXX seconds
```

Buka browser dan akses: **http://localhost:8080**

---

## 4. Struktur Project

Setelah project dibuat, struktur folder akan terlihat seperti ini:

```
Skulin/
├── src/
│   └── main/
│       ├── java/com/skulin/skulin/
│       │   ├── controller/
│       │   │   └── JadwalController.java
│       │   ├── model/
│       │   │   └── Jadwal.java
│       │   ├── repository/
│       │   │   └── JadwalRepository.java
│       │   └── SkulinApplication.java
│       └── resources/
│           ├── static/
│           │   └── css/
│           │       └── jadwal.css
│           ├── templates/
│           │   └── jadwal/
│           │       ├── index.html
│           │       └── edit.html
│           └── application.properties
├── pom.xml
└── mvnw
```

---

## 5. Konfigurasi Database

Skulin menggunakan **SQLite** sebagai database — ringan, tidak perlu install server terpisah, dan datanya tersimpan dalam satu file `.db`.

### Tambah Dependency SQLite ke pom.xml

Buka `pom.xml`, cari bagian `<dependencies>`, lalu tambahkan:

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>

<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>
```

### Konfigurasi application.properties

Buka `src/main/resources/application.properties` dan isi dengan:

```properties
spring.datasource.url=jdbc:sqlite:skulin.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.thymeleaf.cache=false
```

Penjelasan:
- `skulin.db` — nama file database yang akan dibuat otomatis
- `ddl-auto=update` — Hibernate otomatis membuat/mengupdate tabel sesuai model
- `thymeleaf.cache=false` — perubahan HTML langsung terlihat tanpa restart

---

## 6. Membuat Model (Jadwal.java)

Model adalah representasi data yang akan disimpan ke database.

Buat file `src/main/java/com/skulin/skulin/model/Jadwal.java`:

```java
package com.skulin.skulin.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity
@Table(name = "jadwal")
public class Jadwal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mataPelajaran;
    private String tipe;           // kuliah, ujian, nongki
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

    // Getters & Setters
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
```

---

## 7. Membuat Repository

Repository adalah interface yang mengurus komunikasi antara aplikasi dan database.

Buat file `src/main/java/com/skulin/skulin/repository/JadwalRepository.java`:

```java
package com.skulin.skulin.repository;

import com.skulin.skulin.model.Jadwal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JadwalRepository extends JpaRepository<Jadwal, Long> {
}
```

Cukup segini — Spring Boot secara otomatis menyediakan operasi CRUD (Create, Read, Update, Delete) tanpa perlu menulis query SQL.

---

## 8. Membuat Controller

Controller mengatur logika aplikasi: menerima request dari browser, mengambil data dari database, dan mengirim data ke tampilan HTML.

Buat file `src/main/java/com/skulin/skulin/controller/JadwalController.java`:

```java
package com.skulin.skulin.controller;

import com.skulin.skulin.model.Jadwal;
import com.skulin.skulin.repository.JadwalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jadwal")
public class JadwalController {

    @Autowired
    private JadwalRepository jadwalRepository;

    @GetMapping
    public String index(Model model,
                        @RequestParam(required = false) String tanggal,
                        @RequestParam(defaultValue = "tanggal") String sort) {

        var semua = jadwalRepository.findAll();

        // Filter by tanggal jika ada
        if (tanggal != null && !tanggal.isEmpty()) {
            LocalDate tgl = LocalDate.parse(tanggal);
            semua = semua.stream()
                    .filter(j -> tgl.equals(j.getTanggal()))
                    .collect(Collectors.toList());
        }

        // Sorting
        semua.sort((a, b) -> {
            if (sort.equals("tanggal")) {
                if (a.getTanggal() == null) return 1;
                if (b.getTanggal() == null) return -1;
                int tgl = a.getTanggal().compareTo(b.getTanggal());
                if (tgl != 0) return tgl;
                return a.getJamMulai().compareTo(b.getJamMulai());
            } else if (sort.equals("jam")) {
                return a.getJamMulai().compareTo(b.getJamMulai());
            } else if (sort.equals("matkul")) {
                return a.getMataPelajaran().compareToIgnoreCase(b.getMataPelajaran());
            }
            return 0;
        });

        var all = jadwalRepository.findAll();
        model.addAttribute("jadwalList", semua);
        model.addAttribute("jadwal", new Jadwal());
        model.addAttribute("totalJadwal", all.size());
        model.addAttribute("totalKuliah", all.stream().filter(j -> "kuliah".equals(j.getTipe())).count());
        model.addAttribute("totalUjian",  all.stream().filter(j -> "ujian".equals(j.getTipe())).count());
        model.addAttribute("totalNongki", all.stream().filter(j -> "nongki".equals(j.getTipe())).count());
        model.addAttribute("filterTanggal", tanggal);
        model.addAttribute("currentSort", sort);
        return "jadwal/index";
    }

    @PostMapping("/tambah")
    public String tambah(@ModelAttribute Jadwal jadwal) {
        jadwalRepository.save(jadwal);
        return "redirect:/jadwal?success=1";
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

    @GetMapping("/hapus/{id}")
    public String hapus(@PathVariable Long id) {
        jadwalRepository.deleteById(id);
        return "redirect:/jadwal?hapus=1";
    }
}
```

---

## 9. Membuat Tampilan (Frontend)

### Buat Folder dan File HTML

Buat struktur folder berikut di dalam `src/main/resources/`:

```
resources/
├── static/
│   └── css/
│       └── jadwal.css
└── templates/
    └── jadwal/
        ├── index.html
        └── edit.html
```

### File CSS (jadwal.css)

File CSS mengatur tampilan visual aplikasi — warna, font, layout, responsivitas.

Letakkan di `src/main/resources/static/css/jadwal.css`. File ini mencakup:

- **Variabel CSS** — warna, ukuran, bayangan yang bisa diubah di satu tempat
- **Layout sidebar** — navigasi di sisi kiri halaman
- **Topbar** — header halaman dengan tombol tambah jadwal
- **Stats cards** — kartu statistik total jadwal
- **Tabel jadwal** — tampilan data jadwal
- **Modal form** — popup untuk tambah/edit jadwal
- **Responsive design** — tampilan menyesuaikan layar HP dan tablet
- **Hamburger menu** — tombol buka/tutup sidebar di mobile

### File HTML Utama (index.html)

Letakkan di `src/main/resources/templates/jadwal/index.html`. File ini menggunakan **Thymeleaf** (template engine Java) untuk menampilkan data dari database secara dinamis.

Komponen utama:
- Sidebar navigasi dengan menu Jadwal, Rekomendasi Nongki, dan Cek Konflik
- Dashboard statistik (total jadwal, kuliah, ujian, nongki)
- Filter tanggal dan tombol sort (Tanggal / Jam / A-Z)
- Tabel jadwal dengan kolom Mata Kuliah, Tipe, Tanggal & Hari, Jam, Kelas/Lokasi
- Tombol Edit dan Hapus di setiap baris
- Modal popup untuk tambah jadwal baru
- Modal rekomendasi nongki (mencari slot waktu kosong otomatis)
- Bottom navigation untuk tampilan mobile

### File HTML Edit (edit.html)

Letakkan di `src/main/resources/templates/jadwal/edit.html`. Halaman ini menampilkan form yang sudah terisi data jadwal yang dipilih untuk diedit.

---

## 10. Menjalankan Aplikasi

### Pertama Kali

```powershell
.\mvnw spring-boot:run
```

### Jika JAVA_HOME tidak terdefinisi

```powershell
$env:JAVA_HOME = "C:\path\ke\jdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw spring-boot:run
```

### Akses Aplikasi

Setelah server jalan, buka browser dan akses:

```
http://localhost:8080/jadwal
```

### Menghentikan Server

Tekan `Ctrl + C` di terminal.

---

## 11. Fitur Aplikasi

### Manajemen Jadwal (CRUD)

| Fitur | Cara Pakai |
|---|---|
| Tambah jadwal | Klik tombol "+ Tambah Jadwal" di pojok kanan atas |
| Edit jadwal | Klik tombol "Edit" di baris jadwal yang ingin diubah |
| Hapus jadwal | Klik tombol "Hapus", konfirmasi di dialog |
| Lihat semua | Otomatis tampil saat halaman dibuka |

### Filter dan Sorting

- **Filter by tanggal** — pilih tanggal di input date, klik "Filter"
- **Reset filter** — klik tombol "Reset" untuk kembali ke semua jadwal
- **Sort by Tanggal** — urutkan jadwal dari tanggal paling awal
- **Sort by Jam** — urutkan berdasarkan jam mulai
- **Sort by A-Z** — urutkan berdasarkan nama mata kuliah

### Rekomendasi Nongki (Greedy Algorithm)

Fitur ini mencari slot waktu kosong di antara jadwal yang sudah ada:

1. Klik "Rekomendasi Nongki" di sidebar
2. Pilih hari yang ingin dicek
3. Sistem otomatis menampilkan jam-jam kosong yang tersedia

### Cek Konflik Jadwal

Mendeteksi apakah ada jadwal yang waktunya tumpang tindih:

1. Klik "Cek Konflik" di sidebar
2. Jika ada konflik, akan muncul alert berisi detail jadwal yang bentrok
3. Jika tidak ada konflik, muncul notifikasi sukses di pojok bawah

### Tampilan Responsif

- **Desktop** — sidebar di kiri, konten di kanan
- **Tablet** — sidebar lebih kecil, stats 2 kolom
- **Mobile** — sidebar tersembunyi, bisa dibuka dengan tombol hamburger (☰), tersedia bottom navigation

---

## 12. Push ke GitHub

### Persiapan

1. Pastikan Git sudah terinstall: `git --version`
2. Buat akun di https://github.com jika belum punya
3. Buat repository baru di GitHub (klik "+ New repository")

### Langkah-langkah

```bash
# 1. Inisialisasi Git di folder project
git init

# 2. Tambah semua file
git add .

# 3. Buat commit pertama
git commit -m "Initial commit: Skulin web app"

# 4. Hubungkan ke repository GitHub (ganti URL sesuai milikmu)
git remote add origin https://github.com/username/skulin.git

# 5. Push ke GitHub
git push -u origin main
```

### File .gitignore

Pastikan file `.gitignore` sudah ada dan berisi:

```
target/
*.db
.mvn/wrapper/maven-wrapper.jar
```

Ini mencegah file database dan hasil build ikut ter-upload.

---

## 13. Migrasi Database ke Neon (PostgreSQL)

Untuk kebutuhan deployment, database dipindah dari SQLite (lokal) ke **PostgreSQL** yang di-hosting di **Neon** — layanan serverless PostgreSQL gratis yang bisa diakses dari mana saja tanpa perlu install server database sendiri.

### Buat Project di Neon

1. Buka https://neon.tech, lalu daftar/login (bisa langsung pakai akun GitHub)
2. Klik **Create a project**
3. Isi nama project, misalnya `skulin-db`
4. Pilih region terdekat, misalnya **Singapore**, biar koneksinya lebih cepat
5. Klik **Create Project**, tunggu sampai database selesai dibuat

### Ambil Connection String

1. Setelah project jadi, buka tab **Connection Details** di dashboard Neon
2. Neon akan menampilkan connection string dengan format seperti ini:

```
postgresql://<username>:<password>@<host>.neon.tech/<database>?sslmode=require
```

3. Copy connection string ini, nanti dipakai di `application.properties`

### Tambah Dependency PostgreSQL ke pom.xml

Buka `pom.xml`, cari bagian `<dependencies>`, lalu tambahkan (dan boleh hapus/comment dependency SQLite kalau sudah tidak dipakai lagi):

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Update application.properties

Ganti isi `src/main/resources/application.properties` dari konfigurasi SQLite ke PostgreSQL Neon:

```properties
spring.datasource.url=jdbc:postgresql://<host>.neon.tech/<database>?sslmode=require
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.thymeleaf.cache=false
```

Penjelasan:
- `ddl-auto=update` — Hibernate otomatis membuat tabel `jadwal` di Neon sesuai model `Jadwal.java`, tidak perlu bikin skrip SQL manual
- `sslmode=require` — wajib ada karena Neon mengharuskan koneksi terenkripsi (SSL)

### Jalankan dan Cek Koneksi

```powershell
.\mvnw spring-boot:run
```

Kalau berhasil, di terminal akan muncul log Hibernate yang membuat tabel (`create table jadwal ...`), dan di dashboard Neon (tab **Tables**) tabel `jadwal` akan langsung muncul.

> **Catatan:** Neon otomatis "tidur" (auto-suspend) kalau database tidak dipakai, dan akan menyala lagi otomatis begitu ada koneksi baru masuk — jadi tidak perlu khawatir soal biaya server nyala terus-menerus.

> **Penting:** Jangan commit `application.properties` yang berisi username/password Neon ke repository publik. Masukkan file ini ke `.gitignore`, atau pindahkan kredensialnya ke environment variable.

---

## 14. Deploy Publik dengan ngrok

Setelah database sudah pindah ke Neon, aplikasi masih berjalan di `localhost` yang cuma bisa diakses dari komputer sendiri. Supaya orang lain (dosen, teman, atau siapa pun) bisa mengakses aplikasi ini lewat internet tanpa perlu hosting berbayar, dipakai **ngrok** — tool yang bikin "terowongan" dari localhost ke alamat publik.

### Install ngrok

1. Daftar/login di https://ngrok.com
2. Download ngrok sesuai OS (Windows/Mac/Linux) di halaman **Setup & Installation**
3. Extract file ngrok ke folder yang gampang diakses, misalnya `C:\ngrok\`

### Hubungkan ngrok dengan Akun

Copy **Authtoken** dari dashboard ngrok, lalu jalankan sekali saja di terminal:

```bash
ngrok config add-authtoken <authtoken_kamu>
```

### Jalankan Aplikasi Spring Boot

Pastikan aplikasi masih jalan di port 8080 (biarkan terminal ini tetap terbuka):

```powershell
.\mvnw spring-boot:run
```

### Buka Tunnel ke Port 8080

Buka terminal baru (jangan tutup terminal Spring Boot), lalu jalankan:

```bash
ngrok http 8080
```

ngrok akan menampilkan output seperti ini:

```
Forwarding    https://xxxx-xxx-xxx-xxx-xx.ngrok-free.app -> http://localhost:8080
```

### Akses dari Internet

Buka URL di baris **Forwarding** (yang `https://...ngrok-free.app`) dari perangkat mana pun — HP, laptop lain, dll — lalu tambahkan `/jadwal` di belakangnya:

```
https://xxxx-xxx-xxx-xxx-xx.ngrok-free.app/jadwal
```

Aplikasi Skulin sekarang bisa diakses siapa pun yang punya link ini, selama:
- Aplikasi Spring Boot masih jalan di komputer kamu
- Terminal ngrok masih terbuka

### Catatan Penting

| Hal | Keterangan |
|---|---|
| Link berubah-ubah | Di akun ngrok gratis, link publik akan berbeda setiap kali `ngrok http 8080` dijalankan ulang |
| Harus tetap online | ngrok cuma meneruskan koneksi, bukan hosting — laptop/PC kamu harus tetap menyala dan terkoneksi internet |
| Untuk demo/testing | Cocok untuk demo ke dosen atau teman, bukan untuk production jangka panjang |
| Hosting permanen | Kalau butuh link yang permanen, bisa pertimbangkan Railway atau Render di kemudian hari |

---



| Komponen | Teknologi |
|---|---|
| Backend | Java 25 + Spring Boot 3.5 |
| Database (lokal) | SQLite via Hibernate JPA |
| Database (production) | PostgreSQL via Neon (serverless) |
| Publikasi/Tunneling | ngrok |
| Frontend | HTML5 + CSS3 + JavaScript |
| Template Engine | Thymeleaf |
| Build Tool | Maven (via mvnw) |
| Icon | Font Awesome 6 |
| Font | Inter (Google Fonts) |

---

## Informasi Project

**Nama Aplikasi:** Skulin : Smart Kuliah Utility for Learning & Integrated Scheduling

**Dibuat oleh:** Abieza Febrian Mahardika (714250049)

**Dosen Pengampu:** MOHAMAD NURKAMAL FAUZAN, S.T., M.T., SFPC.

**Mata Kuliah:** Algoritma

**Tahun:** 2025/2026
