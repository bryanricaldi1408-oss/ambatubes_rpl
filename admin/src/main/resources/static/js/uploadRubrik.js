document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const btnLogout = document.querySelector(".logout-btn");
    const saveBtn = document.querySelector('.btn-save');
    const dropzone = document.querySelector('.blue-dropzone');
    const fileInput = document.querySelector('input[type="file"]');
    
    // --- 2. HANDLE FILE UPLOAD ---
    if (dropzone && fileInput) {
        // Create element untuk menampilkan nama file
        const fileNameDisplay = document.createElement('div');
        fileNameDisplay.className = 'file-name-display';
        fileNameDisplay.style.display = 'none';
        dropzone.appendChild(fileNameDisplay);

        // File dipilih
        fileInput.addEventListener('change', function(e) {
            if (this.files && this.files[0]) {
                const fileName = this.files[0].name;
                
                // Tampilkan nama file dan sembunyikan plus sign
                const plusSign = dropzone.querySelector('.plus-sign');
                fileNameDisplay.textContent = fileName;
                fileNameDisplay.style.display = 'block';
                plusSign.style.display = 'none';
            }
        });
    }

    // --- 3. LOGIKA TOMBOL SAVE ---
    if (saveBtn) {
        saveBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Validasi: cek apakah file sudah diupload
            if (!fileInput.files || fileInput.files.length === 0) {
                alert('Silakan pilih file jadwal terlebih dahulu!');
                return;
            }

            // Tampilkan loading state
            const originalText = this.textContent;
            this.textContent = 'Menyimpan...';
            this.disabled = true;

            // Simulasi proses upload
            setTimeout(() => {
                // Reset button state
                this.textContent = originalText;
                this.disabled = false;
                
                // Redirect ke halaman berikutnya
                window.location.href = 'dosenBuatKelompok.html';
            }, 1500);
        });
    }

    // --- 4. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                window.location.href = "index.html";
            }
        });
    }

    // --- 5. PROGRESS BAR CLICKABLE ---
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
            else if(index === 4){
                window.location.href = "dosenPenilaian.html";
            }
        });
    });
});