document.addEventListener('DOMContentLoaded', function() {
    // Get DOM elements
    const addDosenBtn = document.getElementById('addDosenBtn');
    const addMahasiswaBtn = document.getElementById('addMahasiswaBtn');
    const dosenList = document.getElementById('dosenList');
    const mahasiswaList = document.getElementById('mahasiswaList');
    const saveBtn = document.getElementById('saveBtn');
    
    // Add Dosen button click handler
    addDosenBtn.addEventListener('click', function() {
        window.location.href = 'tambahDosen.html';
    });
    
    // Add Mahasiswa button click handler
    addMahasiswaBtn.addEventListener('click', function() {
        window.location.href = 'tambahMahasiswa.html';
    });
    
    // Save button click handler
    saveBtn.addEventListener('click', function() {
        // Placeholder for save functionality
        alert('Changes saved successfully!');
        // In a real implementation, this would save the data and possibly redirect
        // window.location.href = 'homeAdmin.html';
    });
});