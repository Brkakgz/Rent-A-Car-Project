import { apiFetch } from './common.js';

document.addEventListener("DOMContentLoaded", async () => {
    const cars = await apiFetch('/api/cars', 'GET');
    displayCars(cars);

    // Araçları kategoriye göre filtreleme
    const categories = [...new Set(cars.map(car => car.brand))];
    const categoryContainer = document.getElementById("car-categories");
    categories.forEach(category => {
        const button = document.createElement("button");
        button.textContent = category;
        button.onclick = () => displayCars(cars.filter(car => car.brand === category));
        categoryContainer.appendChild(button);
    });
});

function displayCars(cars) {
    const carList = document.getElementById("car-list");
    carList.innerHTML = '';
    cars.forEach(car => {
        const carDiv = document.createElement("div");
        carDiv.innerHTML = `
            <h3>${car.brand} ${car.model}</h3>
            <p>${car.year}</p>
            <button class="rent-btn" data-id="${car.id}" ${car.available ? "" : "disabled"}>
                ${car.available ? "Rent" : "Unavailable"}
            </button>
        `;
        carList.appendChild(carDiv);
    });

    // Rent butonları için event listener ekle
    const rentButtons = document.querySelectorAll(".rent-btn");
    rentButtons.forEach(button => {
        button.addEventListener("click", async (event) => {
            const carId = event.target.getAttribute("data-id");
            try {
                const response = await apiFetch(`/api/rent/${carId}`, 'POST');
                alert("Car rented successfully!");
                // Kiralandıktan sonra listeyi güncelle
                const updatedCars = await apiFetch('/api/cars', 'GET');
                displayCars(updatedCars);
            } catch (error) {
                alert("Failed to rent the car. Please try again.");
                console.error("Error renting car:", error);
            }
        });
    });
}
