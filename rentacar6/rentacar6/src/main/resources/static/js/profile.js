import { apiFetch } from './common.js';

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("authToken");
    if (!token ) {
        alert("Only customers can be use profile");
        window.location.href = "/index.html";
        return;
    }

    try {
        const user = await apiFetch('/api/customers/profile', 'GET');
        document.getElementById("profile-firstname").textContent = user.firstName;
        document.getElementById("profile-lastname").textContent = user.lastName;
        document.getElementById("profile-email").textContent = user.email;
        document.getElementById("profile-phone").textContent = user.phone;
        document.getElementById("profile-address").textContent = user.address;
        document.getElementById("profile-tc").textContent = user.tcNo;

        // Sipariş geçmişini yükle
        loadOrderHistory(user.tcNo);
    } catch (error) {
        console.error("Error fetching profile:", error);
        alert("Failed to load profile. Please try again.");
    }
});

async function loadOrderHistory(tcNo) {
    try {
        const history = await apiFetch(`/api/history/customer/${tcNo}`, 'GET');
        const orderHistoryList = document.getElementById("order-history-list");

        if (history.length === 0) {
            orderHistoryList.innerHTML = '<p>No orders found in history.</p>';
            return;
        }

        orderHistoryList.innerHTML = history.map(entry => {
            const rentDate = new Date(entry.rentDate).toLocaleDateString();
            const returnDate = new Date(entry.returnDate).toLocaleDateString();
            const totalPrice = entry.totalPrice;

            return `
                <div class="order-item">
                    <p><strong>Car:</strong> ${entry.carBrand} ${entry.carModel}</p>
                    <p><strong>Rent Date:</strong> ${rentDate}</p>
                    <p><strong>Return Date:</strong> ${returnDate}</p>
                    <p><strong>Total Price:</strong> $${totalPrice}</p>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error("Error fetching order history:", error);
        alert("Failed to load order history from history table. Please try again.");
    }
}



function calculateTotalPrice(order) {
    const rentDate = new Date(order.rentDate);
    const returnDate = new Date(order.returnDate);
    const days = Math.ceil((returnDate - rentDate) / (1000 * 60 * 60 * 24));
    return days * order.car.dailyPrice;
}

// Update profile information
document.getElementById("update-profile-btn").addEventListener("click", async () => {
    // Mevcut profil bilgilerini localStorage'dan al
    const currentProfile = JSON.parse(localStorage.getItem("currentProfile"));

    // Güncellenmiş profil verilerini hazırlayın
    const updatedProfile = {};

    // Form alanlarını alın
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const address = document.getElementById("address").value.trim();

    // Eğer bir alan boşsa, mevcut profilden alın
    updatedProfile.firstName = firstName || currentProfile.firstName;
    updatedProfile.lastName = lastName || currentProfile.lastName;
    updatedProfile.email = email || currentProfile.email;
    updatedProfile.phone = phone || currentProfile.phone;
    updatedProfile.address = address || currentProfile.address;

    try {
        const response = await apiFetch('/api/customers/profile', 'PUT', updatedProfile);

        // Yeni token varsa kaydet
        if (response.newToken) {
            localStorage.setItem('authToken', response.newToken);
        }

        alert("Profile updated successfully!");
        window.location.reload(); // Sayfayı yenile
    } catch (error) {
        console.error("Error updating profile:", error);
        alert("Failed to update profile. Please try again.");
    }
});

// Profil bilgilerini localStorage'a kaydetmek için DOMContentLoaded olayını kullanın
document.addEventListener("DOMContentLoaded", async () => {
    try {
        const profile = await apiFetch('/api/customers/profile', 'GET');
        localStorage.setItem("currentProfile", JSON.stringify(profile));

        // Formu doldurun
        document.getElementById("firstName").value = profile.firstName || "";
        document.getElementById("lastName").value = profile.lastName || "";
        document.getElementById("email").value = profile.email || "";
        document.getElementById("phone").value = profile.phone || "";
        document.getElementById("address").value = profile.address || "";
    } catch (error) {
        console.error("Error loading profile:", error);
        alert("Failed to load profile information.");
    }
});


//Logout

document.getElementById("logout").addEventListener("click", (event) => {
    event.preventDefault(); // Varsayılan davranışı engelle (Sayfanın yeniden yüklenmesini durdur)
    localStorage.removeItem("authToken"); // Token'ı kaldır
    alert("You have been logged out.");
    window.location.href = "/index.html"; // Ana sayfaya yönlendir
});


document.addEventListener("DOMContentLoaded", () => {
    // Diğer başlangıç işlemleri...

    // Logout listener'ı ekle
    document.getElementById("logout").addEventListener("click", (event) => {
        event.preventDefault();
        localStorage.removeItem("authToken");
        alert("You have been logged out.");
        window.location.href = "/index.html";
    });
});
