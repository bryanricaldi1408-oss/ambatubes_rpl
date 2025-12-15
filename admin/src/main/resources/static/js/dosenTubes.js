document.addEventListener('DOMContentLoaded', function() {
    // Setup event listener untuk tombol add-card-dashed
    const addButton = document.querySelector('.add-card-dashed');
    
    if (addButton) {
        // Remove any existing click listeners
        addButton.replaceWith(addButton.cloneNode(true));
        
        // Get the new button reference
        const newAddButton = document.querySelector('.add-card-dashed');
        
        newAddButton.addEventListener('click', function() {
            // Ambil kelasId dari URL
            const urlParams = new URLSearchParams(window.location.search);
            const kelasId = urlParams.get('kelasId');
            
            if (kelasId) {
                // Redirect ke halaman dosenEdit.html dengan parameter kelasId
                window.location.href = '/dosen/edit?kelasId=' + kelasId;
            } else {
                alert('Error: Tidak dapat menemukan ID kelas. Silakan kembali ke dashboard.');
            }
        });
    }

    // Logout button
    const btnLogout = document.querySelector('.logout-btn');
    if (btnLogout) {
        btnLogout.addEventListener("click", function (e) {
            e.preventDefault();
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                localStorage.clear();
                window.location.href = '/logout';
            }
        });
    }
    
    // Tambahkan efek hover untuk add button
    const addCardBtn = document.querySelector('.add-card-dashed');
    if (addCardBtn) {
        addCardBtn.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.05)';
            this.style.boxShadow = '0 8px 25px rgba(1, 105, 190, 0.3)';
        });
        
        addCardBtn.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
            this.style.boxShadow = '0 4px 15px rgba(0,0,0,0.1)';
        });
    }
});