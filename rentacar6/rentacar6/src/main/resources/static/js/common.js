// API Fetch Fonksiyonu
export async function apiFetch(url, method, body = null) {
    const token = localStorage.getItem('authToken');
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        }
    };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(url, options);
    if (!response.ok) {
        if (response.status === 401) {
            alert("Unauthorized! Please login again.");
            window.location.href = "/login.html";
        } else {
            const errorMessage = await response.text();
            throw new Error(errorMessage || "API Error");
        }
    }
    return response.json();
}

// Genel Mesaj Gösterme Fonksiyonu
export function showMessage(type, text) {
    const messageBox = document.getElementById("message-box");
    messageBox.textContent = text;
    messageBox.className = `message ${type}`;
    messageBox.style.display = "block";
    setTimeout(() => {
        messageBox.style.display = "none";
    }, 3000);
}

