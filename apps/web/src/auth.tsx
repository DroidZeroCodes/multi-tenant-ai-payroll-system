import React, {createContext, useContext, useEffect, useState} from 'react'
import {useMutation, useQuery} from "@tanstack/react-query";
import {type AuthTokenDto, login as authenticate, validateToken} from "#/client/api";
import {validateTokenQueryKey} from "#/client/api/@tanstack/react-query.gen.ts";

interface User extends Omit<AuthTokenDto, 'token'> {
}

export interface AuthState {
    isAuthenticated: boolean
    user: User | null
    login: (username: string, password: string, tenantId: string) => Promise<void>
    logout: () => void
}

const AuthContext = createContext<AuthState | undefined>(undefined)

// Helper function for logging (disabled in production)
const log = (label: string, data?: any) => {
    if (process.env.NODE_ENV !== 'production') {
        console.log(`AuthProvider: ${label}`);
        console.log(data);
    }
};

export function AuthProvider({children}: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    // 1. Get the token from storage
    const token = typeof window !== 'undefined' ? localStorage.getItem('auth-token') : null;

    // 2. Handle Auto-Login on mount using useQuery at the TOP LEVEL
    const {data: validationResponse } = useQuery({
        queryKey: validateTokenQueryKey(),
        queryFn: () => validateToken({
            headers: {Authorization: `Bearer ${token}`}
        }),
        enabled: !!token, // Only run if token exists
        retry: false,
    });

    // Sync validation result to state
    useEffect(() => {
        if (validationResponse?.data?.data) {
            setUser(validationResponse.data.data);
            setIsAuthenticated(true);
            log('User data after validation:', validationResponse.data.data);
        } else if (validationResponse?.data?.errors) {
            log('Validation error:', validationResponse.data.errors);
            localStorage.removeItem('auth-token');
        }
    }, [validationResponse]);

    // 3. Handle Login using useMutation
    const loginMutation = useMutation({
        mutationFn: ({credentials, tenantId}: any) => {
            log('Login mutation input:', {credentials, tenantId});
            return authenticate({
                headers: {
                    Authorization: `Basic ${credentials}`,
                    'X-Tenant-Id': tenantId
                },
            });
        },
        onSuccess: (response) => {
            log('Login mutation response:', response);
            log('Login mutation response data:', response.data);
            const userData = response.data?.data;
            if (userData) {
                log('User data after login:', userData);
                setUser(userData);
                setIsAuthenticated(true);
                localStorage.setItem('auth-token', userData.token);
            }
            log('Login mutation completed', {userData});
        }
    });

    const login = async (username: string, password: string, tenantId: string) => {
        const credentials = btoa(`${username}:${password}`);
        log('Starting login mutation:', {username, password, tenantId});
        // Trigger the mutation imperatively
        await loginMutation.mutateAsync({credentials, tenantId});
    };

    const logout = () => {
        setUser(null);
        setIsAuthenticated(false);
        log('Logout triggered');
        localStorage.removeItem('auth-token');
    };

    return (
        <AuthContext.Provider value={{isAuthenticated, user, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider')
    }
    return context
}