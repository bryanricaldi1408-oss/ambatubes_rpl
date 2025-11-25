drop table if exists admins;
DROP TABLE IF EXISTS Nilai_Mahasiswa CASCADE;
DROP TABLE IF EXISTS Nilai_Kelompok CASCADE;
DROP TABLE IF EXISTS Anggota_Kelompok CASCADE; -- Junction Mahasiswa-Kelompok
DROP TABLE IF EXISTS Kelompok CASCADE;
DROP TABLE IF EXISTS Kegiatan CASCADE;
DROP TABLE IF EXISTS Jadwal CASCADE;
DROP TABLE IF EXISTS Tugas_Besar CASCADE;
DROP TABLE IF EXISTS Pengambilan_Kelas CASCADE; -- Junction Mahasiswa-Kelas
DROP TABLE IF EXISTS Pengajaran_Kelas CASCADE; -- Junction Dosen-Kelas
DROP TABLE IF EXISTS Kelas CASCADE;
DROP TABLE IF EXISTS Mata_Kuliah CASCADE;
DROP TABLE IF EXISTS Dosen CASCADE;
DROP TABLE IF EXISTS Mahasiswa CASCADE;
DROP TABLE IF EXISTS DosenCredentials CASCADE;
DROP TABLE IF EXISTS MahasiswaCredentials CASCADE;

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

select * from Dosen;
select * from Pengajaran_Kelas;
select * from Mahasiswa;
select * from Pengambilan_Kelas;



