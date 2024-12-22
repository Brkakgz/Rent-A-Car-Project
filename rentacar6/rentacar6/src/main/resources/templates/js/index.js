// Kiralama modalında tarih seçimi ve fiyat hesaplama fonksiyonu
// Bu kod index.js dosyasına entegre edilecek

// Modal için global değişkenler
let selectedCar = null; // Seçilen araba detayları

// Modal içeriğini yükleme fonksiyonu
function showRentModal(car) {
    selectedCar = car; // Seçilen araba detaylarını kaydet

    // Modal içeriğini doldur
    document.getElementById("modal-car-title").textContent = `${car.brand} ${car.model}`;
    document.getElementById("modal-car-description").textContent = `Year: ${car.year}, Color: ${car.color}`;
    document.getElementById("modal-car-price").textContent = `$${car.dailyPrice}`;

    // Modal'ı göster
    const modal = document.getElementById("car-details-modal");
    modal.style.display = "flex";

    // Modal kapatma butonu
    modal.querySelector(".close-btn").addEventListener("click", () => {
        modal.style.display = "none";
    });
}

// Toplam fiyat hesaplama fonksiyonu
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

// Kiralama formunu gönderme işlemi
async function submitRentalForm() {
    const rentDate = document.getElementById("rent-date").value;
    const returnDate = document.getElementById("return-date").value;

    try {
        await apiFetch('/api/orders', 'POST', {
            carId: selectedCar.id,
            rentDate,
            returnDate
        });

        alert("Order placed successfully!");
        location.reload();
    } catch (error) {
        console.error("Error placing order:", error);
        alert("Failed to place order. Please try again.");
    }
}

// Modal içinde tarih seçimi değiştikçe toplam fiyatı hesapla
function attachDateChangeListeners() {
    document.getElementById("rent-date").addEventListener("change", calculateTotalPrice);
    document.getElementById("return-date").addEventListener("change", calculateTotalPrice);
}

// Modal kiralama formu gönderim işlemi
function attachFormSubmissionListener() {
    document.getElementById("modal-rent-button").addEventListener("click", submitRentalForm);
}

// Fonksiyonları başlat
attachDateChangeListeners();
attachFormSubmissionListener();
