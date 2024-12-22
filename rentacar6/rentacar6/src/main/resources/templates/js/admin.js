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
                    ${users.map(user => `
                        <li>
                            T.C. No: ${user.tcNo}, Name: ${user.firstName} ${user.lastName}
                            <button class="btn user-details-btn" data-tcno="${user.tcNo}">View Details</button>
                        </li>
                    `).join('')}
                </ul>
            `;

            // Kullanıcı detaylarını göster
            document.querySelectorAll('.user-details-btn').forEach(button => {
                button.addEventListener('click', async (e) => {
                    const tcNo = e.target.dataset.tcno;
                    try {
                        const userDetails = await apiFetch(`/api/admin/users/${tcNo}`, 'GET');
                        showModal(`User Details: T.C. No: ${userDetails.tcNo}`, `
                            <p><strong>Name:</strong> ${userDetails.firstName} ${userDetails.lastName}</p>
                            <p><strong>Email:</strong> ${userDetails.email}</p>
                            <p><strong>Phone:</strong> ${userDetails.phone}</p>
                            <p><strong>Address:</strong> ${userDetails.address}</p>
                        `);
                    } catch (error) {
                        showMessage("error", "Failed to fetch user details!");
                    }
                });
            });
        } catch (error) {
            showMessage("error", "Failed to load users!");
        }
    });

    // Araç Kiralama ve Teslim Alma
    document.getElementById("view-rented-cars-btn").addEventListener("click", async () => {
        try {
            const rentedCars = await apiFetch('/api/admin/rented-cars', 'GET');
            adminContent.innerHTML = `
                <h3>Rented Cars</h3>
                <ul>
                    ${rentedCars.map(car => `
                        <li>${car.brand} ${car.model} - Rented by T.C. No: ${car.rentedByTcNo}
                            <button class="btn return-car-btn" data-id="${car.id}">Return Car</button>
                        </li>
                    `).join('')}
                </ul>
            `;
        } catch (error) {
            showMessage("error", "Failed to load rented cars!");
        }
    });

    adminContent.addEventListener("click", async (e) => {
        if (e.target.classList.contains("return-car-btn")) {
            const carId = e.target.dataset.id;
            try {
                await apiFetch(`/api/admin/cars/${carId}/return`, 'PUT');
                showMessage("success", "Car returned successfully!");
                e.target.closest("li").remove();
            } catch (error) {
                showMessage("error", "Failed to return car!");
            }
        }
    });

    // Yeni Araç Ekleme (FormData ile)
    document.getElementById("add-car-btn").addEventListener("click", () => {
        adminContent.innerHTML = `
            <h3>Add New Car</h3>
            <form id="add-car-form" enctype="multipart/form-data">
                <label for="car-brand">Brand</label>
                <input type="text" id="car-brand" name="brand" required>
                <label for="car-model">Model</label>
                <input type="text" id="car-model" name="model" required>
                <label for="car-year">Year</label>
                <input type="number" id="car-year" name="year" required>
                <label for="car-color">Color</label>
                <input type="text" id="car-color" name="color" required>
                <label for="car-price">Daily Price</label>
                <input type="number" id="car-price" name="dailyPrice" required>
                <label for="car-image">Image</label>
                <input type="file" id="car-image" name="image" accept="image/*">
                <button type="submit" class="btn">Add Car</button>
            </form>
        `;

        document.getElementById("add-car-form").addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);

            try {
                const response = await fetch('/api/admin/cars', {
                    method: 'POST',
                    body: formData,
                });

                if (response.ok) {
                    alert("Car added successfully!");
                    location.reload();
                } else {
                    throw new Error("Failed to add car");
                }
            } catch (error) {
                console.error("Error adding car:", error);
                alert("Error adding car!");
            }
        });
    });

    // Modal Gösterimi
    function showModal(title, content) {
        const modal = document.createElement('div');
        modal.classList.add('modal');
        modal.innerHTML = `
            <div class="modal-content">
                <span class="close-btn">&times;</span>
                <h3>${title}</h3>
                ${content}
            </div>
        `;
        document.body.appendChild(modal);

        modal.querySelector('.close-btn').addEventListener('click', () => {
            modal.remove();
        });
    }
});
