document.addEventListener('DOMContentLoaded', () => {
    const detailBtn = document.querySelector('.detail-btn');
    const modal = document.getElementById('keterangan-panel');
    const closeBtn = document.querySelector('.close-btn');
    
    detailBtn.addEventListener('click', () => {
        modal.classList.add('active'); // Tampilkan modal
    });
    
    closeBtn.addEventListener('click', () => {
        modal.classList.remove('active'); // Sembunyikan modal
    });
    
    // Sembunyikan jika mengklik di luar modal
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
});