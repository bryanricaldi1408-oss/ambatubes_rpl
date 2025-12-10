document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');

    loginForm.addEventListener('submit', function(event) {
        // Clear previous error messages
        clearErrorMessages();

        let isValid = true;

        // Validate email
        if (!emailInput.value.trim()) {
            showError(emailInput, 'Email is required');
            isValid = false;
        } else if (!isValidEmail(emailInput.value.trim())) {
            showError(emailInput, 'Please enter a valid email address');
            isValid = false;
        }

        // Validate password
        if (!passwordInput.value.trim()) {
            showError(passwordInput, 'Password is required');
            isValid = false;
        } else if (passwordInput.value.trim().length < 3) {
            showError(passwordInput, 'Password must be at least 3 characters long');
            isValid = false;
        }

        // If not valid, prevent form submission
        if (!isValid) {
            event.preventDefault();
        }
        // If valid, the form will submit normally and Spring will handle the authentication
        // Spring will redirect to adminHome.html if credentials are correct
    });

    // Real-time validation as user types
    emailInput.addEventListener('blur', function() {
        clearErrorForField(emailInput);
        if (!emailInput.value.trim()) {
            showError(emailInput, 'Email is required');
        } else if (!isValidEmail(emailInput.value.trim())) {
            showError(emailInput, 'Please enter a valid email address');
        }
    });

    passwordInput.addEventListener('blur', function() {
        clearErrorForField(passwordInput);
        if (!passwordInput.value.trim()) {
            showError(passwordInput, 'Password is required');
        } else if (passwordInput.value.trim().length < 3) {
            showError(passwordInput, 'Password must be at least 3 characters long');
        }
    });

    // Clear errors when user starts typing
    emailInput.addEventListener('input', function() {
        clearErrorForField(emailInput);
    });

    passwordInput.addEventListener('input', function() {
        clearErrorForField(passwordInput);
    });

    function showError(inputElement, message) {
        // Remove any existing error for this field first
        clearErrorForField(inputElement);
        
        // Add error class to input
        inputElement.classList.add('error');
        
        // Create error message element
        const errorElement = document.createElement('div');
        errorElement.className = 'error-message';
        errorElement.style.color = 'red';
        errorElement.style.fontSize = '0.8em';
        errorElement.style.marginTop = '-15px';
        errorElement.style.marginBottom = '15px';
        errorElement.textContent = message;
        
        // Insert error message after the input
        inputElement.parentNode.insertBefore(errorElement, inputElement.nextSibling);
    }

    function clearErrorForField(inputElement) {
        inputElement.classList.remove('error');
        const errorElement = inputElement.parentNode.querySelector('.error-message');
        if (errorElement) {
            errorElement.remove();
        }
    }

    function clearErrorMessages() {
        const errorMessages = document.querySelectorAll('.error-message');
        errorMessages.forEach(function(element) {
            element.remove();
        });
        
        const errorInputs = document.querySelectorAll('.error');
        errorInputs.forEach(function(input) {
            input.classList.remove('error');
        });
    }

    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
});