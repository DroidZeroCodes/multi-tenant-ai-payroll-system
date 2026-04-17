import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {TanStackDevtools} from '@tanstack/react-devtools'
import {ReactQueryDevtoolsPanel} from '@tanstack/react-query-devtools'
import {TanStackRouterDevtoolsPanel} from '@tanstack/react-router-devtools'
import {AuthProvider, useAuth} from "#/auth.tsx";
import {RouterProvider} from "@tanstack/react-router";
import TanstackQueryProvider from "#/integrations/tanstack-query/root-provider.tsx";
import {router} from "#/router.tsx";

function AppWithRouter() {
    const auth = useAuth();
    return <RouterProvider router={router} context={{ auth }} />;
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <TanstackQueryProvider>
            <AuthProvider>
                <AppWithRouter />
            </AuthProvider>
        </TanstackQueryProvider>
        <TanStackDevtools
            plugins={[
                {
                    name: 'TanStack Query',
                    render: <ReactQueryDevtoolsPanel/>,
                },
                {
                    name: 'TanStack Router',
                    render: <TanStackRouterDevtoolsPanel/>,
                },
            ]}
        />
    </StrictMode>,
)
