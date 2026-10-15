# Zenith '26 — College Fest Management System

A full-stack, responsive college fest event portal and coordinator admin dashboard built with Python (FastAPI), SQLite, HTML, Tailwind CSS, and vanilla JavaScript.

---

## 🚀 Tech Stack

- **Web Frontend:** HTML5, Tailwind CSS (via Play CDN), Vanilla JavaScript (ES6+)
- **Mobile Frontend (Native Android):** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Retrofit 2, OkHttp 3, Coroutines, StateFlow
- **Backend:** Python 3.10+, FastAPI, Uvicorn, Pydantic v2
- **Database:** SQLite3 (`fest.db`) with relational integrity (`events` and `registrations`)
- **Data Ingestion:** `openpyxl` reading spreadsheet `data/events_seed.xlsx`
- **Testing & API Contracts:** Postman (`zenith26-api.postman_collection.json`)

---

## 📁 Project Structure

```text
Mobile app/
├── backend/
│   ├── database.py       # SQLite connection helper & schema creation
│   ├── models.py         # Pydantic schemas (EventOut, RegistrationIn, StatsOut, etc.)
│   ├── seed_data.py      # Spreadsheet loader (ingests events_seed.xlsx into SQLite)
│   └── main.py           # FastAPI routes, server validation, static file mounting
├── frontend/
│   ├── index.html        # Student event discovery portal & registration modal
│   ├── admin.html        # Coordinator operations dashboard & stats roster
│   └── js/
│       ├── app.js        # Student portal client logic, filters, modal handling
│       └── admin.js      # Admin dashboard metrics, table sorting, cancellation
├── android-app/          # Native Android Application (Kotlin + Jetpack Compose)
│   ├── app/
│   │   ├── build.gradle.kts
│   │   ├── proguard-rules.pro
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── res/
│   │       └── java/com/zenith26/fest/
│   │           ├── MainActivity.kt
│   │           ├── data/
│   │           │   ├── api/          # Retrofit ZenithApiService & NetworkClient
│   │           │   ├── model/        # Kotlin data models matching FastAPI schemas
│   │           │   └── repository/   # Coroutine-safe ZenithRepository
│   │           └── ui/
│   │               ├── admin/        # AdminDashboardScreen & ViewModel
│   │               ├── navigation/   # Screen navigation routes
│   │               ├── portal/       # StudentPortalScreen & ViewModel
│   │               └── theme/        # Exact Tailwind brand color palette & Typography
│   ├── gradle/wrapper/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── gradle.properties
├── data/
│   └── events_seed.xlsx  # 14 Seed events spreadsheet
├── zenith26-api.postman_collection.json # Exported API test suite
├── requirements.txt      # Python dependencies
└── README.md
```

---

## ⚡ How to Run the Project

### 1. Set Up Virtual Environment & Dependencies
```powershell
# Create virtual environment
python -m venv venv

# Activate virtual environment
# On Windows:
.\venv\Scripts\activate
# On macOS/Linux:
# source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

### 2. Seed Database (Optional - runs automatically on startup)
```powershell
python -m backend.seed_data
```

### 3. Start the FastAPI Server
```powershell
uvicorn backend.main:app --reload --port 8000
```

### 4. Run the Web Application (Browser)
- **Student Event Portal:** [http://localhost:8000/](http://localhost:8000/)
- **Admin Coordinator Dashboard:** [http://localhost:8000/admin.html](http://localhost:8000/admin.html)
- **Interactive Swagger API Docs:** [http://localhost:8000/docs](http://localhost:8000/docs)

### 5. Run the Native Android App (Kotlin + Compose)
1. Open **Android Studio**.
2. Select **Open** and choose the `android-app` folder inside this directory.
3. Allow Gradle to sync dependencies (Jetpack Compose, Retrofit, OkHttp, Coroutines).
4. Run the app on an Android Emulator or connected physical device:
   - For **Android Emulator**: The app is pre-configured to use `http://10.0.2.2:8000/`, which directly routes to the host computer running FastAPI.
   - For **Physical Android Device**: Ensure phone and computer are on the same Wi-Fi, and configure the base URL to your computer's local IP address (e.g., `http://192.168.x.x:8000/`) via `NetworkClient.setCustomBaseUrl(...)`.
5. Ensure the FastAPI backend is running on port 8000 before launching the app.

---

## 📡 REST API Contract

| Method | Path | Description | Key Status Codes |
|---|---|---|---|
| `GET` | `/api/events` | List all events (supports `?category=` and `?search=`). `available_seats` calculated dynamically. | `200 OK` |
| `GET` | `/api/events/{id}` | Single event detail view. | `200 OK`, `404 Not Found` |
| `POST` | `/api/events/{id}/register` | Register student. Validates required fields, email format, capacity, and uniqueness. | `201 Created`, `400 Bad Request`, `409 Conflict`, `404 Not Found` |
| `GET` | `/api/registrations` | List all registrations (supports `?event_id=`), with event title included. | `200 OK` |
| `GET` | `/api/stats` | Real-time aggregate fest metrics & category counts. | `200 OK` |
| `POST` | `/api/events` | *Stretch Goal:* Admin adds a new event. | `201 Created`, `400 Bad Request` |
| `DELETE` | `/api/registrations/{id}` | *Stretch Goal:* Admin cancels registration and restores available seat. | `200 OK`, `404 Not Found` |
