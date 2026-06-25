export default `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spilt - Share Bills & Track Spending</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;800&display=swap" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/tesseract.js@5/dist/tesseract.min.js"></script>
    <style>
        :root {
            --primary: #6366f1;
            --primary-hover: #4f46e5;
            --accent: #a855f7;
            --background: #0b0f19;
            --card-bg: rgba(255, 255, 255, 0.04);
            --card-border: rgba(255, 255, 255, 0.08);
            --text: #f3f4f6;
            --text-muted: #9ca3af;
            --success: #10b981;
            --error: #ef4444;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Outfit', sans-serif;
            -webkit-font-smoothing: antialiased;
        }

        body {
            background-color: var(--background);
            color: var(--text);
            min-height: 100vh;
            overflow-x: hidden;
            position: relative;
        }

        /* Ambient Blobs */
        .ambient-blobs {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: -1;
            pointer-events: none;
            overflow: hidden;
        }

        .blob {
            position: absolute;
            border-radius: 50%;
            filter: blur(140px);
            opacity: 0.25;
            animation: float 20s infinite alternate ease-in-out;
        }

        .blob-1 {
            top: -10%;
            left: -10%;
            width: 50vw;
            height: 50vw;
            background: var(--primary);
        }

        .blob-2 {
            bottom: -10%;
            right: -10%;
            width: 55vw;
            height: 55vw;
            background: var(--accent);
            animation-delay: -5s;
        }

        .blob-3 {
            top: 40%;
            left: 30%;
            width: 35vw;
            height: 35vw;
            background: #38bdf8;
            opacity: 0.15;
            animation-delay: -10s;
        }

        @keyframes float {
            0% { transform: translate(0, 0) scale(1); }
            50% { transform: translate(4% , 6%) scale(1.1); }
            100% { transform: translate(-2%, -4%) scale(0.95); }
        }

        /* Navigation */
        nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1.5rem 2rem;
            max-width: 1200px;
            margin: 0 auto;
        }

        .logo {
            font-size: 2rem;
            font-weight: 800;
            background: linear-gradient(135deg, var(--text) 30%, var(--primary) 70%, var(--accent) 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            letter-spacing: -0.5px;
        }

        .nav-links {
            display: flex;
            gap: 1rem;
        }

        .nav-btn {
            background: transparent;
            border: none;
            color: var(--text-muted);
            padding: 0.5rem 1rem;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            border-radius: 12px;
            transition: all 0.3s;
        }

        .nav-btn:hover, .nav-btn.active {
            color: var(--text);
            background: rgba(255, 255, 255, 0.05);
        }

        /* Container */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 1.5rem 3rem 1.5rem;
            display: grid;
            grid-template-columns: 1fr;
            gap: 2rem;
        }

        @media (min-width: 900px) {
            .container {
                grid-template-columns: 2fr 1fr;
            }
        }

        /* Glass Card style */
        .glass-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 24px;
            padding: 2rem;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
            transition: transform 0.3s, border-color 0.3s;
        }

        .glass-card:hover {
            border-color: rgba(255, 255, 255, 0.12);
        }

        /* Screen views */
        .screen {
            display: none;
        }

        .screen.active {
            display: block;
            animation: fadeIn 0.4s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h2 {
            font-size: 1.75rem;
            font-weight: 800;
            margin-bottom: 1.5rem;
            background: linear-gradient(135deg, #fff 0%, #cbd5e1 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        /* Inputs & Buttons */
        .input-group {
            margin-bottom: 1.25rem;
        }

        .input-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-size: 0.9rem;
            color: var(--text-muted);
            font-weight: 600;
        }

        input[type="text"], input[type="number"], select, textarea {
            width: 100%;
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid var(--card-border);
            padding: 0.8rem 1rem;
            border-radius: 12px;
            color: var(--text);
            font-size: 1rem;
            outline: none;
            transition: all 0.3s;
        }

        input[type="text"]:focus, input[type="number"]:focus, select:focus, textarea:focus {
            border-color: var(--primary);
            background: rgba(255, 255, 255, 0.06);
            box-shadow: 0 0 10px rgba(99, 102, 241, 0.2);
        }

        .btn {
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-hover) 100%);
            border: none;
            color: white;
            padding: 0.8rem 1.5rem;
            font-size: 1rem;
            font-weight: 600;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
        }

        .btn-secondary {
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid var(--card-border);
            color: var(--text);
        }

        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.12);
            box-shadow: none;
        }

        /* Roommate pills / items selector */
        .pill-container {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-top: 0.5rem;
        }

        .pill {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid var(--card-border);
            padding: 0.4rem 0.8rem;
            border-radius: 20px;
            font-size: 0.9rem;
            cursor: pointer;
            transition: all 0.2s;
            display: flex;
            align-items: center;
            gap: 0.3rem;
        }

        .pill.active {
            background: rgba(99, 102, 241, 0.2);
            border-color: var(--primary);
            color: var(--text);
        }

        /* Bill Item List Editor */
        .item-row {
            display: grid;
            grid-template-columns: 2fr 1fr 2fr auto;
            gap: 0.5rem;
            align-items: center;
            margin-bottom: 0.75rem;
        }

        .delete-btn {
            background: transparent;
            border: none;
            color: var(--error);
            font-size: 1.25rem;
            cursor: pointer;
            padding: 0.25rem;
        }

        /* Sidebar / Balance board */
        .sidebar {
            display: flex;
            flex-direction: column;
            gap: 2rem;
        }

        .balance-item {
            display: flex;
            justify-content: space-between;
            padding: 0.75rem 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        }

        .balance-item:last-child {
            border-bottom: none;
        }

        .amount {
            font-weight: 600;
        }

        .amount.positive {
            color: var(--success);
        }

        .amount.negative {
            color: var(--error);
        }

        /* OCR Dropzone */
        .dropzone {
            border: 2px dashed var(--card-border);
            padding: 2rem;
            border-radius: 16px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            background: rgba(255, 255, 255, 0.01);
            margin-bottom: 1.5rem;
        }

        .dropzone:hover {
            border-color: var(--primary);
            background: rgba(99, 102, 241, 0.02);
        }

        .dropzone p {
            color: var(--text-muted);
            margin-top: 0.5rem;
        }

        .ocr-progress {
            display: none;
            margin-top: 1rem;
            padding: 1rem;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 12px;
            font-size: 0.9rem;
        }

        .progress-bar {
            height: 6px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 3px;
            margin-top: 0.5rem;
            overflow: hidden;
        }

        .progress-fill {
            height: 100%;
            width: 0%;
            background: var(--primary);
            transition: width 0.1s;
        }

        /* Recent Activity / Bills list */
        .bill-card {
            background: rgba(255, 255, 255, 0.02);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 1rem;
            margin-bottom: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            cursor: pointer;
            transition: all 0.2s;
        }

        .bill-card:hover {
            background: rgba(255, 255, 255, 0.04);
            transform: scale(1.01);
        }

        /* SMS sync panel */
        .sms-box {
            background: rgba(0, 0, 0, 0.2);
            font-family: monospace;
            padding: 1rem;
            border-radius: 12px;
            border: 1px solid var(--card-border);
            font-size: 0.9rem;
            margin-bottom: 1rem;
        }

        /* Modal / overlay */
        .modal {
            position: fixed;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(0, 0, 0, 0.6);
            backdrop-filter: blur(8px);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 100;
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.3s;
        }

        .modal.active {
            opacity: 1;
            pointer-events: auto;
        }

        .modal-content {
            background: var(--background);
            border: 1px solid var(--card-border);
            width: 90%;
            max-width: 600px;
            border-radius: 24px;
            padding: 2rem;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
        }
    </style>
</head>
<body>
    <div class="ambient-blobs">
        <div class="blob blob-1"></div>
        <div class="blob blob-2"></div>
        <div class="blob blob-3"></div>
    </div>

    <nav>
        <div class="logo">Spilt</div>
        <div class="nav-links">
            <button class="nav-btn active" onclick="switchTab('dashboard')">Dashboard</button>
            <button class="nav-btn" onclick="switchTab('scanner')">Scan Bill</button>
            <button class="nav-btn" onclick="switchTab('homes')">Homes</button>
            <button class="nav-btn" onclick="switchTab('bank')">Bank Sync</button>
        </div>
    </nav>

    <div class="container">
        <!-- Main Panel -->
        <main>
            <!-- Dashboard Screen -->
            <section id="dashboard-screen" class="screen active">
                <div class="glass-card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                        <h2>Recent Activity</h2>
                        <button class="btn" onclick="switchTab('scanner')">+ New Bill</button>
                    </div>
                    <div id="bills-list">
                        <!-- Dynamic bill list -->
                    </div>
                </div>
            </section>

            <!-- Scanner / Creation Screen -->
            <section id="scanner-screen" class="screen">
                <div class="glass-card">
                    <h2>Add New Bill</h2>
                    <div class="dropzone" id="dropzone">
                        <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" style="color: var(--text-muted); margin: 0 auto;">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path>
                        </svg>
                        <p><strong>Upload receipt photo</strong> to run OCR scanner</p>
                        <p style="font-size: 0.8rem;">or click to select file</p>
                        <input type="file" id="file-input" style="display: none;" accept="image/*">
                    </div>

                    <div class="ocr-progress" id="ocr-progress">
                        <span id="ocr-status-text">Starting OCR scanning...</span>
                        <div class="progress-bar">
                            <div class="progress-fill" id="ocr-progress-fill"></div>
                        </div>
                    </div>

                    <div class="input-group">
                        <label for="bill-title">Bill Description</label>
                        <input type="text" id="bill-title" placeholder="e.g. Costco Groceries">
                    </div>

                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                        <div class="input-group">
                            <label for="bill-payer">Paid By</label>
                            <select id="bill-payer">
                                <!-- Dynamic roomies options -->
                            </select>
                        </div>
                        <div class="input-group">
                            <label for="bill-date">Date</label>
                            <input type="text" id="bill-date" placeholder="YYYY-MM-DD">
                        </div>
                    </div>

                    <h3 style="margin: 1.5rem 0 0.75rem 0; font-size: 1.2rem; font-weight: 600;">Items</h3>
                    <div id="bill-items-container">
                        <!-- Dynamic items layout -->
                    </div>
                    <button class="btn btn-secondary" onclick="addBlankItemRow()" style="margin-bottom: 1.5rem;">+ Add Item</button>

                    <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--card-border); padding-top: 1.5rem;">
                        <div>
                            <span style="color: var(--text-muted); font-size: 0.9rem;">Total Amount</span>
                            <div id="bill-total" style="font-size: 1.5rem; font-weight: 800; color: var(--primary);">$0.00</div>
                        </div>
                        <button class="btn" onclick="saveBill()">Save Bill</button>
                    </div>
                </div>
            </section>

            <!-- Homes Screen -->
            <section id="homes-screen" class="screen">
                <div class="glass-card">
                    <h2>Collaborative Homes</h2>
                    <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Manage your roommates and shared living spaces.</p>

                    <div class="input-group">
                        <label for="home-name">Home / Apartment Name</label>
                        <input type="text" id="home-name" value="The Grand Apt">
                    </div>

                    <div class="input-group">
                        <label>Roommates</label>
                        <div class="pill-container" id="roommates-list">
                            <!-- Dynamic roommates pills -->
                        </div>
                        <div style="display: flex; gap: 0.5rem; margin-top: 0.75rem;">
                            <input type="text" id="new-roommate-name" placeholder="e.g. Charlie" style="flex: 1;">
                            <button class="btn" onclick="addRoommate()">Add</button>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Bank Sync Screen -->
            <section id="bank-screen" class="screen">
                <div class="glass-card">
                    <h2>SMS & Bank Sync Simulator</h2>
                    <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Simulate automatic split detection from bank transaction messages.</p>

                    <div class="sms-box">
                        [Bank Notify] Txn of $85.50 at WHOLEFDS 1204. Balance details inside app.
                    </div>

                    <div class="input-group">
                        <label for="sms-input">Paste Simulated Bank SMS</label>
                        <textarea id="sms-input" rows="3" placeholder="e.g. You spent $45.20 at TRADER JOE'S on your card..."></textarea>
                    </div>

                    <button class="btn" onclick="parseSms()">Detect & Autocomplete Bill</button>
                </div>
            </section>
        </main>

        <!-- Sidebar / Balances & Stats -->
        <aside class="sidebar">
            <div class="glass-card">
                <h3>Balances</h3>
                <div id="balances-container" style="margin-top: 1rem;">
                    <!-- Roommates balances -->
                </div>
            </div>

            <div class="glass-card">
                <h3>Who owes Whom</h3>
                <div id="debts-container" style="margin-top: 1rem;">
                    <!-- Optimal debts settlement -->
                </div>
            </div>
        </aside>
    </div>

    <!-- Bill Detail Modal -->
    <div class="modal" id="bill-detail-modal">
        <div class="modal-content">
            <h2 id="modal-bill-title">Bill Title</h2>
            <p id="modal-bill-meta" style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">Meta info</p>
            <div id="modal-bill-items" style="margin-bottom: 1.5rem;">
                <!-- Items list -->
            </div>
            <div style="display: flex; justify-content: flex-end; gap: 0.5rem;">
                <button class="btn btn-secondary" onclick="closeModal()">Close</button>
                <button class="btn" id="delete-bill-btn" style="background: var(--error);">Delete Bill</button>
            </div>
        </div>
    </div>

    <script>
        // State
        let state = {
            homeName: "The Grand Apt",
            roommates: ["Alice", "Bob", "Luyu"],
            bills: [
                {
                    id: 1,
                    title: "Trader Joe's Groceries",
                    payer: "Alice",
                    date: "2026-06-24",
                    total: 45.00,
                    items: [
                        { name: "Organic Milk", price: 5.50, splitWith: ["Alice", "Bob", "Luyu"] },
                        { name: "Cereal", price: 4.50, splitWith: ["Alice", "Bob"] },
                        { name: "Fresh Berries", price: 12.00, splitWith: ["Alice", "Luyu"] },
                        { name: "Premium Steaks", price: 23.00, splitWith: ["Alice", "Bob", "Luyu"] }
                    ]
                }
            ]
        };

        // Load state from local storage if exists
        const savedState = localStorage.getItem("spilt_state");
        if (savedState) {
            try {
                state = JSON.parse(savedState);
            } catch(e) {}
        }

        function saveState() {
            localStorage.setItem("spilt_state", JSON.stringify(state));
            updateUi();
        }

        // Tabs
        function switchTab(tabId) {
            document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
            document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
            
            document.getElementById(tabId + '-screen').classList.add('active');
            event.target.classList.add('active');
        }

        // UI rendering
        function updateUi() {
            // Roommates dropdown & list
            const payerSelect = document.getElementById('bill-payer');
            if (payerSelect) {
                payerSelect.innerHTML = state.roommates.map(r => \`<option value="\${r}">\${r}</option>\`).join('');
            }

            const roomiesList = document.getElementById('roommates-list');
            if (roomiesList) {
                roomiesList.innerHTML = state.roommates.map(r => \`<span class="pill active">\${r}</span>\`).join('');
            }

            // Bills List
            const billsList = document.getElementById('bills-list');
            if (billsList) {
                if (state.bills.length === 0) {
                    billsList.innerHTML = '<p style="color: var(--text-muted); text-align: center; padding: 2rem 0;">No bills added yet.</p>';
                } else {
                    billsList.innerHTML = state.bills.map(b => \`
                        <div class="bill-card" onclick="viewBill(\${b.id})">
                            <div>
                                <strong style="font-size: 1.1rem;">\${b.title}</strong>
                                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.25rem;">
                                    Paid by <strong>\${b.payer}</strong> • \${b.date}
                                </div>
                            </div>
                            <div style="font-size: 1.25rem; font-weight: 800; color: var(--primary);">\$\${b.total.toFixed(2)}</div>
                        </div>
                    \`).join('');
                }
            }

            calculateBalances();
        }

        // Calculation of Balances and optimal debts settlement
        function calculateBalances() {
            // Net balance for each roommate
            const netBalances = {};
            state.roommates.forEach(r => netBalances[r] = 0);

            state.bills.forEach(bill => {
                const payer = bill.payer;
                bill.items.forEach(item => {
                    const price = item.price;
                    const splitCount = item.splitWith.length;
                    if (splitCount === 0) return;

                    const perPerson = price / splitCount;
                    
                    // Payer lent the money
                    netBalances[payer] += price;
                    
                    // Everyone splitting owes the share
                    item.splitWith.forEach(person => {
                        if (netBalances[person] !== undefined) {
                            netBalances[person] -= perPerson;
                        }
                    });
                });
            });

            // Display Balances
            const container = document.getElementById('balances-container');
            if (container) {
                container.innerHTML = Object.entries(netBalances).map(([name, bal]) => {
                    const sign = bal >= 0 ? '+' : '';
                    const cls = bal >= 0 ? 'positive' : 'negative';
                    return \`
                        <div class="balance-item">
                            <span>\${name}</span>
                            <span class="amount \${cls}">\${sign}\$\${bal.toFixed(2)}</span>
                        </div>
                    \`;
                }).join('');
            }

            // Simplify debts (greedy matching)
            const debtors = [];
            const creditors = [];
            Object.entries(netBalances).forEach(([name, bal]) => {
                if (bal < -0.01) {
                    debtors.push({ name, amount: -bal });
                } else if (bal > 0.01) {
                    creditors.push({ name, amount: bal });
                }
            });

            // Sort descending
            debtors.sort((a, b) => b.amount - a.amount);
            creditors.sort((a, b) => b.amount - a.amount);

            const transactions = [];
            let i = 0, j = 0;
            while (i < debtors.length && j < creditors.length) {
                const debtor = debtors[i];
                const creditor = creditors[j];
                const amount = Math.min(debtor.amount, creditor.amount);

                transactions.push({ from: debtor.name, to: creditor.name, amount });
                debtor.amount -= amount;
                creditor.amount -= amount;

                if (debtor.amount < 0.01) i++;
                if (creditor.amount < 0.01) j++;
            }

            const debtsContainer = document.getElementById('debts-container');
            if (debtsContainer) {
                if (transactions.length === 0) {
                    debtsContainer.innerHTML = '<p style="color: var(--text-muted); font-size: 0.9rem;">All settled up!</p>';
                } else {
                    debtsContainer.innerHTML = transactions.map(t => \`
                        <div class="balance-item" style="font-size: 0.9rem;">
                            <span><strong>\${t.from}</strong> owes <strong>\${t.to}</strong></span>
                            <span class="amount negative">\$\${t.amount.toFixed(2)}</span>
                        </div>
                    \`).join('');
                }
            }
        }

        // Bill details / Modal
        function viewBill(id) {
            const bill = state.bills.find(b => b.id === id);
            if (!bill) return;

            document.getElementById('modal-bill-title').textContent = bill.title;
            document.getElementById('modal-bill-meta').innerHTML = \`Paid by <strong>\${bill.payer}</strong> on \${bill.date}\`;
            
            const itemsContainer = document.getElementById('modal-bill-items');
            itemsContainer.innerHTML = bill.items.map(item => \`
                <div style="display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid rgba(255, 255, 255, 0.03);">
                    <div>
                        <div>\${item.name}</div>
                        <div style="font-size: 0.75rem; color: var(--text-muted);">Split with: \${item.splitWith.join(', ')}</div>
                    </div>
                    <div style="font-weight: 600;">\$\${item.price.toFixed(2)}</div>
                </div>
            \`).join('');

            const delBtn = document.getElementById('delete-bill-btn');
            delBtn.onclick = () => {
                state.bills = state.bills.filter(b => b.id !== id);
                closeModal();
                saveState();
            };

            document.getElementById('bill-detail-modal').classList.add('active');
        }

        function closeModal() {
            document.getElementById('bill-detail-modal').classList.remove('active');
        }

        // Dynamic bill creation form helper
        let currentFormItems = [];

        function renderFormItems() {
            const container = document.getElementById('bill-items-container');
            if (!container) return;

            container.innerHTML = currentFormItems.map((item, idx) => \`
                <div class="item-row" data-idx="\${idx}">
                    <input type="text" placeholder="Item name" value="\${item.name}" oninput="updateFormItem(\${idx}, 'name', this.value)">
                    <input type="number" step="0.01" placeholder="Price" value="\${item.price || ''}" oninput="updateFormItem(\${idx}, 'price', parseFloat(this.value) || 0)">
                    <div class="pill-container">
                        \${state.roommates.map(r => {
                            const active = item.splitWith.includes(r) ? 'active' : '';
                            return \`<span class="pill \${active}" onclick="toggleItemSplit(\${idx}, '\${r}')">\${r}</span>\`;
                        }).join('')}
                    </div>
                    <button class="delete-btn" onclick="removeFormItemRow(\${idx})">&times;</button>
                </div>
            \`).join('');

            // Calculate total
            const total = currentFormItems.reduce((acc, item) => acc + (item.price || 0), 0);
            document.getElementById('bill-total').textContent = \`$\${total.toFixed(2)}\`;
        }

        function addBlankItemRow(name = "", price = 0) {
            currentFormItems.push({
                name: name,
                price: price,
                splitWith: [...state.roommates]
            });
            renderFormItems();
        }

        function removeFormItemRow(idx) {
            currentFormItems.splice(idx, 1);
            renderFormItems();
        }

        function updateFormItem(idx, field, val) {
            currentFormItems[idx][field] = val;
            if (field === 'price') {
                const total = currentFormItems.reduce((acc, item) => acc + (item.price || 0), 0);
                document.getElementById('bill-total').textContent = \`$\${total.toFixed(2)}\`;
            }
        }

        function toggleItemSplit(itemIdx, roomie) {
            const item = currentFormItems[itemIdx];
            if (item.splitWith.includes(roomie)) {
                item.splitWith = item.splitWith.filter(r => r !== roomie);
            } else {
                item.splitWith.push(roomie);
            }
            renderFormItems();
        }

        function saveBill() {
            const title = document.getElementById('bill-title').value.trim() || "Shared Bill";
            const payer = document.getElementById('bill-payer').value;
            const date = document.getElementById('bill-date').value || new Date().toISOString().split('T')[0];
            const total = currentFormItems.reduce((acc, item) => acc + (item.price || 0), 0);

            if (currentFormItems.length === 0) {
                alert("Please add at least one item.");
                return;
            }

            const newBill = {
                id: Date.now(),
                title,
                payer,
                date,
                total,
                items: currentFormItems
            };

            state.bills.push(newBill);
            saveState();

            // Reset
            document.getElementById('bill-title').value = '';
            document.getElementById('bill-date').value = '';
            currentFormItems = [];
            
            switchTab('dashboard');
        }

        // Roommates management
        function addRoommate() {
            const input = document.getElementById('new-roommate-name');
            const name = input.value.trim();
            if (name && !state.roommates.includes(name)) {
                state.roommates.push(name);
                input.value = '';
                saveState();
            }
        }

        // SMS sync parsing
        function parseSms() {
            const sms = document.getElementById('sms-input').value;
            if (!sms) return;

            // Simple regex extraction
            const amountMatch = sms.match(/\\$\\s*([0-9]+(?:\\.[0-9]{2})?)/) || sms.match(/([0-9]+\\.[0-9]{2})/);
            const merchantMatch = sms.match(/at\\s+([A-Za-z0-9'\\s]+?)(?:\\.|Balance|Txn)/i) || sms.match(/at\\s+([A-Za-z0-9'\\s]+)/i);

            const amount = amountMatch ? parseFloat(amountMatch[1]) : 0;
            const merchant = merchantMatch ? merchantMatch[1].trim() : "Simulated Txn";

            document.getElementById('bill-title').value = merchant;
            document.getElementById('bill-date').value = new Date().toISOString().split('T')[0];

            currentFormItems = [];
            addBlankItemRow("Total Txn", amount);

            switchTab('scanner');
        }

        // OCR logic
        const dropzone = document.getElementById('dropzone');
        const fileInput = document.getElementById('file-input');

        dropzone.onclick = () => fileInput.click();

        dropzone.ondragover = (e) => {
            e.preventDefault();
            dropzone.style.borderColor = 'var(--primary)';
        };

        dropzone.ondragleave = () => {
            dropzone.style.borderColor = 'var(--card-border)';
        };

        dropzone.ondrop = (e) => {
            e.preventDefault();
            if (e.dataTransfer.files.length > 0) {
                processImage(e.dataTransfer.files[0]);
            }
        };

        fileInput.onchange = (e) => {
            if (e.target.files.length > 0) {
                processImage(e.target.files[0]);
            }
        };

        function processImage(file) {
            const progressContainer = document.getElementById('ocr-progress');
            const progressFill = document.getElementById('ocr-progress-fill');
            const statusText = document.getElementById('ocr-status-text');

            progressContainer.style.display = 'block';
            statusText.textContent = "Loading Tesseract OCR...";
            progressFill.style.width = '10%';

            Tesseract.recognize(
                file,
                'eng',
                {
                    logger: m => {
                        if (m.status === 'recognizing text') {
                            progressFill.style.width = Math.round(m.progress * 100) + '%';
                            statusText.textContent = \`Recognizing text: \${Math.round(m.progress * 100)}%\`;
                        }
                    }
                }
            ).then(({ data: { text } }) => {
                progressFill.style.width = '100%';
                statusText.textContent = "OCR Done! Parsing items...";
                
                // Parse text lines for prices/items
                const lines = text.split('\\n');
                currentFormItems = [];

                let totalFound = false;

                lines.forEach(line => {
                    // Try to match a price (e.g. 10.99) and a description
                    const priceMatch = line.match(/\\b([0-9]+\\.[0-9]{2})\\b/);
                    if (priceMatch) {
                        const price = parseFloat(priceMatch[1]);
                        // Clean line to get product name
                        const name = line.replace(priceMatch[0], '').replace(/[$\\|:;_=]/g, '').trim() || "Item";
                        
                        if (name.toLowerCase().includes('total') || name.toLowerCase().includes('subtotal')) {
                            // Don't duplicate total
                            totalFound = true;
                        } else if (price > 0 && name.length > 2) {
                            currentFormItems.push({
                                name: name,
                                price: price,
                                splitWith: [...state.roommates]
                            });
                        }
                    }
                });

                // Set default bill title
                document.getElementById('bill-title').value = "Scanned Receipt";
                document.getElementById('bill-date').value = new Date().toISOString().split('T')[0];

                if (currentFormItems.length === 0) {
                    addBlankItemRow("Scanned Bill Total", 0.00);
                }

                renderFormItems();
                setTimeout(() => {
                    progressContainer.style.display = 'none';
                }, 2000);

            }).catch(err => {
                console.error(err);
                statusText.textContent = "OCR Failed. Adding blank row.";
                addBlankItemRow("Manual Entry", 0.00);
            });
        }

        // Initialize Form
        addBlankItemRow("Milk & Bread", 12.50);
        addBlankItemRow("Chips", 4.20);
        
        // Initial setup
        updateUi();
    </script>
</body>
</html>
`;
