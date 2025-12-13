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

    // Daftar kegiatan sesuai tabel Deadline (dibaca dari DOM agar dinamis)
    // Support both jadwal-table (shared style) and legacy deadline-table
    let kegiatanRows = Array.from(document.querySelectorAll('.jadwal-table tbody tr, .deadline-table tbody tr'));
    let kegiatanList = kegiatanRows.map(r => {
        // Table now: 1=Deadline, 2=Kegiatan, 3=Aksi
        const name = r.querySelector('td:nth-child(2)') ? r.querySelector('td:nth-child(2)').textContent.trim() : '';
        const deadline = r.querySelector('td:nth-child(1)') ? r.querySelector('td:nth-child(1)').textContent.trim() : '';
        const id = r.querySelector('.input-nilai-btn') ? r.querySelector('.input-nilai-btn').getAttribute('data-idkegiatan') : null;
        return { name, id: id ? parseInt(id) : null, deadline };
    });

        // Ambil daftar kelompok yang ada di tabel Kelompok (hanya kelompok yang dirender untuk Tubes saat ini)
        const kelompokNames = Array.from(document.querySelectorAll('.kelompok-penilaian-table tbody tr .kelompok-nama')).map(td => td.textContent.replace(/^Kelompok\s*/i, '').trim());
        const uniqueKelompok = [...new Set(kelompokNames)];

        // Isi dropdown kelompok pada form input (kelompok & perorangan) - clear first to avoid duplicates
        const kelompokSelect = document.getElementById("kelompokSelect");
        if (kelompokSelect) {
            kelompokSelect.innerHTML = '<option value="">-- Pilih Kelompok --</option>';
            uniqueKelompok.forEach(k => {
                const opt = document.createElement('option');
                opt.value = k;
                opt.textContent = `Kelompok ${k}`;
                kelompokSelect.appendChild(opt);
            });
        }
        if (kelompokSelectPerorangan) {
            kelompokSelectPerorangan.innerHTML = '<option value="">-- Pilih Kelompok --</option>';
            uniqueKelompok.forEach(k => {
                const opt = document.createElement('option');
                opt.value = k;
                opt.textContent = `Kelompok ${k}`;
                kelompokSelectPerorangan.appendChild(opt);
            });
        }

    // --- 2. HANDLE TOMBOL INPUT NILAI ---
    inputNilaiBtns.forEach(btn => {
        btn.addEventListener("click", function() {
            // Table layout: 1=Deadline, 2=Kegiatan, 3=Aksi
            const kegiatan = this.closest("tr").querySelector("td:nth-child(2)").textContent;
            const idKegiatan = this.getAttribute('data-idkegiatan');
            document.getElementById("popupKegiatan").textContent = kegiatan;
            // store idKegiatan on popup for later
            inputNilaiPopup.setAttribute('data-idkegiatan', idKegiatan);
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

        // Try fetching members from server for this kelompok (requires idKegiatan context)
        const idKegiatan = inputNilaiPopup.getAttribute('data-idkegiatan');
        const urlParams = new URLSearchParams(window.location.search);
        const idTubes = urlParams.get('idTubes');

        if (selectedKelompok && idTubes && idKegiatan) {
            fetch(`/dosen/penilaian/kelompok-nilai?idTubes=${idTubes}&idKegiatan=${idKegiatan}&namaKelompok=${encodeURIComponent(selectedKelompok)}`)
                .then(res => res.json())
                .then(data => {
                    if (Array.isArray(data) && data.length > 0) {
                        mahasiswaSelect.disabled = false;
                        data.forEach(m => {
                            const option = document.createElement("option");
                            option.value = m.npm;
                            option.textContent = `${m.name} (${m.npm})`;
                            mahasiswaSelect.appendChild(option);
                        });
                    } else {
                        mahasiswaSelect.disabled = true;
                    }
                })
                .catch(err => {
                    console.error('Gagal fetch anggota kelompok:', err);
                    mahasiswaSelect.disabled = true;
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

        if (tipeInput === "perorangan") {
            const kelompok = document.getElementById("kelompokSelectPerorangan").value;
            const mahasiswaNpm = document.getElementById("mahasiswaSelect").value;
            const mahasiswaName = document.getElementById("mahasiswaSelect").selectedOptions[0].textContent.split('(')[0].trim();
            const nilai = document.getElementById("inputNilaiPerorangan").value;
            const catatan = document.getElementById("catatanNilaiPerorangan").value;

            // Get kegiatan id from popup dataset
            const idKegiatan = inputNilaiPopup.getAttribute('data-idkegiatan');

            // Build payload for server
            const payload = [{
                npm: mahasiswaNpm,
                kelompok: kelompok,
                idKegiatan: parseInt(idKegiatan),
                nilai: parseFloat(nilai),
                keterangan: catatan
            }];

            // Send to server
            const urlParams = new URLSearchParams(window.location.search);
            const kelasId = urlParams.get('kelasId');
            const idTubes = urlParams.get('idTubes');

            const body = { kelasId: parseInt(kelasId), idTubes: parseInt(idTubes), nilaiData: payload };

            fetch('/dosen/simpan-nilai', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);

                    // Update local data structure so UI reflects the change immediately
                    if (!dataNilai[kegiatan]) dataNilai[kegiatan] = { perorangan: [] };
                    const existingIndex = dataNilai[kegiatan].perorangan.findIndex(
                        x => x.npm === mahasiswaNpm && x.kelompok === kelompok
                    );
                    const now = new Date();
                    const tanggal = `${now.getDate().toString().padStart(2, '0')} ${getMonthName(now.getMonth())} ${now.getFullYear()} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;

                    const newEntry = { npm: mahasiswaNpm, name: mahasiswaName, kelompok, nilai: parseInt(nilai), catatan: catatan, tanggal };
                    if (existingIndex !== -1) {
                        dataNilai[kegiatan].perorangan[existingIndex] = newEntry;
                    } else {
                        dataNilai[kegiatan].perorangan.push(newEntry);
                    }

                    updateTabelNilai();
                    closeInputNilaiPopup();
                } else {
                    alert('Gagal: ' + data.message);
                }
            })
            .catch(err => {
                console.error(err);
                alert('Terjadi kesalahan saat menyimpan nilai');
            });
        } else {
            // Process kelompok input: send group nilai to server
            const kelompok = document.getElementById("kelompokSelect").value;
            const nilaiKelompok = document.getElementById("inputNilaiKelompok").value;

            const idKegiatan = inputNilaiPopup.getAttribute('data-idkegiatan');
            const urlParams = new URLSearchParams(window.location.search);
            const kelasId = urlParams.get('kelasId');
            const idTubes = urlParams.get('idTubes');

            const payloadItem = {
                kelompok: kelompok,
                idKegiatan: parseInt(idKegiatan),
                nilai: parseFloat(nilaiKelompok)
            };

            const body = { kelasId: parseInt(kelasId), idTubes: parseInt(idTubes), nilaiData: [payloadItem] };

            fetch('/dosen/simpan-nilai', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    // Immediately update the kelompok table cell for feedback
                    try {
                        const idKegiatanNum = parseInt(idKegiatan);
                        const row = document.querySelector(`.kelompok-penilaian-table tbody tr[data-kelompok="${kelompok}"]`);
                        if (row) {
                            const cell = row.querySelector(`.nilai-cell[data-idkegiatan="${idKegiatanNum}"]`);
                            if (cell) {
                                cell.textContent = Math.round(parseFloat(nilaiKelompok));
                                cell.dataset.groupNilai = parseFloat(nilaiKelompok);
                            }
                        }
                    } catch (e) {
                        console.error('Error updating table cell immediately:', e);
                    }

                    // Refresh kelompok table values for this idKegiatan to sync members
                    populateKelompokTable().then(() => {
                        updateTabelNilai();
                        closeInputNilaiPopup();
                    });
                } else {
                    alert('Gagal: ' + data.message);
                }
            })
            .catch(err => {
                console.error(err);
                alert('Terjadi kesalahan saat menyimpan nilai kelompok');
            });
        }
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
            option.value = kegiatan.name;
            option.dataset.idkegiatan = kegiatan.id;
            option.textContent = kegiatan.name;
            filterKegiatanDetail.appendChild(option);
        });

        // Tampilkan data berdasarkan filter default
        const defaultKeg = kegiatanList[0];
        if (defaultKeg) tampilkanDetailNilai(kelompok, defaultKeg.name);

        // Event listener untuk perubahan filter
        filterKegiatanDetail.onchange = function() {
            const selectedKegiatan = this.value;
            tampilkanDetailNilai(kelompok, selectedKegiatan);
        };
    }

    // Fungsi untuk menampilkan detail nilai mahasiswa
    // Fungsi untuk menampilkan detail nilai mahasiswa
async function tampilkanDetailNilai(kelompok, filterKegiatan) {
    const detailNilaiList = document.getElementById("detailNilaiList");
    
    detailNilaiList.innerHTML = '';

    const kegiatanObj = kegiatanList.find(k => k.name === filterKegiatan);
    const idKegiatan = kegiatanObj ? kegiatanObj.id : null;

    const urlParams = new URLSearchParams(window.location.search);
    const idTubes = urlParams.get('idTubes');

    // Fetch server-side data for this kelompok + kegiatan
    let members = [];
    if (idTubes && idKegiatan) {
        try {
            const res = await fetch(`/dosen/penilaian/kelompok-nilai?idTubes=${idTubes}&idKegiatan=${idKegiatan}&namaKelompok=${encodeURIComponent(kelompok)}`);
            if (res.ok) {
                members = await res.json();
            }
        } catch (err) {
            console.error('Error fetching kelompok nilai:', err);
        }
    }

    if (!members || members.length === 0) {
        detailNilaiList.innerHTML = `
            <div class="no-data">
                <div class="no-data-icon">📊</div>
                <p class="no-data-text">Tidak ada mahasiswa dalam kelompok ini</p>
            </div>
        `;
        return;
    }

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

    members.forEach((m, index) => {
        const row = document.createElement('tr');
        row.style.cssText = index % 2 === 0 ? 'background-color: #f8f9fa;' : '';

        row.innerHTML = `
            <td style="font-family: nunitoSemiBold; color: var(--text-dark);">${m.name}</td>
            <td style="color: #666;">${m.npm}</td>
            <td>${m.nilai != null ? `<span class="badge-nilai">${m.nilai}</span>` : `<span class="badge-belum">Belum dinilai</span>`}</td>
            <td>${m.keterangan ? `<div class="catatan-text">${m.keterangan}</div>` : '-'}</td>
        `;

        tbody.appendChild(row);
    });

    table.appendChild(tbody);
    detailNilaiList.appendChild(table);
}

    function closeDetailNilaiPopup() {
        detailNilaiPopup.style.display = 'none';
    }

    // --- 10. UPDATE TABEL NILAI SETELAH SIMPAN ---
    function updateTabelNilai() {
        const rows = document.querySelectorAll('.kelompok-penilaian-table tbody tr');
        
        rows.forEach(row => {
            const kelompok = row.querySelector('.detail-nilai-btn').getAttribute('data-kelompok');
            
            // Hitung nilai rata-rata untuk setiap kegiatan (perorangan override jika ada)
            kegiatanList.forEach((kegiatan) => {
                const idKegiatan = kegiatan.id;
                const nilaiCell = idKegiatan ? row.querySelector(`.nilai-cell[data-idkegiatan="${idKegiatan}"]`) : null;

                // Ambil semua nilai perorangan untuk kelompok dan kegiatan ini
                const namaKegiatan = kegiatan.name || kegiatan;
                const nilaiPerorangan = dataNilai[namaKegiatan]?.perorangan?.filter(
                    item => item.kelompok === kelompok
                ) || [];

                if (nilaiCell) {
                    if (nilaiPerorangan.length > 0) {
                        // Hitung rata-rata nilai perorangan dan gunakan sebagai tampilan
                        const total = nilaiPerorangan.reduce((sum, item) => sum + item.nilai, 0);
                        const rataRata = Math.round(total / nilaiPerorangan.length);
                        // Only override cell if there is no group nilai set
                        if (!nilaiCell.dataset.groupNilai) {
                            nilaiCell.textContent = rataRata;
                        }
                    } else if (!nilaiCell.textContent || nilaiCell.textContent.trim() === '') {
                        nilaiCell.textContent = '-';
                    }
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
                localStorage.clear();
                window.location.href = "/logout";
            }
        });
    }

    // --- 13. PROGRESS BAR CLICKABLE ---
    const urlParamsForProgress = new URLSearchParams(window.location.search);
    const kelasIdForProgress = urlParamsForProgress.get('kelasId');
    const idTubesForProgress = urlParamsForProgress.get('idTubes');

    const steps = document.querySelectorAll(".step[data-step]");
    steps.forEach((step) => {
        step.addEventListener("click", function() {
            const stepNumber = this.getAttribute('data-step');
            if (!stepNumber) return;
            if (!kelasIdForProgress || !idTubesForProgress) {
                alert('Error: ID Kelas atau ID Tugas tidak ditemukan.');
                return;
            }
            switch(stepNumber) {
                case '1': window.location.href = `/dosen/edit?kelasId=${kelasIdForProgress}&idTubes=${idTubesForProgress}`; break;
                case '2': window.location.href = `/dosen/upload-jadwal?kelasId=${kelasIdForProgress}&idTubes=${idTubesForProgress}`; break;
                case '3': window.location.href = `/dosen/upload-rubrik?kelasId=${kelasIdForProgress}&idTubes=${idTubesForProgress}`; break;
                case '4': window.location.href = `/dosen/buat-kelompok?kelasId=${kelasIdForProgress}&idTubes=${idTubesForProgress}`; break;
                case '5': /* already here */ return;
            }
        });
    });
    
    // Populate kelompok table from server (group nilai per kegiatan), then overlay perorangan if present
    populateKelompokTable().then(() => {
        updateTabelNilai();
    });

    // Fetch group nilai for each kelompok & kegiatan and populate cells
    async function populateKelompokTable() {
        const urlParams = new URLSearchParams(window.location.search);
        const idTubes = urlParams.get('idTubes');
        if (!idTubes) return;

        const rows = document.querySelectorAll('.kelompok-penilaian-table tbody tr');
        const tasks = [];

        rows.forEach(row => {
            const kelompokFull = row.querySelector('.kelompok-nama').textContent || '';
            const kelompok = kelompokFull.replace(/^Kelompok\s*/i, '').trim();

            kegiatanList.forEach(kegiatan => {
                const idKegiatan = kegiatan.id;
                if (!idKegiatan) return;
                const cell = row.querySelector(`.nilai-cell[data-idkegiatan=\"${idKegiatan}\"]`);
                if (!cell) return;

                const p = fetch(`/dosen/penilaian/kelompok-nilai?idTubes=${idTubes}&idKegiatan=${idKegiatan}&namaKelompok=${encodeURIComponent(kelompok)}`)
                        .then(res => res.json())
                        .then(data => {
                            // `data` is array of members with fields: npm, name, nilai, keterangan, plus groupNilai/groupKeterangan on first item
                            if (!Array.isArray(data)) {
                                cell.textContent = '-';
                                return;
                            }

                            // If group-level nilai exists, show it (group nilai should be authoritative for the kelompok column)
                            if (Array.isArray(data) && data.length > 0 && data[0].groupNilai != null) {
                                cell.textContent = Math.round(data[0].groupNilai);
                                cell.dataset.groupNilai = data[0].groupNilai;

                                // Also merge perorangan into client-side dataNilai for later perorangan views/overrides
                                const peroranganVals = data.filter(m => m.nilai != null).map(m => ({ npm: m.npm, name: m.name, kelompok: kelompok, nilai: Math.round(m.nilai), catatan: m.keterangan || '' }));
                                if (peroranganVals.length > 0) {
                                    if (!dataNilai[kegiatan.name]) dataNilai[kegiatan.name] = { perorangan: [] };
                                    peroranganVals.forEach(pv => {
                                        const idx = dataNilai[kegiatan.name].perorangan.findIndex(x => x.npm === pv.npm);
                                        if (idx !== -1) dataNilai[kegiatan.name].perorangan[idx] = pv;
                                        else dataNilai[kegiatan.name].perorangan.push(pv);
                                    });
                                }
                            } else {
                                // No group nilai; if there are perorangan values, compute average otherwise '-'
                                const peroranganVals = data.filter(m => m.nilai != null).map(m => ({ npm: m.npm, name: m.name, kelompok: kelompok, nilai: Math.round(m.nilai), catatan: m.keterangan || '' }));
                                if (peroranganVals.length > 0) {
                                    const total = peroranganVals.reduce((s, it) => s + it.nilai, 0);
                                    const avg = Math.round(total / peroranganVals.length);
                                    cell.textContent = avg;
                                    delete cell.dataset.groupNilai;
                                    if (!dataNilai[kegiatan.name]) dataNilai[kegiatan.name] = { perorangan: [] };
                                    peroranganVals.forEach(pv => {
                                        const idx = dataNilai[kegiatan.name].perorangan.findIndex(x => x.npm === pv.npm);
                                        if (idx !== -1) dataNilai[kegiatan.name].perorangan[idx] = pv;
                                        else dataNilai[kegiatan.name].perorangan.push(pv);
                                    });
                                } else {
                                    cell.textContent = '-';
                                }
                            }
                        })
                    .catch(err => {
                        console.error('Gagal fetch group nilai:', err);
                        cell.textContent = '-';
                    });

                tasks.push(p);
            });

            // After all kegiatan for this row are fetched, compute nilai akhir
            // We'll compute after all fetches finish
        });

        await Promise.all(tasks);

        // Compute nilai akhir for each row now
        rows.forEach(r => {
            const kelompok = r.querySelector('.kelompok-nama').textContent.replace(/^Kelompok\s*/i, '').trim();
            updateNilaiAkhir(r, kelompok);
        });
    }
});