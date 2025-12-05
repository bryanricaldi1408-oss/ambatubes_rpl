const ALL_STUDENTS = [
    { name: 'Ahmad Rizki', npm: '6182301010' },
    { name: 'Budi Santoso', npm: '6182301011' },
    { name: 'Citra Lestari', npm: '6182301012' },
    { name: 'Dewi Anggraini', npm: '6182301013' },
    { name: 'Eko Prasetyo', npm: '6182301014' },
    { name: 'Fitriani Sari', npm: '6182301015' },
    { name: 'Gunawan Wibowo', npm: '6182301016' },
    { name: 'Hana Melati', npm: '6182301017' },
    { name: 'Irfan Maulana', npm: '6182301018' },
    { name: 'Jihan Amanda', npm: '6182301019' },
    { name: 'Kurniawan Adi', npm: '6182301020' },
    { name: 'Lestari Ningrum', npm: '6182301021' },
    // Anggota yang sudah ada di kelompokDataContoh:
    { name: 'Robert Saputra', npm: '6182301001' },
    { name: 'Bryan Ricaldi', npm: '6182301002' },
    { name: 'Ethan', npm: '6182301003' },
    { name: 'Eric Leilpaly', npm: '6182301007' },
    { name: 'Sarah Johnson', npm: '6182301004' },
    { name: 'Michael Brown', npm: '6182301005' },
    { name: 'Lisa Anderson', npm: '6182301006' }
];

document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const btnLogout = document.querySelector(".logout-btn");
    const kelompokForm = document.getElementById("kelompokForm");
    const tabelContainer = document.getElementById("tabelContainer");
    const tableBody = document.getElementById("tableBody");
    const selesaiBtn = document.getElementById("selesaiBtn");

    // --- DATA KELOMPOK ---
    const kelompokDataContoh = {
        'Kelompok A': [
            { name: 'Robert Saputra', npm: '6182301001' },
            { name: 'Bryan Ricaldi', npm: '6182301002' },
            { name: 'Ethan', npm: '6182301003' },
            { name: 'Eric Leilpaly', npm: '6182301007' }
        ],
        'Kelompok B': [
            { name: 'Sarah Johnson', npm: '6182301004' },
            { name: 'Michael Brown', npm: '6182301005' },
            { name: 'Lisa Anderson', npm: '6182301006' }
        ]
    };

    // --- 2. HANDLE FORM SUBMIT ---
    if (kelompokForm) {
        kelompokForm.addEventListener("submit", function (e) {
            e.preventDefault();
            
            // Ambil nilai dari form (TANPA autoCreate)
            const jumlahGrup = parseInt(document.getElementById("jumlahGrup").value);
            const jumlahAnggota = parseInt(document.getElementById("jumlahAnggota").value);
            
            // Validasi input
            if (!jumlahGrup || !jumlahAnggota) {
                alert("Silakan isi jumlah grup dan jumlah anggota!");
                return;
            }
            
            if (jumlahGrup < 1 || jumlahAnggota < 1) {
                alert("Jumlah grup dan anggota harus lebih dari 0!");
                return;
            }
            
            // Buat tabel kelompok (TANPA autoCreate parameter)
            buatTabelKelompok(jumlahGrup, jumlahAnggota);
            
            // Tampilkan tabel container
            tabelContainer.style.display = "block";
            
            // Scroll ke tabel
            tabelContainer.scrollIntoView({ behavior: 'smooth' });
        });
    }

    // --- 3. FUNGSI UNTUK MEMBUAT TABEL KELOMPOK (TANPA autoCreate) ---
    function buatTabelKelompok(jumlahGrup, maxAnggota) {
        // Kosongkan tabel sebelumnya
        tableBody.innerHTML = "";
        
        // Buat data kelompok (contoh data)
        const kelompokData = [];
        const kelompokHuruf = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
        
        for (let i = 0; i < jumlahGrup; i++) {
            const namaKelompok = `Kelompok ${kelompokHuruf[i]}`;
            
            // Gunakan data contoh jika tersedia, atau array kosong (TIDAK ada autoCreate)
            const anggota = kelompokDataContoh[namaKelompok] ? 
                kelompokDataContoh[namaKelompok].map(member => member.name) : 
                []; // Selalu array kosong karena tidak ada autoCreate
            
            kelompokData.push({
                nama: namaKelompok,
                jumlahAnggota: anggota.length,
                maxAnggota: maxAnggota,
                anggota: anggota
            });
        }
        
        // Isi tabel dengan data
        kelompokData.forEach((kelompok, index) => {
            const row = document.createElement("tr");
            
            // Kolom Nama Kelompok
            const tdNama = document.createElement("td");
            tdNama.className = "kelompok-nama";
            tdNama.textContent = kelompok.nama;
            
            // Kolom Jumlah Anggota
            const tdJumlah = document.createElement("td");
            tdJumlah.className = "jumlah-anggota";
            tdJumlah.textContent = `${kelompok.jumlahAnggota}/${kelompok.maxAnggota}`;
            
            // Kolom Anggota
            const tdAnggota = document.createElement("td");
            tdAnggota.textContent = kelompok.anggota.join(", ");
            
            // Kolom Aksi (Edit)
            const tdAksi = document.createElement("td");
            const editBtn = document.createElement("button");
            editBtn.className = "edit-kelompok-btn";
            editBtn.textContent = "Edit";
            editBtn.addEventListener("click", function() {
                openEditPopup(kelompok.nama);
            });
            tdAksi.appendChild(editBtn);
            
            // Tambahkan kolom ke row
            row.appendChild(tdNama);
            row.appendChild(tdJumlah);
            row.appendChild(tdAnggota);
            row.appendChild(tdAksi);
            
            // Tambahkan row ke tabel
            tableBody.appendChild(row);
        });
    }

    // --- 4. FUNGSI UNTUK GENERATE ANGGOTA OTOMATIS (TIDAK DIPAKAI LAGI) ---
    // function generateAnggota(jumlah) {
    //     const namaContoh = [
    //         "Ahmad Rizki", "Budi Santoso", "Citra Lestari", "Dewi Anggraini", 
    //         "Eko Prasetyo", "Fitriani Sari", "Gunawan Wibowo", "Hana Melati",
    //         "Irfan Maulana", "Jihan Amanda", "Kurniawan Adi", "Lestari Ningrum",
    //         "Mochammad Ali", "Nadia Putri", "Oki Setiawan", "Putri Ayu",
    //         "Rizky Pratama", "Sari Indah", "Teguh Wijaya", "Umi Kulsum"
    //     ];
        
    //     const anggota = [];
    //     for (let i = 0; i < jumlah; i++) {
    //         const randomIndex = Math.floor(Math.random() * namaContoh.length);
    //         anggota.push(namaContoh[randomIndex]);
    //     }
    //     return anggota;
    // }

    // --- 5. FUNGSI POPUP EDIT KELOMPOK ---
    function openEditPopup(groupName) {
        document.getElementById('popupGroupName').textContent = groupName;
        
        // Isi tabel dengan data anggota
        const tableBody = document.getElementById('popupTableBody');
        tableBody.innerHTML = '';
        
        const members = kelompokDataContoh[groupName] || [];
        
        members.forEach(member => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${member.name}</td>
                <td>${member.npm}</td>
                <td><button class="hapus-btn" onclick="hapusAnggota(this)">Hapus</button></td>
            `;
            tableBody.appendChild(row);
        });
        
        // Tampilkan popup
        document.getElementById('editPopup').style.display = 'flex';
        // Sembunyikan tombol "Tambahkan Anggota" (kita ganti dengan search bar)
        document.getElementById('tambahAnggotaBtn').style.display = 'none';

        // Inisialisasi Search Bar
        const searchInput = document.getElementById('memberSearchInput');
        const searchResultsContainer = document.getElementById('searchResults');
        
        // Kosongkan hasil search sebelumnya
        searchInput.value = '';
        searchResultsContainer.innerHTML = ''; 

        // Tambahkan event listener untuk search
        searchInput.removeEventListener('input', handleMemberSearch); // Hapus jika sudah ada
        searchInput.addEventListener('input', handleMemberSearch);
    }

    // Fungsi untuk memfilter dan menampilkan hasil pencarian
    function handleMemberSearch() {
        const searchInput = document.getElementById('memberSearchInput');
        const searchResultsContainer = document.getElementById('searchResults');
        const searchTerm = searchInput.value.toLowerCase().trim();
        const groupName = document.getElementById('popupGroupName').textContent;
        
        searchResultsContainer.innerHTML = '';
        
        if (searchTerm.length < 3) {
            searchResultsContainer.innerHTML = `
                <div style="padding: 10px; color: #666; font-style: italic; border: none; background: transparent;">
                    Ketik minimal 3 karakter untuk mencari.
                </div>
            `;
            return;
        }

        // Mendapatkan NPM anggota yang sudah ada dalam grup saat ini
        const existingNPMs = (kelompokDataContoh[groupName] || []).map(member => member.npm);

        const filteredStudents = ALL_STUDENTS.filter(student => {
            const studentName = student.name.toLowerCase();
            const studentNPM = student.npm;
            
            // Cek jika sudah ada di grup ini
            const isExisting = existingNPMs.includes(studentNPM);

            // Filter berdasarkan Nama ATAU NPM
            const matchesSearch = studentName.includes(searchTerm) || studentNPM.includes(searchTerm);
            
            return matchesSearch && !isExisting; // Tampilkan yang match dan BUKAN anggota grup ini
        });

        if (filteredStudents.length === 0) {
            searchResultsContainer.innerHTML = '<div style="padding: 10px; color: #e74c3c;">Tidak ada mahasiswa ditemukan atau sudah di dalam kelompok.</div>';
            return;
        }

        // Tampilkan hasil pencarian
        filteredStudents.forEach(student => {
            const resultItem = document.createElement('div');
            resultItem.className = 'search-result-item';
            resultItem.style.cssText = 'display: flex; justify-content: space-between; align-items: center; padding: 10px; border-bottom: 1px solid #eee; cursor: pointer; transition: background-color 0.2s;';
            resultItem.innerHTML = `
                <div>
                    <span style="font-family: 'nunitoBold';">${student.name}</span>
                    <br><span style="font-size: 12px; color: #666;">NPM: ${student.npm}</span>
                </div>
                <button class="tambah-ke-grup-btn" data-name="${student.name}" data-npm="${student.npm}" 
                        style="background-color: #2ecc71; color: white; border: none; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-size: 12px;">
                    + Tambah
                </button>
            `;
            
            // Tambahkan event listener untuk tombol tambah
            resultItem.querySelector('.tambah-ke-grup-btn').addEventListener('click', function(e) {
                e.stopPropagation();
                addMemberToGroup(this.dataset.name, this.dataset.npm);
            });
            
            searchResultsContainer.appendChild(resultItem);
        });
    }

    // Fungsi untuk menambahkan anggota ke grup dari hasil pencarian
    function addMemberToGroup(name, npm) {
        const groupName = document.getElementById('popupGroupName').textContent;
        const tableBody = document.getElementById('popupTableBody');
        
        // Tambahkan ke data simulasi (kelompokDataContoh)
        if (!kelompokDataContoh[groupName]) {
            kelompokDataContoh[groupName] = [];
        }
        
        // Periksa duplikasi terakhir kali (meskipun sudah difilter di search)
        const isDuplicate = kelompokDataContoh[groupName].some(member => member.npm === npm);
        if (isDuplicate) {
            alert(`${name} (${npm}) sudah menjadi anggota kelompok ini.`);
            return;
        }

        kelompokDataContoh[groupName].push({ name, npm });
        
        // Tambahkan ke tampilan tabel di popup
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
            <td>${name}</td>
            <td>${npm}</td>
            <td><button class="hapus-btn" onclick="hapusAnggota(this)">Hapus</button></td>
        `;
        tableBody.appendChild(newRow);
        
        // Feedback dan refresh hasil search
        alert(`${name} berhasil ditambahkan ke ${groupName}!`);
        document.getElementById('memberSearchInput').value = '';
        document.getElementById('searchResults').innerHTML = '';
    }

    // Fungsi untuk menutup popup
    function closeEditPopup() {
        document.getElementById('editPopup').style.display = 'none';
    }

    // Fungsi untuk menghapus anggota
    window.hapusAnggota = function(button) {
        const row = button.closest('tr');
        const groupName = document.getElementById('popupGroupName').textContent;
        const memberName = row.cells[0].textContent;
        
        if (confirm(`Apakah Anda yakin ingin menghapus ${memberName} dari kelompok?`)) {
            row.remove();
            // Update data di kelompokDataContoh
            const index = kelompokDataContoh[groupName].findIndex(member => member.name === memberName);
            if (index !== -1) {
                kelompokDataContoh[groupName].splice(index, 1);
            }
        }
    };

    // Fungsi untuk menambah anggota baru (tidak dipakai karena sudah ada search)
    // function tambahAnggota() {
    //     const groupName = document.getElementById('popupGroupName').textContent;
    //     const tableBody = document.getElementById('popupTableBody');
        
    //     // Buat form input untuk anggota baru
    //     const newRow = document.createElement('tr');
    //     newRow.innerHTML = `
    //         <td><input type="text" class="new-member-name" placeholder="Nama anggota" style="width: 100%; padding: 5px; border: 1px solid #ddd; border-radius: 3px;"></td>
    //         <td><input type="text" class="new-member-npm" placeholder="NPM" style="width: 100%; padding: 5px; border: 1px solid #ddd; border-radius: 3px;"></td>
    //         <td>
    //             <button class="simpan-btn" onclick="simpanAnggotaBaru(this)" style="padding: 5px 10px; font-size: 12px; margin-right: 5px;">Simpan</button>
    //             <button class="hapus-btn" onclick="batalTambahAnggota(this)" style="padding: 5px 10px; font-size: 12px;">Batal</button>
    //         </td>
    //     `;
    //     tableBody.appendChild(newRow);
    // }

    // Fungsi untuk menyimpan anggota baru
    // function simpanAnggotaBaru(button) {
    //     const row = button.closest('tr');
    //     const nameInput = row.querySelector('.new-member-name');
    //     const npmInput = row.querySelector('.new-member-npm');
        
    //     const name = nameInput.value.trim();
    //     const npm = npmInput.value.trim();
        
    //     if (!name || !npm) {
    //         alert('Nama dan NPM harus diisi!');
    //         return;
    //     }
        
    //     const groupName = document.getElementById('popupGroupName').textContent;
        
    //     // Tambahkan ke data
    //     if (!kelompokDataContoh[groupName]) {
    //         kelompokDataContoh[groupName] = [];
    //     }
        
    //     kelompokDataContoh[groupName].push({ name, npm });
        
    //     // Update tampilan
    //     row.innerHTML = `
    //         <td>${name}</td>
    //         <td>${npm}</td>
    //         <td><button class="hapus-btn" onclick="hapusAnggota(this)">Hapus</button></td>
    //     `;
    // }

    // Fungsi untuk membatalkan penambahan anggota
    // function batalTambahAnggota(button) {
    //     const row = button.closest('tr');
    //     row.remove();
    // }

    // Fungsi untuk menyimpan perubahan
    function saveChanges() {
        // Di sini Anda bisa menambahkan logika untuk menyimpan perubahan ke server
        alert('Perubahan berhasil disimpan!');
        closeEditPopup();
        
        // Refresh tampilan tabel utama jika perlu
        window.location.reload();
    }

    // --- 6. EVENT LISTENERS POPUP ---
    document.getElementById('closePopup').addEventListener('click', closeEditPopup);
    // document.getElementById('tambahAnggotaBtn').addEventListener('click', tambahAnggota); // Tidak dipakai
    document.getElementById('simpanPerubahanBtn').addEventListener('click', saveChanges);
    
    // Tutup popup jika mengklik di luar konten popup
    document.getElementById('editPopup').addEventListener('click', function(event) {
        if (event.target === this) {
            closeEditPopup();
        }
    });
    
    // Tutup popup dengan tombol ESC
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeEditPopup();
        }
    });

    // --- 7. HANDLE TOMBOL SELESAI ---
    if (selesaiBtn) {
        selesaiBtn.addEventListener("click", function() {
            if (confirm("Apakah Anda yakin kelompok sudah selesai dibuat?")) {
                // Redirect ke halaman penilaian
                window.location.href = "dosenPenilaian.html";
            }
        });
    }

    // --- 8. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                window.location.href = "index.html";
            }
        });
    }

    // --- 9. PROGRESS BAR CLICKABLE ---
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
                // Sudah di halaman ini
                return;
            }
            else if(index === 4){
                window.location.href = "dosenPenilaian.html";
            }
        });
    });
});

// Hapus fungsi yang tidak dipakai lagi
// window.simpanAnggotaBaru = function(button) {
//     const row = button.closest('tr');
//     const nameInput = row.querySelector('.new-member-name');
//     const npmInput = row.querySelector('.new-member-npm');
    
//     const name = nameInput.value.trim();
//     const npm = npmInput.value.trim();
    
//     if (!name || !npm) {
//         alert('Nama dan NPM harus diisi!');
//         return;
//     }
    
//     row.innerHTML = `
//         <td>${name}</td>
//         <td>${npm}</td>
//         <td><button class="hapus-btn" onclick="hapusAnggota(this)">Hapus</button></td>
//     `;
// };

// window.batalTambahAnggota = function(button) {
//     const row = button.closest('tr');
//     row.remove();
// };