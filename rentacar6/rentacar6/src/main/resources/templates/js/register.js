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

document.getElementById("register-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const user = {
        username: document.getElementById("username").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

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
            }, 2000);
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
