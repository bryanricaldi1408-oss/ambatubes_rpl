document.addEventListener('DOMContentLoaded', () => {
    // 1. Ambil elemen-elemen penting
    const showButtons = document.querySelectorAll('.show-btn');
    const modalOverlay = document.getElementById('anggota-panel');
    const closeBtn = document.querySelector('#anggota-panel .close-btn');
    const modalTitle = document.querySelector('#anggota-panel .modal-title');
    const anggotaTableBody = document.querySelector('#anggota-panel .anggota-table tbody');

    // Contoh data anggota yang akan ditampilkan (dalam kasus nyata, ini diambil dari server)
    const dataAnggota = {
        'A': { count: '5/5', list: [
            { nama: 'Diki Chandra', npm: '6182301004' },
            { nama: 'Siti Rahmawati', npm: '6182301005' },
            // ... Tambahkan anggota lain
        ]},
        'B': { count: '3/5', list: [
            { nama: 'Robert Saputra', npm: '6182301001' },
            { nama: 'Bryan Ricaldi', npm: '6182301002' },
            { nama: 'Ethan', npm: '6182301003' }
        ]},
        'C': { count: '4/5', list: [
            { nama: 'Fajar Nugraha', npm: '6182301006' },
            // ...
        ]},
        'D': { count: '5/5', list: [
            { nama: 'Dewi Lestari', npm: '6182301007' },
            // ...
        ]}
    };

    // 2. Fungsi untuk MENAMPILKAN MODAL
    showButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const row = e.target.closest('tr');
            
            const groupID = row.querySelector('td:nth-child(2)').textContent;
            
            const groupData = dataAnggota[groupID];

            if (groupData) {
                // Update Judul Modal: Anggota Kelompok B 3/5
                modalTitle.innerHTML = `Anggota Kelompok ${groupID} <span class="member-count">${groupData.count}</span>`;
                
                // Isi Tabel Anggota
                anggotaTableBody.innerHTML = ''; // Kosongkan tabel sebelumnya
                groupData.list.forEach((anggota, index) => {
                    const newRow = anggotaTableBody.insertRow();
                    // Terapkan class untuk styling selang-seling (even/odd)
                    if (index % 2 !== 0) {
                        newRow.classList.add('odd-row'); 
                    }
                    
                    const nameCell = newRow.insertCell();
                    nameCell.textContent = anggota.nama;

                    const npmCell = newRow.insertCell();
                    npmCell.textContent = anggota.npm;
                });

                // Tampilkan Modal
                modalOverlay.classList.add('active');
            }
        });
    });

    // 3. Fungsi untuk MENYEMBUNYIKAN MODAL (Tombol X)
    closeBtn.addEventListener('click', () => {
        modalOverlay.classList.remove('active');
    });

    // 4. Fungsi untuk MENYEMBUNYIKAN MODAL (Klik di luar pop-up)
    modalOverlay.addEventListener('click', (e) => {
        // Cek jika target klik adalah overlay itu sendiri (bukan konten modal)
        if (e.target.classList.contains('modal-overlay')) {
            modalOverlay.classList.remove('active');
        }
    });

});