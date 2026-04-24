import React, { useState } from 'react'
import BookingForm from './components/bookings/BookingForm'
import MyBookings from './components/bookings/MyBookings'
import AdminBookings from './components/bookings/AdminBookings'
import TicketForm from './components/tickets/TicketForm'
import MyTickets from './components/tickets/MyTickets'
import AdminTickets from './components/tickets/AdminTickets'
import Home from './components/common/Home'
import logo from './assets/logo.png'
import './App.css'

const PAGES = [
  { id: 'home', label: '🏠 Home' },
  { id: 'my-bookings', label: '📅 My Bookings' },
  { id: 'new-booking', label: '➕ New Booking' },
  { id: 'admin-bookings', label: '🛡️ Admin Bookings' },
  { id: 'my-tickets', label: '🎫 My Tickets' },
  { id: 'new-ticket', label: '🚨 Report Incident' },
  { id: 'admin-tickets', label: '🛠️ Admin Tickets' },
]

function App() {
  const [activePage, setActivePage] = useState('home')

  const renderPage = () => {
    switch (activePage) {
      case 'home': return <Home onNavigate={setActivePage} />
      case 'my-bookings': return <MyBookings userId={1} />
      case 'new-booking': return <BookingForm userId={1} />
      case 'admin-bookings': return <AdminBookings />
      case 'my-tickets': return <MyTickets userId={1} />
      case 'new-ticket': return <TicketForm userId={1} />
      case 'admin-tickets': return <AdminTickets adminId={1} />
      default: return <Home onNavigate={setActivePage} />
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <img src={logo} alt="Smart Campus Logo" className="app-logo" />
          <h1>Smart Campus Operations Hub</h1>
        </div>
        <nav className="app-nav">
          {PAGES.map(page => (
            <button
              key={page.id}
              className={`nav-btn ${activePage === page.id ? 'active' : ''}`}
              onClick={() => setActivePage(page.id)}
            >
              {page.label}
            </button>
          ))}
        </nav>
      </header>
      <main className="app-main">
        {renderPage()}
      </main>
    </div>
  )
}

export default App
