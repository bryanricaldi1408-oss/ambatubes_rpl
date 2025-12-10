document.addEventListener('DOMContentLoaded', () => {
    
    // 1. Ambil Elemen
    const modal = document.getElementById('keterangan-panel');
    const modalText = document.getElementById('modal-text');
    const closeBtn = document.querySelector('.close-btn');
    
    // 2. Ambil Semua Tombol Detail
    const detailBtns = document.querySelectorAll('.detail-btn');

    // 3. Pasang Event Listener
    detailBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            // Ambil teks dari atribut tombol
            const keterangan = this.getAttribute('data-ket');
            
            // Masukkan teks ke dalam modal
            if (modalText) {
                modalText.textContent = keterangan;
            }

            modal.classList.add('active');
        });
    });

    // 4. Tutup Modal (Tombol X)
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            // PERBAIKAN: Hapus class active
            modal.classList.remove('active');
        });
    }

    // 5. Tutup Modal (Klik Luar)
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            // PERBAIKAN: Hapus class active
            modal.classList.remove('active');
        }
    });
});