// Kanban Task Manager - JavaScript
// API Base URL - Port 8081
const API_BASE = 'http://localhost:8081/kanban-app/';

console.log("🔌 Kanban app initialized");
console.log("🌐 API Base:", API_BASE);

// ========== PAGE LOAD ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log("📄 Page loaded");
    loadTasks();
});

// ========== LOAD ALL TASKS ==========
function loadTasks() {
    console.log("🔄 Loading tasks...");
    
    fetch(`${API_BASE}`)
        .then(response => response.json())
        .then(tasks => {
            console.log("✅ Loaded tasks:", tasks);
            
            // Clear all columns
            document.getElementById('todo-list').innerHTML = '';
            document.getElementById('in-progress-list').innerHTML = '';
            document.getElementById('done-list').innerHTML = '';
            
            // Update counts
            document.getElementById('todo-count').textContent = '0';
            document.getElementById('in-progress-count').textContent = '0';
            document.getElementById('done-count').textContent = '0';
            
            let todoCnt = 0, inProgressCnt = 0, doneCnt = 0;
            
            // Add each task to the board
            tasks.forEach(task => {
                addTaskToBoard(task);
                
                // Count by status
                if (task.status === 'TODO') todoCnt++;
                else if (task.status === 'IN_PROGRESS') inProgressCnt++;
                else if (task.status === 'DONE') doneCnt++;
            });
            
            // Update counts
            document.getElementById('todo-count').textContent = todoCnt;
            document.getElementById('in-progress-count').textContent = inProgressCnt;
            document.getElementById('done-count').textContent = doneCnt;
        })
        .catch(error => {
            console.error('❌ Error loading tasks:', error);
        });
}

// ========== ADD TASK TO BOARD (HTML) ==========
function addTaskToBoard(task) {
    const taskCard = document.createElement('div');
    taskCard.className = `task-card priority-${task.priority.toLowerCase()}`;
    taskCard.id = `task-${task.id}`;
    taskCard.draggable = true;
    
    // Task HTML
    taskCard.innerHTML = `
        <div class="task-header">
            <h3 class="task-title">${task.title}</h3>
            <button class="task-delete-btn" onclick="deleteTask(${task.id})">✕</button>
        </div>
        ${task.description ? `<p class="task-description">${task.description}</p>` : ''}
        <div class="task-footer">
            <span class="task-priority priority-${task.priority.toLowerCase()}">${task.priority}</span>
        </div>
    `;
    
    // Add drag handlers
    taskCard.addEventListener('dragstart', handleDragStart);
    
    // Add to correct column
    const columnMap = {
        'TODO': 'todo-list',
        'IN_PROGRESS': 'in-progress-list',
        'DONE': 'done-list'
    };
    
    const columnId = columnMap[task.status];
    document.getElementById(columnId).appendChild(taskCard);
}

// ========== DRAG & DROP HANDLERS ==========
function handleDragStart(event) {
    event.dataTransfer.effectAllowed = 'move';
    event.dataTransfer.setData('taskId', event.currentTarget.id);
    event.currentTarget.style.opacity = '0.6';
}

function handleDragOver(event) {
    event.preventDefault();
    event.currentTarget.style.backgroundColor = '#f0f0f0';
    event.currentTarget.style.borderWidth = '2px';
}

function handleDrop(event, newStatus) {
    event.preventDefault();
    event.currentTarget.style.backgroundColor = 'transparent';
    event.currentTarget.style.borderWidth = '0';
    
    const taskId = event.dataTransfer.getData('taskId').replace('task-', '');
    const taskCard = document.getElementById(`task-${taskId}`);
    
    if (taskCard) {
        // Update in database
        updateTaskStatus(taskId, newStatus);
        
        // Move in UI
        event.currentTarget.appendChild(taskCard);
        taskCard.style.opacity = '1';
        
        // Reload to update counts
        setTimeout(loadTasks, 300);
    }
}

// ========== UPDATE TASK STATUS ==========
function updateTaskStatus(taskId, newStatus) {
    console.log(`🔄 Updating task ${taskId} to ${newStatus}`);
    
    fetch(`${API_BASE}/update`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: parseInt(taskId),
            status: newStatus
        })
    })
    .catch(error => console.error('❌ Error updating task:', error));
}

// ========== DELETE TASK ==========
function deleteTask(taskId) {
    if (confirm('Are you sure you want to delete this task?')) {
        console.log(`🗑️ Deleting task ${taskId}`);
        
        fetch(`${API_BASE}/delete`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: taskId
            })
        })
        .then(response => response.json())
        .then(() => {
            console.log('✅ Task deleted');
            loadTasks(); // Reload
        })
        .catch(error => console.error('❌ Error deleting task:', error));
    }
}

// ========== MODAL FUNCTIONS ==========
function openModal() {
    console.log("📝 Opening task modal");
    document.getElementById('taskModal').classList.add('active');
    document.getElementById('taskTitle').focus();
}

function closeModal() {
    console.log("❌ Closing task modal");
    document.getElementById('taskModal').classList.remove('active');
    document.getElementById('taskForm').reset();
}

// ========== ADD TASK FORM ==========
function handleAddTask(event) {
    event.preventDefault();
    
    const title = document.getElementById('taskTitle').value;
    const description = document.getElementById('taskDescription').value;
    const priority = document.getElementById('taskPriority').value;
    
    console.log(`✏️ Adding task: ${title}`);
    
    fetch(`${API_BASE}/add`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            title: title,
            description: description,
            priority: priority
        })
    })
    .then(response => response.json())
    .then(newTask => {
        console.log('✅ Task created:', newTask);
        closeModal();
        loadTasks(); // Reload to show new task
    })
    .catch(error => {
        console.error('❌ Error adding task:', error);
        alert('Failed to add task');
    });
}

// Close modal when clicking outside
document.addEventListener('click', function(event) {
    const modal = document.getElementById('taskModal');
    const isClickInsideModal = modal.querySelector('.modal-content').contains(event.target);
    const isAddBtn = event.target.classList.contains('add-task-btn');
    
    if (!isClickInsideModal && !isAddBtn && modal.classList.contains('active')) {
        closeModal();
    }
});