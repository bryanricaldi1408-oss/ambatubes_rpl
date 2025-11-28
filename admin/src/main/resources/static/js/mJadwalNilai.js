document.addEventListener('DOMContentLoaded', () => {
    
    // 1. Ambil SEMUA tombol detail (gunakan querySelectorAll)
    const detailBtns = document.querySelectorAll('.detail-btn');
    
    const modal = document.getElementById('keterangan-panel');
    const closeBtn = document.querySelector('.close-btn');
    
    // Ambil elemen <p> di dalam modal untuk diisi teks nanti
    // (Pastikan di HTML ada id="modal-text" atau struktur .modal-body > p)
    let modalText = document.getElementById('modal-text');
    if (!modalText) {
        modalText = modal.querySelector('.modal-body p');
    }

    // 2. Loop setiap tombol untuk memasang fungsi klik
    detailBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            // Ambil text keterangan dari atribut 'data-ket' tombol yang diklik
            const keterangan = this.getAttribute('data-ket');
            
            // Masukkan text tersebut ke dalam modal
            if (modalText) {
                modalText.textContent = keterangan;
            }

            // Tampilkan modal
            modal.classList.add('active'); 
        });
    });
    
    // 3. Logic Tutup Modal (Tetap sama)
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            modal.classList.remove('active'); 
        });
    }
    
    // Sembunyikan jika mengklik di luar modal
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
});