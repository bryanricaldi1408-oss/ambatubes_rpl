document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const formTugas = document.querySelector(".detail-form-card");
    const btnLogout = document.querySelector(".logout-btn");
    const inputNama = document.getElementById("namaTugas");
    const inputDeskripsi = document.getElementById("deskripsi");

    // --- 2. SIMPAN PARAMETER DARI URL ---
    const urlParams = new URLSearchParams(window.location.search);
    const kelasIdFromUrl = urlParams.get('kelasId');
    const idTubesFromUrl = urlParams.get('idTubes');
    
    if (kelasIdFromUrl) {
        localStorage.setItem('currentKelasId', kelasIdFromUrl);
    }
    if (idTubesFromUrl) {
        localStorage.setItem('currentIdTubes', idTubesFromUrl);
    }

    // --- 3. PROGRESS BAR CLICK ---
    const steps = document.querySelectorAll(".step:not(.disabled)");
    steps.forEach((step, index) => {
        step.addEventListener("click", function() {
            // Ambil data dari localStorage
            let kelasId = localStorage.getItem('currentKelasId');
            let idTubes = localStorage.getItem('currentIdTubes');
            
            // Validasi
            if (!kelasId) {
                alert("Error: Tidak dapat menemukan ID kelas");
                return;
            }
            
            // Navigasi
            switch(index) {
                case 0: // Step 1: Edit/Buat Tugas
                    if (idTubes) {
                        window.location.href = `/dosen/edit?kelasId=${kelasId}&idTubes=${idTubes}`;
                    } else {
                        window.location.href = `/dosen/edit?kelasId=${kelasId}`;
                    }
                    break;
                    
                case 1: // Step 2: Upload Jadwal
                    if (idTubes) {
                        window.location.href = `/dosen/upload-jadwal?kelasId=${kelasId}&idTubes=${idTubes}`;
                    } else {
                        alert("Silakan simpan tugas terlebih dahulu!");
                    }
                    break;
                    
                case 2: // Step 3: Upload Rubrik
                    if (idTubes) {
                        window.location.href = `/dosen/upload-rubrik?kelasId=${kelasId}&idTubes=${idTubes}`;
                    } else {
                        alert("Silakan selesaikan step sebelumnya");
                    }
                    break;
                    
                case 3: // Step 4: Buat Kelompok
                    if (idTubes) {
                        window.location.href = `/dosen/buat-kelompok?kelasId=${kelasId}&idTubes=${idTubes}`;
                    } else {
                        alert("Silakan selesaikan step sebelumnya");
                    }
                    break;
                    
                case 4: // Step 5: Penilaian
                    if (idTubes) {
                        window.location.href = `/dosen/penilaian?kelasId=${kelasId}&idTubes=${idTubes}`;
                    } else {
                        alert("Silakan selesaikan semua step");
                    }
                    break;
            }
        });
    });

    // --- 4. LOGIKA SAVE FORM ---
    if (formTugas) {
        formTugas.addEventListener("submit", function (event) {
            event.preventDefault();

            const namaTugas = inputNama.value;
            const deskripsi = inputDeskripsi.value;

            if (!namaTugas.trim()) {
                alert("Nama tugas wajib diisi!");
                inputNama.focus();
                return;
            }

            // Tampilkan loading
            const saveBtn = document.querySelector('.save-btn');
            saveBtn.textContent = 'Menyimpan...';
            saveBtn.disabled = true;

            // Submit form
            formTugas.submit();
        });
    }

    // --- 5. LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            if (confirm("Apakah Anda yakin ingin keluar?")) {
                localStorage.clear();
                window.location.href = '/logout';
            }
        });
    }
});