import { apiFetch, showMessage } from './common.js';

// Şifreyi Görüntüleme/Gizleme
document.getElementById("toggle-password").addEventListener("click", () => {
    const passwordField = document.getElementById("password");
    const toggleIcon = document.querySelector("#toggle-password i");

    // Şifre alanı tipi değiştir
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

// Tarayıcıdan JWT Token'ı Temizleme
function clearToken() {
    console.log("Eski token temizleniyor...");
    localStorage.removeItem('authToken'); // localStorage'dan token sil
    sessionStorage.removeItem('authToken'); // sessionStorage'dan token sil (varsa)
}

// Login Form Submit İşlemi
// Login Form Submit İşlemi
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("login-form").addEventListener("submit", async function (event) {
        event.preventDefault(); // Varsayılan davranışı engelle
        clearToken(); // Giriş yapmadan önce eski token'ı temizle

        // Kullanıcı giriş bilgilerini al
        const email = document.getElementById("email")?.value.trim(); // Boşlukları temizle
        const password = document.getElementById("password")?.value;

        // Email veya şifre boşsa kullanıcıyı bilgilendir
        if (!email || !password) {
            showMessage("error", "Email and password are required!");
            return;
        }

        try {
            const response = await apiFetch('/api/auth/login', 'POST', { email, password });

            // Başarılı giriş işlemi
            if (response?.token && response?.role) {
                localStorage.setItem('authToken', response.token); // Yeni token'ı sakla

                const role = response.role; // Rol kontrolü
                if (role === 'ADMIN') {
                    showMessage("success", "Admin login successful! Redirecting to admin panel...");
                    setTimeout(() => {
                        window.location.href = "/admin.html";
                    }, 1000);
                } else if (role === 'USER') {
                    showMessage("success", "Login successful! Redirecting to homepage...");
                    setTimeout(() => {
                        window.location.href = "/index.html";
                    }, 1000);
                } else {
                    showMessage("error", "Unknown role. Please contact support.");
                }
            } else {
                console.error("Unexpected server response format:", response);
                showMessage("error", "Login failed! Please try again.");
            }
        } catch (error) {
            console.error("Error details:", error.message || error.response);
            showMessage("error", "Incorrect email or password.");
        }
    });
});


// Oturumu Kapatma (Logout) İşlevi
document.getElementById("logout-button")?.addEventListener("click", () => {
    clearToken(); // Token'ı temizle
    showMessage("success", "Oturum kapatıldı. Ana sayfaya yönlendiriliyorsunuz...");

    setTimeout(() => {
        window.location.href = "/login.html"; // Login sayfasına yönlendir
    }, 1000);
});
