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
