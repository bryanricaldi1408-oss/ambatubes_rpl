DROP TABLE IF EXISTS Nilai_Mahasiswa CASCADE;
DROP TABLE IF EXISTS Nilai_Kelompok CASCADE;
DROP TABLE IF EXISTS Anggota_Kelompok CASCADE;
DROP TABLE IF EXISTS Kegiatan CASCADE;
DROP TABLE IF EXISTS Kelompok CASCADE;
DROP TABLE IF EXISTS Jadwal CASCADE;
DROP TABLE IF EXISTS Pengambilan_Kelas CASCADE;
DROP TABLE IF EXISTS Pengajaran_Kelas CASCADE;
DROP TABLE IF EXISTS Tugas_Besar CASCADE;
DROP TABLE IF EXISTS Kelas CASCADE;
DROP TABLE IF EXISTS DosenCredentials CASCADE;
DROP TABLE IF EXISTS MahasiswaCredentials CASCADE;
DROP TABLE IF EXISTS Mata_Kuliah CASCADE;
DROP TABLE IF EXISTS Dosen CASCADE;
DROP TABLE IF EXISTS Mahasiswa CASCADE;
DROP TABLE IF EXISTS admins CASCADE;

-- Create table for admins
CREATE TABLE admins (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Tabel Mahasiswa
CREATE TABLE Mahasiswa (
    NPM VARCHAR(20) PRIMARY KEY,
    Nama VARCHAR(100)
);

CREATE TABLE MahasiswaCredentials (
    NPM VARCHAR(20) PRIMARY KEY,
    Email VARCHAR(100),
	Password VARCHAR(20)
);

-- Tabel Dosen
CREATE TABLE Dosen (
    NIK VARCHAR(20) PRIMARY KEY,
    Nama VARCHAR(100)
);

CREATE TABLE DosenCredentials (
    NIK VARCHAR(20) PRIMARY KEY,
    Email VARCHAR(100),
	Password VARCHAR(20)
);

CREATE TABLE Mata_Kuliah (
    Kode_MK VARCHAR(20) PRIMARY KEY,
    Nama_MK VARCHAR(100)
);

CREATE TABLE Kelas (
    idKelas SERIAL PRIMARY KEY,
    Nama_Kelas VARCHAR(1), -- A, B, C
    Semester VARCHAR(20),
    
    Kode_MK VARCHAR(20),
    NIK VARCHAR(20),
    
    FOREIGN KEY (Kode_MK) REFERENCES Mata_Kuliah(Kode_MK),
    FOREIGN KEY (NIK) REFERENCES Dosen(NIK)
);

-- Tabel Penghubung: Mahasiswa Mengambil Kelas (Many-to-Many)
CREATE TABLE Pengambilan_Kelas (
    id_pengambilan SERIAL PRIMARY KEY,
    NPM VARCHAR(20),
    idKelas INT,
    FOREIGN KEY (NPM) REFERENCES Mahasiswa(NPM),
    FOREIGN KEY (idKelas) REFERENCES Kelas(idKelas)
);

-- Tabel Penghubung: Dosen Mengajar Kelas (Many-to-Many)
CREATE TABLE Pengajaran_Kelas (
    id_pengajaran SERIAL PRIMARY KEY,
    NIK VARCHAR(20),
    idKelas INT,
    FOREIGN KEY (NIK) REFERENCES Dosen(NIK),
    FOREIGN KEY (idKelas) REFERENCES Kelas(idKelas)
);

-- Tabel Tugas Besar
CREATE TABLE Tugas_Besar (
    idTubes SERIAL PRIMARY KEY,
    Nama_Tugas TEXT,
    Deskripsi TEXT, -- Menggunakan TEXT sesuai request
    Tanggal_Dibuat DATE DEFAULT CURRENT_DATE,
    
    idKelas INT,
    FOREIGN KEY (idKelas) REFERENCES Kelas(idKelas)
);

-- Tabel Jadwal
-- Terhubung ke Tugas Besar (Sesuai ERD)
CREATE TABLE Jadwal (
    idJadwal SERIAL PRIMARY KEY,
    Deadline TIMESTAMP WITHOUT TIME ZONE,
    
    idTubes INT,
    FOREIGN KEY (idTubes) REFERENCES Tugas_Besar(idTubes)
);

-- Tabel Kegiatan
-- Terhubung ke Jadwal
CREATE TABLE Kegiatan (
    idKegiatan SERIAL PRIMARY KEY,
    Nama_Kegiatan VARCHAR(100),
    
    idJadwal INT,
    FOREIGN KEY (idJadwal) REFERENCES Jadwal(idJadwal)
);

-- === 4. TABEL KELOMPOK & PENILAIAN ===

-- Tabel Kelompok
-- Terhubung ke Tugas Besar (Sesuai ERD)
CREATE TABLE Kelompok (
    idKelompok SERIAL PRIMARY KEY,
    Nama_Kelompok CHAR(1), -- A, B, C (Manual input atau Trigger)
    Jumlah_Anggota INT,
    
    idTubes INT,
    FOREIGN KEY (idTubes) REFERENCES Tugas_Besar(idTubes)
);

-- Tabel Penghubung: Anggota Kelompok (Mahasiswa masuk Kelompok)
CREATE TABLE Anggota_Kelompok (
    id_anggota SERIAL PRIMARY KEY,
    idKelompok INT,
    NPM VARCHAR(20),
    
    FOREIGN KEY (idKelompok) REFERENCES Kelompok(idKelompok),
    FOREIGN KEY (NPM) REFERENCES Mahasiswa(NPM),
    UNIQUE(idKelompok, NPM) -- Mencegah 1 orang masuk 2x di kelompok sama
);

-- Tabel Nilai Kelompok
-- Dinilai berdasarkan Kegiatan tertentu
CREATE TABLE Nilai_Kelompok (
    idNilaiKelompok SERIAL PRIMARY KEY,
    Nilai DECIMAL(5, 2),
    Keterangan TEXT,
    
    idKelompok INT,
    idKegiatan INT,
    FOREIGN KEY (idKelompok) REFERENCES Kelompok(idKelompok),
    FOREIGN KEY (idKegiatan) REFERENCES Kegiatan(idKegiatan)
);

-- Tabel Nilai Mahasiswa (Yang terlewat di skripmu)
-- Nilai individu per anggota (turunan dari Nilai Kelompok)
CREATE TABLE Nilai_Mahasiswa (
    idNilaiMahasiswa SERIAL PRIMARY KEY,
    Nilai DECIMAL(5, 2),
    Keterangan TEXT,
    
    idNilaiKelompok INT,
    NPM VARCHAR(20),
    FOREIGN KEY (idNilaiKelompok) REFERENCES Nilai_Kelompok(idNilaiKelompok),
    FOREIGN KEY (NPM) REFERENCES Mahasiswa(NPM)
);


-- Insert dummy data

TRUNCATE TABLE Nilai_Mahasiswa, Nilai_Kelompok, Anggota_Kelompok, Kelompok, Kegiatan, Jadwal, Tugas_Besar, Pengambilan_Kelas, Pengajaran_Kelas, Kelas, Mata_Kuliah, Dosen, Mahasiswa, DosenCredentials, MahasiswaCredentials, admins RESTART IDENTITY CASCADE;

INSERT INTO admins (email, password) VALUES 
('admin@ambatubes.com', 'admin123'),
('a@gmail.com', 'aaa'),
('user@example.com', 'password123');

INSERT INTO Mahasiswa (NPM, Nama) VALUES 
('61823011', 'Bryan Rizzcaldi'),
('61823012', 'Filipo Lautan'),
('61823013', 'Andi Pratama'),
('61823014', 'Budi Santoso'),
('61823015', 'Citra Kirana');

INSERT INTO MahasiswaCredentials (NPM, Email, Password) VALUES 
('61823011', '61823011@student.unpar.ac.id', 'aaa'),
('61823012', '61823012@student.unpar.ac.id', 'aaa'),
('61823013', '61823013@student.unpar.ac.id', 'aaa'),
('61823014', '61823014@student.unpar.ac.id', 'aaa'),
('61823015', '61823015@student.unpar.ac.id', 'aaa');

INSERT INTO Dosen (NIK, Nama) VALUES 
('19800001', 'Aditya Bagoes Saputra'),
('19800002', 'Cecilia Esti Nugraheni'),
('19800003', 'Elisati Hulu'),
('19800004', 'Gede Karya'),
('19800005', 'Husnul Hakim');

INSERT INTO DosenCredentials (NIK, Email, Password) VALUES 
('19800001', 'aditya.bagoes@unpar.ac.id', 'aaa'),
('19800002', 'cecilia.esti@unpar.ac.id', 'aaa'),
('19800003', 'elisati.hulu@unpar.ac.id', 'aaa'),
('19800004', 'gede.karya@unpar.ac.id', 'aaa'),
('19800005', 'husnul.hakim@unpar.ac.id', 'aaa');

INSERT INTO Mata_Kuliah (Kode_MK, Nama_MK) VALUES 
('11111111', 'Operating System'),
('22222222', 'Artifical Intelligence'),
('33333333', 'Desain Antar Grafis');

INSERT INTO Kelas (Nama_Kelas, Semester, Kode_MK, NIK) VALUES 
('A', 'Ganjil 2025/2026', '11111111', '19800001'),
('B', 'Genap 2025/2026', '11111111', '19800001'),
('C', 'Pendek 2025/2026', '11111111', '19800001'),
('A', 'Ganjil 2025/2026', '22222222', '19800002'),
('B', 'Ganjil 1945/1946', '22222222', '19800002'),
('A', 'Ganjil 1000/1001', '33333333', '19800003');

INSERT INTO Pengambilan_Kelas(NPM, idKelas) VALUES 
('61823011', 1),
('61823012', 1),
('61823013', 1),
('61823014', 2),
('61823015', 3),
('61823011', 4),
('61823012', 4),
('61823014', 5),
('61823013', 6);

INSERT INTO Pengajaran_Kelas(NIK, idKelas) VALUES 
('19800001', 1),
('19800001', 2),
('19800001', 3),
('19800002', 4),
('19800002', 5),
('19800003', 6),
('19800004', 1),
('19800005', 1);

INSERT INTO Tugas_Besar (Nama_Tugas, Deskripsi, Tanggal_Dibuat, idKelas) 
VALUES 
-- ==========================================
-- Data untuk Kelas ID 1 (Operating System)
-- ==========================================
(
    'Tubes 1: Simulasi CPU Scheduling', 
    'Tugas ini bertujuan untuk membandingkan performa algoritma penjadwalan CPU. Mahasiswa diminta untuk mengimplementasikan algoritma Round Robin (RR), First-Come First-Served (FCFS), dan Shortest Job First (SJF) menggunakan bahasa C. Program harus dapat membaca input berupa file teks yang berisi daftar proses beserta arrival time dan burst time-nya. Output yang diharapkan meliputi visualisasi Gantt Chart sederhana di terminal serta perhitungan rata-rata Waiting Time dan Turnaround Time untuk setiap algoritma. Laporan analisis perbandingan performa wajib dikumpulkan dalam format PDF beserta source code.', 
    '2024-10-15', 
    1
),
(
    'Tubes 2: File System User Space (FUSE)', 
    'Pada tugas besar ini, mahasiswa diminta membuat file system sederhana yang berjalan di user space menggunakan library FUSE pada lingkungan Linux. File system harus memiliki kemampuan dasar seperti membuat direktori (mkdir), membaca file (read), menulis file (write), dan menghapus file (unlink). Tantangan tambahan meliputi enkripsi isi file secara otomatis saat disimpan (At-Rest Encryption) dan manajemen hak akses user. Wajib melakukan demo program ke asisten laboratorium dan menjelaskan bagaimana struktur data i-node diimplementasikan dalam memori.', 
    '2024-11-20', 
    1
),

-- ==========================================
-- Data untuk Kelas ID 2 (Web Programming)
-- ==========================================
(
    'Tubes Akhir: Fullstack E-Commerce', 
    'Rancang dan bangun aplikasi web E-Commerce yang aman dan skalabel. Backend wajib menggunakan Java Spring Boot dengan arsitektur MVC, sedangkan Frontend bebas memilih antara Thymeleaf atau React.js. Fitur wajib mencakup: (1) Autentikasi & Otorisasi User (Admin/Customer) menggunakan Spring Security, (2) Katalog Produk dengan fitur pencarian dan filter, (3) Shopping Cart dan Checkout, serta (4) Integrasi Payment Gateway Dummy. Database harus menggunakan PostgreSQL dengan relasi tabel yang ternormalisasi. Proyek harus di-deploy ke cloud service (seperti Heroku/Render/AWS) untuk penilaian akhir.', 
    '2024-12-05', 
    2
),

-- ==========================================
-- Data untuk Kelas ID 4 (Artificial Intelligence)
-- ==========================================
(
    'Explorasi Algoritma Pencarian Jalur (A*)', 
    'Implementasikan algoritma A* (A-Star) untuk menyelesaikan permasalahan pencarian jalur terpendek (Shortest Path Finding) pada peta kota Bandung yang disederhanakan menjadi graf berbobot. Mahasiswa harus mendefinisikan fungsi heuristik yang tepat (misalnya Euclidean Distance atau Manhattan Distance) untuk mengoptimalkan pencarian. Tugas mencakup pembuatan visualisasi grafis yang menunjukkan node yang dikunjungi (visited nodes) dan jalur final yang ditemukan. Bandingkan efisiensi algoritma A* yang dibuat dengan algoritma Dijkstra standar dalam hal waktu komputasi dan jumlah node yang diekspansi.', 
    '2024-11-01', 
    4
),
(
    'Final Project: Klasifikasi Citra Medis', 
    'Kembangkan model Deep Learning menggunakan Convolutional Neural Network (CNN) untuk mengklasifikasikan citra rontgen paru-paru menjadi tiga kategori: Normal, Viral Pneumonia, dan COVID-19. Dataset disediakan di e-learning dalam format JPG. Tahapan pengerjaan meliputi: (1) Preprocessing data (resize, normalisasi, augmentasi), (2) Perancangan arsitektur CNN (layer konvolusi, pooling, dense), (3) Pelatihan model dengan validasi silang (cross-validation), dan (4) Evaluasi performa menggunakan confusion matrix, akurasi, presisi, dan recall. Laporan akhir berupa paper format IEEE.', 
    '2024-12-10', 
    4
);


INSERT INTO Jadwal (Deadline, idTubes) VALUES 
('2024-10-25 23:59:00', 1), -- idJadwal 1
('2024-11-10 23:59:00', 1), -- idJadwal 2
('2024-11-30 23:59:00', 1); -- idJadwal 3


INSERT INTO Kegiatan (Nama_Kegiatan, idJadwal) VALUES 
('Presentasi Proposal (Bab 1)', 1), -- idKegiatan 1 (Link ke Jadwal 1)
('Progress Report 50%', 2),        -- idKegiatan 2 (Link ke Jadwal 2)
('Demo Aplikasi Final & Laporan', 3); -- idKegiatan 3 (Link ke Jadwal 3)

INSERT INTO Kelompok (Nama_Kelompok, Jumlah_Anggota, idTubes) VALUES 
('A', 5, 1), -- ID: 1
('B', 3, 1);


INSERT INTO Anggota_Kelompok (idKelompok, NPM) VALUES 
(1, '61823012'), -- Filipo
(1, '61823013'), -- Andi
(1, '61823014'); -- Budi


INSERT INTO Nilai_Kelompok (Nilai, Keterangan, idKelompok, idKegiatan) VALUES 
(85.00, 'Presentasi lancar, namun slide kurang visual.', 1, 1);

INSERT INTO Nilai_Mahasiswa (Nilai, Keterangan, idNilaiKelompok, NPM) VALUES 
(85.00, 'Sesuai nilai kelompok', 1, '61823011'), -- Nilai buat Bryan
(85.00, 'Sesuai nilai kelompok', 1, '61823012'),
(80.00, 'Kurang aktif saat sesi tanya jawab', 1, '61823013');

INSERT INTO Nilai_Kelompok (Nilai, Keterangan, idKelompok, idKegiatan) VALUES 
(90.00, 'Progress sangat baik, fitur utama sudah jalan.', 1, 2);

INSERT INTO Nilai_Mahasiswa (Nilai, Keterangan, idNilaiKelompok, NPM) VALUES 
(90.00, 'Codingan rapi', 2, '61823011');


select * from Dosen;
select * from Pengajaran_Kelas;
select * from Mahasiswa;
select * from Pengambilan_Kelas;



