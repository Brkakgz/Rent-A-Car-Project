import { apiFetch, showMessage } from './common.js';

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("authToken");
    if (!token) {
        alert("You must be logged in to access the admin panel.");
        window.location.href = "/login.html";
        return;
    }

    try {
        const user = await apiFetch('/api/customers/me', 'GET');
        if (user.role !== 'ADMIN') {
            alert("Access denied. You are not authorized to view this page.");
            window.location.href = "/index.html";
        }
    } catch (error) {
        console.error("Error verifying user role:", error);
        alert("An error occurred. Please try again.");
        window.location.href = "/login.html";
    }
});


document.addEventListener("DOMContentLoaded", () => {
    const adminContent = document.getElementById("admin-content");

    // Kullanıcıları Görüntüleme
    document.getElementById("view-users-btn").addEventListener("click", async () => {
        try {
            const users = await apiFetch('/api/admin/users', 'GET');
            adminContent.innerHTML = `
                <h3>Users</h3>
                <ul>
                    ${users.map(user => `<li>${user.username} (${user.email})</li>`).join('')}
                </ul>
            `;
        } catch (error) {
            showMessage("error", "Failed to load users!");
        }
    });

    // Kiralanan Araçları Görüntüleme
    document.getElementById("view-rented-cars-btn").addEventListener("click", async () => {
        try {
            const rentedCars = await apiFetch('/api/admin/rented-cars', 'GET');
            adminContent.innerHTML = `
                <h3>Rented Cars</h3>
                <ul>
                    ${rentedCars.map(car => `
                        <li>${car.brand} ${car.model} - Rented by ${car.rentedBy}
                            <button class="btn delete-car-btn" data-id="${car.id}">Delete</button>
                        </li>
                    `).join('')}
                </ul>
            `;
        } catch (error) {
            showMessage("error", "Failed to load rented cars!");
        }
    });

    // Yeni Araç Ekleme
    document.getElementById("add-car-btn").addEventListener("click", () => {
        adminContent.innerHTML = `
            <h3>Add New Car</h3>
            <form id="add-car-form" class="form">
                <label for="car-brand">Brand</label>
                <input type="text" id="car-brand" name="brand" required>
                <label for="car-model">Model</label>
                <input type="text" id="car-model" name="model" required>
                <label for="car-year">Year</label>
                <input type="number" id="car-year" name="year" required>
                <label for="car-price">Daily Price</label>
                <input type="number" id="car-price" name="price" required>
                <button type="submit" class="btn">Add Car</button>
            </form>
        `;

        document.getElementById("add-car-form").addEventListener("submit", async (e) => {
            e.preventDefault();
            const newCar = {
                brand: document.getElementById("car-brand").value,
                model: document.getElementById("car-model").value,
                year: document.getElementById("car-year").value,
                dailyPrice: document.getElementById("car-price").value
            };

            try {
                await apiFetch('/api/admin/cars', 'POST', newCar);
                showMessage("success", "Car added successfully!");
            } catch (error) {
                showMessage("error", "Failed to add car!");
            }
        });
    });

    // Araç Silme
    adminContent.addEventListener("click", async (e) => {
        if (e.target.classList.contains("delete-car-btn")) {
            const carId = e.target.dataset.id;
            try {
                await apiFetch(`/api/admin/cars/${carId}`, 'DELETE');
                showMessage("success", "Car deleted successfully!");
                e.target.closest("li").remove(); // Listeden sil
            } catch (error) {
                showMessage("error", "Failed to delete car!");
            }
        }
    });
// Tüm Araçları Görüntüleme
document.getElementById("view-all-cars-btn").addEventListener("click", async () => {
    try {
        const cars = await apiFetch('/api/admin/cars', 'GET');
        adminContent.innerHTML = `
            <h3>All Cars</h3>
            <ul>
                ${cars.map(car => `
                    <li>
                        ${car.brand} ${car.model} - $${car.dailyPrice}
                        <button class="btn delete-car-btn" data-id="${car.id}">Delete</button>
                    </li>
                `).join('')}
            </ul>
        `;
    } catch (error) {
        showMessage("error", "Failed to load cars!");
    }
});

    
});
