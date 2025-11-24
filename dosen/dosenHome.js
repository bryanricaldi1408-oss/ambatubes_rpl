document.addEventListener('DOMContentLoaded', function() {
            // Toggle filter panel
            document.getElementById('filterBtn').addEventListener('click', function() {
                const filterPanel = document.getElementById('filterPanel');
                filterPanel.classList.toggle('active');
            });

            // Add event listeners to all edit icons     
            const editIcons = document.querySelectorAll('.edit-icon');     
            editIcons.forEach(icon => {
                icon.addEventListener('click', function() {
                    // Get the class ID from the card
                    const classCard = this.closest('.class-card');
                    const classId = classCard.id;
                    
                    // Store class ID in session storage for context
                    sessionStorage.setItem('currentClassId', classId);
                    
                    // Redirect to edit class page with class ID
                    window.location.href = `editKelas.html?classId=${classId}`;
                });
            });
        });