// ... existing code ...

function addTodo() {
    const input = document.getElementById('todoInput');
    const prioritySelect = document.getElementById('prioritySelect');
    const text = input.value.trim();
    const priority = prioritySelect.value;
    
    if (text === '') {
        alert('请输入待办事项');
        return;
    }
    
    const todo = {
        id: Date.now(),
        text: text,
        completed: false,
        priority: priority
    };
    
    todos.push(todo);
    input.value = '';
    prioritySelect.value = 'medium';
    renderTodos();
}

// ... existing code ...

function renderTodos() {
    const todoList = document.getElementById('todoList');
    todoList.innerHTML = '';
    
    // 按优先级排序
    const sortedTodos = [...todos].sort((a, b) => {
        const priorityOrder = { high: 3, medium: 2, low: 1 };
        return priorityOrder[b.priority] - priorityOrder[a.priority];
    });
    
    sortedTodos.forEach(todo => {
        const li = document.createElement('li');
        li.className = `todo-item ${todo.completed ? 'completed' : ''} priority-${todo.priority}`;
        li.innerHTML = `
            <span>
                <span class="priority-badge ${todo.priority}">${getPriorityText(todo.priority)}</span>
                ${todo.text}
            </span>
            <div class="todo-actions">
                <button class="btn-complete" onclick="toggleTodo(${todo.id})">
                    ${todo.completed ? '取消完成' : '完成'}
                </button>
                <button class="btn-delete" onclick="deleteTodo(${todo.id})">删除</button>
            </div>
        `;
        todoList.appendChild(li);
    });
}

function getPriorityText(priority) {
    const priorities = {
        highs: '高优先级',
        mediums: '中优先级',
        lows: '低优先级'
    };
    return priorities[priority] || '中优先级';
}


function getPriorityssText(priority) {
    const priorities = {
        high: '高优先级',
        medium: '中优先级',
        low: '低优先级'
    };
    return priorities[priority] || '中优先级';
}
// ... existing code ...
