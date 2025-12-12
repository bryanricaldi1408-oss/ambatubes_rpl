document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const btnLogout = document.querySelector(".logout-btn");

    
    // --- 2. AMBIL PARAMETER DARI URL ---
    const urlParams = new URLSearchParams(window.location.search);
    const kelasId = urlParams.get('kelasId');
    const idTubes = urlParams.get('idTubes');
    

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

    //----------------tambahan buat tabel -----------------------
        
        // Check if there's existing data in session on page load
        checkExistingData();
        
        // File input change handler
        document.getElementById('jadwalFile').addEventListener('change', function(e) {
            const fileNameDisplay = document.getElementById('fileNameDisplay');
            if (this.files.length > 0) {
                fileNameDisplay.textContent = this.files[0].name;
            } else {
                fileNameDisplay.textContent = '';
            }
        });
        
        // Save button click handler
        document.getElementById('saveBtn').addEventListener('click', function() {
            uploadFile();
        });
        
        // Delete button click handler
        document.getElementById('deleteBtn').addEventListener('click', function() {
            deleteData();
        });
        
        // Click on plus sign triggers file input
        document.getElementById('plusSign').addEventListener('click', function() {
            document.getElementById('jadwalFile').click();
        });
        
        // Function to upload file
        function uploadFile() {
            const fileInput = document.getElementById('jadwalFile');
            if (!fileInput.files.length) {
                alert('Pilih file Excel terlebih dahulu!');
                return;
            }
            
            // Show loading
            document.getElementById('loading').classList.add('show');
            
            const formData = new FormData(document.getElementById('uploadForm'));
            
            fetch('/dosen/upload-jadwal-file', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('loading').classList.remove('show');
                
                if (data.success) {
                    // Update UI with table
                    showTable(data.data, data.dataCount);
                    alert(data.message);
                } else {
                    alert('Error: ' + data.message);
                    if (data.redirect) {
                        window.location.href = data.redirect;
                    }
                }
            })
            .catch(error => {
                document.getElementById('loading').classList.remove('show');
                alert('Terjadi kesalahan saat mengupload file');
                console.error('Error:', error);
            });
        }
        
        // Function to delete data
        function deleteData() {
            if (!confirm('Apakah Anda yakin ingin menghapus data jadwal yang sudah diupload?')) {
                return;
            }
            
            const formData = new FormData();
            formData.append('idTubes', idTubes);
            
            fetch('/dosen/delete-jadwal', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Revert to upload state
                    showUploadState();
                    alert(data.message);
                } else {
                    alert('Error: ' + data.message);
                    if (data.redirect) {
                        window.location.href = data.redirect;
                    }
                }
            })
            .catch(error => {
                alert('Terjadi kesalahan saat menghapus data');
                console.error('Error:', error);
            });
        }
        
        // Function to show table with data
        function showTable(data, count) {
            // Update upload card
            document.getElementById('uploadCard').classList.add('hidden');
            
            // Update table container
            const tableContainer = document.getElementById('tableContainer');
            tableContainer.classList.add('show');
            
            // Update data count
            document.getElementById('dataCount').textContent = count;
            
            // Populate table
            const tableBody = document.getElementById('tableBody');
            tableBody.innerHTML = '';
            
            data.forEach((row, index) => {
                const tr = document.createElement('tr');
                
                const tdDeadline = document.createElement('td');
                tdDeadline.textContent = row.deadline;
                
                const tdKegiatan = document.createElement('td');
                tdKegiatan.textContent = row.namaKegiatan;
                
                const tdNilai = document.createElement('td');
                tdNilai.textContent = row.nilai;
                
                const tdKeterangan = document.createElement('td');
                tdKeterangan.textContent = row.keterangan;
                
                tr.appendChild(tdDeadline);
                tr.appendChild(tdKegiatan);
                tr.appendChild(tdNilai);
                tr.appendChild(tdKeterangan);
                
                tableBody.appendChild(tr);
            });
            
            // Update buttons
            document.getElementById('saveBtn').classList.add('hidden');
            document.getElementById('deleteBtn').classList.add('show');
        }
        
        // Function to show upload state (initial state)
        function showUploadState() {
            // Reset file input
            document.getElementById('jadwalFile').value = '';
            document.getElementById('fileNameDisplay').textContent = '';
            
            // Show upload card
            document.getElementById('uploadCard').classList.remove('hidden');
            
            // Hide table container
            document.getElementById('tableContainer').classList.remove('show');
            
            // Update buttons
            document.getElementById('saveBtn').classList.remove('hidden');
            document.getElementById('deleteBtn').classList.remove('show');
        }
        
        // Function to check for existing data on page load
        function checkExistingData() {
            fetch('/dosen/check-existing-jadwal?idTubes=' + idTubes)
                .then(response => response.json())
                .then(data => {
                    if (data.hasData) {
                        showTable(data.data, data.dataCount);
                    }
                })
                .catch(error => {
                    console.error('Error checking existing data:', error);
                });
        }
});