// Kanban Task Manager - JavaScript
// Day 1: Placeholder - Full functionality on Day 6

// API Base URL - Port 8081!
const API_BASE = 'http://localhost:8081/kanban-app/api/tasks';

console.log("🔌 API Base URL:", API_BASE);

// Load all tasks when page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log("📄 Page loaded, fetching tasks...");
    loadTasks();
});

// ========== LOAD TASKS ==========
function loadTasks() {
    console.log("🔄 Loading tasks from:", API_BASE);
    
    fetch(`${API_BASE}`)
        .then(response => {
            console.log("✅ Response status:", response.status);
            return response.json();
        })
        .then(tasks => {
            console.log("✅ Loaded tasks:", tasks);
            console.log("📊 Total tasks:", tasks.length);
        })
        .catch(error => {
            console.error('❌ Error loading tasks:', error);
        });
}