document.addEventListener('DOMContentLoaded', () => {
    
    const modalOverlay = document.getElementById('anggota-panel');
    const closeBtn = document.querySelector('.close-btn');
    const modalGroupName = document.getElementById('modal-group-name');
    const modalMemberCount = document.getElementById('modal-member-count'); // Span untuk angka 3/5
    const modalTbody = document.getElementById('modal-tbody');
    const showButtons = document.querySelectorAll('.show-btn');
    
    showButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Ambil data
            const idKelompok = this.getAttribute('data-id');
            const namaKelompok = this.getAttribute('data-nama');
            const maxKapasitas = this.getAttribute('data-max'); // Ambil kapasitas max saja

            // Set Judul Awal
            modalGroupName.textContent = namaKelompok;
            modalMemberCount.textContent = ".../" + maxKapasitas; // Placeholder loading
            
            // Tampilkan Loading
            modalTbody.innerHTML = '<tr><td colspan="2" style="text-align:center; padding:20px;">Memuat data...</td></tr>';
            modalOverlay.classList.add('active');

            // FETCH API
            fetch(`/mahasiswa/api/kelompok/${idKelompok}/anggota`)
                .then(response => response.json())
                .then(data => {
                    modalTbody.innerHTML = ''; 

                    // --- PERBAIKAN UTAMA: UPDATE JUMLAH REAL-TIME ---
                    // Hitung jumlah data yang didapat dari server
                    const currentCount = data.length; 
                    modalMemberCount.textContent = `${currentCount}/${maxKapasitas}`; // Contoh: 3/5

                    if (currentCount === 0) {
                        modalTbody.innerHTML = '<tr><td colspan="2" style="text-align:center; padding:20px; color:#666;">Belum ada anggota.</td></tr>';
                    } else {
                        data.forEach((mhs, index) => {
                            const row = modalTbody.insertRow();
                            
                            const cellNama = row.insertCell();
                            cellNama.textContent = mhs.nama;

                            const cellNpm = row.insertCell();
                            cellNpm.textContent = mhs.npm;
                        });
                    }
                })
                .catch(err => {
                    console.error('Error:', err);
                    modalTbody.innerHTML = '<tr><td colspan="2" style="color:red; text-align:center;">Gagal memuat data.</td></tr>';
                });
        });
    });

    if(closeBtn) {
        closeBtn.addEventListener('click', () => {
            modalOverlay.classList.remove('active');
        });
    }

    window.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
            modalOverlay.classList.remove('active');
        }
    });
});