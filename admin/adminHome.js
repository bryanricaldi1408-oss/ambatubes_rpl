document.addEventListener('DOMContentLoaded', function() {
            const uploadButton = document.getElementById("upload");
            //redirect to adminHome.html
            uploadButton.addEventListener('click', function() {
                window.location.href = 'addClasses.html';
            });

            // Add event listeners to all edit icons
            const editIcons = document.querySelectorAll('.edit-icon');
            
            editIcons.forEach(icon => {
                icon.addEventListener('click', function() {
                    window.location.href = 'editKelas.html';
                });
            });

            // Toggle filter panel
            document.getElementById('filterBtn').addEventListener('click', function() {
                const filterPanel = document.getElementById('filterPanel');
                filterPanel.classList.toggle('active');
            });
        });