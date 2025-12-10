document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const btnLogout = document.querySelector(".logout-btn");
    const inputNilaiBtns = document.querySelectorAll(".input-nilai-btn:not(.disabled)");
    const detailNilaiBtns = document.querySelectorAll(".detail-nilai-btn");
    const closeNilaiPopup = document.getElementById("closeNilaiPopup");
    const batalInputBtn = document.getElementById("batalInputBtn");
    const simpanNilaiBtn = document.getElementById("simpanNilaiBtn");
    const inputNilaiPopup = document.getElementById("inputNilaiPopup");
    const closeDetailPopup = document.getElementById("closeDetailPopup");
    const tutupDetailBtn = document.getElementById("tutupDetailBtn");
    const detailNilaiPopup = document.getElementById("detailNilaiPopup");
    
    // Elemen untuk tipe input
    const tipeInputRadios = document.querySelectorAll('input[name="tipeInput"]');
    const formKelompok = document.getElementById("formKelompok");
    const formPerorangan = document.getElementById("formPerorangan");
    const kelompokSelectPerorangan = document.getElementById("kelompokSelectPerorangan");
    const mahasiswaSelect = document.getElementById("mahasiswaSelect");
    const filterKegiatanDetail = document.getElementById("filterKegiatanDetail");

    // Data contoh mahasiswa per kelompok
    const dataMahasiswa = {
        'A': [
            { npm: '6182301001', name: 'Robert Saputra' },
            { npm: '6182301002', name: 'Bryan Ricaldi' },
            { npm: '6182301003', name: 'Ethan' },
            { npm: '6182301007', name: 'Eric Leilpaly' }
        ],
        'B': [
            { npm: '6182301004', name: 'Sarah Johnson' },
            { npm: '6182301005', name: 'Michael Brown' },
            { npm: '6182301006', name: 'Lisa Anderson' }
        ],
        'C': [
            { npm: '6182301008', name: 'David Wilson' },
            { npm: '6182301009', name: 'Emma Thompson' },
            { npm: '6182301010', name: 'James Miller' }
        ],
        'D': [
            { npm: '6182301011', name: 'Olivia Davis' },
            { npm: '6182301012', name: 'William Garcia' }
        ]
    };

    // Data contoh nilai - HANYA nilai perorangan
    const dataNilai = {
        'Pertemuan ke-1': {
            perorangan: [
                {
                    npm: '6182301001',
                    name: 'Robert Saputra',
                    kelompok: 'A',
                    nilai: 88,
                    catatan: 'Kontribusi sangat aktif dalam diskusi, menguasai materi dengan baik.',
                    tanggal: '05 Jan 2024 14:35'
                },
                {
                    npm: '6182301002',
                    name: 'Bryan Ricaldi',
                    kelompok: 'A',
                    nilai: 82,
                    catatan: 'Presentasi cukup baik, namun perlu lebih percaya diri saat menyampaikan materi.',
                    tanggal: '05 Jan 2024 14:40'
                },
                {
                    npm: '6182301004',
                    name: 'Sarah Johnson',
                    kelompok: 'B',
                    nilai: 80,
                    catatan: 'Analisis masalah cukup mendalam, namun solusi yang diberikan kurang inovatif.',
                    tanggal: '06 Jan 2024 10:20'
                },
                {
                    npm: '6182301008',
                    name: 'David Wilson',
                    kelompok: 'C',
                    nilai: 95,
                    catatan: 'Outstanding performance! Very creative in problem solving.',
                    tanggal: '07 Jan 2024 09:50'
                },
                {
                    npm: '6182301005',
                    name: 'Michael Brown',
                    kelompok: 'B',
                    nilai: 75,
                    catatan: 'Perlu lebih banyak kontribusi dalam diskusi kelompok.',
                    tanggal: '06 Jan 2024 10:25'
                }
            ]
        },
        'Presentasi': {
            perorangan: []
        },
        'Demo Aplikasi': {
            perorangan: []
        },
        'Laporan Final': {
            perorangan: []
        }
    };

    // Daftar kegiatan sesuai tabel Deadline
    const kegiatanList = [
        'Pertemuan ke-1',
        'Presentasi',
        'Demo Aplikasi',
        'Laporan Final'
    ];

    // --- 2. HANDLE TOMBOL INPUT NILAI ---
    inputNilaiBtns.forEach(btn => {
        btn.addEventListener("click", function() {
            const kegiatan = this.closest("tr").querySelector("td:first-child").textContent;
            document.getElementById("popupKegiatan").textContent = kegiatan;
            openInputNilaiPopup();
        });
    });

    // --- 3. HANDLE TOMBOL DETAIL NILAI ---
    detailNilaiBtns.forEach(btn => {
        btn.addEventListener("click", function() {
            const kelompok = this.getAttribute("data-kelompok");
            openDetailNilaiPopup(kelompok);
        });
    });

    // --- 4. HANDLE PERUBAHAN TIPE INPUT ---
    tipeInputRadios.forEach(radio => {
        radio.addEventListener("change", function() {
            if (this.value === "kelompok") {
                formKelompok.style.display = "flex";
                formPerorangan.style.display = "none";
            } else {
                formKelompok.style.display = "none";
                formPerorangan.style.display = "flex";
            }
        });
    });

    // --- 5. HANDLE PERUBAHAN KELOMPOK UNTUK INPUT PERORANGAN ---
    kelompokSelectPerorangan.addEventListener("change", function() {
        const selectedKelompok = this.value;
        mahasiswaSelect.innerHTML = '<option value="">-- Pilih Mahasiswa --</option>';
        
        if (selectedKelompok && dataMahasiswa[selectedKelompok]) {
            mahasiswaSelect.disabled = false;
            dataMahasiswa[selectedKelompok].forEach(mahasiswa => {
                const option = document.createElement("option");
                option.value = mahasiswa.npm;
                option.textContent = `${mahasiswa.name} (${mahasiswa.npm})`;
                mahasiswaSelect.appendChild(option);
            });
        } else {
            mahasiswaSelect.disabled = true;
        }
    });

    // --- 6. FUNGSI POPUP INPUT NILAI ---
    function openInputNilaiPopup() {
        inputNilaiPopup.style.display = 'flex';
        // Reset form
        resetForms();
        // Set default ke nilai kelompok
        document.querySelector('input[name="tipeInput"][value="kelompok"]').checked = true;
        formKelompok.style.display = "flex";
        formPerorangan.style.display = "none";
    }

    function resetForms() {
        // Reset form kelompok
        document.getElementById("kelompokSelect").value = "";
        document.getElementById("inputNilaiKelompok").value = "";
        
        // Reset form perorangan
        document.getElementById("kelompokSelectPerorangan").value = "";
        document.getElementById("mahasiswaSelect").innerHTML = '<option value="">-- Pilih Mahasiswa --</option>';
        document.getElementById("mahasiswaSelect").disabled = true;
        document.getElementById("inputNilaiPerorangan").value = "";
        document.getElementById("catatanNilaiPerorangan").value = "";
    }

    function closeInputNilaiPopup() {
        inputNilaiPopup.style.display = 'none';
    }

    // --- 7. VALIDASI FORM INPUT NILAI ---
    function validateForm() {
        const tipeInput = document.querySelector('input[name="tipeInput"]:checked').value;
        
        if (tipeInput === "kelompok") {
            const kelompok = document.getElementById("kelompokSelect").value;
            const nilai = document.getElementById("inputNilaiKelompok").value;
            
            if (!kelompok) {
                alert("Pilih kelompok terlebih dahulu!");
                return false;
            }
            if (!nilai) {
                alert("Isi nilai kelompok terlebih dahulu!");
                return false;
            }
            if (nilai < 0 || nilai > 100) {
                alert("Nilai harus antara 0-100!");
                return false;
            }
            return true;
            
        } else {
            const kelompok = document.getElementById("kelompokSelectPerorangan").value;
            const mahasiswa = document.getElementById("mahasiswaSelect").value;
            const nilai = document.getElementById("inputNilaiPerorangan").value;
            
            if (!kelompok) {
                alert("Pilih kelompok terlebih dahulu!");
                return false;
            }
            if (!mahasiswa) {
                alert("Pilih mahasiswa terlebih dahulu!");
                return false;
            }
            if (!nilai) {
                alert("Isi nilai individu terlebih dahulu!");
                return false;
            }
            if (nilai < 0 || nilai > 100) {
                alert("Nilai harus antara 0-100!");
                return false;
            }
            return true;
        }
    }

    // --- 8. SIMPAN NILAI ---
    function simpanNilai() {
        if (!validateForm()) return;

        const tipeInput = document.querySelector('input[name="tipeInput"]:checked').value;
        const kegiatan = document.getElementById("popupKegiatan").textContent;

        // Hanya proses Tipe Perorangan
        if (tipeInput === "perorangan") {
            const kelompok = document.getElementById("kelompokSelectPerorangan").value;
            const mahasiswaNpm = document.getElementById("mahasiswaSelect").value;
            const mahasiswaName = document.getElementById("mahasiswaSelect").selectedOptions[0].textContent.split('(')[0].trim();
            const nilai = document.getElementById("inputNilaiPerorangan").value;
            const catatan = document.getElementById("catatanNilaiPerorangan").value;
            
            // Format tanggal sekarang
            const now = new Date();
            const tanggal = `${now.getDate().toString().padStart(2, '0')} ${getMonthName(now.getMonth())} ${now.getFullYear()} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
            
            // Simpan ke dataNilai
            if (!dataNilai[kegiatan]) {
                dataNilai[kegiatan] = { perorangan: [] };
            }
            
            // Cek apakah mahasiswa sudah punya nilai untuk kegiatan ini
            const existingIndex = dataNilai[kegiatan].perorangan.findIndex(
                nilaiItem => nilaiItem.npm === mahasiswaNpm && nilaiItem.kelompok === kelompok
            );
            
            if (existingIndex !== -1) {
                // Update nilai yang sudah ada
                dataNilai[kegiatan].perorangan[existingIndex] = {
                    npm: mahasiswaNpm,
                    name: mahasiswaName,
                    kelompok: kelompok,
                    nilai: parseInt(nilai),
                    catatan: catatan,
                    tanggal: tanggal
                };
                alert(`Nilai individu berhasil diperbarui!\nMahasiswa: ${mahasiswaName}\nKelompok: ${kelompok}\nKegiatan: ${kegiatan}\nNilai: ${nilai}`);
            } else {
                // Tambah nilai baru
                dataNilai[kegiatan].perorangan.push({
                    npm: mahasiswaNpm,
                    name: mahasiswaName,
                    kelompok: kelompok,
                    nilai: parseInt(nilai),
                    catatan: catatan,
                    tanggal: tanggal
                });
                alert(`Nilai individu berhasil disimpan!\nMahasiswa: ${mahasiswaName}\nKelompok: ${kelompok}\nKegiatan: ${kegiatan}\nNilai: ${nilai}`);
            }
        } else {
            alert("Input nilai kelompok sudah dinonaktifkan. Silakan gunakan input nilai perorangan.");
            return;
        }

        closeInputNilaiPopup();
        updateTabelNilai();
    }

    function getMonthName(monthIndex) {
        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agu', 'Sep', 'Okt', 'Nov', 'Des'];
        return months[monthIndex];
    }

    // --- 9. FUNGSI POPUP DETAIL NILAI ---
    function openDetailNilaiPopup(kelompok) {
        detailNilaiPopup.style.display = 'flex';
        document.getElementById("popupKelompokNama").textContent = `Kelompok ${kelompok}`;
        
        // Isi dropdown filter dengan kegiatan
        filterKegiatanDetail.innerHTML = '';
        
        kegiatanList.forEach(kegiatan => {
            const option = document.createElement("option");
            option.value = kegiatan;
            option.textContent = kegiatan;
            filterKegiatanDetail.appendChild(option);
        });
        
        // Tampilkan data berdasarkan filter default
        tampilkanDetailNilai(kelompok, kegiatanList[0]);
        
        // Event listener untuk perubahan filter
        filterKegiatanDetail.onchange = function() {
            const selectedKegiatan = this.value;
            tampilkanDetailNilai(kelompok, selectedKegiatan);
        };
    }

    // Fungsi untuk menampilkan detail nilai mahasiswa
    // Fungsi untuk menampilkan detail nilai mahasiswa
function tampilkanDetailNilai(kelompok, filterKegiatan) {
    const detailNilaiList = document.getElementById("detailNilaiList");
    
    detailNilaiList.innerHTML = '';
    
    // Ambil semua mahasiswa dalam kelompok ini
    const mahasiswaKelompok = dataMahasiswa[kelompok] || [];
    
    if (mahasiswaKelompok.length === 0) {
        detailNilaiList.innerHTML = `
            <div class="no-data">
                <div class="no-data-icon">📊</div>
                <p class="no-data-text">Tidak ada mahasiswa dalam kelompok ini</p>
            </div>
        `;
        return;
    }
    
    // Cari nilai untuk kegiatan yang dipilih
    const nilaiKegiatan = dataNilai[filterKegiatan]?.perorangan?.filter(
        item => item.kelompok === kelompok
    ) || [];
    
    // Buat elemen tabel
    const table = document.createElement('table');
    table.className = 'detail-nilai-table';
    
    // Header tabel
    const thead = document.createElement('thead');
    thead.innerHTML = `
        <tr>
            <th>Nama Mahasiswa</th>
            <th>NPM</th>
            <th>Nilai</th>
            <th>Catatan</th>
        </tr>
    `;
    table.appendChild(thead);
    
    // Body tabel
    const tbody = document.createElement('tbody');
    
    if (nilaiKegiatan.length === 0) {
        // Jika belum ada nilai untuk kegiatan ini, tampilkan semua mahasiswa dengan status "Belum dinilai"
        mahasiswaKelompok.forEach((mahasiswa, index) => {
            const row = document.createElement('tr');
            row.style.cssText = index % 2 === 0 ? 'background-color: #f8f9fa;' : '';
            
            row.innerHTML = `
                <td style="font-family: nunitoSemiBold; color: var(--text-dark);">
                    ${mahasiswa.name}
                </td>
                <td style="color: #666;">
                    ${mahasiswa.npm}
                </td>
                <td>
                    <span class="badge-belum">Belum dinilai</span>
                </td>
                <td>-</td>
            `;
            
            tbody.appendChild(row);
        });
    } else {
        // Tampilkan mahasiswa yang sudah dinilai
        mahasiswaKelompok.forEach((mahasiswa, index) => {
            // Cari nilai mahasiswa ini
            const nilaiItem = nilaiKegiatan.find(item => item.npm === mahasiswa.npm);
            
            const row = document.createElement('tr');
            row.style.cssText = index % 2 === 0 ? 'background-color: #f8f9fa;' : '';
            
            row.innerHTML = `
                <td style="font-family: nunitoSemiBold; color: var(--text-dark);">
                    ${mahasiswa.name}
                </td>
                <td style="color: #666;">
                    ${mahasiswa.npm}
                </td>
                <td>
                    ${nilaiItem ? 
                        `<span class="badge-nilai">${nilaiItem.nilai}</span>` : 
                        `<span class="badge-belum">Belum dinilai</span>`
                    }
                </td>
                <td>
                    <div class="catatan-text">${nilaiItem ? nilaiItem.catatan : '-'}</div>
                    ${nilaiItem ? `<div class="catatan-date">${nilaiItem.tanggal}</div>` : ''}
                </td>
            `;
            
            tbody.appendChild(row);
        });
    }
    
    table.appendChild(tbody);
    
    // Cek apakah ada data yang ditampilkan
    const rows = tbody.querySelectorAll('tr');
    if (rows.length === 0) {
        detailNilaiList.innerHTML = `
            <div class="no-data">
                <div class="no-data-icon">📊</div>
                <p class="no-data-text">Belum ada mahasiswa dalam kelompok ini</p>
            </div>
        `;
    } else {
        detailNilaiList.appendChild(table);
    }
}

    function closeDetailNilaiPopup() {
        detailNilaiPopup.style.display = 'none';
    }

    // --- 10. UPDATE TABEL NILAI SETELAH SIMPAN ---
    function updateTabelNilai() {
        const rows = document.querySelectorAll('.kelompok-penilaian-table tbody tr');
        
        rows.forEach(row => {
            const kelompok = row.querySelector('.detail-nilai-btn').getAttribute('data-kelompok');
            
            // Hitung nilai rata-rata untuk setiap kegiatan
            kegiatanList.forEach((kegiatan, index) => {
                const nilaiCell = row.querySelector(`.nilai-cell:nth-child(${index + 2})`);
                
                // Ambil semua nilai perorangan untuk kelompok dan kegiatan ini
                const nilaiPerorangan = dataNilai[kegiatan]?.perorangan?.filter(
                    item => item.kelompok === kelompok
                ) || [];
                
                if (nilaiPerorangan.length > 0) {
                    // Hitung rata-rata nilai
                    const total = nilaiPerorangan.reduce((sum, item) => sum + item.nilai, 0);
                    const rataRata = Math.round(total / nilaiPerorangan.length);
                    nilaiCell.textContent = rataRata;
                } else {
                    nilaiCell.textContent = '-';
                }
            });
            
            // Hitung nilai akhir (rata-rata dari semua kegiatan yang sudah dinilai)
            updateNilaiAkhir(row, kelompok);
        });
    }

    function updateNilaiAkhir(row, kelompok) {
        const nilaiAkhirCell = row.querySelector('.nilai-akhir');
        let totalNilai = 0;
        let jumlahKegiatan = 0;
        
        // Ambil nilai dari setiap kolom nilai
        const nilaiCells = row.querySelectorAll('.nilai-cell');
        nilaiCells.forEach(cell => {
            const nilai = cell.textContent;
            if (nilai !== '-') {
                totalNilai += parseInt(nilai);
                jumlahKegiatan++;
            }
        });
        
        if (jumlahKegiatan > 0) {
            const nilaiAkhir = Math.round(totalNilai / jumlahKegiatan);
            nilaiAkhirCell.textContent = nilaiAkhir;
        } else {
            nilaiAkhirCell.textContent = '-';
        }
    }

    // --- 11. EVENT LISTENERS POPUP ---
    closeNilaiPopup.addEventListener('click', closeInputNilaiPopup);
    batalInputBtn.addEventListener('click', closeInputNilaiPopup);
    simpanNilaiBtn.addEventListener('click', simpanNilai);
    
    closeDetailPopup.addEventListener('click', closeDetailNilaiPopup);
    tutupDetailBtn.addEventListener('click', closeDetailNilaiPopup);

    // Tutup popup jika mengklik di luar konten popup
    inputNilaiPopup.addEventListener('click', function(event) {
        if (event.target === this) {
            closeInputNilaiPopup();
        }
    });
    
    detailNilaiPopup.addEventListener('click', function(event) {
        if (event.target === this) {
            closeDetailNilaiPopup();
        }
    });
    
    // Tutup popup dengan tombol ESC
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeInputNilaiPopup();
            closeDetailNilaiPopup();
        }
    });

    // --- 12. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                window.location.href = "index.html";
            }
        });
    }

    // --- 13. PROGRESS BAR CLICKABLE ---
    const steps = document.querySelectorAll(".step");
    steps.forEach((step, index) => {
        step.addEventListener("click", () => {
            if (index === 0) {
                window.location.href = "dosenEdit.html";
            } else if (index === 1) {
                window.location.href = "uploadJadwal.html";
            }
            else if (index === 2) {
                window.location.href = "uploadRubrik.html";
            }
            else if (index === 3) {
                window.location.href = "dosenBuatKelompok.html";
            }
            else if (index === 4) {
                // Sudah di halaman ini
                return;
            }
        });
    });
    
    // Inisialisasi tabel nilai saat pertama kali load
    updateTabelNilai();
});