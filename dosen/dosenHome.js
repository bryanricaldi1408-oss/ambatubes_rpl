document.addEventListener('DOMContentLoaded', function() {
    // Toggle filter panel
    const filterBtn = document.getElementById('filterBtn');
    const filterPanel = document.getElementById('filterPanel');

     filterBtn.addEventListener('click', function() {
        // Toggle active class for styling
        filterPanel.classList.toggle('active');
        
        // Also toggle display if not using CSS
        if (filterPanel.style.display === 'none' || !filterPanel.style.display) {
            filterPanel.style.display = 'block';
        } else {
            filterPanel.style.display = 'none';
        }
    });

    // Get all class cards once when page loads
    const classCards = document.querySelectorAll('.class-card');
    const semesterSelect = document.getElementById('semesterSelect');
    const searchInput = document.querySelector('.search-bar input');
    const inProgressCheckbox = document.getElementById('inProgress');
    const pastCheckbox = document.getElementById('past');
    const btnLogout = document.querySelector('.logout-btn');

    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                // Arahkan ke halaman login atau home
                window.location.href = "index.html"; // Ganti dengan halaman login Anda
            }
        });
    }

    // Filter functionality
    function filterClasses() {
        const selectedSemester = semesterSelect.value;
        const searchTerm = searchInput.value.toLowerCase();
        const showInProgress = inProgressCheckbox.checked;
        const showPast = pastCheckbox.checked;
        
        // If no status checkbox is selected, show all
        const noStatusSelected = !showInProgress && !showPast;
        
        classCards.forEach(card => {
            const classDetails = card.querySelector('.class-details');
            const className = classDetails.querySelector('h3').textContent.toLowerCase();
            const classSemester = classDetails.querySelector('p:nth-child(3)').textContent;
            const classStatus = getClassStatus(classSemester); // Determine status based on semester
            
            const matchesSemester = !selectedSemester || classSemester === selectedSemester;
            const matchesSearch = !searchTerm || className.includes(searchTerm);
            
            // Status filtering logic
            let matchesStatus = noStatusSelected;
            if (showInProgress && classStatus === 'in-progress') matchesStatus = true;
            if (showPast && classStatus === 'past') matchesStatus = true;
            
            // Show card only if all conditions are met
            const shouldShow = matchesSemester && matchesSearch && matchesStatus;
            card.style.display = shouldShow ? 'block' : 'none';
        });
        
        updateEmptyState();
    }

    // Helper function to determine class status based on semester
    function getClassStatus(semester) {
        // Simple logic: if semester contains current year or future, it's in-progress
        // Otherwise, it's past
        const currentYear = new Date().getFullYear();
        const yearMatch = semester.match(/\d{4}/);
        
        if (yearMatch) {
            const classYear = parseInt(yearMatch[0]);
            // If the class year is current or future, consider it in-progress
            // This is a simplified logic - you might want to make it more sophisticated
            return classYear >= currentYear ? 'in-progress' : 'past';
        }
        
        // Default to in-progress if we can't determine
        return 'in-progress';
    }

    // Replace the entire updateEmptyState function with this:
    function updateEmptyState() {
        const classGrid = document.querySelector('.class-grid');
        
        // Remove existing empty state message if any
        const existingEmptyMessage = document.querySelector('.empty-state');
        if (existingEmptyMessage) {
            existingEmptyMessage.remove();
        }
        
        // Count visible class cards (excluding the add-class card)
        const allClassCards = document.querySelectorAll('.class-card');
        let visibleCount = 0;
        
        allClassCards.forEach(card => {
            // Check if card is visible by checking computed style
            const style = window.getComputedStyle(card);
            if (style.display !== 'none') {
                visibleCount++;
            }
        });
        
        // If no class cards are visible, show empty state message
        if (visibleCount === 0) {
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'empty-state';
            emptyMessage.innerHTML = `
                <div class="empty-state-content">
                    <h3>No classes found</h3>
                    <p>Try adjusting your filters or search terms</p>
                    <button id="clearFilters" class="clear-filters-btn">Clear all filters</button>
                </div>
            `;
            classGrid.appendChild(emptyMessage);
            
            // Add event listener to clear filters button
            document.getElementById('clearFilters').addEventListener('click', clearAllFilters);
        }
    }

    // Function to clear all filters
    function clearAllFilters() {
        semesterSelect.value = '';
        searchInput.value = '';
        inProgressCheckbox.checked = false;
        pastCheckbox.checked = false;
        filterClasses();
    }

    // Event listeners for filter changes
    semesterSelect.addEventListener('change', filterClasses);
    searchInput.addEventListener('input', filterClasses);
    inProgressCheckbox.addEventListener('change', filterClasses);
    pastCheckbox.addEventListener('change', filterClasses);

    // Initialize filters on page load
    filterClasses();
    });

    // Add event listeners to all edit icons     
    const editIcons = document.querySelectorAll('.edit-icon');     
    editIcons.forEach(icon => {
        icon.addEventListener('click', function(e) {
            // Stop propagation agar tidak trigger class card click
            e.stopPropagation();
            
            // Get the class ID from the card
            const classCard = this.closest('.class-card');
            const classId = classCard.id;
            
            // Store class ID in session storage for context
            sessionStorage.setItem('currentClassId', classId);
            
            // Redirect to edit class page with class ID
            window.location.href = `dosenEdit.html?classId=${classId}`;
        });
    });

    // Add event listeners to all delete icons
    const deleteIcons = document.querySelectorAll('.delete-icon');
    deleteIcons.forEach(icon => {
        icon.addEventListener('click', function(e) {
            // Stop propagation agar tidak trigger class card click
            e.stopPropagation();
            
            const classCard = this.closest('.class-card');
            const classId = classCard.id;
            const className = classCard.querySelector('h3').textContent;
            
            // Simple confirmation
            if (confirm(`Yakin ingin menghapus kelas "${className}"?`)) {
                // Simple animation + remove
                classCard.style.opacity = '0';
                classCard.style.transform = 'scale(0.8)';
                
                setTimeout(() => {
                    classCard.remove();
                }, 300);
            }
        });
    });

    // Add event listeners to all class cards untuk redirect ke dosenTubes.html
    const classCards = document.querySelectorAll('.class-card');
    classCards.forEach(card => {
        card.addEventListener('click', function() {
            // Get the class ID from the card
            const classId = this.id;
            const className = this.querySelector('h3').textContent;
            
            // Store class info untuk digunakan di halaman berikutnya
            sessionStorage.setItem('currentClassId', classId);
            sessionStorage.setItem('currentClassName', className);
            
            // Redirect ke dosenTubes.html dengan class ID
            window.location.href = `dosenTubes.html?classId=${classId}`;
        });
    });

    