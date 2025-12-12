document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const btnLogout = document.querySelector(".logout-btn");
    const saveBtn = document.getElementById('saveBtn');
    const dropzone = document.querySelector('.blue-dropzone');
    const fileInput = document.getElementById('rubrikFile');
    const plusSign = document.getElementById('plusSign');
    const fileNameDisplay = document.getElementById('fileNameDisplay');
    const uploadForm = document.getElementById('uploadForm');

    // --- 2. AMBIL PARAMETER DARI URL ---
    const urlParams = new URLSearchParams(window.location.search);
    const kelasId = urlParams.get('kelasId');
    const idTubes = urlParams.get('idTubes');

    // --- 3. HANDLE FILE UPLOAD ---
    if (dropzone && fileInput) {
        // File dipilih
        fileInput.addEventListener('change', function(e) {
            if (this.files && this.files[0]) {
                const fileName = this.files[0].name;
                fileNameDisplay.textContent = fileName;
                fileNameDisplay.style.display = 'block';
                if(plusSign) plusSign.style.display = 'none';
                if(saveBtn) saveBtn.disabled = false;
            }
        });
        
        // Drag & drop
        dropzone.addEventListener('dragover', (e) => e.preventDefault());
        dropzone.addEventListener('drop', (e) => {
            e.preventDefault();
            if (e.dataTransfer.files.length) {
                fileInput.files = e.dataTransfer.files;
                fileInput.dispatchEvent(new Event('change'));
            }
        });
    }

    // --- 4. LOGIKA TOMBOL SAVE ---
    if (saveBtn && uploadForm) {
        saveBtn.addEventListener('click', async function(e) {
            e.preventDefault();
            if (!fileInput.files || fileInput.files.length === 0) {
                alert('Silakan pilih file rubrik terlebih dahulu!');
                return;
            }
            this.textContent = 'Menyimpan...';
            this.disabled = true;

            try {
                const formData = new FormData(uploadForm);
                const response = await fetch(uploadForm.action, {
                    method: 'POST',
                    body: formData
                });
                if (response.ok) {
                    const data = await response.json();
                    if (data.success) {
                        alert(data.message || 'Rubrik berhasil diupload!');
                        if (data.redirect) {
                            window.location.href = data.redirect;
                        } else {
                            window.location.href = `/dosen/buat-kelompok?kelasId=${kelasId}&idTubes=${idTubes}`;
                        }
                    } else {
                        alert(data.message || 'Gagal mengupload rubrik.');
                        this.textContent = 'Save & Continue';
                        this.disabled = false;
                    }
                } else {
                     throw new Error('Network response was not ok');
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Terjadi kesalahan saat mengupload rubrik.');
                this.textContent = 'Save & Continue';
                this.disabled = false;
            }
        });
    }

    // --- 5. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            if (confirm("Apakah Anda yakin ingin keluar?")) {
                window.location.href = "/logout";
            }
        });
    }

    // --- 6. PROGRESS BAR CLICKABLE ---
    const steps = document.querySelectorAll(".step[data-step]");
    steps.forEach((step) => {
        step.addEventListener("click", function() {
            const stepNumber = this.getAttribute('data-step');
            if (!this.classList.contains('active') && !this.classList.contains('disabled')) {
                if (!kelasId || !idTubes) {
                    alert('Error: ID Kelas atau ID Tugas tidak ditemukan.');
                    return;
                }
                let url = '';
                switch(stepNumber) {
                    case '1': url = `/dosen/edit?kelasId=${kelasId}&idTubes=${idTubes}`; break;
                    case '2': url = `/dosen/upload-jadwal?kelasId=${kelasId}&idTubes=${idTubes}`; break;
                    case '4': url = `/dosen/buat-kelompok?kelasId=${kelasId}&idTubes=${idTubes}`; break;
                }
                if(url) window.location.href = url;
            }
        });
    });
});