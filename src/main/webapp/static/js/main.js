/**
 * APEX UNIVERSITY OF TECHNOLOGY (APEX)
 * Main Interactive Portal Script
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Initialize Theme (Light / Dark Mode)
    initTheme();

    // 2. Initialize Update Tabs on Homepage
    initUpdateTabs();

    // 3. Initialize Confirm Dialogs on Dangerous Actions
    initConfirmDialogs();

    // 4. Initialize Table Search Filters
    initTableSearch();
});

/**
 * Handles Dark/Light Mode Theme Switching
 */
function initTheme() {
    const savedTheme = localStorage.getItem('apex_theme') || 'light';
    applyTheme(savedTheme);

    const toggleBtn = document.getElementById('themeToggleBtn');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', function () {
            const currentTheme = document.documentElement.getAttribute('data-bs-theme') || 'light';
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            applyTheme(newTheme);
            localStorage.setItem('apex_theme', newTheme);
        });
    }
}

function applyTheme(theme) {
    document.documentElement.setAttribute('data-bs-theme', theme);
    document.documentElement.setAttribute('data-theme', theme);

    const icon = document.getElementById('themeToggleIcon');
    const label = document.getElementById('themeToggleLabel');
    if (icon) {
        icon.className = theme === 'dark' ? 'bi bi-sun-fill text-warning' : 'bi bi-moon-stars-fill';
    }
    if (label) {
        label.textContent = theme === 'dark' ? 'Light Mode' : 'Dark Mode';
    }
}

/**
 * Homepage Update Filter Tabs ("All", "Instructions", "HelpDesk", "News")
 */
function initUpdateTabs() {
    const tabButtons = document.querySelectorAll('.updates-tab-btn');
    const updateCards = document.querySelectorAll('.update-card');

    if (!tabButtons.length || !updateCards.length) return;

    tabButtons.forEach(btn => {
        btn.addEventListener('click', function () {
            tabButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            const filter = this.getAttribute('data-filter');

            updateCards.forEach(card => {
                const category = card.getAttribute('data-category');
                if (filter === 'all' || category === filter) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
}

/**
 * Confirmation on Delete Operations
 */
function initConfirmDialogs() {
    document.querySelectorAll('.btn-confirm-delete').forEach(btn => {
        btn.addEventListener('click', function (e) {
            const msg = this.getAttribute('data-confirm') || 'Are you sure you want to permanently delete this record?';
            if (!confirm(msg)) {
                e.preventDefault();
            }
        });
    });
}

/**
 * Live Table Filter
 */
function initTableSearch() {
    const searchInputs = document.querySelectorAll('.table-search-input');
    searchInputs.forEach(input => {
        input.addEventListener('keyup', function () {
            const targetTableId = this.getAttribute('data-table-id');
            const table = document.getElementById(targetTableId);
            if (!table) return;

            const term = this.value.toLowerCase();
            const rows = table.querySelectorAll('tbody tr');

            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                if (text.includes(term)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    });
}

/**
 * Open Sandbox Payment Modal
 */
function openSandboxPaymentModal(formId, examId, examName, amount) {
    const formIdField = document.getElementById('payFormId');
    const examIdField = document.getElementById('payExamId');
    const examNameField = document.getElementById('payExamName');
    const amountField = document.getElementById('payAmount');
    const displayAmount = document.getElementById('payDisplayAmount');

    if (formIdField) formIdField.value = formId;
    if (examIdField) examIdField.value = examId;
    if (examNameField) examNameField.textContent = examName;
    if (amountField) amountField.value = amount;
    if (displayAmount) displayAmount.textContent = 'INR ' + Number(amount).toFixed(2);

    const modalEl = document.getElementById('sandboxPaymentModal');
    if (modalEl && typeof bootstrap !== 'undefined') {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }
}
