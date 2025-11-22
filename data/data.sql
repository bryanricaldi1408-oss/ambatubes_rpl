-- Hapus tabel jika sudah ada (Opsional, agar bersih saat di-run ulang)
DROP TABLE IF EXISTS Tugas_Besar;
DROP TABLE IF EXISTS Mata_Kuliah;
DROP TABLE IF EXISTS Dosen;
DROP TABLE IF EXISTS Mahasiswa;

-- 1. Tabel Mahasiswa
CREATE TABLE Mahasiswa (
    NPM VARCHAR(20) PRIMARY KEY,
    Nama VARCHAR(100),
    Email VARCHAR(100)
);

-- 2. Tabel Dosen
CREATE TABLE Dosen (
    NIK VARCHAR(20) PRIMARY KEY,
    Nama VARCHAR(100),
    Email VARCHAR(100)
);

-- 3. Tabel Mata Kuliah
CREATE TABLE Mata_Kuliah (
    id SERIAL PRIMARY KEY, 
    Kode_MK VARCHAR(20),
    Nama_Mata_Kuliah VARCHAR(100),
    Sem_akd VARCHAR(20),
    Tahun_akd VARCHAR(20),
    Kelas CHAR(1)
);

-- 4. Tabel Tugas Besar
CREATE TABLE Tugas_Besar (
    Id_Tugas_Besar INT PRIMARY KEY,
    Nama_Tugas_Besar VARCHAR(100),
    path_Deskripsi VARCHAR(255),
    tanggal_upload DATE,
    ID_KELAS INT,
    FOREIGN KEY (ID_KELAS) REFERENCES Mata_Kuliah(id)
);

-- A. Insert Data Mahasiswa (20 Data)
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