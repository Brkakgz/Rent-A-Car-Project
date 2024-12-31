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

async function loadOrderHistory() {
    try {
        const orders = await apiFetch(`/api/orders/customer/history`, 'GET');
        const orderHistoryList = document.getElementById("order-history-list");

        if (orders.length === 0) {
            orderHistoryList.innerHTML = '<p>No orders found.</p>';
            return;
        }

        orderHistoryList.innerHTML = orders.map(order => {
            const rentDate = new Date(order.rentDate).toLocaleDateString();
            const returnDate = new Date(order.returnDate).toLocaleDateString();
            const totalPrice = order.totalPrice;

            return `
                <div class="order-item">
                    <p><strong>Car:</strong> ${order.car.brand} ${order.car.model}</p>
                    <p><strong>Rent Date:</strong> ${rentDate}</p>
                    <p><strong>Return Date:</strong> ${returnDate}</p>
                    <p><strong>Total Price:</strong> $${totalPrice}</p>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error("Error fetching order history:", error);
        alert("Failed to load order history. Please try again.");
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
    const updatedProfile = {};

    // Sadece doldurulmuş alanları ekle
    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const address = document.getElementById("address").value.trim();

    if (firstName) updatedProfile.firstName = firstName;
    if (lastName) updatedProfile.lastName = lastName;
    if (email) updatedProfile.email = email;
    if (phone) updatedProfile.phone = phone;
    if (address) updatedProfile.address = address;

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
