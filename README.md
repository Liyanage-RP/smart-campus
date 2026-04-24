# Smart Campus Operations Hub - How to Work

Welcome to the **Smart Campus Operations Hub**! This guide will walk you through the core modules: **Booking Management** and **Maintenance Ticketing**.

## 🚀 Getting Started

1.  **Start Backend**: Open PowerShell in the root directory and run `./run-backend.ps1`.
2.  **Start Frontend**: Open another terminal in the `frontend` folder and run `npm run dev`.
3.  **Access App**: Open your browser at [http://localhost:5173](http://localhost:5173).

---

## 📅 1. Booking Management (Member 2)

This module allows users to reserve campus facilities and admins to manage those requests.

### **Step 1: Submit a Booking**
- Navigate to the **"New Booking"** tab.
- Select a facility (e.g., "Conference Room A").
- Pick a **Date**, **Start Time**, and **End Time**.
- Enter the **Purpose** (e.g., "AI Workshop") and **Expected Attendees**.
- Click **Book Facility**.

### **Step 2: Track Your Request**
- Go to the **"My Bookings"** tab.
- You will see your booking with a `PENDING` status.
- Once an admin approves it, the status will turn green (`APPROVED`).

### **Step 3: Admin Approval (Workflow)**
- Navigate to the **"Admin Bookings"** tab.
- You will see all pending campus bookings.
- Click **Approve** to confirm or **Reject** (which prompts for a reason).

---

## 🚨 2. Maintenance & Incident Ticketing (Member 3)

This module handles campus repairs and facility issues.

### **Step 1: Report an Incident**
- Navigate to the **"Report Incident"** tab.
- Provide a **Title** (e.g., "AC Leaking"), **Location**, and **Priority** (Critical/High/Medium/Low).
- Describe the problem in detail.
- Click **Submit Ticket**.

### **Step 2: Admin/Technician Management**
- Navigate to the **"Admin Tickets"** tab.
- Click **"View Details"** on any ticket.
- **Assign Technician**: Select a staff member to handle the fix.
- **Add Comments**: Admins and users can chat inside the ticket to provide updates.
- **Update Status**: Move the ticket from `OPEN` to `IN_PROGRESS` and finally `RESOLVED`.

---

## 🐳 3. Advanced Features

- **Docker**: You can run the entire system using `docker-compose up`.
- **CI/CD**: Every push to GitHub is automatically validated via the included GitHub Actions workflow.
- **Tests**: Run `mvn test` in the backend to see the automated unit tests in action.

---

> [!TIP]
> Use the **Home** page to quickly jump between these modules. The interactive cards provide a direct path to the most common tasks.
