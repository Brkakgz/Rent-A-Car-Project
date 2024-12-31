import { apiFetch, showMessage } from './common.js';

let selectedCar = null;

// Sayfa yüklendiğinde çalışacak ana fonksiyon
document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("authToken");
    if (!token) {
        alert("You must be logged in to view your orders.");
        window.location.href = "/login.html";
        return;
    }

    // Sipariş geçmişini getir
    try {
        const orders = await apiFetch('/api/orders/customer/history', 'GET');
        renderOrders(orders);
    } catch (error) {
        console.error("Error fetching orders:", error);
        showMessage("error", "Failed to load orders.");
    }

    // Kiralama formundaki düğmeyi dinle
    attachRentalFormListener();
});

// Sipariş geçmişini listeleme ve detaylar
function renderOrders(orders) {
    const orderList = document.getElementById("order-list");
    orderList.innerHTML = orders.map(order => `
        <div class="order">
            <p><strong>Car:</strong> ${order.car.brand} ${order.car.model}</p>
            <p><strong>Rent Date:</strong> ${order.rentDate}</p>
            <p><strong>Return Date:</strong> ${order.returnDate}</p>
            <p><strong>Status:</strong> ${order.returned ? "Returned" : "Active"}</p>
            <button class="btn order-details-btn" data-id="${order.id}">Details</button>
        </div>
    `).join('');

    // Detay düğmelerini bağlama
    document.querySelectorAll(".order-details-btn").forEach(button => {
        button.addEventListener("click", async (e) => {
            const orderId = e.target.dataset.id;
            try {
                const order = await apiFetch(`/api/orders/${orderId}`, 'GET');
                showModal("Order Details", `
                    <p><strong>Car:</strong> ${order.car.brand} ${order.car.model}</p>
                    <p><strong>Rent Date:</strong> ${order.rentDate}</p>
                    <p><strong>Return Date:</strong> ${order.returnDate}</p>
                    <p><strong>Total Price:</strong> $${order.totalPrice}</p>
                `);
            } catch (error) {
                console.error("Error fetching order details:", error);
                showModal("Error", "Failed to fetch order details.");
            }
        });
    });
}

// Sipariş oluşturma formunu dinleyiciye bağlama
/unction attachRentalFormListener() {
    const orderForm = document.getElementById("order-form");
    if (!orderForm) {
        console.error("Order form not found in the DOM.");
        return;
    }

    orderForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const rentDate = document.getElementById("rent-date").value;
        const returnDate = document.getElementById("return-date").value;
        const dropoffLocation = document.getElementById("dropoff-location").value;

        if (!selectedCar) {
            alert("No car selected!");
            return;
        }

        try {
            await apiFetch('/api/orders/create', 'POST', {
                carId: selectedCar.id,
                rentDate,
                returnDate,
                pickupLocation: selectedCar.location, // Araç bilgisi üzerinden lokasyon
                dropoffLocation // Kullanıcının seçtiği teslim lokasyonu
            });

            alert("Order placed successfully!");
            document.getElementById("order-form").reset(); // Formu temizle
            location.reload();
        } catch (error) {
            console.error("Error creating order:", error);
            alert("Failed to create order. Please try again.");
        }
    });
}

// Modal gösterimi
function showModal(title, content) {
    const modal = document.getElementById("modal");
    document.getElementById("modal-title").textContent = title;
    document.getElementById("modal-message").innerHTML = content;
    modal.style.display = "flex";

    const closeButton = modal.querySelector(".close-btn");
    closeButton.removeEventListener("click", closeModal); // Eski dinleyiciyi kaldır
    closeButton.addEventListener("click", closeModal); // Yeni dinleyiciyi ekle
}

function closeModal() {
    const modal = document.getElementById("modal");
    modal.style.display = "none";
}
