// patientDashboard.js
import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));

    const searchInput = document.getElementById("searchBar");
    const timeFilter = document.getElementById("filterTime");
    const specialtyFilter = document.getElementById("filterSpecialty");

    if (searchInput) searchInput.addEventListener("input", filterDoctorsOnChange);
    if (timeFilter) timeFilter.addEventListener("change", filterDoctorsOnChange);
    if (specialtyFilter) specialtyFilter.addEventListener("change", filterDoctorsOnChange);
});

async function loadDoctorCards() {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;
    contentDiv.innerHTML = "";
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";
    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors available</p>";
        return;
    }
    doctors.forEach(doc => {
        const card = createDoctorCard(doc);
        contentDiv.appendChild(card);
    });
}

async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";
    const contentDiv = document.getElementById("content");
    const doctors = await filterDoctors(name, time, specialty);
    renderDoctorCards(doctors);
}

// Patient Signup
window.signupPatient = async function () {
    const name = document.getElementById("signupName").value;
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;
    const phone = document.getElementById("signupPhone").value;
    const address = document.getElementById("signupAddress").value;

    const result = await patientSignup({ name, email, password, phone, address });
    if (result.success) {
        alert("Signup successful!");
        // optionally close modal
        loadDoctorCards();
    } else {
        alert("Signup failed: " + result.message);
    }
};

// Patient Login
window.loginPatient = async function () {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await patientLogin({ email, password });
    if (response?.ok) {
        const data = await response.json();
        localStorage.setItem("token", data.token);
        localStorage.setItem("userRole", "loggedPatient");
        window.location.href = "loggedPatientDashboard.html";
    } else {
        alert("Login failed!");
    }
};
