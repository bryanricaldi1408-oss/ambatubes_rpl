document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. REFERENSI ELEMEN ---
    const formTugas = document.querySelector(".detail-form-card");
    const btnUploadDirect = document.querySelector(".upload-file-btn");
    const btnLogout = document.querySelector(".logout-btn");
    
    // Input Fields
    const inputNama = document.getElementById("namaTugas");
    const inputDeskripsi = document.getElementById("deskripsi");

    // --- 2. LOGIKA TOMBOL SAVE (SUBMIT FORM) ---
    if (formTugas) {
        formTugas.addEventListener("submit", function (event) {
            // Mencegah reload halaman default
            event.preventDefault();

            // Ambil data dari input
            const namaTugas = inputNama.value;
            const deskripsi = inputDeskripsi.value;

            // Validasi Sederhana (Meskipun HTML 'required' sudah menanganinya)
            if (!namaTugas.trim()) {
                alert("Nama tugas wajib diisi!");
                return;
            }

            // (Opsional) Simpan data ke LocalStorage agar bisa diambil di halaman berikutnya
            // Ini membuat aplikasi terasa lebih nyata
            localStorage.setItem("currentTugas_Nama", namaTugas);
            localStorage.setItem("currentTugas_Deskripsi", deskripsi);

            // Tampilkan pesan sukses (opsional)
            // alert("Data berhasil disimpan! Lanjut ke Upload Jadwal.");

            // REDIRECT: Pindah ke halaman Step 2
            window.location.href = "uploadJadwal.html";
        });
    }

    // --- 4. LOGIKA TOMBOL LOGOUT ---
    if (btnLogout) {
        btnLogout.addEventListener("click", function () {
            const confirmLogout = confirm("Apakah Anda yakin ingin keluar?");
            if (confirmLogout) {
                // Arahkan ke halaman login atau home
                window.location.href = "index.html"; // Ganti dengan halaman login Anda
            }
        });
    }

    // --- 5. PROGRESS BAR CLICKABLE (OPSIONAL) ---
    // Jika Anda ingin lingkaran progress bar bisa diklik untuk navigasi
    const steps = document.querySelectorAll(".step");
    steps.forEach((step, index) => {
        step.addEventListener("click", () => {
            // Logika sederhana: 
            // Step 1 (index 0) -> dosenEdit.html
            // Step 2 (index 1) -> uploadJadwal.html
            
            if (index === 0) {
                window.location.href = "dosenEdit.html";
            } else if (index === 1) {
                window.location.href = "uploadJadwal.html";
            }
            else if (index === 2) {
                window.location.href = "uploadRubrik.html";
            }
            else if (index === 3) {
                window.location.href = "dosenBuatKelompok.html";
            }
            else if(index === 4){
                window.location.href = "dosenPenilaian.html";
            }
            
            // Tambahkan else if untuk step selanjutnya jika halaman sudah ada
        });
    });

});