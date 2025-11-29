document.addEventListener('DOMContentLoaded', function() {
    // Setup event listener untuk tombol add-card-dashed
    const addButton = document.querySelector('.add-card-dashed');
    if (addButton) {
        addButton.addEventListener('click', function() {
            // Redirect ke halaman dosenEdit.html
            window.location.href = 'dosenEdit.html';
        });
    }

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
});
