import { apiFetch, showMessage } from './common.js';


// Araç ekleme modalını göster ve enum değerlerini yükle
function showAddCarModal() {
    const modal = document.getElementById("add-car-modal");
    modal.style.display = "flex";

    // Modal açıldığında enum değerlerini yükle
    loadEnumValues('gear-types', 'car-gear-type');
    loadEnumValues('fuel-types', 'car-fuel-type');
    loadEnumValues('locations', 'car-location');
}
// Araç ekleme modalını kapat
function closeAddCarModal() {
    const modal = document.getElementById("add-car-modal");
    modal.style.display = "none";
}

document.addEventListener("DOMContentLoaded", function () {
    const filePathInput = document.getElementById("filePath"); // Dosya seçme alanının ID'si
    if (filePathInput) {
        filePathInput.addEventListener("change", function (event) {
            const fullPath = event.target.value; // Tam dosya yolu
            const fileName = fullPath.split("\\").pop(); // Sadece dosya adını al
            const imageUrlInput = document.getElementById("car-image-url");
            if (imageUrlInput) {
                imageUrlInput.value = fileName; // Görsel URL alanını doldur
            } else {
                console.error("Element with ID 'car-image-url' not found");
            }
        });
    } else {
        console.error("Element with ID 'filePath' not found");
    }
});


// Yeni araç ekle - GÜNCELLENDİ
async function addNewCar() {
    const carDetails = {
        brand: document.getElementById("car-brand").value,
        model: document.getElementById("car-model").value,
        year: parseInt(document.getElementById("car-year").value),
        color: document.getElementById("car-color").value,
        dailyPrice: parseFloat(document.getElementById("car-daily-price").value),
        availableCount: parseInt(document.getElementById("car-available-count").value),
        available: document.getElementById("car-availability").checked,
        imageUrl: document.getElementById("car-image-url").value || "/uploads/cars/default.jpg", // Dosya adı
        gearType: document.getElementById("car-gear-type").value,
        fuelType: document.getElementById("car-fuel-type").value,
        kilometer: parseInt(document.getElementById("car-kilometer").value) || 0,
        location: document.getElementById("car-location").value
    };

    try {
        const queryParams = new URLSearchParams({
            brand: carDetails.brand,
            model: carDetails.model,
            year: carDetails.year,
            color: carDetails.color,
            gearType: carDetails.gearType,
            fuelType: carDetails.fuelType,
            location: carDetails.location,
            dailyPrice: carDetails.dailyPrice
        });

        const duplicateCheckResponse = await apiFetch(`/api/admin/cars/check-duplicate?${queryParams.toString()}`, 'GET');

        if (duplicateCheckResponse) {
            const confirmUpdate = window.confirm("Aynı özelliklere sahip bir araç zaten mevcut. Sayısını artırmak ister misiniz?");
            if (confirmUpdate) {
                await apiFetch(`/api/admin/cars?confirmUpdate=true`, 'POST', carDetails);
                alert("Araç sayısı başarıyla artırıldı!");
            } else {
                alert("İşlem iptal edildi.");
            }
        } else {
            await apiFetch(`/api/admin/cars`, 'POST', carDetails);
            alert("Yeni araç başarıyla eklendi!");
        }

        closeAddCarModal();
        await loadAllCars();
    } catch (error) {
        console.error("Error adding car:", error);
        alert("Araç eklenirken bir hata oluştu. Lütfen tekrar deneyin.");
    }
}





// Tüm araçları yükle - ÇALIŞIYOR
async function loadAllCars() {
    const displayArea = document.getElementById("admin-display-area");

    try {
        const cars = await apiFetch('/api/admin/cars', 'GET');
        displayArea.innerHTML = `
            <h3>All Cars</h3>
            <table>
             <thead>
                <tr>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Year</th>
                    <th>Color</th>
                    <th>Daily Price</th>
                    <th>Availability</th>
                    <th>Actions</th>
                </tr>
             </thead>
             <tbody>
                ${cars.map(car => `
                    <tr>
                        <td>${car.brand}</td>
                        <td>${car.model}</td>
                        <td>${car.year}</td>
                        <td>${car.color}</td>
                        <td>$${car.dailyPrice}</td>
                        <td>${car.available ? "Yes" : "No"}</td>
                        <td>
                            <button class="toggle-availability-btn" data-id="${car.id}" data-available="${car.available}">
                                ${car.available ? "Disable" : "Enable"}
                            </button>
                            <button data-id="${car.id}" class="update-car-btn">Update</button>
                        </td>
                    </tr>
                `).join("")}
                </tbody>
            </table>
        `;
         // Yeni elemanlara event listener ekle
                document.querySelectorAll(".toggle-availability-btn").forEach(button => {
                    button.addEventListener("click", () => {
                        const carId = button.dataset.id;
                        const availability = button.dataset.available === "true";
                        updateCarAvailability(carId, !availability); // Durumu tersine çevir
                    });
                });
    } catch (error) {
        console.error("Error loading cars:", error);
        displayArea.innerHTML = '<p>Error loading cars. Please try again later.</p>';
    }
}



// Araç durumu güncelle
async function updateCarAvailability(carId, availability) {
    try {
        await apiFetch(`/api/admin/cars/${carId}/availability?available=${availability}`, 'PUT');
        alert("Car availability updated!");
        loadAllCars();
    } catch (error) {
        console.error("Error updating car availability:", error);
        alert("Failed to update car availability.");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".toggle-availability-btn").forEach(button => {
        button.addEventListener("click", () => {
            const carId = button.dataset.id;
            const availability = button.dataset.available === "true";
            updateCarAvailability(carId, !availability); // Durumu tersine çevir
        });
    });
});


// Kiralanan araçları yükle
async function loadRentedCars() {
    const displayArea = document.getElementById("admin-display-area");

    try {
        const orders = await apiFetch('/api/admin/orders?returned=false', 'GET');
        displayArea.innerHTML =`
            <h3>Rented Cars</h3>
            <table>
             <thead>
                <tr>
                    <th>Customer</th>
                    <th>Car</th>
                    <th>Rent Date</th>
                    <th>Return Date</th>
                    <th>Actions</th>
                </tr>
                 <thead>
                ${orders.map(order =>`
                    <tr>
                        <td>${order.customer.firstName} ${order.customer.lastName}</td>
                        <td>${order.car.brand} ${order.car.model}</td>
                        <td>${order.rentDate}</td>
                        <td>${order.returnDate}</td>
                        <td>
                            <button class="mark-return-btn" data-id="${order.id}">Mark as Returned</button>
                        </td>
                    </tr>
                `).join("")}
            </table>`
        ;

        // Butonlara event listener ekle
        document.querySelectorAll(".mark-return-btn").forEach(button => {
            button.addEventListener("click", async (e) => {
                const orderId = e.target.dataset.id;
                try {
                    await apiFetch(`/api/admin/orders/${orderId}/return`, 'PUT');
                    alert("Order marked as returned!");
                    loadRentedCars(); // Tablonun güncellenmesi
                } catch (error) {
                    console.error("Error marking order as returned:", error);
                    alert("Failed to mark order as returned.");
                }
            });
        });
    } catch (error) {
        console.error("Error loading rented cars:", error);
        displayArea.innerHTML = '<p>Error loading rented cars. Please try again later.</p>';
    }
}


// Sipariş teslim alma
async function markOrderAsReturned(orderId) {
    try {
        await apiFetch(`/api/admin/orders/${orderId}/return`, 'PUT');
        alert("Order marked as returned!");
        loadRentedCars();
    } catch (error) {
        console.error("Error marking order as returned:", error);
        alert("Failed to mark order as returned.");
    }
}



//API ENUM DEĞELERİ ALMA
async function loadEnumValues(endpoint, dropdownId) {
    try {
        const response = await apiFetch(`/api/cars/${endpoint}`, 'GET'); // /api/admin/ yerine /api/cars/
        const dropdown = document.getElementById(dropdownId);

        // Dropdown'ı temizle
        dropdown.innerHTML = '';

        // Gelen değerleri dropdown'a ekle
        response.forEach(value => {
            const option = document.createElement("option");
            option.value = value;
            option.textContent = value;
            dropdown.appendChild(option);
        });
    } catch (error) {
        console.error(`Error loading ${endpoint}:`, error);
    }
}

//Kullanıcıları gösterme - API KONTROL ET
document.getElementById("view-users-btn").addEventListener("click", async () => {
    try {
        const users = await apiFetch("/api/admin/users", "GET");
        const adminDisplayArea = document.getElementById("admin-display-area");
        adminDisplayArea.innerHTML =`
            <h3>Registered Users</h3>
            <ul>
                ${users.map(user =>` <li>${user}</li>`).join("")}
            </ul>
        `;
    } catch (error) {
        console.error("Error fetching users:", error);
        alert("Failed to load users. Please try again.");
    }
});

//ORDER HISTORY API KONTROL ET
document.getElementById("view-orders-btn").addEventListener("click", async () => {
    try {
        const orders = await apiFetch("/api/admin/orders?returned=false", "GET");
        const adminDisplayArea = document.getElementById("admin-display-area");
        adminDisplayArea.innerHTML =`
            <h3>All Orders</h3>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Car</th>
                        <th>Customer</th>
                        <th>Rent Date</th>
                        <th>Return Date</th>
                        <th>Returned</th>
                    </tr>
                </thead>
                <tbody>
                    ${orders
                        .map(
                            order =>`
                        <tr>
                            <td>${order.id}</td>
                            <td>${order.car.brand} ${order.car.model}</td>
                            <td>${order.customer.firstName} ${order.customer.lastName}</td>
                            <td>${order.rentDate}</td>
                            <td>${order.returnDate}</td>
                            <td>${order.returned ? "Yes" : "No"}</td>
                        </tr>
                        `)
                        .join("")}
                </tbody>
            </table>
        `;
    } catch (error) {
        console.error("Error fetching orders:", error);
        alert("Failed to load orders. Please try again.");
    }
});

//ARAÇ DİSABLE ACTIVATE - API CONTROL ET
document.getElementById("view-cars-btn").addEventListener("click", async () => {
    try {
        await loadAllCars(); // Tüm araçları yükle
        addUpdateCarButtonListeners(); // Update butonları için event listener ekle
    } catch (error) {
        console.error("Error fetching cars:", error);
        alert("Failed to load cars. Please try again.");
    }
});

//Update butonu bağımsız hale geldi
function addUpdateCarButtonListeners() {
    document.querySelectorAll(".update-car-btn").forEach(button => {
        button.addEventListener("click", async (e) => {
            const carId = e.target.dataset.id;
            showUpdateCarModal(carId); // Güncelleme modalını göster
        });
    });
}


// Modalı açmadan önce temizle
function clearUpdateCarModal() {
    document.getElementById("update-car-id").value = "";
    document.getElementById("update-car-brand").value = "";
    document.getElementById("update-car-model").value = "";
    document.getElementById("update-car-year").value = "";
    document.getElementById("update-car-color").value = "";
    document.getElementById("update-car-daily-price").value = "";
    document.getElementById("update-car-available-count").value = "";
    document.getElementById("update-car-availability").checked = false;
    // Eğer enum dropdownlar varsa, bunları da sıfırla
    document.getElementById("update-car-gear-type").innerHTML = "";
    document.getElementById("update-car-fuel-type").innerHTML = "";
    document.getElementById("update-car-location").innerHTML = "";
}

async function showUpdateCarModal(carId) {
    console.log(`Fetching details for car ID: ${carId}`);
    try {
        const car = await apiFetch(`/api/admin/cars/${carId}`, 'GET');
        console.log("Fetched Car Details:", car);

        // Modalı temizle
        clearUpdateCarModal();

        // Modal içeriği
        const modal = document.getElementById("update-car-modal");
        modal.style.display = "flex";

        // Alanları doldur
        document.getElementById("update-car-id").value = car.id;
        document.getElementById("update-car-brand").value = car.brand;
        document.getElementById("update-car-model").value = car.model;
        document.getElementById("update-car-year").value = car.year;
        document.getElementById("update-car-color").value = car.color;
        document.getElementById("update-car-daily-price").value = car.dailyPrice;
        document.getElementById("update-car-available-count").value = car.availableCount;
        document.getElementById("update-car-availability").checked = car.available;
        document.getElementById("update-car-kilometer").value = car.kilometer || ""; // Kilometre verisini doldur

        await loadEnumValues('gear-types', 'update-car-gear-type', car.gearType);
        await loadEnumValues('fuel-types', 'update-car-fuel-type', car.fuelType);
        await loadEnumValues('locations', 'update-car-location', car.location);
    } catch (error) {
        console.error("Error fetching car details:", error);
        alert("Araç bilgileri alınamadı.");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const updateFilePathInput = document.getElementById("update-filePath");
    if (updateFilePathInput) {
        updateFilePathInput.addEventListener("change", function (event) {
            const fullPath = event.target.value; // Tam dosya yolu
            const fileName = fullPath.split("\\").pop(); // Sadece dosya adını al
            const imageUrlInput = document.getElementById("update-car-image-url");
            if (imageUrlInput) {
                imageUrlInput.value = fileName; // Görsel URL alanını doldur
            } else {
                console.error("Element with ID 'update-car-image-url' not found");
            }
        });
    } else {
        console.error("Element with ID 'update-filePath' not found");
    }
});

async function updateCar() {
    const carId = document.getElementById("update-car-id").value;
    const carDetails = {
        brand: document.getElementById("update-car-brand").value,
        model: document.getElementById("update-car-model").value,
        year: parseInt(document.getElementById("update-car-year").value),
        color: document.getElementById("update-car-color").value,
        dailyPrice: parseFloat(document.getElementById("update-car-daily-price").value),
        availableCount: parseInt(document.getElementById("update-car-available-count").value),
        available: document.getElementById("update-car-availability").checked,
        kilometer: parseInt(document.getElementById("update-car-kilometer").value) || 0,
        gearType: document.getElementById("update-car-gear-type").value,
        fuelType: document.getElementById("update-car-fuel-type").value,
        location: document.getElementById("update-car-location").value,
        imageUrl: document.getElementById("update-car-image-url").value || "/uploads/cars/default.jpg"
    };

    try {
        console.log("Gönderilen Güncelleme Verileri:", carDetails);
        await apiFetch(`/api/admin/cars/${carId}`, "PUT", carDetails);
        alert("Araç başarıyla güncellendi!");
        closeUpdateCarModal();
        await loadAllCars(); // Tüm araçları yeniden yükle
    } catch (error) {
        console.error("Error updating car:", error);
        alert("Araç güncellenemedi. Lütfen tekrar deneyin.");
    }
}



function closeUpdateCarModal() {
    const modal = document.getElementById("update-car-modal");
    modal.style.display = "none";
    clearUpdateCarModal();
}

// Save Changes butonuna eventListener ekle
document.addEventListener("DOMContentLoaded", () => {
    // Save Changes butonu için event listener
    document
        .getElementById("update-car-save")
        .addEventListener("click", updateCar);

    // Close butonu için event listener
    document
        .getElementById("update-car-close")
        .addEventListener("click", closeUpdateCarModal);
});


async function loadRentalDetails() {
    const displayArea = document.getElementById("admin-display-area");

    try {
        // Kiralama bilgilerini alın
        const rentals = await apiFetch('/api/admin/orders', 'GET');

        // Kiralama bilgilerini tabloya dökün
        displayArea.innerHTML = `
            <h3>Rental Details</h3>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Customer</th>
                        <th>Car</th>
                        <th>Pickup Date</th>
                        <th>Dropoff Date</th>
                        <th>Pickup Location</th>
                        <th>Dropoff Location</th>
                    </tr>
                </thead>
                <tbody>
                    ${rentals
                        .map(
                            rental => `
                        <tr>
                            <td>${rental.customer.firstName} ${rental.customer.lastName}</td>
                            <td>${rental.car.brand} ${rental.car.model} (${rental.car.year})</td>
                            <td>${rental.pickupDate}</td>
                            <td>${rental.dropoffDate}</td>
                            <td>${rental.pickupLocation}</td>
                            <td>${rental.dropoffLocation}</td>
                        </tr>
                    `
                        )
                        .join('')}
                </tbody>
            </table>
        `;
    } catch (error) {
        console.error("Error fetching rental details:", error);
        displayArea.innerHTML = '<p>Error loading rental details. Please try again later.</p>';
    }
}


async function loadRentalHistory() {
    const displayArea = document.getElementById("admin-display-area");

    try {
        const history = await apiFetch('/api/history/admin', 'GET');

        displayArea.innerHTML = `
            <h3>Rental History</h3>
            <table>
                <thead>
                    <tr>
                        <th>Customer</th>
                        <th>Car</th>
                        <th>Rent Date</th>
                        <th>Return Date</th>
                        <th>Pickup Location</th>
                        <th>Dropoff Location</th>
                        <th>Total Price</th>
                    </tr>
                </thead>
                <tbody>
                    ${history.map(record => `
                        <tr>
                            <td>${record.customerName} (T.C. ${record.tcNo})</td>
                            <td>${record.carBrand} ${record.carModel} (${record.carYear})</td>
                            <td>${record.rentDate}</td>
                            <td>${record.returnDate}</td>
                            <td>${record.pickupLocation}</td>
                            <td>${record.dropoffLocation}</td>
                            <td>${record.totalPrice.toFixed(2)} $</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    } catch (error) {
        console.error("Error loading rental history:", error);
        displayArea.innerHTML = '<p>Error loading rental history. Please try again later.</p>';
    }
}


document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("view-history-btn").addEventListener("click", loadRentalHistory);
});



document.getElementById("logout").addEventListener("click", (e) => {
    e.preventDefault();
    localStorage.removeItem('authToken');
    sessionStorage.clear();
    window.location.href = '/login.html';
});

document.addEventListener("DOMContentLoaded", () => {
    loadEnumValues('gear-types', 'car-gear-type');
    loadEnumValues('fuel-types', 'car-fuel-type');
    loadEnumValues('locations', 'car-location');
});

// Event listenerlar
    // Kiralanan araçları gösterme
    document.getElementById("view-rented-cars-btn").addEventListener("click", loadRentedCars);

    // Araç ekleme modalını açma
    document.getElementById("add-car-btn").addEventListener("click", showAddCarModal);

    // Yeni araç ekleme
    document.getElementById("add-car-submit").addEventListener("click", addNewCar);

    // Araç ekleme modalını kapatma
    document.getElementById("add-car-cancel").addEventListener("click", closeAddCarModal);

