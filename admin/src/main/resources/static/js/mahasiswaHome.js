document.addEventListener('DOMContentLoaded', function() {
    
    // =========================================
    // 1. LOGOUT LOGIC (Agar tombol exit tetap jalan)
    // =========================================
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            if (confirm("Apakah Anda yakin ingin keluar?")) {
                window.location.href = '/logout';
            }
        });
    }

    // =========================================
    // 2. SETUP VARIABEL & ELEMEN HTML
    // =========================================
    
    // Toggle filter panel elements
    const filterBtn = document.getElementById('filterBtn');
    const filterPanel = document.getElementById('filterPanel');
    
    // Filter input elements
    const classCards = document.querySelectorAll('.class-card');
    const semesterSelect = document.getElementById('semesterSelect');
    // Perbaikan: Menggunakan querySelector karena input tidak punya ID di HTML kamu
    const searchInput = document.querySelector('.search-bar input'); 
    const inProgressCheckbox = document.getElementById('inProgress');
    const pastCheckbox = document.getElementById('past');
    const classGrid = document.getElementById('classGrid'); // atau querySelector('.class-grid')

    // --- TRIK TAMBAHAN: Inject Opsi "Semua Semester" ---
    // Agar logika filter bekerja dengan baik (default kosong/semua)
    if (semesterSelect) {
        const allOption = document.createElement("option");
        allOption.text = "Semua Semester";
        allOption.value = ""; // Value kosong untuk merepresentasikan 'semua'
        semesterSelect.insertBefore(allOption, semesterSelect.firstChild);
        semesterSelect.value = ""; // Set default ke semua
    }

    // =========================================
    // 3. LOGIKA TOGGLE PANEL
    // =========================================
    if (filterBtn && filterPanel) {
        filterBtn.addEventListener('click', function() {
            // Toggle active class for styling
            filterPanel.classList.toggle('active');
            
            // Toggle display manual (jika CSS tidak menangani class active)
            if (filterPanel.style.display === 'none' || !filterPanel.style.display) {
                filterPanel.style.display = 'block';
            } else {
                filterPanel.style.display = 'none';
            }
        });
    }

    // =========================================
    // 4. HELPER FUNCTIONS
    // =========================================

    // Fungsi menentukan status berdasarkan tahun di semester
    function getClassStatus(semesterText) {
        // Logika: ambil 4 digit angka (tahun)
        const currentYear = new Date().getFullYear();
        const yearMatch = semesterText.match(/\d{4}/);
        
        if (yearMatch) {
            const classYear = parseInt(yearMatch[0]);
            // Jika tahun >= tahun ini, anggap in-progress (Logic sederhana)
            // Kamu bisa mempercanggih ini jika perlu membandingkan bulan/semester ganjil genap
            return classYear >= currentYear ? 'in-progress' : 'past';
        }
        
        // Default ke in-progress jika tidak ada tahun
        return 'in-progress';
    }

    // Fungsi untuk menampilkan pesan kosong jika tidak ada hasil
    function updateEmptyState() {
        // Hapus pesan kosong lama jika ada
        const existingEmptyMessage = document.querySelector('.empty-state');
        if (existingEmptyMessage) {
            existingEmptyMessage.remove();
        }
        
        // Hitung kartu yang terlihat (display tidak none)
        let visibleCount = 0;
        classCards.forEach(card => {
            if (card.style.display !== 'none') {
                visibleCount++;
            }
        });
        
        // Jika 0 kartu terlihat, buat pesan empty state
        if (visibleCount === 0 && classGrid) {
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'empty-state';
            // Styling inline sederhana agar langsung terlihat rapi
            emptyMessage.style.textAlign = 'center';
            emptyMessage.style.padding = '40px';
            emptyMessage.style.color = '#666';
            
            emptyMessage.innerHTML = `
                <div class="empty-state-content">
                    <h3 style="margin-bottom:10px;">Tidak ada kelas ditemukan</h3>
                    <p style="margin-bottom:20px;">Coba sesuaikan filter atau kata kunci pencarian Anda</p>
                    <button id="clearFilters" class="clear-filters-btn" style="padding:8px 16px; cursor:pointer;">Hapus semua filter</button>
                </div>
            `;
            classGrid.appendChild(emptyMessage);
            
            // Tambahkan event listener ke tombol "Hapus semua filter" yang baru dibuat
            const clearBtn = document.getElementById('clearFilters');
            if(clearBtn) clearBtn.addEventListener('click', clearAllFilters);
        }
    }

    // Fungsi reset filter
    function clearAllFilters() {
        if(semesterSelect) semesterSelect.value = '';
        if(searchInput) searchInput.value = '';
        if(inProgressCheckbox) inProgressCheckbox.checked = false;
        if(pastCheckbox) pastCheckbox.checked = false;
        
        // Jalankan filter ulang
        filterClasses();
    }

    // =========================================
    // 5. CORE FILTER LOGIC
    // =========================================
    function filterClasses() {
        // Ambil nilai-nilai filter saat ini
        const selectedSemester = semesterSelect ? semesterSelect.value : '';
        // Ambil teks dropdown jika value kosong tapi bukan "Semua Semester" (handle kasus opsi HTML hardcode)
        const selectedSemesterText = (semesterSelect && semesterSelect.selectedIndex >= 0) 
                                     ? semesterSelect.options[semesterSelect.selectedIndex].text 
                                     : '';

        const searchTerm = searchInput ? searchInput.value.toLowerCase() : '';
        const showInProgress = inProgressCheckbox ? inProgressCheckbox.checked : false;
        const showPast = pastCheckbox ? pastCheckbox.checked : false;
        
        // Jika tidak ada checkbox status dicentang, anggap user ingin melihat SEMUA (default behavior)
        const noStatusSelected = !showInProgress && !showPast;
        
        classCards.forEach(card => {
            // Ambil data dari elemen HTML kartu
            const classDetails = card.querySelector('.class-details');
            const className = classDetails.querySelector('h3').textContent.toLowerCase();
            
            // Semester ada di paragraf ke-2 (index 1) di dalam class-details sesuai struktur HTML kamu
            // Struktur: h3 (Nama), p (Kelas), p (Semester), p (Dosen)
            const pTags = classDetails.querySelectorAll('p');
            const classSemesterText = pTags.length >= 2 ? pTags[1].textContent : ''; 
            
            // Tentukan status (in-progress/past)
            const classStatus = getClassStatus(classSemesterText);
            
            // 1. Cek Semester
            // Cocok jika filter kosong ("") ATAU filter "Semua Semester" ATAU teks sama persis
            const matchesSemester = selectedSemester === '' || 
                                    selectedSemester === 'Semua Semester' ||
                                    classSemesterText === selectedSemesterText;

            // 2. Cek Search
            const matchesSearch = !searchTerm || className.includes(searchTerm);
            
            // 3. Cek Status (Checkbox)
            let matchesStatus = noStatusSelected; // Default true jika tidak ada yg dicentang
            if (showInProgress && classStatus === 'in-progress') matchesStatus = true;
            if (showPast && classStatus === 'past') matchesStatus = true;
            
            // Gabungkan semua kondisi
            if (matchesSemester && matchesSearch && matchesStatus) {
                card.style.display = ''; // Reset display (muncul)
            } else {
                card.style.display = 'none'; // Sembunyikan
            }
        });
        
        // Cek apakah perlu menampilkan pesan kosong
        updateEmptyState();
    }

    // =========================================
    // 6. EVENT LISTENERS
    // =========================================
    if(semesterSelect) semesterSelect.addEventListener('change', filterClasses);
    if(searchInput) searchInput.addEventListener('keyup', filterClasses); // keyup lebih responsif untuk search
    if(inProgressCheckbox) inProgressCheckbox.addEventListener('change', filterClasses);
    if(pastCheckbox) pastCheckbox.addEventListener('change', filterClasses);

    // Jalankan filter sekali saat halaman dimuat
    filterClasses();
});