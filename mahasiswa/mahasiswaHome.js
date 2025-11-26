let filterBtn = document.getElementById('filterBtn');
filterBtn.addEventListener('click', () => {
    const filterPanel = document.getElementById('filterPanel');
    filterPanel.classList.toggle('active');
});

let classCard = document.getElementsByClassName('class-card');
for (let i = 0; i < classCard.length; i++) {
    classCard[i].addEventListener('click', () => {
        window.location.href = 'mTubesDashboard.html';
    });
}