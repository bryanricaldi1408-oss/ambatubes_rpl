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

    // --- CHECK EXISTING DATA FROM SERVER ---
    if (typeof serverExistingGroups !== 'undefined' && serverExistingGroups.length > 0) {
        console.log("Loading existing groups:", serverExistingGroups);
        renderTabelKelompok(serverExistingGroups);
        tabelContainer.style.display = "block";
    }

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
            
            // Ambil URL params
            const urlParams = new URLSearchParams(window.location.search);
            const kelasId = urlParams.get('kelasId');
            const idTubes = urlParams.get('idTubes');
            
            if (!kelasId || !idTubes) {
                alert("Error: Data parameter hilang (kelasId/idTubes)");
                return;
            }

            // Ambil nilai dari form
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

            // Panggil API Backend
            const formData = new FormData();
            formData.append('kelasId', kelasId);
            formData.append('idTubes', idTubes);
            formData.append('jumlahGrup', jumlahGrup);
            formData.append('jumlahAnggota', jumlahAnggota);

            // Tampilkan loading state (opsional)
            const btnSubmit = kelompokForm.querySelector('button[type="submit"]');
            const originalText = btnSubmit.textContent;
            btnSubmit.textContent = "Processing...";
            btnSubmit.disabled = true;

            fetch('/dosen/generate-kelompok', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    
                    alert(data.message);
                    window.location.reload();
                } else {
                    alert("Gagal: " + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("Terjadi kesalahan sistem saat generate kelompok.");
            })
            .finally(() => {
                btnSubmit.textContent = originalText;
                btnSubmit.disabled = false;
            });
        });
    }

    // --- 3. FUNGSI UNTUK MEMBUAT TABEL KELOMPOK (TANPA autoCreate) ---
    // --- 3. FUNGSI UNTUK MERENDER TABEL KELOMPOK DARI DATA SERVER ---
    function renderTabelKelompok(groupsData) {
        // Kosongkan tabel sebelumnya
        tableBody.innerHTML = "";
        
        // Isi tabel dengan data
        groupsData.forEach((kelompok, index) => {
            const row = document.createElement("tr");
            
            // Kolom Nama Kelompok
            const tdNama = document.createElement("td");
            tdNama.className = "kelompok-nama";
            tdNama.textContent = kelompok.nama; // Ex: "Kelompok A"
            
            // Kolom Jumlah Anggota
            const tdJumlah = document.createElement("td");
            tdJumlah.className = "jumlah-anggota";
            tdJumlah.textContent = `${kelompok.jumlahAnggota}/${kelompok.maxAnggota}`;
            
            // Kolom Anggota
            const tdAnggota = document.createElement("td");
            // Cek jika anggota kosong
            if (kelompok.anggota && kelompok.anggota.length > 0) {
                 tdAnggota.textContent = kelompok.anggota.map(m => m.name).join(", ");
            } else {
                 tdAnggota.textContent = "-";
            }
           
            
            // Kolom Aksi (Edit)
            const tdAksi = document.createElement("td");
            const editBtn = document.createElement("button");
            editBtn.className = "edit-kelompok-btn";
            editBtn.textContent = "Edit";
            editBtn.addEventListener("click", function() {
                openEditPopup(kelompok.nama, kelompok.id, kelompok.anggota);
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
    let currentGroupId = null;
    let allAvailableStudents = []; // Cache for available students

    function openEditPopup(groupName, groupId, members) {
        document.getElementById('popupGroupName').textContent = groupName;
        currentGroupId = groupId;
        
        const tableBody = document.getElementById('popupTableBody');
        tableBody.innerHTML = '';
        
        members.forEach(member => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${member.name}</td>
                <td>${member.npm}</td>
                <td><button class="hapus-btn" onclick="hapusAnggota(this, '${member.npm}')">Hapus</button></td>
            `;
            tableBody.appendChild(row);
        });
        
        document.getElementById('editPopup').style.display = 'flex';
        document.getElementById('tambahAnggotaBtn').style.display = 'none';

        const searchInput = document.getElementById('memberSearchInput');
        const searchResultsContainer = document.getElementById('searchResults');
        
        searchInput.value = '';
        searchResultsContainer.innerHTML = '<div style="padding: 10px; color: #666;">Loading students...</div>';
        
        searchInput.oninput = handleMemberSearch;

        // Fetch all available students immediately
        const urlParams = new URLSearchParams(window.location.search);
        const kelasId = urlParams.get('kelasId');
        const idTubes = urlParams.get('idTubes');

        fetch(`/dosen/kelompok/available-students?idTubes=${idTubes}&kelasId=${kelasId}`)
            .then(res => res.json())
            .then(data => {
                allAvailableStudents = data; // Store in cache
                renderStudentList(allAvailableStudents); // Show all initially
            })
            .catch(err => {
                console.error(err);
                searchResultsContainer.innerHTML = '<div style="padding: 10px; color: red;">Failed to load students.</div>';
            });
    }

    function renderStudentList(students) {
        const searchResultsContainer = document.getElementById('searchResults');
        searchResultsContainer.innerHTML = '';

        if (students.length === 0) {
            searchResultsContainer.innerHTML = '<div style="padding: 10px; color: #666;">Tidak ada mahasiswa yang tersedia.</div>';
            return;
        }

        students.forEach(student => {
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
            
            resultItem.querySelector('.tambah-ke-grup-btn').addEventListener('click', function(e) {
                e.stopPropagation();
                addMemberToGroup(this.dataset.name, this.dataset.npm);
            });
            
            searchResultsContainer.appendChild(resultItem);
        });
    }

    // Fungsi untuk memfilter dan menampilkan hasil pencarian (Client-side)
    function handleMemberSearch() {
        const searchInput = document.getElementById('memberSearchInput');
        const searchTerm = searchInput.value.toLowerCase().trim();
        
        const filteredStudents = allAvailableStudents.filter(student => {
             return student.name.toLowerCase().includes(searchTerm) || student.npm.includes(searchTerm);
        });

        renderStudentList(filteredStudents);
    }

    // Fungsi untuk menambahkan anggota ke grup
    function addMemberToGroup(name, npm) {
        if(!currentGroupId) return;

        const formData = new FormData();
        formData.append('npm', npm);
        formData.append('idKelompok', currentGroupId);

        fetch('/dosen/kelompok/add-member', {
            method: 'POST',
            body: formData
        })
        .then(res => res.json())
        .then(data => {
            if(data.success) {
                alert(`Berhasil menambahkan ${name}!`);
                
                // Add to table immediately
                const tableBody = document.getElementById('popupTableBody');
                const newRow = document.createElement('tr');
                newRow.innerHTML = `
                    <td>${name}</td>
                    <td>${npm}</td>
                    <td><button class="hapus-btn" onclick="hapusAnggota(this, '${npm}')">Hapus</button></td>
                `;
                tableBody.appendChild(newRow);

                // Clear search
                document.getElementById('memberSearchInput').value = '';
                document.getElementById('searchResults').innerHTML = '';
            } else {
                alert('Gagal: ' + data.message);
            }
        })
        .catch(err => alert('Error: ' + err));
    }

    // Fungsi untuk menutup popup
    function closeEditPopup() {
        document.getElementById('editPopup').style.display = 'none';
        window.location.reload(); // Refresh to update main table counts
    }

    // Fungsi untuk menghapus anggota
    window.hapusAnggota = function(button, npm) {
        if(!currentGroupId) return;
        
        const row = button.closest('tr');
        const memberName = row.cells[0].textContent;
        
        if (confirm(`Apakah Anda yakin ingin menghapus ${memberName} dari kelompok?`)) {
            const formData = new FormData();
            formData.append('npm', npm);
            formData.append('idKelompok', currentGroupId);

            fetch('/dosen/kelompok/remove-member', {
                method: 'POST',
                body: formData
            })
            .then(res => res.json())
            .then(data => {
                if(data.success) {
                    row.remove();
                } else {
                    alert('Gagal: ' + data.message);
                }
            })
            .catch(err => alert('Error: ' + err));
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
            if (!confirm("Apakah Anda yakin kelompok sudah selesai dibuat?")) return;

            const urlParams = new URLSearchParams(window.location.search);
            const kelasId = urlParams.get('kelasId');
            const idTubes = urlParams.get('idTubes');

            if (!kelasId || !idTubes) {
                alert('Parameter idTubes/kelasId hilang');
                return;
            }

            const formData = new FormData();
            formData.append('kelasId', kelasId);
            formData.append('idTubes', idTubes);

            fetch('/dosen/selesai-kelompok', {
                method: 'POST',
                body: formData
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    window.location.href = data.redirect;
                } else {
                    alert('Gagal: ' + data.message);
                }
            })
            .catch(err => {
                console.error(err);
                alert('Terjadi kesalahan saat menyelesaikan kelompok');
            });
        });
    }

    // --- 8. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                localStorage.clear();
                window.location.href = "/logout";
            }
        });
    }

    // --- 9. PROGRESS BAR CLICKABLE ---
    // Navigation is now handled by <a> tags in HTML

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