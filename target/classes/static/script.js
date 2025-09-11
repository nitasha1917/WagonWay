// Configuration and Constants
const CONFIG = {
    baseUrl: "http://localhost:8080/api",
    apiTimeout: 10000, // 10 seconds timeout
    animationDelay: 300
};

// Application State
let appState = {
    loggedInUser: null,
    editingTicketId: null,
    isLoading: false
};

// Enhanced train data with more realistic pricing and categories
const trainData = {
    "Vande Bharat Express": { price: 2500, category: "Premium" },
    "Shatabdi Express": { price: 1800, category: "Express" },
    "Rajdhani Express": { price: 2200, category: "Premium" },
    "Duronto Express": { price: 1500, category: "Express" },
    "Garib Rath Express": { price: 800, category: "Economy" },
    "Jan Shatabdi Express": { price: 600, category: "Economy" },
    "Intercity Express": { price: 400, category: "Regular" },
    "Local Passenger": { price: 150, category: "Local" }
};

const stationData = [
    "New Delhi", "Mumbai Central", "Chennai Central", "Kolkata",
    "Bangalore City", "Hyderabad", "Pune", "Ahmedabad",
    "Jaipur", "Lucknow", "Kanpur", "Nagpur"
];

// Utility Functions
const utils = {
    showLoading: (show = true) => {
        appState.isLoading = show;
        document.body.style.cursor = show ? 'wait' : 'default';
    },

    showNotification: (message, type = 'info') => {
        // Remove any existing notifications if more than 2
        const existingNotifications = document.querySelectorAll('.notification');
        if (existingNotifications.length > 2) {
            existingNotifications[0].remove();
        }

        // Create notification with proper type class
        const notification = document.createElement('div');
        const typeClass = type === 'success' ? 'notification-success' :
            type === 'error' ? 'notification-error' : 'notification-info';

        notification.className = `notification ${typeClass}`;
        notification.innerHTML = `
            <div class="notification-content">
                <span class="notification-icon">${type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️'}</span>
                <span class="notification-message">${message}</span>
            </div>
        `;

        document.body.appendChild(notification);

        // Trigger animation
        requestAnimationFrame(() => {
            notification.classList.add('show');
        });

        // Auto remove after 4 seconds
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }, 4000);
    },

    validateForm: (formData) => {
        const errors = [];

        if (!formData.username?.trim()) errors.push('Username is required');
        if (formData.email && !formData.email.includes('@')) errors.push('Valid email is required');
        if (!formData.password?.trim()) errors.push('Password is required');
        if (formData.password && formData.password.length < 6) errors.push('Password must be at least 6 characters');

        return errors;
    },

    clearForm: (formId) => {
        const form = document.getElementById(formId);
        if (form) {
            const inputs = form.querySelectorAll('input, select');
            inputs.forEach(input => input.value = '');
        }
    }
};

// UI Management
const ui = {
    updateHeader() {
        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            if (appState.loggedInUser) {
                logoutBtn.style.display = 'block';
                logoutBtn.textContent = `Sign Out (${appState.loggedInUser})`;
            } else {
                logoutBtn.style.display = 'none';
            }
        }
    }
};

// API Functions with enhanced error handling
const api = {
    async makeRequest(url, options = {}) {
        utils.showLoading(true);

        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), CONFIG.apiTimeout);

            const response = await fetch(url, {
                ...options,
                signal: controller.signal,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });

            clearTimeout(timeoutId);

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP ${response.status}: ${response.statusText}`);
            }

            return response;
        } catch (error) {
            if (error.name === 'AbortError') {
                throw new Error('Request timed out. Please check your connection and try again.');
            }
            throw error;
        } finally {
            utils.showLoading(false);
        }
    },

    async register(userData) {
        return await this.makeRequest(`${CONFIG.baseUrl}/auth/register`, {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    },

    async login(credentials) {
        return await this.makeRequest(`${CONFIG.baseUrl}/auth/login`, {
            method: 'POST',
            body: JSON.stringify(credentials)
        });
    },

    async saveTicket(ticketData, ticketId = null) {
        const url = ticketId ? `${CONFIG.baseUrl}/tickets/${ticketId}` : `${CONFIG.baseUrl}/tickets`;
        const method = ticketId ? 'PUT' : 'POST';

        return await this.makeRequest(url, {
            method,
            body: JSON.stringify(ticketData)
        });
    },

    async getUserTickets(username) {
        const response = await this.makeRequest(`${CONFIG.baseUrl}/tickets/user/${username}`);
        return await response.json();
    },

    async deleteTicket(ticketId) {
        return await this.makeRequest(`${CONFIG.baseUrl}/tickets/${ticketId}`, {
            method: 'DELETE'
        });
    }
};

// Enhanced Section Management
const sections = {
    show(sectionId) {
        // Hide all sections with smooth animation
        const allSections = document.querySelectorAll('.card');
        allSections.forEach(section => {
            section.classList.remove('active');
        });

        // Show target section after animation delay
        setTimeout(() => {
            const targetSection = document.getElementById(sectionId);
            if (targetSection) {
                targetSection.classList.add('active');

                // Initialize section-specific functionality
                if (sectionId === 'dashboard-section') {
                    this.initializeDashboard();
                }
            }
        }, CONFIG.animationDelay);
    },

    showRegister() {
        this.show('register-section');
        utils.clearForm('register-section');
    },

    showLogin() {
        this.show('login-section');
        utils.clearForm('login-section');
        ui.updateHeader(); // Update header when showing login
    },

    showDashboard() {
        if (!appState.loggedInUser) {
            utils.showNotification('Please log in first', 'error');
            return;
        }
        this.show('dashboard-section');
    },

    initializeDashboard() {
        this.populateDropdowns();
        this.loadUserTickets();
        this.resetTicketForm();
    },

    populateDropdowns() {
        this.populateTrainDropdown();
        this.populateStationDropdowns();
        this.setupEventListeners();
    },

    populateTrainDropdown() {
        const trainSelect = document.getElementById("train-name");
        trainSelect.innerHTML = '<option value="">Select a train</option>';

        Object.entries(trainData).forEach(([trainName, data]) => {
            const option = document.createElement("option");
            option.value = trainName;
            option.textContent = `${trainName} (${data.category}) - ₹${data.price}`;
            trainSelect.appendChild(option);
        });
    },

    populateStationDropdowns() {
        const sourceSelect = document.getElementById("source");
        const destinationSelect = document.getElementById("destination");

        [sourceSelect, destinationSelect].forEach(select => {
            select.innerHTML = '<option value="">Choose station</option>';
            stationData.forEach(station => {
                const option = document.createElement("option");
                option.value = station;
                option.textContent = station;
                select.appendChild(option);
            });
        });
    },

    setupEventListeners() {
        const trainSelect = document.getElementById("train-name");
        trainSelect.addEventListener("change", this.updateTicketPrice);
        this.updateTicketPrice(); // Initial price update
    },

    updateTicketPrice() {
        const selectedTrain = document.getElementById("train-name").value;
        const priceInput = document.getElementById("ticket-price");

        if (selectedTrain && trainData[selectedTrain]) {
            priceInput.value = trainData[selectedTrain].price;
        } else {
            priceInput.value = '';
        }
    },

    resetTicketForm() {
        utils.clearForm('dashboard-section');
        appState.editingTicketId = null;
        const submitBtn = document.querySelector('#dashboard-section button[onclick*="addTicket"]');
        if (submitBtn) {
            submitBtn.textContent = 'Reserve Ticket';
        }
    },

    async loadUserTickets() {
        if (!appState.loggedInUser) return;

        try {
            const tickets = await api.getUserTickets(appState.loggedInUser);
            this.renderTicketList(tickets);
        } catch (error) {
            utils.showNotification('Failed to load your tickets: ' + error.message, 'error');
        }
    },

    renderTicketList(tickets) {
        const ticketList = document.getElementById("ticket-list");

        if (!tickets || tickets.length === 0) {
            ticketList.innerHTML = '<li class="empty-state">No tickets booked yet. Reserve your first ticket!</li>';
            return;
        }

        ticketList.innerHTML = tickets.map(ticket => `
            <li class="ticket-item">
                <div class="ticket-info">
                    <span class="train-name">${ticket.trainName}</span>
                    <span class="route">${ticket.source} → ${ticket.destination}</span>
                </div>
                <div class="ticket-details">
                    <span class="price">₹${ticket.price}</span>
                    <span class="booking-date">Booked: ${new Date().toLocaleDateString()}</span>
                </div>
                <div class="button-group">
                    <button onclick="ticketManager.editTicket('${ticket.id}','${ticket.trainName}','${ticket.source}','${ticket.destination}','${ticket.price}')" 
                            class="btn-edit" title="Edit Ticket">
                        ✏️ Edit
                    </button>
                    <button onclick="ticketManager.deleteTicket('${ticket.id}')" 
                            class="btn-delete" title="Cancel Ticket">
                        ❌ Cancel
                    </button>
                </div>
            </li>
        `).join('');
    }
};

// Enhanced User Authentication
const auth = {
    async register() {
        const formData = {
            username: document.getElementById("reg-username").value.trim(),
            email: document.getElementById("reg-email").value.trim(),
            password: document.getElementById("reg-password").value
        };

        // Validate form
        const errors = utils.validateForm(formData);
        if (errors.length > 0) {
            utils.showNotification(errors.join('. '), 'error');
            return;
        }

        try {
            await api.register(formData);
            utils.showNotification('Account created successfully! Please sign in to continue.', 'success');
            sections.showLogin();
        } catch (error) {
            utils.showNotification('Registration failed: ' + error.message, 'error');
        }
    },

    async login() {
        const credentials = {
            username: document.getElementById("login-username").value.trim(),
            password: document.getElementById("login-password").value
        };

        if (!credentials.username || !credentials.password) {
            utils.showNotification('Please enter both username and password', 'error');
            return;
        }

        try {
            await api.login(credentials);
            appState.loggedInUser = credentials.username;

            // Update UI to show logout button
            ui.updateHeader();

            utils.showNotification(`Welcome back, ${credentials.username}!`, 'success');
            sections.showDashboard();
        } catch (error) {
            utils.showNotification('Sign in failed: ' + error.message, 'error');
        }
    },

    logout() {
        // Check if user is actually logged in
        if (!appState.loggedInUser) {
            utils.showNotification('You are not currently signed in', 'info');
            sections.showLogin(); // Ensure we're on login page
            return;
        }

        if (confirm(`Sign out from ${appState.loggedInUser}'s account?`)) {
            const username = appState.loggedInUser;

            // Clear user data
            appState.loggedInUser = null;
            appState.editingTicketId = null;

            // Clear any form data
            utils.clearForm('dashboard-section');

            // Update UI to hide logout button
            ui.updateHeader();

            // Show success message with username
            utils.showNotification(`Successfully signed out. Goodbye, ${username}!`, 'success');

            // Navigate to login
            sections.showLogin();
        }
    }
};

// Enhanced Ticket Management
const ticketManager = {
    async saveTicket() {
        if (!appState.loggedInUser) {
            utils.showNotification('Please sign in first', 'error');
            return;
        }

        const ticketData = this.collectTicketData();

        // Validate ticket data
        const validation = this.validateTicketData(ticketData);
        if (!validation.isValid) {
            utils.showNotification(validation.errors.join('. '), 'error');
            return;
        }

        try {
            await api.saveTicket(ticketData, appState.editingTicketId);
            const action = appState.editingTicketId ? 'updated' : 'reserved';
            utils.showNotification(`Ticket ${action} successfully!`, 'success');

            sections.resetTicketForm();
            sections.loadUserTickets();
        } catch (error) {
            utils.showNotification('Failed to save ticket: ' + error.message, 'error');
        }
    },

    collectTicketData() {
        return {
            username: appState.loggedInUser,
            trainName: document.getElementById("train-name").value,
            source: document.getElementById("source").value,
            destination: document.getElementById("destination").value,
            price: parseFloat(document.getElementById("ticket-price").value) || 0
        };
    },

    validateTicketData(data) {
        const errors = [];

        if (!data.trainName) errors.push('Please select a train');
        if (!data.source) errors.push('Please select departure station');
        if (!data.destination) errors.push('Please select arrival station');
        if (data.source === data.destination) errors.push('Departure and arrival stations must be different');
        if (!data.price || data.price <= 0) errors.push('Invalid ticket price');

        return {
            isValid: errors.length === 0,
            errors
        };
    },

    editTicket(id, trainName, source, destination, price) {
        document.getElementById("train-name").value = trainName;
        document.getElementById("source").value = source;
        document.getElementById("destination").value = destination;
        document.getElementById("ticket-price").value = price;

        appState.editingTicketId = id;

        // Update button text
        const submitBtn = document.querySelector('#dashboard-section button[onclick*="addTicket"]');
        if (submitBtn) {
            submitBtn.textContent = 'Update Ticket';
        }

        utils.showNotification('Ticket loaded for editing', 'info');
    },

    async deleteTicket(ticketId) {
        if (!confirm('Are you sure you want to cancel this ticket? This action cannot be undone.')) {
            return;
        }

        try {
            await api.deleteTicket(ticketId);
            utils.showNotification('Ticket cancelled successfully', 'success');
            sections.loadUserTickets();
        } catch (error) {
            utils.showNotification('Failed to cancel ticket: ' + error.message, 'error');
        }
    }
};

// Global function mappings for HTML onclick handlers
function showRegister() { sections.showRegister(); }
function showLogin() { sections.showLogin(); }
function register() { auth.register(); }
function login() { auth.login(); }
function logout() { auth.logout(); }
function addTicket() { ticketManager.saveTicket(); }

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    // Start with login section
    sections.showLogin();

    // Initialize UI state
    ui.updateHeader(); // Hide logout button initially

    // Add global error handler
    window.addEventListener('unhandledrejection', function(event) {
        console.error('Unhandled promise rejection:', event.reason);
        utils.showNotification('An unexpected error occurred. Please try again.', 'error');
    });

    console.log('TripSync application initialized successfully');
});
