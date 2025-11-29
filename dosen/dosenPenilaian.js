document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const btnLogout = document.querySelector(".logout-btn");
    const inputNilaiBtns = document.querySelectorAll(".input-nilai-btn:not(.disabled)");
    const lihatCatatanBtns = document.querySelectorAll(".lihat-catatan-btn:not(.disabled)");
    const detailNilaiBtns = document.querySelectorAll(".detail-nilai-btn");
    const closeNilaiPopup = document.getElementById("closeNilaiPopup");
    const batalInputBtn = document.getElementById("batalInputBtn");
    const simpanNilaiBtn = document.getElementById("simpanNilaiBtn");
    const inputNilaiPopup = document.getElementById("inputNilaiPopup");
    const closeCatatanPopup = document.getElementById("closeCatatanPopup");
    const tutupCatatanBtn = document.getElementById("tutupCatatanBtn");
    const lihatCatatanPopup = document.getElementById("lihatCatatanPopup");
    
    // Elemen untuk tipe input
    const tipeInputRadios = document.querySelectorAll('input[name="tipeInput"]');
    const formKelompok = document.getElementById("formKelompok");
    const formPerorangan = document.getElementById("formPerorangan");
    const kelompokSelectPerorangan = document.getElementById("kelompokSelectPerorangan");
    const mahasiswaSelect = document.getElementById("mahasiswaSelect");
    const tipeCatatanRadios = document.querySelectorAll('input[name="tipeCatatan"]');

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

    // Data contoh catatan
    const dataCatatan = {
        'Pertemuan ke-1': {
            kelompok: [
                {
                    kelompok: 'A',
                    nilai: 85,
                    catatan: 'Presentasi sangat baik, materi disampaikan dengan jelas dan sistematis. Hanya perlu memperbaiki bagian kesimpulan.',
                    tanggal: '05 Jan 2024 14:30'
                },
                {
                    kelompok: 'B',
                    nilai: 78,
                    catatan: 'Konsep sudah baik namun delivery presentasi perlu ditingkatkan. Slide terlalu banyak text.',
                    tanggal: '06 Jan 2024 10:15'
                },
                {
                    kelompok: 'C',
                    nilai: 92,
                    catatan: 'Excellent presentation! Very innovative approach and good team coordination.',
                    tanggal: '07 Jan 2024 09:45'
                }
            ],
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
                }
            ]
        },
        'Presentasi': {
            kelompok: [],
            perorangan: []
        },
        'Demo Aplikasi': {
            kelompok: [],
            perorangan: []
        },
        'Laporan Final': {
            kelompok: [],
            perorangan: []
        }
    };

    // --- 2. HANDLE TOMBOL INPUT NILAI ---
    inputNilaiBtns.forEach(btn => {
        btn.addEventListener("click", function() {
            const kegiatan = this.closest("tr").querySelector("td:first-child").textContent;
            document.getElementById("popupKegiatan").textContent = kegiatan;
            openInputNilaiPopup();
        });
    });

    // --- 3. HANDLE TOMBOL LIHAT CATATAN ---
    lihatCatatanBtns.forEach(btn => {
        btn.addEventListener("click", function() {
            const kegiatan = this.closest("tr").querySelector("td:first-child").textContent;
            document.getElementById("popupCatatanKegiatan").textContent = kegiatan;
            openLihatCatatanPopup(kegiatan);
        });
    });

    // --- 4. HANDLE TOMBOL DETAIL NILAI ---
    detailNilaiBtns.forEach(btn => {
        btn.addEventListener("click", function() {
            const kelompok = this.closest("tr").querySelector(".kelompok-nama").textContent;
            alert(`Detail nilai untuk ${kelompok}\n\n(Fitur detail nilai akan dikembangkan lebih lanjut)`);
        });
    });

    // --- 5. HANDLE PERUBAHAN TIPE INPUT ---
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

    // --- 6. HANDLE PERUBAHAN KELOMPOK UNTUK INPUT PERORANGAN ---
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

    // --- 7. FUNGSI POPUP INPUT NILAI ---
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
        document.getElementById("catatanNilaiKelompok").value = "";
        
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

    // --- 8. VALIDASI FORM INPUT NILAI ---
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

    // --- 9. SIMPAN NILAI ---
    function simpanNilai() {
        if (!validateForm()) return;

        const tipeInput = document.querySelector('input[name="tipeInput"]:checked').value;
        const kegiatan = document.getElementById("popupKegiatan").textContent;

        if (tipeInput === "kelompok") {
            const kelompok = document.getElementById("kelompokSelect").value;
            const nilai = document.getElementById("inputNilaiKelompok").value;
            const catatan = document.getElementById("catatanNilaiKelompok").value;

            // Simpan ke dataCatatan
            if (!dataCatatan[kegiatan]) {
                dataCatatan[kegiatan] = { kelompok: [], perorangan: [] };
            }
            
            dataCatatan[kegiatan].kelompok.push({
                kelompok: kelompok,
                nilai: parseInt(nilai),
                catatan: catatan,
                tanggal: new Date().toLocaleString('id-ID', { 
                    day: '2-digit', 
                    month: 'short', 
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                })
            });

            alert(`Nilai kelompok berhasil disimpan!\nKelompok: ${kelompok}\nKegiatan: ${kegiatan}\nNilai: ${nilai}`);
            
        } else {
            const kelompok = document.getElementById("kelompokSelectPerorangan").value;
            const mahasiswaNpm = document.getElementById("mahasiswaSelect").value;
            const mahasiswaName = document.getElementById("mahasiswaSelect").selectedOptions[0].textContent.split(' (')[0];
            const nilai = document.getElementById("inputNilaiPerorangan").value;
            const catatan = document.getElementById("catatanNilaiPerorangan").value;

            // Simpan ke dataCatatan
            if (!dataCatatan[kegiatan]) {
                dataCatatan[kegiatan] = { kelompok: [], perorangan: [] };
            }
            
            dataCatatan[kegiatan].perorangan.push({
                npm: mahasiswaNpm,
                name: mahasiswaName,
                kelompok: kelompok,
                nilai: parseInt(nilai),
                catatan: catatan,
                tanggal: new Date().toLocaleString('id-ID', { 
                    day: '2-digit', 
                    month: 'short', 
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                })
            });

            alert(`Nilai individu berhasil disimpan!\nMahasiswa: ${mahasiswaName}\nKelompok: ${kelompok}\nKegiatan: ${kegiatan}\nNilai: ${nilai}`);
        }

        closeInputNilaiPopup();
        
        // Update tampilan tabel
        updateTabelNilai();
    }

    // --- 10. FUNGSI POPUP LIHAT CATATAN ---
    function openLihatCatatanPopup(kegiatan) {
        lihatCatatanPopup.style.display = 'flex';
        loadCatatanData(kegiatan);
    }

    function closeLihatCatatanPopup() {
        lihatCatatanPopup.style.display = 'none';
    }

    function loadCatatanData(kegiatan) {
        const catatanData = dataCatatan[kegiatan] || { kelompok: [], perorangan: [] };
        
        // Tampilkan/menyembunyikan pesan tidak ada catatan
        const noCatatanMessage = document.getElementById("noCatatanMessage");
        const hasKelompokCatatan = catatanData.kelompok.length > 0;
        const hasPeroranganCatatan = catatanData.perorangan.length > 0;
        
        if (!hasKelompokCatatan && !hasPeroranganCatatan) {
            noCatatanMessage.style.display = 'block';
            document.getElementById("catatanKelompokList").style.display = 'none';
            document.getElementById("catatanPeroranganList").style.display = 'none';
            return;
        } else {
            noCatatanMessage.style.display = 'none';
        }
        
        // Load catatan kelompok
        const kelompokList = document.getElementById("catatanKelompokList");
        kelompokList.innerHTML = '';
        
        if (hasKelompokCatatan) {
            catatanData.kelompok.forEach(catatan => {
                const catatanItem = createCatatanKelompokElement(catatan);
                kelompokList.appendChild(catatanItem);
            });
        } else {
            kelompokList.innerHTML = '<div class="no-catatan"><p>Tidak ada catatan kelompok</p></div>';
        }
        
        // Load catatan perorangan
        const peroranganList = document.getElementById("catatanPeroranganList");
        peroranganList.innerHTML = '';
        
        if (hasPeroranganCatatan) {
            catatanData.perorangan.forEach(catatan => {
                const catatanItem = createCatatanPeroranganElement(catatan);
                peroranganList.appendChild(catatanItem);
            });
        } else {
            peroranganList.innerHTML = '<div class="no-catatan"><p>Tidak ada catatan perorangan</p></div>';
        }
        
        // Set default view berdasarkan ketersediaan data
        const tipeCatatanDefault = hasKelompokCatatan ? 'kelompok' : 'perorangan';
        document.querySelector(`input[name="tipeCatatan"][value="${tipeCatatanDefault}"]`).checked = true;
        toggleCatatanView(tipeCatatanDefault);
    }

    function createCatatanKelompokElement(catatan) {
        const div = document.createElement('div');
        div.className = 'catatan-item';
        div.innerHTML = `
            <div class="catatan-header">
                <span class="kelompok-nama-catatan">Kelompok ${catatan.kelompok}</span>
                <span class="nilai-catatan">${catatan.nilai}</span>
            </div>
            <div class="catatan-content">
                <p class="catatan-text">${catatan.catatan}</p>
                <div class="catatan-meta">
                    <span class="catatan-date">Diinput: ${catatan.tanggal}</span>
                </div>
            </div>
        `;
        return div;
    }

    function createCatatanPeroranganElement(catatan) {
        const div = document.createElement('div');
        div.className = 'catatan-item';
        div.innerHTML = `
            <div class="catatan-header">
                <span class="mahasiswa-nama-catatan">${catatan.name} (${catatan.npm}) - Kelompok ${catatan.kelompok}</span>
                <span class="nilai-catatan">${catatan.nilai}</span>
            </div>
            <div class="catatan-content">
                <p class="catatan-text">${catatan.catatan}</p>
                <div class="catatan-meta">
                    <span class="catatan-date">Diinput: ${catatan.tanggal}</span>
                </div>
            </div>
        `;
        return div;
    }

    function toggleCatatanView(tipe) {
        if (tipe === 'kelompok') {
            document.getElementById("catatanKelompokList").style.display = 'block';
            document.getElementById("catatanPeroranganList").style.display = 'none';
        } else {
            document.getElementById("catatanKelompokList").style.display = 'none';
            document.getElementById("catatanPeroranganList").style.display = 'block';
        }
    }

    // --- 11. UPDATE TABEL NILAI SETELAH SIMPAN ---
    function updateTabelNilai() {
        // Update nilai di tabel kelompok berdasarkan dataCatatan
        const rows = document.querySelectorAll('.kelompok-penilaian-table tbody tr');
        
        rows.forEach(row => {
            const kelompok = row.querySelector('.kelompok-nama').textContent.replace('Kelompok ', '');
            const nilaiPertemuan1Cell = row.querySelector('.nilai-cell:nth-child(2)');
            
            // Cari nilai untuk kelompok ini di dataCatatan
            let nilaiPertemuan1 = '-';
            
            if (dataCatatan['Pertemuan ke-1'] && dataCatatan['Pertemuan ke-1'].kelompok) {
                const catatanKelompok = dataCatatan['Pertemuan ke-1'].kelompok.find(
                    catatan => catatan.kelompok === kelompok
                );
                if (catatanKelompok) {
                    nilaiPertemuan1 = catatanKelompok.nilai;
                }
            }
            
            nilaiPertemuan1Cell.textContent = nilaiPertemuan1;
        });
    }

    // --- 12. HANDLE PERUBAHAN TIPE CATATAN ---
    tipeCatatanRadios.forEach(radio => {
        radio.addEventListener("change", function() {
            toggleCatatanView(this.value);
        });
    });

    // --- 13. EVENT LISTENERS POPUP ---
    closeNilaiPopup.addEventListener('click', closeInputNilaiPopup);
    batalInputBtn.addEventListener('click', closeInputNilaiPopup);
    simpanNilaiBtn.addEventListener('click', simpanNilai);
    
    closeCatatanPopup.addEventListener('click', closeLihatCatatanPopup);
    tutupCatatanBtn.addEventListener('click', closeLihatCatatanPopup);

    // Tutup popup jika mengklik di luar konten popup
    inputNilaiPopup.addEventListener('click', function(event) {
        if (event.target === this) {
            closeInputNilaiPopup();
        }
    });
    
    lihatCatatanPopup.addEventListener('click', function(event) {
        if (event.target === this) {
            closeLihatCatatanPopup();
        }
    });
    
    // Tutup popup dengan tombol ESC
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeInputNilaiPopup();
            closeLihatCatatanPopup();
        }
    });

    // --- 14. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                window.location.href = "index.html";
            }
        });
    }

    // --- 15. PROGRESS BAR CLICKABLE ---
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
});