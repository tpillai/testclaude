import { useEffect, useState } from 'react'

const API = '/api/todos'

export default function App() {
  const [todos, setTodos] = useState([])
  const [input, setInput] = useState('')
  const [error, setError] = useState(null)

  useEffect(() => { fetchTodos() }, [])

  async function fetchTodos() {
    try {
      const res = await fetch(API)
      setTodos(await res.json())
    } catch {
      setError('Failed to load todos')
    }
  }

  async function addTodo(e) {
    e.preventDefault()
    const title = input.trim()
    if (!title) return
    const res = await fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, completed: false })
    })
    if (res.ok) {
      setInput('')
      fetchTodos()
    }
  }

  async function toggleTodo(todo) {
    await fetch(`${API}/${todo.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: todo.title, completed: !todo.completed })
    })
    fetchTodos()
  }

  async function deleteTodo(id) {
    await fetch(`${API}/${id}`, { method: 'DELETE' })
    fetchTodos()
  }

  const remaining = todos.filter(t => !t.completed).length

  return (
    <div className="container">
      <h1>Todo App</h1>

      <form onSubmit={addTodo} className="add-form">
        <input
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder="What needs to be done?"
        />
        <button type="submit">Add</button>
      </form>

      {error && <p className="error">{error}</p>}

      <ul className="todo-list">
        {todos.map(todo => (
          <li key={todo.id} className={todo.completed ? 'done' : ''}>
            <input
              type="checkbox"
              checked={todo.completed}
              onChange={() => toggleTodo(todo)}
            />
            <span>{todo.title}</span>
            <button className="delete" onClick={() => deleteTodo(todo.id)}>✕</button>
          </li>
        ))}
      </ul>

      {todos.length > 0 && (
        <p className="count">{remaining} of {todos.length} remaining</p>
      )}
    </div>
  )
}
