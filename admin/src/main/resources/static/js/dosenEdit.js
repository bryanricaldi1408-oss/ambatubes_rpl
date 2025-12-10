document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const formTugas = document.querySelector(".detail-form-card");
    const btnUploadDirect = document.querySelector(".upload-file-btn");
    const btnLogout = document.querySelector(".logout-btn");
    
    // Input Fields
    const inputNama = document.getElementById("namaTugas");
    const inputDeskripsi = document.getElementById("deskripsi");

    // --- 2. LOGIKA TOMBOL SAVE (SUBMIT FORM) ---
    if (formTugas) {
        formTugas.addEventListener("submit", function (event) {
            // Mencegah reload halaman default
            event.preventDefault();

            // Ambil data dari input
            const namaTugas = inputNama.value;
            const deskripsi = inputDeskripsi.value;

            // Validasi
            if (!namaTugas.trim()) {
                alert("Nama tugas wajib diisi!");
                inputNama.focus();
                return;
            }

            // Simpan data ke LocalStorage (sementara)
            localStorage.setItem("currentTugas_Nama", namaTugas);
            localStorage.setItem("currentTugas_Deskripsi", deskripsi);

            // Ambil kelasId dari form
            const kelasIdInput = document.querySelector('input[name="kelasId"]');
            const kelasId = kelasIdInput ? kelasIdInput.value : null;

            if (!kelasId) {
                alert("Error: Tidak dapat menemukan ID kelas");
                return;
            }

            // Tampilkan loading
            const saveBtn = document.querySelector('.save-btn');
            const originalText = saveBtn.textContent;
            saveBtn.textContent = 'Menyimpan...';
            saveBtn.disabled = true;

            // Simpan data ke server (AJAX) atau langsung redirect
            // Untuk sementara, kita langsung redirect
            
            // Simulasi delay untuk UX
            setTimeout(() => {
                // REDIRECT: Pindah ke halaman Step 2 dengan kelasId
                window.location.href = '/dosen/upload-jadwal?kelasId=' + kelasId;
            }, 1000);
        });
    }

    // --- 3. TOMBOL UPLOAD FILE ---
    if (btnUploadDirect) {
        btnUploadDirect.addEventListener("click", function () {
            // Panggil fungsi upload file
            handleUploadFile();
        });
    }

    // Fungsi handle upload file
    function handleUploadFile() {
        const input = document.createElement('input');
        input.type = 'file';
        input.multiple = true;
        input.accept = '.pdf,.doc,.docx,.txt,.zip,.rar,.ppt,.pptx,.xls,.xlsx';
        
        input.onchange = function(e) {
            const files = e.target.files;
            if (files.length > 0) {
                // Tampilkan informasi file
                let fileList = '';
                for (let i = 0; i < Math.min(files.length, 3); i++) {
                    fileList += `\n• ${files[i].name}`;
                }
                if (files.length > 3) {
                    fileList += `\n• ...dan ${files.length - 3} file lainnya`;
                }
                
                alert(`Berhasil memilih ${files.length} file(s):${fileList}`);
                
                // Di sini bisa ditambahkan logic untuk upload file ke server
                // uploadFilesToServer(files);
            }
        };
        
        input.click();
    }

    // --- 4. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                // Clear localStorage data tugas
                localStorage.removeItem("currentTugas_Nama");
                localStorage.removeItem("currentTugas_Deskripsi");
                
                // Arahkan ke halaman logout
                window.location.href = '/logout';
            }
        });
    }

    // --- 5. PROGRESS BAR CLICKABLE ---
    const steps = document.querySelectorAll(".step:not(.disabled)");
    steps.forEach((step, index) => {
        step.addEventListener("click", () => {
            // Ambil kelasId dari URL atau form
            const urlParams = new URLSearchParams(window.location.search);
            let kelasId = urlParams.get('kelasId');
            
            if (!kelasId) {
                // Coba ambil dari hidden input
                const kelasIdInput = document.querySelector('input[name="kelasId"]');
                kelasId = kelasIdInput ? kelasIdInput.value : null;
            }
            
            if (!kelasId) {
                alert("Error: Tidak dapat menemukan ID kelas");
                return;
            }
            
            // Navigasi berdasarkan step
            if (index === 0) {
                window.location.href = '/dosen/edit?kelasId=' + kelasId;
            } else if (index === 1) {
                // Cek apakah form sudah diisi sebelum lanjut
                const namaTugas = inputNama.value.trim();
                if (!namaTugas) {
                    alert("Silakan isi nama tugas terlebih dahulu sebelum melanjutkan!");
                    inputNama.focus();
                    return;
                }
                window.location.href = '/dosen/upload-jadwal?kelasId=' + kelasId;
            } else if (index === 2) {
                window.location.href = '/dosen/upload-rubrik?kelasId=' + kelasId;
            } else if (index === 3) {
                window.location.href = '/dosen/buat-kelompok?kelasId=' + kelasId;
            } else if (index === 4) {
                window.location.href = '/dosen/penilaian?kelasId=' + kelasId;
            }
        });
    });

    // --- 6. AUTO-SAVE DRAFT (OPTIONAL) ---
    // Simpan draft otomatis setiap 30 detik
    let autoSaveTimer;
    
    function autoSaveDraft() {
        const namaTugas = inputNama.value;
        const deskripsi = inputDeskripsi.value;
        const kelasId = document.querySelector('input[name="kelasId"]')?.value;
        
        if (namaTugas || deskripsi) {
            const draftData = {
                namaTugas: namaTugas,
                deskripsi: deskripsi,
                kelasId: kelasId,
                timestamp: new Date().toISOString()
            };
            
            localStorage.setItem('tugasDraft', JSON.stringify(draftData));
            console.log('Draft disimpan otomatis');
        }
    }
    
    // Setup auto-save
    if (inputNama && inputDeskripsi) {
        inputNama.addEventListener('input', () => {
            clearTimeout(autoSaveTimer);
            autoSaveTimer = setTimeout(autoSaveDraft, 30000); // 30 detik
        });
        
        inputDeskripsi.addEventListener('input', () => {
            clearTimeout(autoSaveTimer);
            autoSaveTimer = setTimeout(autoSaveDraft, 30000); // 30 detik
        });
    }
    
    // --- 7. LOAD DRAFT (OPTIONAL) ---
    // Muat draft yang tersimpan jika ada
    function loadDraft() {
        const draftData = localStorage.getItem('tugasDraft');
        if (draftData) {
            try {
                const draft = JSON.parse(draftData);
                const currentKelasId = document.querySelector('input[name="kelasId"]')?.value;
                
                // Hanya load draft jika untuk kelas yang sama
                if (draft.kelasId === currentKelasId) {
                    if (inputNama && !inputNama.value) {
                        inputNama.value = draft.namaTugas || '';
                    }
                    if (inputDeskripsi && !inputDeskripsi.value) {
                        inputDeskripsi.value = draft.deskripsi || '';
                    }
                    
                    console.log('Draft dimuat:', new Date(draft.timestamp).toLocaleString());
                }
            } catch (e) {
                console.error('Error loading draft:', e);
            }
        }
    }
    
    // Load draft saat halaman dimuat
    loadDraft();
});