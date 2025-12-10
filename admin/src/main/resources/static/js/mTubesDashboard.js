let taskCard = document.getElementsByClassName('task-card');
for (let i = 0; i < taskCard.length; i++) {
    taskCard[i].addEventListener('click', () => {
        window.location.href = 'deskripsiTubes.html';
    });
}

