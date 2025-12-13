document.addEventListener('DOMContentLoaded', function() {
    // Elements
    const filterBtn = document.getElementById('filterBtn');
    const filterPanel = document.getElementById('filterPanel');
    const searchForm = document.getElementById('searchForm');
    const clearFilterBtn = document.getElementById('clearFilter');
    
    // Toggle filter panel
    if (filterBtn && filterPanel) {
        filterBtn.addEventListener('click', function() {
            filterPanel.classList.toggle('active');
        });
    }
    
    // Clear filter button
    if (clearFilterBtn) {
        clearFilterBtn.addEventListener('click', function() {
            // Reset form
            if (searchForm) {
                // Reset checkboxes
                const inProgressCheckbox = document.getElementById('inProgress');
                const pastCheckbox = document.getElementById('past');
                const semesterSelect = document.getElementById('semesterSelect');
                const searchInput = document.getElementById('searchInput');
                
                if (inProgressCheckbox) inProgressCheckbox.checked = false;
                if (pastCheckbox) pastCheckbox.checked = false;
                if (semesterSelect) semesterSelect.value = '';
                if (searchInput) searchInput.value = '';
                
                // Submit form tanpa parameter filter
                const url = new URL(window.location.href);
                const baseUrl = url.origin + url.pathname;
                window.location.href = baseUrl;
            }
        });
    }
    
    // Tambahkan styling untuk clickable cards
    const classCards = document.querySelectorAll('.class-card.clickable-card');
    
    classCards.forEach(card => {
        // Tambahkan cursor pointer
        card.style.cursor = 'pointer';
        
        // Tambahkan efek hover
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px)';
            this.style.boxShadow = '0 20px 40px rgba(1, 105, 190, 0.2)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = 'var(--shadow-soft)';
        });
        
        // Tambahkan tooltip text
        card.setAttribute('title', 'Klik untuk melihat tugas besar');
    });
    
    // Client-side filtering untuk UX yang lebih baik
    const allClassCards = document.querySelectorAll('.class-card');
    
    if (allClassCards.length > 0) {
        const searchInput = document.getElementById('searchInput');
        const inProgressCheckbox = document.getElementById('inProgress');
        const pastCheckbox = document.getElementById('past');
        const semesterSelect = document.getElementById('semesterSelect');
        
        // Function untuk client-side filtering
        function filterClassesClientSide() {
            const searchTerm = searchInput ? searchInput.value.toLowerCase() : '';
            const showInProgress = inProgressCheckbox ? inProgressCheckbox.checked : false;
            const showPast = pastCheckbox ? pastCheckbox.checked : false;
            const selectedSemester = semesterSelect ? semesterSelect.value : '';
            
            let visibleCount = 0;
            
            allClassCards.forEach(card => {
                const cardSemester = card.getAttribute('data-semester') || '';
                const cardText = card.textContent.toLowerCase();
                
                // Filter by semester
                const semesterMatch = !selectedSemester || cardSemester === selectedSemester;
                
                // Filter by status (determine from semester)
                let statusMatch = true;
                if (showInProgress || showPast) {
                    const isInProgress = determineStatusFromSemester(cardSemester);
                    
                    if (showInProgress && !showPast) {
                        statusMatch = isInProgress;
                    } else if (!showInProgress && showPast) {
                        statusMatch = !isInProgress;
                    }
                    // Jika keduanya dicentang, statusMatch tetap true
                }
                
                // Filter by search term
                const searchMatch = !searchTerm || cardText.includes(searchTerm);
                
                // Show/hide card
                const shouldShow = semesterMatch && statusMatch && searchMatch;
                card.style.display = shouldShow ? 'flex' : 'none';
                
                if (shouldShow) visibleCount++;
            });
            
            // Show empty state jika tidak ada card yang visible
            updateEmptyState(visibleCount === 0);
        }
        
        // Helper function untuk menentukan status dari semester
        function determineStatusFromSemester(semester) {
            if (!semester) return true; // Default in-progress
            
            try {
                // Cari tahun 4 digit
                const yearMatch = semester.match(/\d{4}/);
                if (yearMatch) {
                    const classYear = parseInt(yearMatch[0]);
                    const currentYear = new Date().getFullYear();
                    
                    // Jika semester mengandung "pendek", anggap in-progress
                    if (semester.toLowerCase().includes('pendek')) {
                        return true;
                    }
                    
                    return classYear >= currentYear;
                }
            } catch (e) {
                console.error('Error determining status:', e);
            }
            
            return true; // Default in-progress
        }
        
        // Update empty state
        function updateEmptyState(isEmpty) {
            const classGrid = document.querySelector('.class-grid');
            const existingEmptyState = document.querySelector('.client-empty-state');
            
            if (isEmpty) {
                if (!existingEmptyState && classGrid) {
                    const emptyState = document.createElement('div');
                    emptyState.className = 'client-empty-state';
                    emptyState.innerHTML = `
                        <div style="text-align: center; padding: 40px; color: #999; grid-column: 1 / -1;">
                            <h3>Tidak ada kelas yang sesuai dengan filter</h3>
                            <p>Coba ubah kriteria pencarian atau filter Anda</p>
                            <button onclick="clearClientFilters()" style="margin-top: 10px; padding: 8px 16px; background: #0169be; color: white; border: none; border-radius: 5px; cursor: pointer;">
                                Hapus Filter
                            </button>
                        </div>
                    `;
                    classGrid.appendChild(emptyState);
                }
            } else if (existingEmptyState) {
                existingEmptyState.remove();
            }
        }
        
        // Global function untuk clear client filters
        window.clearClientFilters = function() {
            if (inProgressCheckbox) inProgressCheckbox.checked = false;
            if (pastCheckbox) pastCheckbox.checked = false;
            if (semesterSelect) semesterSelect.value = '';
            if (searchInput) searchInput.value = '';
            
            filterClassesClientSide();
        };
        
        // Attach event listeners
        if (searchInput) searchInput.addEventListener('input', filterClassesClientSide);
        if (inProgressCheckbox) inProgressCheckbox.addEventListener('change', filterClassesClientSide);
        if (pastCheckbox) pastCheckbox.addEventListener('change', filterClassesClientSide);
        if (semesterSelect) semesterSelect.addEventListener('change', filterClassesClientSide);
        
        // Initial filtering
        filterClassesClientSide();
    }
});