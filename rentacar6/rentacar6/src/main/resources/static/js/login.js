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
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("login-form").addEventListener("submit", async function (event) {
        console.log("Form submit olayı tetiklendi");
        event.preventDefault(); // Varsayılan davranışı engelle
        console.log("event.preventDefault() çalıştı ve sayfa yenilemesi engellendi.");

        // Giriş yapmadan önce eski token'ı temizle
        clearToken();

        // Kullanıcı giriş bilgilerini al
        const email = document.getElementById("email")?.value.trim(); // Boşlukları temizle
        const password = document.getElementById("password")?.value;

        // Email veya şifre boşsa kullanıcıyı bilgilendir
        if (!email || !password) {
            console.warn("Email veya şifre eksik.");
            showMessage("error", "Email ve şifre gereklidir!");
            return;
        }

        try {
            console.log("API çağrısı yapılıyor:", { email, password });
            const response = await apiFetch('/api/auth/login', 'POST', { email, password });

            // Başarılı giriş işlemi
            if (response?.token && response?.role) {
                console.log("Giriş başarılı, token alındı:", response.token);
                localStorage.setItem('authToken', response.token); // Yeni token'ı sakla

                // Rol kontrolü
                const role = response.role; // Rolü API yanıtından alıyoruz
                if (role === 'ADMIN') {
                    showMessage("success", "Admin girişi başarılı! Admin paneline yönlendiriliyorsunuz...");
                    setTimeout(() => {
                        window.location.href = "/admin.html"; // Admin paneline yönlendirme
                    }, 1000);
                } else if (role === 'USER') {
                    showMessage("success", "Giriş başarılı! Ana sayfaya yönlendiriliyorsunuz...");
                    setTimeout(() => {
                        window.location.href = "/index.html"; // Ana sayfaya yönlendirme
                    }, 1000);
                } else {
                    showMessage("error", "Bilinmeyen bir rol. Destek ile iletişime geçin.");
                }
            } else {
                console.error("Sunucu beklenen formatta yanıt dönmedi:", response);
                showMessage("error", "Giriş işlemi başarısız! Sunucu hatası.");
            }
        } catch (error) {
            // Hata detayını konsola ve ekrana yazdır
            console.error("Hata detayı:", error.message || error.response);
            showMessage("error", error.message || "Email veya şifre hatalı.");
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
