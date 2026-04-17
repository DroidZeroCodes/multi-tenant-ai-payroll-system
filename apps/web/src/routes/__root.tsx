import {createRootRouteWithContext, ErrorComponent, HeadContent, Scripts,} from '@tanstack/react-router'
import Footer from '../components/Footer'
import Header from '../components/Header'

import appCss from '../styles.css?url'

import type {QueryClient} from '@tanstack/react-query'
import type {useAuth} from "#/auth.tsx";
import {SideBar} from "#/components/SideBar.tsx";
import BreadCrumb from "#/components/BreadCrumb.tsx";
import {useLocation} from "@tanstack/react-router";

interface MyRouterContext {
    auth: ReturnType<typeof useAuth>,
    queryClient: QueryClient
}

export const Route = createRootRouteWithContext<MyRouterContext>()({
    head: () => ({
        meta: [
            {
                charSet: 'utf-8',
            },
            {
                name: 'viewport',
                content: 'width=device-width, initial-scale=1',
            },
            {
                title: 'Payroll System',
            },
        ],
        links: [
            {
                rel: 'stylesheet',
                href: appCss,
            },
        ],
    }),
    beforeLoad: ({context}) => {
        return {
            auth: {
                isAuthenticated: context.auth?.isAuthenticated,
                user: context.auth?.user,
            },
        }
    },
    shellComponent: RootDocument,
    errorComponent: ErrorComponent,
    notFoundComponent: () => <div>Not Found</div>,
})

function RootDocument({ children }: { children: React.ReactNode }) {
    const location = useLocation()
    const isLoginPage = location.pathname === '/login'

    if (isLoginPage) {
        return (
            <>
                <HeadContent/>
                <div className="min-h-screen bg-gray-50">
                    {children}
                </div>
                <Scripts/>
            </>
        )
    }

    return (
        <>
            <HeadContent/>
            <Header/>
            <div
                className="min-h-[calc(100dvh-var(--header-height))] flex min-w-0 flex-row w-full pt-(--header-height)">
                <SideBar/>
                <div className="min-h-[calc(100dvh-var(--header-height))] flex flex-col flex-1 ">
                    <BreadCrumb path={null}/>
                    <div className="flex-1">
                        {children}
                    </div>
                    <Footer/>
                    <Scripts/>
                </div>
            </div>
        </>
    )
}

