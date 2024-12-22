import { apiFetch, showMessage } from './common.js';

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("authToken");
    if (!token) {
        alert("You must be logged in to view your orders.");
        window.location.href = "/login.html";
        return;
    }

    // Sipariş geçmişini getir
    try {
        const orders = await apiFetch('/api/orders/history', 'GET');
        renderOrders(orders);
    } catch (error) {
        console.error("Error fetching orders:", error);
        showMessage("error", "Failed to load orders.");
    }

    // Yeni sipariş oluşturma
    document.getElementById("order-form").addEventListener("submit", async (event) => {
        event.preventDefault();

        const carId = document.getElementById("car-id").value;
        const rentDate = document.getElementById("rent-date").value;
        const returnDate = document.getElementById("return-date").value;

        try {
            await apiFetch('/api/orders', 'POST', { carId, rentDate, returnDate });
            showModal("Success", "Order placed successfully!");
            document.getElementById("order-form").reset(); // Form temizleme
            setTimeout(() => window.location.reload(), 2000); // Sayfa yenilenir
        } catch (error) {
            console.error("Error creating order:", error);
            showModal("Error", "Failed to create order. Please try again.");
        }
    });

    // Siparişleri listeleme ve detaylar
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
});
