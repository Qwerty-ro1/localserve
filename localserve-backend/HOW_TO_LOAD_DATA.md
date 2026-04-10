# 📊 How to Load Test Data into Database

## Location of SQL Script
```
D:\STUDY\Projects\localserve\localserve-backend\insert-test-data.sql
```

---

## Method 1: Using pgAdmin (Easiest - GUI)

### Step 1: Open pgAdmin
1. Go to **http://localhost:5050** (default pgAdmin port)
2. Login with your pgAdmin credentials
3. Navigate to **Servers → PostgreSQL → Databases → localserve_db**

### Step 2: Execute the Script
1. Right-click on **localserve_db** database
2. Click **Query Tool**
3. A query editor window opens
4. Open the SQL file: **Ctrl+O** or **File → Open**
5. Select `insert-test-data.sql` from `D:\STUDY\Projects\localserve\localserve-backend\`
6. Click **Execute** or press **F5**
7. You should see: "Query returned successfully"

---

## Method 2: Using psql Command Line (Windows PowerShell)

### Step 1: Navigate to Project
```powershell
cd D:\STUDY\Projects\localserve\localserve-backend
```

### Step 2: Execute SQL Script
```powershell
psql -U postgres -d localserve_db -f insert-test-data.sql
```

**When prompted, enter your PostgreSQL password** (from `application.properties` - should be `qwert@5247`)

### Step 3: Verify Success
You should see messages like:
```
INSERT 0 10
INSERT 0 10
INSERT 0 15
INSERT 0 19
```

---

## Method 3: Using DBeaver (Alternative GUI)

### Step 1: Open DBeaver
1. Open DBeaver
2. Connect to your `localserve_db` database

### Step 2: Execute Script
1. Click **File → Open File** 
2. Select `insert-test-data.sql`
3. Click the **Execute** button (green play icon)

---

## What Gets Created?

### Users (Regular)
```
5 users who can book services
- john@gmail.com
- jane@gmail.com
- mike@gmail.com
- sarah@gmail.com
- robert@gmail.com
```

### Users (Providers)
```
10 provider accounts:
- john.plumber@email.com
- sarah.electric@email.com
- mike.carpenter@email.com
- lisa.painter@email.com
- david.hvac@email.com
- emma.cleaning@email.com
- tom.landscape@email.com
- alex.locksmith@email.com
- chris.handyman@email.com
- nina.inspector@email.com
```

### Service Categories
```
10 categories:
- Plumbing
- Electrical
- Carpentry
- House Cleaning
- Painting
- HVAC
- Landscaping
- Locksmith
- General Repair
- Home Inspection
```

### Data Summary
- **5** Regular Users
- **10** Provider Users
- **10** Providers (linked to users)
- **10** Service Categories
- **15** Service Offerings (providers offering services)
- **19** Bookings (test bookings in various states)

---

## Test Login Credentials

### Login as Regular User:
```
Email: john@gmail.com
Password: password123
```

### Login as Provider:
```
Email: john.plumber@email.com
Password: password123
```

---

## Verify Data in Database

### Using psql:
```powershell
psql -U postgres -d localserve_db

# Then run:
SELECT COUNT(*) FROM users;              -- Should show 15
SELECT COUNT(*) FROM providers;          -- Should show 10
SELECT COUNT(*) FROM master_service_categories;  -- Should show 10
SELECT COUNT(*) FROM service_offerings;  -- Should show 15
SELECT COUNT(*) FROM bookings;           -- Should show 19

# View all providers
SELECT * FROM providers;

# View all users
SELECT * FROM users;

# View bookings
SELECT * FROM bookings;
```

---

## If You Need to Reload Data

### Option A: Keep existing data
Just run the script again (it will add more data)

### Option B: Clean and reload
Uncomment these lines at the top of `insert-test-data.sql`:
```sql
DELETE FROM bookings;
DELETE FROM service_offerings;
DELETE FROM providers;
DELETE FROM users;
DELETE FROM master_service_categories;
ALTER TABLE users AUTO_INCREMENT = 1;
...etc...
```

Then run the script to have fresh data.

---

## Useful SQL Queries for Testing

### View all providers with distance from New York
```sql
SELECT p.id, u.name, p.business_name, p.rating, 
       ROUND(SQRT(POWER(p.latitude - 40.7128, 2) + POWER(p.longitude - (-74.0060), 2)) * 111.2, 2) as distance_km
FROM providers p
JOIN users u ON p.user_id = u.id
ORDER BY distance_km;
```

### View all bookings with provider and user details
```sql
SELECT b.id, u.name as user, p.business_name as provider, 
       msc.name as service, b.status, b.booking_time
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN providers p ON b.provider_id = p.id
JOIN service_offerings so ON b.service_offering_id = so.id
JOIN master_service_categories msc ON so.service_category_id = msc.id
ORDER BY b.created_at DESC;
```

### Count bookings by status
```sql
SELECT status, COUNT(*) FROM bookings GROUP BY status;
```

---

## Next Steps

1. ✅ Execute the SQL script
2. ✅ Verify data in database
3. ✅ Test login with credentials
4. ✅ Visit `/providers` page to see the provider list
5. ✅ Test pagination, search, and sorting
6. ✅ Continue to Session 3 (UI Polish + Logout)

---

**All set! Your database is now populated with comprehensive test data.** 🎉

