// API Fetch Fonksiyonu
export async function apiFetch(url, method, body = null) {
    const token = localStorage.getItem('authToken');
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
        }
    };

    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    // Body içeriğini JSON'a dönüştür
    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(url, options);

        if (!response.ok) {
            if (response.status === 401) {
                console.warn("Unauthorized access. Returning as guest.");
                return null; // Unauthorized erişimlerde null döndür
            }

            const errorMessage = await response.text();
            throw new Error(errorMessage || "API Error");
        }

        return await response.json();
    } catch (error) {
        console.error("API Fetch Error:", error.message);
        showMessage("error", `API Error: ${error.message}`); // Hata mesajını göster
        throw error;
    }
}


// Genel Mesaj Gösterme Fonksiyonu
export function showMessage(type, text, duration = 3000) {
    const messageBox = document.getElementById("message-box");
    if (!messageBox) {
        console.error("Message box element not found in DOM.");
        return;
    }

    messageBox.textContent = text;
    messageBox.className = `message ${type}`;
    messageBox.style.display = "block";

    setTimeout(() => {
        messageBox.style.display = "none";
    }, duration); // Mesaj belirtilen süre sonra kaybolur
}

