// Imports
import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// API Endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Event listeners για κουμπιά στο index page
window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    const doctorBtn = document.getElementById('doctorLogin');

    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};

// Admin login handler
export async function adminLoginHandler() {
    try {
        const username = document.getElementById('adminUsername').value;
        const password = document.getElementById('adminPassword').value;

        const admin = { username, password };

        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            selectRole('admin'); // helper function from render.js
            alert("Admin login successful!");
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        alert("Error: " + error.message);
    }
}

// Doctor login handler
export async function doctorLoginHandler() {
    try {
        const email = document.getElementById('doctorEmail').value;
        const password = document.getElementById('doctorPassword').value;

        const doctor = { email, password };

        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            selectRole('doctor'); // helper function from render.js
            alert("Doctor login successful!");
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        alert("Error: " + error.message);
    }
}

// Helper function to store user role in localStorage
function selectRole(role) {
    localStorage.setItem('userRole', role);
}
