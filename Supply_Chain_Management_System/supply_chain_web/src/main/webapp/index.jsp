<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Supply Chain Management System</title>
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f4f7fb;
            color: #1f2937;
        }

        .container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 20px;
        }

        h1 {
            margin-bottom: 10px;
        }

        .status-bar {
            background: #111827;
            color: white;
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 24px;
            font-weight: bold;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }

        .card {
            background: white;
            border: 1px solid #dfe7f1;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 2px 8px rgba(15, 23, 42, 0.05);
        }

        h2 {
            margin-top: 0;
            font-size: 1.2rem;
        }

        form {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        input, select, textarea, button {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font-size: 14px;
        }

        textarea {
            resize: vertical;
            min-height: 120px;
        }

        .checkbox-row {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin: 8px 0;
        }

        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 6px;
            background: #f8fafc;
            border: 1px solid #dbe3ef;
            border-radius: 8px;
            padding: 6px 10px;
        }

        button {
            background: #2563eb;
            color: white;
            font-weight: bold;
            cursor: pointer;
            border: none;
        }

        button.secondary {
            background: #475569;
        }

        button.warning {
            background: #dc2626;
        }

        .output {
            background: #0f172a;
            color: #dbeafe;
            border-radius: 12px;
            padding: 16px;
            min-height: 200px;
            white-space: pre-wrap;
            font-family: Consolas, monospace;
            font-size: 13px;
            overflow: auto;
        }

        .small {
            font-size: 12px;
            color: #475569;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Supply Chain Management System</h1>
    <div id="statusBar" class="status-bar">Not logged in</div>

    <div class="grid">
        <section class="card">
            <h2>Register User</h2>
            <form id="registerForm">
                <input type="text" id="registerUsername" placeholder="Username" required>
                <input type="password" id="registerPassword" placeholder="Password" required>
                <div class="checkbox-row">
                    <label class="checkbox-item"><input type="checkbox" value="ADMIN"> ADMIN</label>
                    <label class="checkbox-item"><input type="checkbox" value="WAREHOUSE_MANAGER">
                        WAREHOUSE_MANAGER</label>
                    <label class="checkbox-item"><input type="checkbox" value="VENDOR"> VENDOR</label>
                    <label class="checkbox-item"><input type="checkbox" value="USER"> USER</label>
                </div>
                <button type="submit">Register</button>
            </form>
        </section>

        <section class="card">
            <h2>Login</h2>
            <form id="loginForm">
                <input type="text" id="loginUsername" placeholder="Username" required>
                <input type="password" id="loginPassword" placeholder="Password" required>
                <button type="submit">Login</button>
                <button type="button" class="secondary" id="logoutBtn">Logout</button>
            </form>
        </section>
    </div>

    <div class="grid">
        <section class="card">
            <h2>Vendor Management</h2>
            <form id="createVendorForm">
                <input type="text" id="vendorName" placeholder="Vendor name" required>
                <input type="email" id="vendorEmail" placeholder="Vendor email" required>
                <button type="submit">Create Vendor</button>
            </form>
            <form id="approveVendorForm" style="margin-top:18px;">
                <input type="email" id="approveVendorEmail" placeholder="Vendor email to approve" required>
                <button type="submit" class="secondary">Approve Vendor</button>
            </form>
            <button type="button" id="getVendorsBtn" class="secondary" style="margin-top:18px;">Get All Vendors</button>
        </section>

        <section class="card">
            <h2>Inventory Management</h2>
            <form id="createInventoryForm">
                <input type="text" id="inventorySku" placeholder="SKU" required>
                <input type="text" id="inventoryName" placeholder="Item name" required>
                <input type="number" id="inventoryQty" placeholder="Quantity" required>
                <input type="number" id="inventoryThreshold" placeholder="Reorder threshold" required>
                <button type="submit">Create Inventory Item</button>
            </form>
            <form id="updateStockForm" style="margin-top:18px;">
                <input type="text" id="stockSku" placeholder="SKU" required>
                <input type="number" id="stockQty" placeholder="Stock change (+/-)" required>
                <button type="submit" class="secondary">Update Stock</button>
            </form>
            <div style="display:flex; gap:10px; margin-top:18px; flex-wrap:wrap;">
                <button type="button" id="getInventoryBtn" class="secondary">Get All Inventory</button>
                <button type="button" id="getLowStockBtn" class="secondary">Get Low Stock</button>
            </div>
        </section>
    </div>

    <div class="grid">
        <section class="card">
            <h2>Order Management</h2>
            <form id="placeOrderForm">
                <input type="email" id="orderVendorEmail" placeholder="Vendor email" required>
                <textarea id="orderItemsJson"
                          placeholder='[ { "sku": "SKU123", "qty": 5 }, { "sku": "SKU456", "qty": 2 } ]'
                          required></textarea>
                <button type="submit">Place Order</button>
            </form>
            <button type="button" id="getOrdersBtn" class="secondary" style="margin-top:18px;">Get All Orders</button>
        </section>

        <section class="card">
            <h2>API Response</h2>
            <div id="apiOutput" class="output">No request made yet.</div>
        </section>
    </div>
</div>

<script>
    const API_BASE = '<%= request.getContextPath() %>/api';
    const tokenKey = 'scm-access-token';
    const userKey = 'scm-user';
    const rolesKey = 'scm-roles';

    const state = {
        token: localStorage.getItem(tokenKey) || '',
        user: localStorage.getItem(userKey) || '',
        roles: JSON.parse(localStorage.getItem(rolesKey) || '[]')
    };

    function updateStatus() {
        const status = document.getElementById('statusBar');
        if (state.token) {
            status.textContent = 'Logged in as ' + state.user + ' | Roles: ' + state.roles.join(', ');
        } else {
            status.textContent = 'Not logged in';
        }
    }

    function showOutput(message, isError = false) {
        const output = document.getElementById('apiOutput');
        output.style.color = isError ? '#fca5a5' : '#dbeafe';
        output.textContent = typeof message === 'string' ? message : JSON.stringify(message, null, 2);
    }

    function authHeaders(jsonPayload = true) {
        const headers = {};
        if (jsonPayload) headers['Content-Type'] = 'application/json';
        if (state.token) {
            headers['Authorization'] = 'Bearer ' + state.token;
        }
        return headers;
    }

    function handleApiResponse(response) {
        return response.text().then((text) => {
            if (!text) {
                return {};
            }
            try {
                const data = JSON.parse(text);
                if (!response.ok) {
                    const message = data.message || data.error || 'Request failed';
                    throw new Error(message);
                }
                return data;
            } catch (error) {
                if (!response.ok) {
                    throw new Error(text || 'Request failed');
                }
                return text;
            }
        });
    }

    function logout() {
        state.token = '';
        state.user = '';
        state.roles = [];
        localStorage.removeItem(tokenKey);
        localStorage.removeItem(userKey);
        localStorage.removeItem(rolesKey);
        updateStatus();
        showOutput('Logged out successfully.');
    }

    function getSelectedRoles() {
        const checked = document.querySelectorAll('input[type="checkbox"]:checked');
        return Array.from(checked).map(el => el.value);
    }

    document.getElementById('registerForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const payload = {
            username: document.getElementById('registerUsername').value.trim(),
            password: document.getElementById('registerPassword').value,
            roles: getSelectedRoles()
        };

        fetch(API_BASE + '/auth/register', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify(payload)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('loginForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const payload = {
            username: document.getElementById('loginUsername').value.trim(),
            password: document.getElementById('loginPassword').value
        };

        fetch(API_BASE + '/auth/login', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify(payload)
        })
            .then(handleApiResponse)
            .then((data) => {
                if (data && data.accessToken) {
                    state.token = data.accessToken;
                    state.user = data.username;
                    state.roles = data.roles || [];
                    localStorage.setItem(tokenKey, state.token);
                    localStorage.setItem(userKey, state.user);
                    localStorage.setItem(rolesKey, JSON.stringify(state.roles));
                    updateStatus();
                }
                showOutput(data);
            })
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('logoutBtn').addEventListener('click', logout);

    document.getElementById('createVendorForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const payload = {
            name: document.getElementById('vendorName').value.trim(),
            email: document.getElementById('vendorEmail').value.trim()
        };

        fetch(API_BASE + '/vendor', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify(payload)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('approveVendorForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const payload = {
            email: document.getElementById('approveVendorEmail').value.trim()
        };

        fetch(API_BASE + '/vendor', {
            method: 'PATCH',
            headers: authHeaders(),
            body: JSON.stringify(payload)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('getVendorsBtn').addEventListener('click', function () {
        fetch(API_BASE + '/vendor', {
            method: 'GET',
            headers: authHeaders(false)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('createInventoryForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const payload = {
            sku: document.getElementById('inventorySku').value.trim(),
            name: document.getElementById('inventoryName').value.trim(),
            qtyOnHand: Number(document.getElementById('inventoryQty').value),
            reorderThreshold: Number(document.getElementById('inventoryThreshold').value)
        };

        fetch(API_BASE + '/inventory', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify(payload)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('updateStockForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const payload = {
            qtyOnHand: Number(document.getElementById('stockQty').value)
        };

        fetch(API_BASE + '/inventory/stock/' + encodeURIComponent(document.getElementById('stockSku').value.trim()), {
            method: 'PATCH',
            headers: authHeaders(),
            body: JSON.stringify(payload)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('getInventoryBtn').addEventListener('click', function () {
        fetch(API_BASE + '/inventory', {
            method: 'GET',
            headers: authHeaders(false)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('getLowStockBtn').addEventListener('click', function () {
        fetch(API_BASE + '/inventory/low_stock', {
            method: 'GET',
            headers: authHeaders(false)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('placeOrderForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const vendorEmail = document.getElementById('orderVendorEmail').value.trim();
        let orderItems;

        try {
            orderItems = JSON.parse(document.getElementById('orderItemsJson').value);
        } catch (error) {
            showOutput('Order items must be valid JSON, e.g. [{"sku":"SKU1","qty":5}]', true);
            return;
        }

        fetch(API_BASE + '/order/' + encodeURIComponent(vendorEmail), {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({orderRequestDTOs: orderItems})
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    document.getElementById('getOrdersBtn').addEventListener('click', function () {
        fetch(API_BASE + '/order', {
            method: 'GET',
            headers: authHeaders(false)
        })
            .then(handleApiResponse)
            .then((data) => showOutput(data))
            .catch((error) => showOutput(error.message, true));
    });

    updateStatus();
</script>
</body>
</html>