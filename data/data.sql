-- === 1. RESET SCHEMA (Hapus urut dari anak ke induk) ===
DROP TABLE IF EXISTS Nilai_Mahasiswa CASCADE;
DROP TABLE IF EXISTS Nilai_Kelompok CASCADE;
DROP TABLE IF EXISTS Anggota_Kelompok CASCADE; -- Junction Mahasiswa-Kelompok
DROP TABLE IF EXISTS Kelompok CASCADE;
DROP TABLE IF EXISTS Kegiatan CASCADE;
DROP TABLE IF EXISTS Jadwal CASCADE;
DROP TABLE IF EXISTS Tugas_Besar CASCADE;
DROP TABLE IF EXISTS Pengambilan_Kelas CASCADE; -- Junction Mahasiswa-Kelas
DROP TABLE IF EXISTS Kelas CASCADE;
DROP TABLE IF EXISTS Mata_Kuliah CASCADE;
DROP TABLE IF EXISTS Dosen CASCADE;
DROP TABLE IF EXISTS Mahasiswa CASCADE;

-- === 2. TABEL MASTER ===

-- Tabel Mahasiswa
CREATE TABLE Mahasiswa (
    NPM VARCHAR(20) PRIMARY KEY,
    Nama VARCHAR(100),
    Email VARCHAR(100)
);

-- Tabel Dosen
CREATE TABLE Dosen (
    NIK VARCHAR(20) PRIMARY KEY,
    Nama VARCHAR(100),
    Email VARCHAR(100)
);

-- Tabel Mata Kuliah (Hanya data dasar MK)
CREATE TABLE Mata_Kuliah (
    Kode_MK VARCHAR(20) PRIMARY KEY,
    Nama_MK VARCHAR(100)
);

-- === 3. TABEL TRANSAKSI UTAMA ===

-- Tabel Kelas
-- Menghubungkan Dosen dan Mata Kuliah
CREATE TABLE Kelas (
    idKelas SERIAL PRIMARY KEY,
    Nama_Kelas CHAR(1), -- A, B, C
    Semester VARCHAR(20), -- Ganjil/Genap
    Tahun_Ajaran VARCHAR(20),
    
    Kode_MK VARCHAR(20),
    NIK_Dosen VARCHAR(20),
    
    FOREIGN KEY (Kode_MK) REFERENCES Mata_Kuliah(Kode_MK),
    FOREIGN KEY (NIK_Dosen) REFERENCES Dosen(NIK)
);

-- Tabel Penghubung: Mahasiswa Mengambil Kelas (Many-to-Many)
CREATE TABLE Pengambilan_Kelas (
    id_pengambilan SERIAL PRIMARY KEY,
    NPM VARCHAR(20),
    idKelas INT,
    FOREIGN KEY (NPM) REFERENCES Mahasiswa(NPM),
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

INSERT INTO Mahasiswa (NPM, Nama, Email) VALUES 
('61823011', 'Bryan Rizzcaldi', '61823011@student.unpar.ac.id'),
('61823012', 'Filipo Lautan Samudra', '61823012@student.unpar.ac.id'),
('61823013', 'Andi Pratama', '61823013@student.unpar.ac.id'),
('61823014', 'Budi Santoso', '61823014@student.unpar.ac.id'),
('61823015', 'Citra Kirana', '61823015@student.unpar.ac.id'),
('61823016', 'Dewi Lestari', '61823016@student.unpar.ac.id'),
('61823017', 'Eko Kurniawan', '61823017@student.unpar.ac.id'),
('61823018', 'Fani Anggraini', '61823018@student.unpar.ac.id'),
('61823019', 'Gilang Ramadhan', '61823019@student.unpar.ac.id'),
('61823020', 'Haniifah Nur', '61823020@student.unpar.ac.id'),
('61823021', 'Irfan Bachdim', '61823021@student.unpar.ac.id'),
('61823022', 'Jessica Mila', '61823022@student.unpar.ac.id'),
('61823023', 'Kevin Sanjaya', '61823023@student.unpar.ac.id'),
('61823024', 'Luna Maya', '61823024@student.unpar.ac.id'),
('61823025', 'Muhammad Rizky', '61823025@student.unpar.ac.id'),
('61823026', 'Nadia Hutagalung', '61823026@student.unpar.ac.id'),
('61823027', 'Oscar Lawalata', '61823027@student.unpar.ac.id'),
('61823028', 'Putri Marino', '61823028@student.unpar.ac.id'),
('61823029', 'Qory Sandioriva', '61823029@student.unpar.ac.id'),
('61823030', 'Reza Rahadian', '61823030@student.unpar.ac.id');

-- B. Insert Data Dosen (Data Real dari Website UNPAR)
INSERT INTO Dosen (NIK, Nama, Email) VALUES 
('19800001', 'Aditya Bagoes Saputra', 'aditya.bagoes@unpar.ac.id'),
('19800002', 'Cecilia Esti Nugraheni', 'cecilia.esti@unpar.ac.id'),
('19800003', 'Elisati Hulu', 'elisati.hulu@unpar.ac.id'),
('19800004', 'Gede Karya', 'gede.karya@unpar.ac.id'),
('19800005', 'Husnul Hakim', 'husnul.hakim@unpar.ac.id'),
('19800006', 'Keenan Adiwijaya Leman', 'keenan.adiwijaya@unpar.ac.id'),
('19800007', 'Lionov', 'lionov@unpar.ac.id'),
('19800008', 'Luciana Abednego', 'luciana.abednego@unpar.ac.id'),
('19800009', 'Maria Veronica Claudia Muljana', 'maria.veronica@unpar.ac.id'),
('19800010', 'Mariskha Tri Adithia', 'mariskha.tri@unpar.ac.id'),
('19800011', 'Natalia', 'natalia@unpar.ac.id'),
('19800012', 'Pascal Alfadian Nugroho', 'pascal.alfadian@unpar.ac.id'),
('19800013', 'Raymond Chandra Putra', 'raymond.chandra@unpar.ac.id'),
('19800014', 'Rosa de Lima Endang Padmowati', 'rosa.delima@unpar.ac.id'),
('19800015', 'Vania Natali', 'vania.natali@unpar.ac.id'),
('19800016', 'Veronica Sri Moertini', 'veronica.sri@unpar.ac.id');