document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('dosenForm');
    
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        
        // Get form data
        const formData = new FormData(form);
        const dosenData = {
            namaLengkap: formData.get('namaLengkap'),
            nik: formData.get('nik')
        };
        
        // Get class ID from URL parameters or session storage
        const urlParams = new URLSearchParams(window.location.search);
        const classId = urlParams.get('classId') || getClassIdFromContext();
        
        // Send POST request to server
        submitDosenData(dosenData, classId)
            .then(() => {
                // Redirect back to edit class page with the same class ID
                window.location.href = `editKelas.html?classId=${classId}`;
            })
            .catch(error => {
                console.error('Error submitting dosen data:', error);
                alert('Failed to add dosen. Please try again.');
            });
    });
    
    // Function to get class ID from context
    function getClassIdFromContext() {
        // Try to get from session storage first
        const storedClassId = sessionStorage.getItem('currentClassId');
        if (storedClassId) {
            return storedClassId;
        }
        
        // If not in session storage, try to get from referrer
        // This would work if coming from editKelas.html with classId in URL
        if (document.referrer) {
            const referrerUrl = new URL(document.referrer);
            const referrerClassId = referrerUrl.searchParams.get('classId');
            if (referrerClassId) {
                // Store for future use
                sessionStorage.setItem('currentClassId', referrerClassId);
                return referrerClassId;
            }
        }
        
        // Fallback - you might want to handle this differently
        console.warn('No class ID found, using default');
        return 'default-class-id';
    }
    
    // Function to submit dosen data to server
    async function submitDosenData(dosenData, classId) {
        // This is where you would make the actual POST request
        // For now, we'll simulate the API call
        
        /*
        const payload = {
            ...dosenData,
            classId: classId
        };
        
        // Simulate API call
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                // Simulate random success/failure for demo
                if (Math.random() > 0.2) { // 80% success rate for demo
                    console.log('Dosen data submitted successfully:', payload);
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
            const response = await fetch('/api/dosen', {
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