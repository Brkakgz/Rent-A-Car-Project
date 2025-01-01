import { apiFetch } from './common.js';


// Çıkış işlemi
document.getElementById("logout-link").addEventListener("click", () => {
    localStorage.removeItem('authToken'); // Token kaldır
    alert("Logged out successfully.");
    window.location.href = "/"; // Ana sayfaya yönlendir
});

// Kullanıcı rolünü kontrol eden yardımcı fonksiyon
async function checkUserRole() {
    try {
        const user = await apiFetch('/api/auth/me', 'GET');
        return { role: user.role || 'guest' }; // Kullanıcı rolünü döndür
    } catch (error) {
        return { role: 'guest' }; // Hata durumunda misafir olarak kabul et
    }
}

// Global `checkUserRole` export edilir
export { checkUserRole };


// Kiralama modalında tarih seçimi ve fiyat hesaplama fonksiyonu
let selectedCar = null;
// Modal içeriğini yükleme fonksiyonu
function showRentModal(car) {
    selectedCar = car;

    document.getElementById("modal-car-title").textContent = `${car.brand} ${car.model}`;
    document.getElementById("modal-car-description").innerHTML = `
        <p><strong>Year:</strong> ${car.year}</p>
        <p><strong>Color:</strong> ${car.color}</p>
        <p><strong>Fuel Type:</strong> ${car.fuelType}</p>
        <p><strong>Gear Type:</strong> ${car.gearType}</p>
    `;
    document.getElementById("modal-car-price").textContent = `Price: $${car.dailyPrice} / day`;
    document.getElementById("modal-car-available-count").textContent = `Available Cars: ${car.availableCount}`;

    attachDateChangeListeners();
    loadDropoffLocations();

    const modal = document.getElementById("car-details-modal");
    modal.style.display = "flex";
}


// Modal içindeki tarih değişikliği dinleyicileri
function attachDateChangeListeners() {
    const rentDateInput = document.getElementById("rent-date");
    const returnDateInput = document.getElementById("return-date");

    if (rentDateInput && returnDateInput) {
        rentDateInput.addEventListener("change", calculateTotalPrice);
        returnDateInput.addEventListener("change", calculateTotalPrice);
    } else {
        console.error("Rent date or return date elements are missing in the DOM.");
    }
}

// Modal kapatma işlevi
window.closeModal = function () {
    const modal = document.getElementById("car-details-modal");
    if (modal) {
        modal.style.display = "none";
    } else {
        console.error("Modal element not found.");
    }
};

// Toplam fiyat hesaplama
function calculateTotalPrice() {
    const rentDate = new Date(document.getElementById("rent-date").value);
    const returnDate = new Date(document.getElementById("return-date").value);

    if (rentDate && returnDate && rentDate < returnDate) {
        const days = Math.ceil((returnDate - rentDate) / (1000 * 60 * 60 * 24));
        const totalPrice = days * selectedCar.dailyPrice;
        document.getElementById("total-price").textContent = `$${totalPrice}`;
    } else {
        document.getElementById("total-price").textContent = "Invalid dates";
    }
}

// Kiralama formunu gönderme
async function submitRentalForm() {
    const rentDate = document.getElementById("rent-date").value;
    const returnDate = document.getElementById("return-date").value;
    const dropoffLocation = document.getElementById("dropoff-location").value; // Kullanıcı seçimi
    const pickupLocation = selectedCar.location; // Araç bilgisi üzerinden lokasyon

    try {
        await apiFetch('/api/orders/create', 'POST', {
            carId: selectedCar.id,
            rentDate,
            returnDate,
            pickupLocation, // Alış lokasyonu
            dropoffLocation // Kullanıcı tarafından seçilecek teslim lokasyonu
        });

        alert("Order placed successfully!");
        location.reload();
    } catch (error) {
        console.error("Error placing order:", error);
        alert("Failed to place order. Please try again.");
    }
}

// Modal form gönderim dinleyicisi
function attachFormSubmissionListener() {
    const rentButton = document.getElementById("modal-rent-button");
    if (rentButton) {
        rentButton.addEventListener("click", submitRentalForm);
    } else {
        console.error("Modal rent button is missing in the DOM.");
    }
}

// Araçları yükleme
async function loadCars() {
    const carsContainer = document.getElementById("cars-container");

    if (!carsContainer) {
        console.error("Cars container element not found in DOM.");
        return; // Eğer eleman bulunamazsa işlemi sonlandır
    }

    try {
        const cars = await apiFetch('/api/cars/allCars', 'GET');

        if (!cars || cars.length === 0) {
            carsContainer.innerHTML = '<p>No cars available at the moment.</p>';
            return;
        }

        carsContainer.innerHTML = cars.map(car => createCarCard(car)).join('');
        attachRentButtons(); // Butonlara olay dinleyicileri ekle
    } catch (error) {
        console.error("Error loading cars:", error);
        carsContainer.innerHTML = '<p class="error-message">Error loading cars. Please try again.</p>';
    }
}





// Rent butonlarına tıklama dinleyicileri
function attachRentButtons() {
    document.querySelectorAll(".rent-btn").forEach(button => {
        button.addEventListener("click", async (e) => {
            const carId = e.target.dataset.id;
            if (!carId) {
                console.error("Car ID is missing for this button.");
                alert("Car ID is missing. Unable to proceed.");
                return;
            }

            const token = localStorage.getItem('authToken');
            if (!token) {
                alert("Sadece üyeler araç kiralayabilir. Lütfen giriş yapınız.");
                return;
            }

            try {
                const car = await apiFetch(`/api/cars/${carId}`, 'GET');
                if (!car.id) {
                    console.error("Fetched car data is missing ID:", car);
                    alert("Car details are incomplete. Please try again.");
                    return;
                }

                showRentModal(car);
            } catch (error) {
                console.error("Error loading car details:", error);
                alert("Failed to load car details. Please try again.");
            }
        });
    });
}

// Filtreleme işlevi
document.getElementById("apply-filters-button").addEventListener("click", applyFilters);

async function applyFilters() {
    const carsContainer = document.getElementById('cars-container'); // ID'yi kontrol edin

    if (!carsContainer) {
        console.error("Cars container not found in DOM."); // Eğer element bulunamazsa hata
        return;
    }

    // Filtreleme için değerleri al
    const brand = document.getElementById('brand')?.value || '';
    const model = document.getElementById('model')?.value || '';
    const color = document.getElementById('color')?.value || '';
    const minPrice = document.getElementById('minPrice')?.value || '';
    const maxPrice = document.getElementById('maxPrice')?.value || '';
    const year = document.getElementById('year')?.value || '';
    const gearType = document.getElementById('gearType')?.value || '';
    const fuelType = document.getElementById('fuelType')?.value || '';
    const location = document.getElementById('location')?.value || '';
    const minKilometers = document.getElementById('minKilometers')?.value || '';
    const maxKilometers = document.getElementById('maxKilometers')?.value || '';

    const queryParams = new URLSearchParams({
        brand,
        model,
        color,
        minPrice,
        maxPrice,
        year,
        gearType,
        fuelType,
        location,
        minKilometers,
        maxKilometers
    }).toString();

    try {
        const cars = await apiFetch(`/api/cars/filteredCars?${queryParams}`, 'GET');

        carsContainer.innerHTML = ''; // Mevcut içeriği temizle

        if (!cars || cars.length === 0) {
            carsContainer.innerHTML = '<p>No cars match your filters.</p>'; // Filtre sonucu yoksa
            return;
        }

        carsContainer.innerHTML = cars.map(car => createCarCard(car)).join('');
         attachRentButtons(); // Rent now butonlarına dinleyici ekleniyor
    } catch (error) {
        console.error("Error applying filters:", error);
        carsContainer.innerHTML = '<p class="error-message">Failed to apply filters. Please try again.</p>';
    }
}




// Kullanıcının giriş durumunu kontrol eden fonksiyon
async function updateNavbar() {
    try {
        const response = await apiFetch('/api/auth/role', 'GET'); // Rol bilgisini al
        const navbar = document.getElementById('nav-links');

        // Navbar'ı temizle
        navbar.innerHTML = '';

        if (response && response.role === 'admin') {
            navbar.innerHTML = `
                <a href="index.html">Home</a>
                <a href="admin.html">Admin Panel</a>
                <a id="logout-link" href="#">Logout</a>
            `;
        } else if (response && response.role === 'user') {
            navbar.innerHTML = `
                <a href="index.html">Home</a>
                <a href="profile.html">Profile</a>
                <a id="logout-link" href="#">Logout</a>
            `;
        } else {
            // Guest kullanıcı (token yok veya hatalı)
            navbar.innerHTML = `
                <a href="index.html">Home</a>
                <a href="login.html">Login</a>
                <a href="register.html">Register</a>
            `;
        }

        // Logout butonuna event listener ekle
        const logoutButton = document.getElementById('logout-link');
        if (logoutButton) {
            logoutButton.addEventListener('click', () => {
                localStorage.removeItem('authToken');
                alert("Logged out successfully.");
                window.location.href = "/"; // Ana sayfaya yönlendir
            });
        }
    } catch (error) {
        console.error('Navbar güncellenemedi:', error.message);
    }
}

//Dropdown dinamik
async function loadDropoffLocations() {
    try {
        const locations = await apiFetch('/api/cars/locations', 'GET'); // Backend'den verileri çek
        const dropoffDropdown = document.getElementById("dropoff-location");

        // Dropdown'ı temizle
        dropoffDropdown.innerHTML = '';

        // Verileri ekle
        locations.forEach(location => {
            const option = document.createElement("option");
            option.value = location;
            option.textContent = location;
            dropoffDropdown.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading dropoff locations:", error);
        alert("Failed to load dropoff locations. Please try again.");
    }
}

// Dropdownları dinamik olarak doldur
async function loadDropdowns() {
    try {
        // Lokasyonlar
        const locations = await apiFetch('/api/cars/locations', 'GET');
        const locationDropdown = document.getElementById('location');
        populateDropdown(locationDropdown, locations);

        // Vites Türleri
        const gearTypes = await apiFetch('/api/cars/gear-types', 'GET');
        const gearTypeDropdown = document.getElementById('gearType');
        populateDropdown(gearTypeDropdown, gearTypes);

        // Yakıt Türleri
        const fuelTypes = await apiFetch('/api/cars/fuel-types', 'GET');
        const fuelTypeDropdown = document.getElementById('fuelType');
        populateDropdown(fuelTypeDropdown, fuelTypes);
    } catch (error) {
        console.error("Error loading dropdown options:", error);
        alert("Failed to load filter options. Please try again.");
    }
}

// Dropdown'ı doldurmak için yardımcı fonksiyon
function populateDropdown(dropdown, options) {
    if (!dropdown || !options) return; // Null kontrolü
    dropdown.innerHTML = '<option value="">All</option>';
    options.forEach(option => {
        const optionElement = document.createElement('option');
        optionElement.value = option;
        optionElement.textContent = option;
        dropdown.appendChild(optionElement);
    });
}

//KArt Detay işlevi
function createCarCard(car) {
    const isRentable = car.available && car.availableCount > 0;
    const buttonClass = isRentable ? "rent-btn" : "rent-btn disabled";
    const buttonText = isRentable ? "Rent Now" : "Not Available";

    return `
        <div class="car-card">
            <img src="${car.imageUrl || '/uploads/cars/default.jpg'}" alt="${car.brand} ${car.model}" class="car-image">
            <div class="car-info">
                <h3 class="car-title">${car.brand} ${car.model}</h3>
                <p class="car-details"><strong>Year:</strong> ${car.year}</p>
                <p class="car-details"><strong>Color:</strong> ${car.color}</p>
                <p class="car-details"><strong>Fuel:</strong> ${car.fuelType}</p>
                <p class="car-details"><strong>Gear:</strong> ${car.gearType}</p>
                <p class="car-km"><strong>Mileage:</strong> ${car.kilometer || 0} km</p>
                <p class="car-location"><strong>Location:</strong> ${car.location}</p>
                <p class="car-price"><strong>Price Per Day:</strong> $${car.dailyPrice.toFixed(2)}</p>
                <p class="car-availability"><strong>Available Cars:</strong> ${car.availableCount}</p>
                <button class="btn ${buttonClass}" data-id="${car.id}">${buttonText}</button>
            </div>
        </div>
    `;
}


//ŞehirBazlıAnasayfa

//ŞehirKartları
document.addEventListener("DOMContentLoaded", async () => {
    const citiesContainer = document.getElementById("cities-container");

    if (!citiesContainer) {
        console.error("Cities container not found in DOM.");
        return; // Eğer eleman bulunamazsa işlemi sonlandır
    }

    try {
        const cities = await apiFetch('/api/cars/locations', 'GET');

        if (!cities || cities.length === 0) {
            citiesContainer.innerHTML = '<p>No cities available.</p>';
            return;
        }

        citiesContainer.innerHTML = cities.map(city => `
            <button class="city-card" data-city="${city}">
                <h3>${city}</h3>
            </button>
        `).join('');

        // Şehir kartlarına tıklama olayı ekle
        document.querySelectorAll(".city-card").forEach(card => {
            card.addEventListener("click", async () => {
                const city = card.dataset.city;
                await loadCarsByCity(city);
            });
        });
    } catch (error) {
        console.error("Error loading cities:", error);
        citiesContainer.innerHTML = '<p>Error loading cities. Please try again later.</p>';
    }
});




//ŞehirBazlı araç yükleme:
async function loadCarsByCity(city) {
    const carsContainer = document.getElementById("cars-container"); // ID'nin var olduğundan emin olun

    if (!carsContainer) {
        console.error("Cars container element not found in DOM.");
        return;
    }

    try {
        const cars = await apiFetch(`/api/cars/by-location?location=${city}`, 'GET');

        if (!cars || cars.length === 0) {
            carsContainer.innerHTML = `<p>No cars available in ${city}.</p>`;
            return;
        }

        carsContainer.innerHTML = cars.map(car => createCarCard(car)).join('');
        attachRentButtons(); // Rent now butonlarına dinleyici ekleniyor
    } catch (error) {
        console.error("Error loading cars for city:", error);
        carsContainer.innerHTML = `<p>Error loading cars for ${city}. Please try again later.</p>`;
    }
}



// Sayfa yüklendiğinde dropdownları doldur
document.addEventListener("DOMContentLoaded", async () => {
    await loadDropdowns(); // Dropdownları dinamik olarak doldur
    await loadCars(); // Araçları yükle
});



// Global `applyFilters`
window.applyFilters = applyFilters;

// Başlatıcı fonksiyonlar
document.addEventListener("DOMContentLoaded", () => {
     updateNavbar();;
    loadCars();
    attachFormSubmissionListener();
});
