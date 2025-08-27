// adminDashboard.js
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

document.addEventListener("DOMContentLoaded", () => {
    const addBtn = document.getElementById('addDocBtn');
    if (addBtn) addBtn.addEventListener('click', () => openModal('addDoctor'));

    loadDoctorCards();

    // Filter/Search listeners
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
    if (doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }
    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
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

// Adding a new doctor
window.adminAddDoctor = async function () {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Admin not authenticated!");
        return;
    }
    const name = document.getElementById("docName").value;
    const email = document.getElementById("docEmail").value;
    const specialty = document.getElementById("docSpecialty").value;
    const password = document.getElementById("docPassword").value;
    const mobile = document.getElementById("docMobile").value;
    const availability = Array.from(document.querySelectorAll("input[name='availability']:checked")).map(el => el.value);

    const doctor = { name, email, specialty, password, mobile, availability };

    const result = await saveDoctor(doctor, token);
    if (result.success) {
        alert("Doctor added successfully!");
        loadDoctorCards();
        // Optionally close modal
    } else {
        alert("Error adding doctor: " + result.message);
    }
};
