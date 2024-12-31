import { apiFetch } from './common.js';

// Şifreyi Görüntüleme/Gizleme
document.getElementById("toggle-password").addEventListener("click", () => {
    const passwordField = document.getElementById("password");
    const toggleIcon = document.querySelector("#toggle-password i");

    if (passwordField.type === "password") {
        passwordField.type = "text";
        toggleIcon.classList.remove("fa-eye");
        toggleIcon.classList.add("fa-eye-slash");
    } else {
        passwordField.type = "password";
        toggleIcon.classList.remove("fa-eye-slash");
        toggleIcon.classList.add("fa-eye");
    }
});

// Form Submit İşlemi
document.getElementById("register-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const user = {
        firstName: document.getElementById("first-name").value,
        lastName: document.getElementById("last-name").value,
        tcNo: document.getElementById("tc-id").value,
        email: document.getElementById("email").value,
        phone: document.getElementById("phone").value,
        address: document.getElementById("address").value,
        password: document.getElementById("password").value
    };

    // Gerekli alanlar kontrolü
    if (!user.firstName || !user.lastName || !user.email || !user.password) {
        showMessage("error", "All fields are required!");
        return;
    }

    const password = user.password;
    if (password.length < 8 || !/[A-Z]/.test(password) || !/[0-9]/.test(password)) {
        showMessage("error", "Password must be at least 8 characters long and include an uppercase letter and a number.");
        return;
    }

    const messageBox = document.getElementById("message-box");

    // Yüklenme mesajını göster
    messageBox.textContent = "Registering...";
    messageBox.className = "message info";
    messageBox.style.display = "block";

    try {
        const response = await apiFetch('/api/auth/register', 'POST', user);
        if (response) {
            showMessage("success", "Registration successful!");
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 2200);
        }
    } catch (error) {
        showMessage("error", error.message || "Registration failed!");
    }
});

function showMessage(type, message) {
    const messageBox = document.getElementById("message-box");
    messageBox.textContent = message;
    messageBox.className = `message ${type}`;
    messageBox.style.display = "block";
}
