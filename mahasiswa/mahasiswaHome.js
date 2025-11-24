let filterBtn = document.getElementById('filterBtn');
filterBtn.addEventListener('click', () => {
    const filterPanel = document.getElementById('filterPanel');
    filterPanel.classList.toggle('active');
});