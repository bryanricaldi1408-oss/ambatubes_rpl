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
            // Reset form and redirect to base URL
            const url = new URL(window.location.href);
            const baseUrl = url.origin + url.pathname;
            window.location.href = baseUrl;
        });
    }
    
    // Add styling for clickable cards
    const classCards = document.querySelectorAll('.class-card.clickable-card');
    
    classCards.forEach(card => {
        // Add cursor pointer
        card.style.cursor = 'pointer';
        
        // Add hover effects
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px)';
            this.style.boxShadow = '0 20px 40px rgba(1, 105, 190, 0.2)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = 'var(--shadow-soft)';
        });
        
        // Add tooltip text
        card.setAttribute('title', 'Klik untuk melihat tugas besar');
    });
    
    // Client-side filtering for better UX
    const allClassCards = document.querySelectorAll('.class-card');
    
    if (allClassCards.length > 0) {
        const searchInput = document.getElementById('searchInput');
        const inProgressCheckbox = document.getElementById('inProgress');
        const pastCheckbox = document.getElementById('past');
        const semesterSelect = document.getElementById('semesterSelect');
        
        // Extract semester data from each card and add data attributes
        allClassCards.forEach(card => {
            // Find the semester paragraph (3rd p tag)
            const semesterParagraph = card.querySelector('.class-details p:nth-child(3)');
            if (semesterParagraph) {
                const semesterText = semesterParagraph.textContent.trim();
                card.setAttribute('data-semester', semesterText);
                
                // Determine and set status (in-progress or past)
                const isInProgress = determineStatusFromSemester(semesterText);
                card.setAttribute('data-status', isInProgress ? 'in-progress' : 'past');
            }
            
            // Extract course name and store it
            const courseNameElement = card.querySelector('.class-details h3');
            if (courseNameElement) {
                card.setAttribute('data-course-name', courseNameElement.textContent.trim().toLowerCase());
            }
        });
        
        // Function for client-side filtering
        function filterClassesClientSide() {
            const searchTerm = searchInput ? searchInput.value.toLowerCase().trim() : '';
            const showInProgress = inProgressCheckbox ? inProgressCheckbox.checked : false;
            const showPast = pastCheckbox ? pastCheckbox.checked : false;
            const selectedSemester = semesterSelect ? semesterSelect.value : '';
            
            let visibleCount = 0;
            
            allClassCards.forEach(card => {
                const cardSemester = card.getAttribute('data-semester') || '';
                const cardStatus = card.getAttribute('data-status') || '';
                const courseName = card.getAttribute('data-course-name') || '';
                
                // 1. Filter by semester
                const semesterMatch = !selectedSemester || cardSemester === selectedSemester;
                
                // 2. Filter by status
                let statusMatch = true;
                if (showInProgress || showPast) {
                    if (showInProgress && !showPast) {
                        statusMatch = cardStatus === 'in-progress';
                    } else if (!showInProgress && showPast) {
                        statusMatch = cardStatus === 'past';
                    }
                    // If both are checked, show all (statusMatch remains true)
                }
                
                // 3. Filter by search term (ONLY search in course name)
                let searchMatch = true;
                if (searchTerm) {
                    searchMatch = courseName.includes(searchTerm);
                }
                
                // Show/hide card
                const shouldShow = semesterMatch && statusMatch && searchMatch;
                card.style.display = shouldShow ? 'flex' : 'none';
                
                if (shouldShow) visibleCount++;
            });
            
            // Show empty state if no cards are visible
            updateEmptyState(visibleCount === 0);
        }
        
        // Helper function to determine status from semester
        function determineStatusFromSemester(semester) {
            if (!semester) return true; // Default in-progress
            
            try {
                // Extract year from semester string
                // Formats might be: "2023/2024", "2023", "2023 Ganjil", etc.
                const currentYear = new Date().getFullYear();
                
                // Look for 4-digit years
                const yearMatch = semester.match(/\b(\d{4})\b/);
                if (yearMatch) {
                    const classYear = parseInt(yearMatch[1]);
                    
                    // If the year is current year or future, consider it in-progress
                    return classYear >= currentYear;
                }
                
                // If no year found, check for common in-progress indicators
                const inProgressIndicators = ['genap', 'ganjil', 'pendek', 'current'];
                const lowerSemester = semester.toLowerCase();
                
                for (const indicator of inProgressIndicators) {
                    if (lowerSemester.includes(indicator)) {
                        return true;
                    }
                }
                
                // Default to past if we can't determine
                return false;
            } catch (e) {
                console.error('Error determining status:', e);
                return true; // Default in-progress
            }
        }
        
        // Update empty state
        function updateEmptyState(isEmpty) {
            const classGrid = document.querySelector('.class-grid');
            if (!classGrid) return;
            
            const existingEmptyState = document.querySelector('.client-empty-state');
            
            if (isEmpty) {
                if (!existingEmptyState) {
                    const emptyState = document.createElement('div');
                    emptyState.className = 'client-empty-state';
                    emptyState.innerHTML = `
                        <div style="text-align: center; padding: 40px; color: #999; grid-column: 1 / -1; width: 100%;">
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
        
        // Global function to clear client filters
        window.clearClientFilters = function() {
            // Clear checkboxes
            if (inProgressCheckbox) inProgressCheckbox.checked = false;
            if (pastCheckbox) pastCheckbox.checked = false;
            
            // Clear semester select
            if (semesterSelect) semesterSelect.value = '';
            
            // Clear search input
            if (searchInput) searchInput.value = '';
            
            // Re-apply filters
            filterClassesClientSide();
        };
        
        // Attach event listeners
        if (searchInput) {
            // Add debounce to search input for better performance
            let searchTimeout;
            searchInput.addEventListener('input', function() {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(filterClassesClientSide, 300);
            });
        }
        
        if (inProgressCheckbox) {
            inProgressCheckbox.addEventListener('change', function() {
                // If this is checked, uncheck the other if needed
                if (this.checked && pastCheckbox && pastCheckbox.checked) {
                    pastCheckbox.checked = false;
                }
                filterClassesClientSide();
            });
        }
        
        if (pastCheckbox) {
            pastCheckbox.addEventListener('change', function() {
                // If this is checked, uncheck the other if needed
                if (this.checked && inProgressCheckbox && inProgressCheckbox.checked) {
                    inProgressCheckbox.checked = false;
                }
                filterClassesClientSide();
            });
        }
        
        if (semesterSelect) {
            semesterSelect.addEventListener('change', filterClassesClientSide);
        }
        
        // Apply initial filtering based on URL parameters if any
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('semester')) {
            const semesterFromUrl = urlParams.get('semester');
            if (semesterSelect && semesterFromUrl) {
                semesterSelect.value = semesterFromUrl;
            }
        }
        
        if (urlParams.has('search')) {
            const searchFromUrl = urlParams.get('search');
            if (searchInput && searchFromUrl) {
                searchInput.value = searchFromUrl;
            }
        }
        
        if (urlParams.has('status')) {
            const statusFromUrl = urlParams.get('status');
            if (statusFromUrl === 'in-progress' && inProgressCheckbox) {
                inProgressCheckbox.checked = true;
            } else if (statusFromUrl === 'past' && pastCheckbox) {
                pastCheckbox.checked = true;
            }
        }
        
        // Initial filtering
        filterClassesClientSide();
        
        // Optional: Update URL when filters change (without page reload)
        function updateURLWithFilters() {
            const params = new URLSearchParams();
            
            if (semesterSelect && semesterSelect.value) {
                params.set('semester', semesterSelect.value);
            }
            
            if (searchInput && searchInput.value) {
                params.set('search', searchInput.value);
            }
            
            if (inProgressCheckbox && inProgressCheckbox.checked) {
                params.set('status', 'in-progress');
            } else if (pastCheckbox && pastCheckbox.checked) {
                params.set('status', 'past');
            }
            
            const newUrl = params.toString() ? 
                `${window.location.pathname}?${params.toString()}` : 
                window.location.pathname;
            
            // Update URL without reloading
            window.history.replaceState({}, '', newUrl);
        }
        
        // Update URL when filters change
        if (searchInput) searchInput.addEventListener('input', updateURLWithFilters);
        if (inProgressCheckbox) inProgressCheckbox.addEventListener('change', updateURLWithFilters);
        if (pastCheckbox) pastCheckbox.addEventListener('change', updateURLWithFilters);
        if (semesterSelect) semesterSelect.addEventListener('change', updateURLWithFilters);
    }
});

// Function to redirect to tubes page (kept for compatibility)
function redirectToTubes(kelasId) {
    window.location.href = `/dosen/tubes?kelasId=${kelasId}`;
}