document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('mahasiswaForm');
    
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        
        // Get form data
        const formData = new FormData(form);
        const mahasiswaData = {
            namaLengkap: formData.get('namaLengkap'),
            nik: formData.get('nik')
        };
        
        // Get class ID from URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        const classId = urlParams.get('classId');
        
        // Send POST request to server
        submitMahasiswaData(mahasiswaData, classId)
            .then(() => {
                // Redirect back to edit class page with the same class ID
                window.location.href = `editKelas.html?classId=${classId}`;
            })
            .catch(error => {
                console.error('Error submitting mahasiswa data:', error);
                alert('Failed to add mahasiswa. Please try again.');
            });
    });

    const linkToEdit = document.getElementById("toEdit");
    linkToEdit.addEventListener("click", function(event){
        // Get class ID from URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        const classId = urlParams.get('classId');
        window.location.href = `editKelas.html?classId=${classId}`;
    });
    
    // Function to submit mahasiswa data to server
    async function submitMahasiswaData(mahasiswaData, classId) {
        
        // This is where you would make the actual POST request
        // For now, we'll simulate the API call
        
        /*
        const payload = {
            ...mahasiswaData,
            classId: classId
        };
        
        // Simulate API call
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                // Simulate random success/failure for demo
                if (Math.random() > 0.2) { // 80% success rate for demo
                    console.log('Mahasiswa data submitted successfully:', payload);
                    resolve();
                } else {
                    reject(new Error('Server error'));
                }
            }, 1000);
        });
        */
        
        // Actual implementation would look like:
        /*
        try {
            const response = await fetch('/api/mahasiswa', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload)
            });
            
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            
            return await response.json();
        } catch (error) {
            throw error;
        }
        */
    }
});