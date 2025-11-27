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
            const fileInput = document.getElementById('fileInput');
            const uploadBtn = document.querySelector('.upload-btn');
            const uploadForm = document.getElementById('uploadForm');

            // File upload functionality
            if (uploadBtn && fileInputInitial) {
                uploadBtn.addEventListener('click', function() {
                    fileInputInitial.click();
                });
            }

            if (fileInputInitial) {
                fileInputInitial.addEventListener('change', function(e) {
                    handleFileSelection(e, fileInputInitial);
                });
            }

            function handleFileSelection(event, inputElement) {
                const file = event.target.files[0];
                
                if (file) {
                    // Check if file is Excel
                    if (file.name.endsWith('.xlsx')) {
                        // Update file name display
                        fileName.textContent = file.name;
                        
                        // Set the file for the form
                        const dataTransfer = new DataTransfer();
                        dataTransfer.items.add(file);
                        fileInput.files = dataTransfer.files;
                        
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

            // Handle form submission
            if (uploadForm) {
                uploadForm.addEventListener('submit', function(e) {
                    if (!fileInput.files || fileInput.files.length === 0) {
                        e.preventDefault();
                        alert('Please select a file first');
                        return false;
                    }
                    
                    // Optional: Show loading indicator
                    const saveBtn = document.getElementById('afterSaveBtn');
                    saveBtn.textContent = 'Uploading...';
                    saveBtn.disabled = true;
                });
            }
            
            // Handle file selection from initial state
            fileInputInitial.addEventListener('change', function(event) {
                handleFileSelection(event);
            });
            
            // Handle file selection from after upload state
            fileInputAfter.addEventListener('change', function(event) {
                handleFileSelection(event);
            });
        });