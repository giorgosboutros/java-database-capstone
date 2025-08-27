// doctorDashboard.js
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

let appointmentBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0]; // today
let token = localStorage.getItem("token");
let patientName = null;

document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("searchBar");
    if (searchInput) {
        searchInput.addEventListener("input", (e) => {
            patientName = e.target.value || null;
            loadAppointments();
        });
    }

    const todayBtn = document.getElementById("todayButton");
    if (todayBtn) todayBtn.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split("T")[0];
        document.getElementById("datePicker").value = selectedDate;
        loadAppointments();
    });

    const datePicker = document.getElementById("datePicker");
    if (datePicker) datePicker.addEventListener("change", (e) => {
        selectedDate = e.target.value;
        loadAppointments();
    });

    loadAppointments();
});

async function loadAppointments() {
    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);
        appointmentBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            appointmentBody.innerHTML = `<tr><td colspan="6">No Appointments found for today</td></tr>`;
            return;
        }

        appointments.forEach(app => {
            const row = createPatientRow(app);
            appointmentBody.appendChild(row);
        });
    } catch (error) {
        console.error(error);
        appointmentBody.innerHTML = `<tr><td colspan="6">Error loading appointments</td></tr>`;
    }
}
