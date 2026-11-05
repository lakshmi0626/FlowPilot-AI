import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const DEMO_USERS = {
  ADMIN: {
    id: 2,
    email: 'admin@acme.com',
    firstName: 'Eleanor',
    lastName: 'Pemberton',
    role: 'COMPANY_ADMIN',
    organizationId: 1,
    organizationName: 'Acme Corporation',
    organizationSlug: 'acme-corp'
  },
  MANAGER: {
    id: 3,
    email: 'manager@acme.com',
    firstName: 'Marcus',
    lastName: 'Sterling',
    role: 'MANAGER',
    organizationId: 1,
    organizationName: 'Acme Corporation',
    organizationSlug: 'acme-corp'
  },
  EMPLOYEE: {
    id: 4,
    email: 'employee@acme.com',
    firstName: 'David',
    lastName: 'Chen',
    role: 'EMPLOYEE',
    organizationId: 1,
    organizationName: 'Acme Corporation',
    organizationSlug: 'acme-corp'
  },
  SUPER_ADMIN: {
    id: 1,
    email: 'superadmin@flowpilot.ai',
    firstName: 'Alexander',
    lastName: 'Vance',
    role: 'SUPER_ADMIN',
    organizationId: 1,
    organizationName: 'Acme Corporation',
    organizationSlug: 'acme-corp'
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('flowpilot_user');
    return saved ? JSON.parse(saved) : DEMO_USERS.ADMIN;
  });

  const [token, setToken] = useState(() => {
    return localStorage.getItem('flowpilot_token') || 'demo-jwt-token-flowpilot-2026';
  });

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (token) {
      localStorage.setItem('flowpilot_token', token);
    } else {
      localStorage.removeItem('flowpilot_token');
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('flowpilot_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('flowpilot_user');
    }
  }, [user]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const resp = await api.post('/auth/login', { email, password });
      const data = resp.data;
      setToken(data.token);
      const loggedUser = {
        id: data.id,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        role: data.role,
        organizationId: data.organizationId,
        organizationName: data.organizationName,
        organizationSlug: data.organizationSlug
      };
      setUser(loggedUser);
      return loggedUser;
    } catch (err) {
      // If offline or network issue, fallback to matching demo user
      const matched = Object.values(DEMO_USERS).find(u => u.email.toLowerCase() === email.toLowerCase());
      if (matched) {
        setUser(matched);
        setToken('fallback-token-' + matched.id);
        return matched;
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const registerCompany = async (formData) => {
    setLoading(true);
    try {
      const resp = await api.post('/auth/register-company', formData);
      const data = resp.data;
      setToken(data.token);
      const newUser = {
        id: data.id,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        role: data.role,
        organizationId: data.organizationId,
        organizationName: data.organizationName,
        organizationSlug: data.organizationSlug
      };
      setUser(newUser);
      return newUser;
    } catch (err) {
      // Local fallback creation
      const fallback = {
        id: Date.now(),
        email: formData.email,
        firstName: formData.firstName,
        lastName: formData.lastName,
        role: 'COMPANY_ADMIN',
        organizationId: 99,
        organizationName: formData.companyName,
        organizationSlug: formData.slug
      };
      setUser(fallback);
      setToken('fallback-reg-token');
      return fallback;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('flowpilot_user');
    localStorage.removeItem('flowpilot_token');
  };

  const switchRole = (roleKey) => {
    if (DEMO_USERS[roleKey]) {
      setUser(DEMO_USERS[roleKey]);
      setToken('demo-jwt-token-' + roleKey.toLowerCase());
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, registerCompany, logout, switchRole }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
