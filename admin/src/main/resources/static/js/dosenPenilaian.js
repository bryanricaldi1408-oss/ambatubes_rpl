document.addEventListener("DOMContentLoaded", function () {
    console.log('dosenPenilaian.js loaded');
    try {
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

    // Event delegation fallback: ensure buttons still work even if earlier code errors
    document.addEventListener('click', function(e) {
        const inputBtn = e.target.closest('.input-nilai-btn');
        console.log('delegated click target:', e.target, 'closest inputBtn:', inputBtn);
        if (inputBtn && !inputBtn.classList.contains('disabled') && !inputBtn.disabled) {
            try {
                const row = inputBtn.closest('tr');
                const kegiatan = row ? (row.querySelector('td:nth-child(2)') ? row.querySelector('td:nth-child(2)').textContent : '') : '';
                const idKegiatan = inputBtn.getAttribute('data-idkegiatan');
                console.log('input-nilai-btn clicked, idKegiatan=', idKegiatan, 'kegiatan=', kegiatan);
                const popupKeg = document.getElementById('popupKegiatan');
                if (popupKeg) popupKeg.textContent = kegiatan;
                if (inputNilaiPopup && idKegiatan) inputNilaiPopup.setAttribute('data-idkegiatan', idKegiatan);
                if (typeof openInputNilaiPopup === 'function') openInputNilaiPopup();
            } catch (err) {
                console.error('Error handling delegated input-nilai-btn click', err);
            }
        }

        const detailBtn = e.target.closest('.detail-nilai-btn');
        if (detailBtn) {
            const kelompok = detailBtn.getAttribute('data-kelompok');
            if (typeof openDetailNilaiPopup === 'function') openDetailNilaiPopup(kelompok);
        }
    });

    // Re-attach per-button listeners in case buttons are rendered/updated after script load
    function attachInputButtonListeners() {
        const buttons = document.querySelectorAll('.input-nilai-btn');
        buttons.forEach(btn => {
            if (!btn.__hasAttach && !btn.disabled) {
                btn.addEventListener('click', (ev) => {
                    ev.preventDefault(); ev.stopPropagation();
                    console.log('direct input-nilai-btn click handler', btn.getAttribute('data-idkegiatan'));
                    const row = btn.closest('tr');
                    const kegiatan = row ? (row.querySelector('td:nth-child(2)') ? row.querySelector('td:nth-child(2)').textContent : '') : '';
                    const idKegiatan = btn.getAttribute('data-idkegiatan');
                    const popupKeg = document.getElementById('popupKegiatan');
                    if (popupKeg) popupKeg.textContent = kegiatan;
                    if (inputNilaiPopup && idKegiatan) inputNilaiPopup.setAttribute('data-idkegiatan', idKegiatan);
                    openInputNilaiPopup();
                });
                btn.__hasAttach = true;
            }
        });
    }

    // run immediately and after populate
    attachInputButtonListeners();

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

        // Reset perorangan inputs
        mahasiswaSelect.disabled = true;
        document.getElementById("inputNilaiPerorangan").value = "";
        document.getElementById("catatanNilaiPerorangan").value = "";

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

                        // Also store perorangan values in client-side cache for quick detail view
                        const peroranganVals = data.filter(m => m.nilai != null).map(m => ({ npm: m.npm, name: m.name, kelompok: selectedKelompok, nilai: Math.round(m.nilai), catatan: m.keterangan || '' }));
                        if (peroranganVals.length > 0) {
                            if (!dataNilai[kegiatanList.find(k=>k.id==idKegiatan)?.name]) dataNilai[kegiatanList.find(k=>k.id==idKegiatan)?.name] = { perorangan: [] };
                            const key = kegiatanList.find(k=>k.id==idKegiatan)?.name;
                            const existingNpm = new Set(dataNilai[key].perorangan.map(x => x.npm));
                            peroranganVals.forEach(pv => { if (!existingNpm.has(pv.npm)) dataNilai[key].perorangan.push(pv); });
                        }
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
    
    function closeInputNilaiPopup() {
        inputNilaiPopup.style.display = 'none';
    }

    function openInputNilaiPopup() {
        console.log('openInputNilaiPopup called');
        inputNilaiPopup.style.display = 'flex';

        // Default to kelompok input
        const kelompokRadio = document.querySelector('input[name="tipeInput"][value="kelompok"]');
        if (kelompokRadio) kelompokRadio.checked = true;
        if (formKelompok) formKelompok.style.display = 'flex';
        if (formPerorangan) formPerorangan.style.display = 'none';

        // Clear previous values
        const selKel = document.getElementById('kelompokSelect');
        if (selKel) { selKel.value = ''; }
        const inKel = document.getElementById('inputNilaiKelompok');
        if (inKel) { inKel.value = ''; }

        const selKelPer = document.getElementById('kelompokSelectPerorangan');
        if (selKelPer) { selKelPer.value = ''; }
        const mahasiswaSel = document.getElementById('mahasiswaSelect');
        if (mahasiswaSel) { mahasiswaSel.innerHTML = '<option value="">-- Pilih Mahasiswa --</option>'; mahasiswaSel.disabled = true; }
        const inPer = document.getElementById('inputNilaiPerorangan'); if (inPer) inPer.value = '';
        const catPer = document.getElementById('catatanNilaiPerorangan'); if (catPer) catPer.value = '';

        // Populate kelompok selects from current rendered table to ensure they're in sync
        const kelompokNames = Array.from(document.querySelectorAll('.kelompok-penilaian-table tbody tr .kelompok-nama')).map(td => td.textContent.replace(/^Kelompok\s*/i,'').trim());
        const uniqueKelompok = [...new Set(kelompokNames)];
        if (selKel) {
            selKel.innerHTML = '<option value="">-- Pilih Kelompok --</option>';
            uniqueKelompok.forEach(k => { const opt = document.createElement('option'); opt.value = k; opt.textContent = `Kelompok ${k}`; selKel.appendChild(opt); });
        }
        if (selKelPer) {
            selKelPer.innerHTML = '<option value="">-- Pilih Kelompok --</option>';
            uniqueKelompok.forEach(k => { const opt = document.createElement('option'); opt.value = k; opt.textContent = `Kelompok ${k}`; selKelPer.appendChild(opt); });
        }

        // keep the data-idkegiatan attribute that was set by click handler
        // focus first field for convenience
        if (selKel) selKel.focus();
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
                    // Refresh kelompok table values for this idKegiatan
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
            
            // For kelompok table, show only group-level nilai (if any). Do not compute or show perorangan averages here.
            kegiatanList.forEach((kegiatan) => {
                const idKegiatan = kegiatan.id;
                const nilaiCell = idKegiatan ? row.querySelector(`.nilai-cell[data-idkegiatan="${idKegiatan}"]`) : null;
                if (nilaiCell) {
                    if (nilaiCell.dataset.groupNilai) {
                        nilaiCell.textContent = Math.round(Number(nilaiCell.dataset.groupNilai));
                    } else {
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
            // Only consider group-level nilai for akhir
            if (cell.dataset && cell.dataset.groupNilai) {
                const nilai = Number(cell.dataset.groupNilai);
                if (!isNaN(nilai)) {
                    totalNilai += nilai;
                    jumlahKegiatan++;
                }
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
        try { attachInputButtonListeners(); } catch (e) { console.warn('attachInputButtonListeners failed', e); }
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
                                    const existingNpm = new Set(dataNilai[kegiatan.name].perorangan.map(x => x.npm));
                                    peroranganVals.forEach(pv => { if (!existingNpm.has(pv.npm)) dataNilai[kegiatan.name].perorangan.push(pv); });
                                }
                            } else {
                                // No group nilai: do NOT display perorangan averages in kelompok column.
                                const peroranganVals = data.filter(m => m.nilai != null).map(m => ({ npm: m.npm, name: m.name, kelompok: kelompok, nilai: Math.round(m.nilai), catatan: m.keterangan || '' }));
                                if (peroranganVals.length > 0) {
                                    if (!dataNilai[kegiatan.name]) dataNilai[kegiatan.name] = { perorangan: [] };
                                    const existingNpm = new Set(dataNilai[kegiatan.name].perorangan.map(x => x.npm));
                                    peroranganVals.forEach(pv => { if (!existingNpm.has(pv.npm)) dataNilai[kegiatan.name].perorangan.push(pv); });
                                }
                                // Keep kelompok column showing '-' when no group nilai exists
                                cell.textContent = '-';
                                delete cell.dataset.groupNilai;
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

        // Ensure listeners on input buttons after data population
        try { attachInputButtonListeners(); } catch (e) { console.warn('attachInputButtonListeners failed', e); }

        // Compute nilai akhir for each row now
        rows.forEach(r => {
            const kelompok = r.querySelector('.kelompok-nama').textContent.replace(/^Kelompok\s*/i, '').trim();
            updateNilaiAkhir(r, kelompok);
        });
    }
    } catch (e) {
        console.error('Error in dosenPenilaian.js:', e);
    }
});