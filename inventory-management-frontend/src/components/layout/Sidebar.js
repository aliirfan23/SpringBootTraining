"use client"

import { useAuth } from "../../context/AuthContext"

const NavItem = ({ icon, title, isActive, onClick }) => (
  <button
    onClick={onClick}
    className={`w-full flex items-center px-4 py-3 mb-2 rounded-lg transition-all duration-200 text-left ${
      isActive ? "bg-blue-600 text-white shadow-md" : "text-gray-700 hover:bg-gray-100 hover:text-gray-900"
    }`}
  >
    <span className="text-xl mr-3">{icon}</span>
    <span className="font-medium">{title}</span>
  </button>
)

const Sidebar = ({ activeTab, onTabChange }) => {
  const { user } = useAuth()

  const navItems = [
    {
      icon: "📊",
      title: "Dashboard",
      tab: "dashboard",
      roles: ["ADMIN", "USER"],
    },
    {
      icon: "📦",
      title: "Items",
      tab: "items",
      roles: ["ADMIN", "USER"],
    },
    {
      icon: "📈",
      title: "Reports",
      tab: "reports",
      roles: ["ADMIN", "USER"],
    },
  ]

  const handleNavClick = (item) => {
    if (item.tab && onTabChange) {
      onTabChange(item.tab)
    }
  }

  return (
    <div className="sidebar">
      {/* Logo Section */}
      <div className="sidebar-header">
        <div className="logo">
          <div className="logo-icon">
            <span>IM</span>
          </div>
          <span className="logo-text">Inventory Pro</span>
        </div>
      </div>

      {/* Navigation */}
      <div className="sidebar-nav">
        <nav>
          {navItems
            .filter((item) => item.roles.some((role) => user?.roles?.includes(role)))
            .map((item) => (
              <NavItem
                key={item.tab}
                {...item}
                isActive={activeTab === item.tab}
                onClick={() => handleNavClick(item)}
              />
            ))}
        </nav>
      </div>

      {/* User Profile Section */}
      <div className="sidebar-footer">
        <div className="user-profile">
          <div className="user-avatar">
            <span>{user?.username?.[0]?.toUpperCase() || "U"}</span>
          </div>
          <div className="user-info">
            <p className="user-name">{user?.username || "User"}</p>
            <p className="user-role">{user?.roles?.join(", ") || "Guest"}</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Sidebar
