document.addEventListener('DOMContentLoaded', function() {
    
    // 1. LOGOUT
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            if (confirm("Apakah Anda yakin ingin keluar?")) {
                window.location.href = '/logout';
            }
        });
    }

    // 2. SETUP VARIABEL
    const filterBtn = document.getElementById('filterBtn');
    const filterPanel = document.getElementById('filterPanel');
    const classCards = document.querySelectorAll('.class-card');
    const semesterSelect = document.getElementById('semesterSelect');
    const searchInput = document.querySelector('.search-bar input'); 
    const inProgressCheckbox = document.getElementById('inProgress');
    const pastCheckbox = document.getElementById('past');
    const classGrid = document.getElementById('classGrid');

    // --- HAPUS BAGIAN AUTO-INJECT OPSI "SEMUA SEMESTER" ---
    // Karena di HTML Anda sudah menulis <option value="">All Semesters</option>
    // Maka kode JS untuk inject option dihapus agar tidak double.

    // 3. TOGGLE PANEL
    if (filterBtn && filterPanel) {
        filterBtn.addEventListener('click', function() {
            filterPanel.classList.toggle('active');
            if (filterPanel.style.display === 'none' || !filterPanel.style.display) {
                filterPanel.style.display = 'block';
            } else {
                filterPanel.style.display = 'none';
            }
        });
    }

    // 4. HELPER: Get Status
    function getClassStatus(semesterText) {
        const currentYear = new Date().getFullYear();
        const yearMatch = semesterText.match(/\d{4}/);
        if (yearMatch) {
            const classYear = parseInt(yearMatch[0]);
            return classYear >= currentYear ? 'in-progress' : 'past';
        }
        return 'in-progress';
    }

    // 5. HELPER: Empty State
    function updateEmptyState() {
        const existingEmptyMessage = document.querySelector('.empty-state');
        if (existingEmptyMessage) existingEmptyMessage.remove();
        
        let visibleCount = 0;
        classCards.forEach(card => {
            if (card.style.display !== 'none') visibleCount++;
        });
        
        if (visibleCount === 0 && classGrid) {
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'empty-state';
            emptyMessage.style.textAlign = 'center';
            emptyMessage.style.padding = '40px';
            emptyMessage.style.color = '#666';
            emptyMessage.innerHTML = `
                <div class="empty-state-content">
                    <h3>Tidak ada kelas ditemukan</h3>
                    <p>Coba sesuaikan filter atau kata kunci pencarian Anda</p>
                    <button id="clearFilters" style="padding:8px 16px; cursor:pointer;">Hapus semua filter</button>
                </div>`;
            classGrid.appendChild(emptyMessage);
            
            const clearBtn = document.getElementById('clearFilters');
            if(clearBtn) clearBtn.addEventListener('click', clearAllFilters);
        }
    }

    // 6. HELPER: Clear Filters
    function clearAllFilters() {
        if(semesterSelect) semesterSelect.value = ""; // Reset ke value kosong (All Semesters)
        if(searchInput) searchInput.value = '';
        if(inProgressCheckbox) inProgressCheckbox.checked = false;
        if(pastCheckbox) pastCheckbox.checked = false;
        filterClasses();
    }

    // 7. CORE FILTER LOGIC
    function filterClasses() {
        // Ambil Value Dropdown (Sekarang kita pakai value, bukan text index)
        const selectedSemester = semesterSelect ? semesterSelect.value : "";
        
        const searchTerm = searchInput ? searchInput.value.toLowerCase() : '';
        const showInProgress = inProgressCheckbox ? inProgressCheckbox.checked : false;
        const showPast = pastCheckbox ? pastCheckbox.checked : false;
        const noStatusSelected = !showInProgress && !showPast;
        
        classCards.forEach(card => {
            // Ambil Data Kartu
            const cardNama = card.getAttribute('data-nama');
            const cardSemester = card.getAttribute('data-semester');
            
            // Cek Status (In-Progress/Past)
            // Kita ambil teks semester asli dari atribut data-semester untuk cek tahun
            const classStatus = getClassStatus(cardSemester); 
            
            // LOGIKA FILTER 1: Semester
            // Cocok jika value dropdown kosong ("") ATAU sama persis dengan semester kartu
            const matchesSemester = (selectedSemester === "") || (cardSemester === selectedSemester);

            // LOGIKA FILTER 2: Search
            const matchesSearch = !searchTerm || (cardNama && cardNama.includes(searchTerm));
            
            // LOGIKA FILTER 3: Checkbox Status
            let matchesStatus = noStatusSelected;
            if (showInProgress && classStatus === 'in-progress') matchesStatus = true;
            if (showPast && classStatus === 'past') matchesStatus = true;
            
            // Final Decision
            if (matchesSemester && matchesSearch && matchesStatus) {
                card.style.display = ''; 
            } else {
                card.style.display = 'none'; 
            }
        });
        
        updateEmptyState();
    }

    // 8. EVENT LISTENERS
    if(semesterSelect) semesterSelect.addEventListener('change', filterClasses);
    if(searchInput) searchInput.addEventListener('keyup', filterClasses);
    if(inProgressCheckbox) inProgressCheckbox.addEventListener('change', filterClasses);
    if(pastCheckbox) pastCheckbox.addEventListener('change', filterClasses);

    // Initial Run
    filterClasses();
});