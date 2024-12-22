import { apiFetch } from './common.js';

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("authToken");
    if (!token) {
        alert("You need to login first.");
        window.location.href = "/login.html";
        return;
    }

    try {
        const user = await apiFetch('/api/customers/me', 'GET');
        document.getElementById("profile-username").textContent = user.username;
        document.getElementById("profile-email").textContent = user.email;
        document.getElementById("profile-phone").textContent = user.phone;
    } catch (error) {
        console.error("Error fetching profile:", error);
        alert("Failed to load profile. Please try again.");
    }
});

document.getElementById("logout").addEventListener("click", () => {
    localStorage.removeItem("authToken");
    window.location.href = "/login.html";
});
