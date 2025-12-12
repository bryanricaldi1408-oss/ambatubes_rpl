document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const btnLogout = document.querySelector(".logout-btn");
    const saveBtn = document.getElementById('saveBtn');
    const dropzone = document.querySelector('.blue-dropzone');
    const fileInput = document.getElementById('jadwalFile');
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
                const fileSize = this.files[0].size;
                const maxSize = 10 * 1024 * 1024; // 10MB
                
                // Validasi ukuran file
                if (fileSize > maxSize) {
                    alert('Ukuran file terlalu besar! Maksimal 10MB');
                    this.value = ''; // Reset input file
                    fileNameDisplay.textContent = '';
                    fileNameDisplay.style.display = 'none';
                    plusSign.style.display = 'block';
                    return;
                }
                
                // Validasi ekstensi file
                const validExtensions = ['.xlsx', '.xls'];
                const fileExt = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
                
                if (!validExtensions.includes(fileExt)) {
                    alert('Format file tidak didukung! Hanya .xlsx atau .xls yang diperbolehkan');
                    this.value = '';
                    fileNameDisplay.textContent = '';
                    fileNameDisplay.style.display = 'none';
                    plusSign.style.display = 'block';
                    return;
                }
                
                // Tampilkan nama file dan sembunyikan plus sign
                fileNameDisplay.textContent = fileName;
                fileNameDisplay.style.display = 'block';
                plusSign.style.display = 'none';
                
                // Enable tombol save
                if (saveBtn) {
                    saveBtn.disabled = false;
                }
            }
        });
        
        // Drag & drop functionality
        dropzone.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.style.backgroundColor = '#004e92';
        });
        
        dropzone.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.style.backgroundColor = '';
        });
        
        dropzone.addEventListener('drop', function(e) {
            e.preventDefault();
            this.style.backgroundColor = '';
            
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
            
            // Validasi: cek apakah file sudah diupload
            if (!fileInput.files || fileInput.files.length === 0) {
                alert('Silakan pilih file jadwal terlebih dahulu!');
                return;
            }

            // Tampilkan loading state
            const originalText = this.textContent;
            this.textContent = 'Menyimpan...';
            this.disabled = true;

            try {
                // Kirim form via AJAX
                const formData = new FormData(uploadForm);
                
                const response = await fetch('/dosen/upload-jadwal-file', {
                    method: 'POST',
                    body: formData
                });

                if (response.ok) {
                    const data = await response.json();
                    
                    if (data.success) {
                        // Tampilkan pesan sukses
                        alert(data.message || 'Jadwal berhasil diupload!');
                        
                        // Redirect ke halaman berikutnya
                        if (data.redirect) {
                            window.location.href = data.redirect;
                        } else {
                            // Default redirect ke upload rubrik
                            window.location.href = '/dosen/upload-rubrik?kelasId=' + kelasId + '&idTubes=' + idTubes;
                        }
                    } else {
                        // Error dari server
                        alert(data.message || 'Gagal mengupload jadwal');
                        this.textContent = originalText;
                        this.disabled = false;
                    }
                } else {
                    throw new Error('Network response was not ok');
                }
                
            } catch (error) {
                console.error('Error:', error);
                alert('Terjadi kesalahan saat mengupload jadwal');
                
                // Reset button state
                this.textContent = originalText;
                this.disabled = false;
            }
        });
    }

    // --- 5. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                window.location.href = "/logout";
            }
        });
    }

    // --- 6. PROGRESS BAR CLICKABLE ---
    const steps = document.querySelectorAll(".step[data-step]");
    steps.forEach((step) => {
        step.addEventListener("click", function() {
            const stepNumber = this.getAttribute('data-step');
            
            if (!kelasId) {
                alert('Error: Tidak dapat menemukan ID kelas');
                return;
            }
            
            // Navigasi berdasarkan step
            if (stepNumber === '1') {
                window.location.href = '/dosen/edit?kelasId=' + kelasId + '&idTubes=' + idTubes;
            } else if (stepNumber === '3') {
                window.location.href = '/dosen/upload-rubrik?kelasId=' + kelasId + '&idTubes=' + idTubes;
            } else if (stepNumber === '4') {
                window.location.href = '/dosen/buat-kelompok?kelasId=' + kelasId + '&idTubes=' + idTubes;
            }
        });
    });

    // --- 7. FUNGSI UNTUK PROGRESS BAR ---
    window.goToStep = function(stepNumber) {
        if (!kelasId) {
            alert('Error: Tidak dapat menemukan ID kelas');
            return;
        }
        
        if (stepNumber === 1) {
            window.location.href = '/dosen/edit?kelasId=' + kelasId + '&idTubes=' + idTubes;
        } else if (stepNumber === 3) {
            window.location.href = '/dosen/upload-rubrik?kelasId=' + kelasId + '&idTubes=' + idTubes;
        } else if (stepNumber === 4) {
            window.location.href = '/dosen/buat-kelompok?kelasId=' + kelasId + '&idTubes=' + idTubes;
        }
    };
});