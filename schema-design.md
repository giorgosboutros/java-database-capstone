schema-design.md
MySQL Database Design
Table: patients
id: INT, Primary Key, AUTO_INCREMENT
name: VARCHAR(100), NOT NULL
email: VARCHAR(100), UNIQUE, NOT NULL
phone: VARCHAR(20), NOT NULL
date_of_birth: DATE
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
Table: doctors
id: INT, Primary Key, AUTO_INCREMENT
name: VARCHAR(100), NOT NULL
specialization: VARCHAR(100), NOT NULL
email: VARCHAR(100), UNIQUE, NOT NULL
phone: VARCHAR(20), NOT NULL
available_from: TIME
available_to: TIME
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
Table: appointments
id: INT, Primary Key, AUTO_INCREMENT
doctor_id: INT, Foreign Key → doctors(id), NOT NULL
patient_id: INT, Foreign Key → patients(id), NOT NULL
appointment_time: DATETIME, NOT NULL
status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled), DEFAULT 0
notes: TEXT
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
Table: admin
id: INT, Primary Key, AUTO_INCREMENT
username: VARCHAR(50), UNIQUE, NOT NULL
password_hash: VARCHAR(255), NOT NULL
role: ENUM('SUPERADMIN', 'STAFF'), DEFAULT 'STAFF'
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
Table: payments (προαιρετικό)
id: INT, Primary Key, AUTO_INCREMENT
patient_id: INT, Foreign Key → patients(id), NOT NULL
appointment_id: INT, Foreign Key → appointments(id), NOT NULL
amount: DECIMAL(10,2), NOT NULL
status: ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING'
paid_at: DATETIME NULL
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

{
"_id": { "$oid": "64abc123456789" },
"appointmentId": 51,
"patientId": 12,
"doctorId": 4,
"medications": [
{
"name": "Amoxicillin",
"dosage": "500mg",
"instructions": "Take 1 tablet every 8 hours for 7 days"
},
{
"name": "Paracetamol",
"dosage": "500mg",
"instructions": "Take 1 tablet every 6 hours if fever persists"
}
],
"doctorNotes": "Patient has mild infection, monitor symptoms.",
"created_at": { "$date": "2025-08-22T10:00:00Z" }
}
