document.addEventListener("DOMContentLoaded", () => {
    // Dinamik Araç Listesi Yükleme
    loadCars();

    // Login Formu Gönderim İşlemi
    const loginForm = document.getElementById("login-form");
    const loginError = document.getElementById("login-error");

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                alert("Login successful! Token: " + data.token);
                localStorage.setItem("authToken", data.token);
                loginError.style.display = "none";
            } else {
                loginError.style.display = "block";
                loginError.textContent = "Invalid username or password.";
            }
        } catch (error) {
            loginError.style.display = "block";
            loginError.textContent = "An error occurred during login. Please try again.";
            console.error("Error during login:", error);
        }
    });
});

// Araçları Yükleyen Fonksiyon
async function loadCars() {
    const spinner = document.getElementById("loading-spinner");
    spinner.style.display = "block";

    try {
        const response = await fetch('/api/admin/cars');
        if (response.ok) {
            const cars = await response.json();
            displayCategories(cars);
            displayCars(cars);
        } else {
            console.error("Failed to load cars:", response.status);
        }
    } catch (error) {
        console.error("Error fetching cars:", error);
    } finally {
        spinner.style.display = "none";
    }
}

// Kategorileri Göster
function displayCategories(cars) {
    const categories = Array.from(new Set(cars.map(car => car.brand)));
    const categoryContainer = document.getElementById("car-categories");
    categoryContainer.innerHTML = "";

    categories.forEach(category => {
        const button = document.createElement("button");
        button.textContent = category;
        button.addEventListener("click", () => filterCarsByCategory(cars, category));
        categoryContainer.appendChild(button);
    });
}

// Araçları Kategoriye Göre Filtrele
function filterCarsByCategory(cars, category) {
    const filteredCars = cars.filter(car => car.brand === category);
    displayCars(filteredCars);
}

// Araçları Göster
function displayCars(cars) {
    const carList = document.getElementById("car-list");
    carList.innerHTML = ""; // Önceki içeriği temizle

    cars.forEach((car) => {
        const carCard = document.createElement("div");
        carCard.classList.add("car-card");
        carCard.innerHTML = `
            <h3>${car.brand} ${car.model}</h3>
            <p><strong>Year:</strong> ${car.year}</p>
            <p><strong>Color:</strong> ${car.color}</p>
            <p><strong>Price per day:</strong> $${car.dailyPrice}</p>
            <p><strong>Available:</strong> ${car.available ? "Yes" : "No"}</p>
        `;
        carList.appendChild(carCard);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    loadCars();

    const loginForm = document.getElementById("login-form");
    const loginError = document.getElementById("login-error");
    const profileSection = document.getElementById("profile");
    const logoutButton = document.getElementById("logout-btn");

    // Login işlemi
    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem("authToken", data.token);
                loginError.style.display = "none";
                loadUserProfile(data.token);
            } else {
                loginError.style.display = "block";
                loginError.textContent = "Invalid username or password.";
            }
        } catch (error) {
            loginError.style.display = "block";
            loginError.textContent = "An error occurred during login.";
            console.error("Error during login:", error);
        }
    });

    // Kullanıcı profilini yükle
    async function loadUserProfile(token) {
        try {
            const response = await fetch('/api/customers/me', {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.ok) {
                const userProfile = await response.json();
                document.getElementById("profile-username").textContent = userProfile.username;
                document.getElementById("profile-email").textContent = userProfile.email;
                document.getElementById("profile-phone").textContent = userProfile.phone;
                profileSection.style.display = "block";
                document.getElementById("login").style.display = "none";
            } else {
                console.error("Failed to load profile:", response.status);
            }
        } catch (error) {
            console.error("Error fetching profile:", error);
        }
    }

    // Çıkış yap
    logoutButton.addEventListener("click", () => {
        localStorage.removeItem("authToken");
        profileSection.style.display = "none";
        document.getElementById("login").style.display = "block";
    });
});

function displayCars(cars) {
    const carList = document.getElementById("car-list");
    carList.innerHTML = "";

    cars.forEach((car) => {
        const carCard = document.createElement("div");
        carCard.classList.add("car-card");
        carCard.innerHTML = `
            <h3>${car.brand} ${car.model}</h3>
            <p><strong>Year:</strong> ${car.year}</p>
            <p><strong>Color:</strong> ${car.color}</p>
            <p><strong>Price per day:</strong> $${car.dailyPrice}</p>
            <p><strong>Available:</strong> ${car.available ? "Yes" : "No"}</p>
            <button class="btn rent-btn" data-id="${car.id}" ${!car.available ? "disabled" : ""}>Rent</button>
        `;
        carList.appendChild(carCard);
    });

    // Kiralama butonlarına event ekleme
    const rentButtons = document.querySelectorAll(".rent-btn");
    rentButtons.forEach(button => {
        button.addEventListener("click", (event) => {
            const carId = event.target.getAttribute("data-id");
            rentCar(carId);
        });
    });
}

async function rentCar(carId) {
    const token = localStorage.getItem("authToken");

    if (!token) {
        alert("You must be logged in to rent a car.");
        return;
    }

    try {
        const response = await fetch(`/api/orders`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ carId })
        });

        if (response.ok) {
            alert("Car successfully rented!");
            loadCars(); // Araç durumunu güncelle
        } else {
            console.error("Failed to rent car:", response.status);
            alert("Failed to rent the car. Please try again.");
        }
    } catch (error) {
        console.error("Error renting car:", error);
    }
}

document.getElementById("register-link").addEventListener("click", () => {
    document.getElementById("login").style.display = "none";
    document.getElementById("register").style.display = "block";
});

document.getElementById("login-link").addEventListener("click", () => {
    document.getElementById("register").style.display = "none";
    document.getElementById("login").style.display = "block";
});

document.getElementById("register-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const username = document.getElementById("register-username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("register-password").value;

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        if (response.ok) {
            alert("Registration successful! You can now login.");
            document.getElementById("register").style.display = "none";
            document.getElementById("login").style.display = "block";
        } else {
            document.getElementById("register-error").textContent = "Registration failed. Please try again.";
        }
    } catch (error) {
        document.getElementById("register-error").textContent = "An error occurred during registration.";
    }
});

document.getElementById("add-car-btn").addEventListener("click", () => {
    const adminContent = document.getElementById("admin-content");
    adminContent.innerHTML = `
        <h3>Add New Car</h3>
        <form id="add-car-form" class="form">
            <label for="car-brand">Brand</label>
            <input type="text" id="car-brand" name="brand" required>
            <label for="car-model">Model</label>
            <input type="text" id="car-model" name="model" required>
            <label for="car-year">Year</label>
            <input type="number" id="car-year" name="year" required>
            <label for="car-price">Price per Day</label>
            <input type="number" id="car-price" name="price" required>
            <button type="submit" class="btn">Add Car</button>
        </form>
    `;

    document.getElementById("add-car-form").addEventListener("submit", async (event) => {
        event.preventDefault();
        const brand = document.getElementById("car-brand").value;
        const model = document.getElementById("car-model").value;
        const year = document.getElementById("car-year").value;
        const price = document.getElementById("car-price").value;

        try {
            const response = await fetch('/api/admin/cars', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem("authToken")}`
                },
                body: JSON.stringify({ brand, model, year, price })
            });

            if (response.ok) {
                alert("Car added successfully!");
            } else {
                alert("Failed to add car. Please try again.");
            }
        } catch (error) {
            console.error("Error adding car:", error);
        }
    });
});

