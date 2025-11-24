document.addEventListener('DOMContentLoaded', function() {
            // Get DOM elements
            const fileInputInitial = document.getElementById('fileInputInitial');
            const fileInputAfter = document.getElementById('fileInputAfter');
            const fileName = document.getElementById('fileName');
            const initialState = document.getElementById('initialState');
            const afterUploadState = document.getElementById('afterUploadState');
            const initialBackBtn = document.getElementById('initialBackBtn');
            const afterSaveBtn = document.getElementById('afterSaveBtn');
            const cancelBtn = document.getElementById('cancelBtn');
            
            // Handle file selection from initial state
            fileInputInitial.addEventListener('change', function(event) {
                handleFileSelection(event);
            });
            
            // Handle file selection from after upload state
            fileInputAfter.addEventListener('change', function(event) {
                handleFileSelection(event);
            });
            
            // Function to handle file selection
            function handleFileSelection(event) {
                const file = event.target.files[0];
                
                if (file) {
                    // Check if file is Excel
                    if (file.name.endsWith('.xlsx')) {
                        // Update file name display
                        fileName.textContent = file.name;
                        
                        // Switch to after-upload state
                        initialState.style.display = 'none';
                        afterUploadState.style.display = 'block';
                    } else {
                        // Show error for non-Excel files
                        alert('File must be an Excel file (.xlsx)');
                        // Reset file input
                        event.target.value = '';
                    }
                }
            }
            
            // Initial save button - redirect to adminHome.html
            initialBackBtn.addEventListener('click', function() {
                window.location.href = 'adminHome.html';
            });
            
            // After upload save button
            afterSaveBtn.addEventListener('click', function() {
                window.location.href = 'adminHome.html';
            });
            
            // Cancel button - redirect to adminHome.html
            cancelBtn.addEventListener('click', function() {
                window.location.href = 'adminHome.html';
            });
        });