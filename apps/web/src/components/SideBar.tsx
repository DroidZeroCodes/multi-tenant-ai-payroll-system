import {Link} from "@tanstack/react-router";

function NavLink({to, children}: {to: string; children: React.ReactNode}) {
    return (
        <Link
            to={to}
            className="nav-link px-3 py-1.5"
            activeProps={{ className: 'nav-link is-active rounded-lg text-primary-foreground bg-primary/90' }}
        >
            {children}
        </Link>
    )
}
export function SideBar() {
    return (
        <div className="flex flex-col h-[calc(100dvh-var(--header-height))] border-gray-500/20 transition-all duration-300 z-49 sticky top-(--header-height) bg-background text-primary px-2 shadow-xl w-1/5 overflow-y-auto">
            <nav className="flex flex-col gap-1 p-4 py-3 sm:py-2">
                <NavLink to="/">Home</NavLink>
                <NavLink to="/about">About</NavLink>
                <a
                    href="https://tanstack.com/start/latest/docs/framework/react/overview"
                    className="nav-link"
                    target="_blank"
                    rel="noreferrer"
                >
                    Docs
                </a>
                <details className="relative w-full sm:w-auto">
                    <summary className="nav-link list-none cursor-pointer">
                        Demos
                    </summary>
                    <div className="mt-2 min-w-56 rounded-xl border border-[var(--line)] bg-[var(--header-bg)] p-2 shadow-lg sm:absolute sm:right-0">
                        <a
                            href="/demo/form/simple"
                            className="block rounded-lg px-3 py-2 text-sm text-[var(--sea-ink-soft)] no-underline transition hover:bg-[var(--link-bg-hover)] hover:text-[var(--sea-ink)]"
                        >
                            Simple Form
                        </a>
                        <a
                            href="/demo/form/address"
                            className="block rounded-lg px-3 py-2 text-sm text-[var(--sea-ink-soft)] no-underline transition hover:bg-[var(--link-bg-hover)] hover:text-[var(--sea-ink)]"
                        >
                            Address Form
                        </a>
                        <a
                            href="/demo/table"
                            className="block rounded-lg px-3 py-2 text-sm text-[var(--sea-ink-soft)] no-underline transition hover:bg-[var(--link-bg-hover)] hover:text-[var(--sea-ink)]"
                        >
                            TanStack Table
                        </a>
                        <a
                            href="/demo/tanstack-query"
                            className="block rounded-lg px-3 py-2 text-sm text-[var(--sea-ink-soft)] no-underline transition hover:bg-[var(--link-bg-hover)] hover:text-[var(--sea-ink)]"
                        >
                            TanStack Query
                        </a>
                        <a
                            href="/schedule"
                            className="block rounded-lg px-3 py-2 text-sm text-[var(--sea-ink-soft)] no-underline transition hover:bg-[var(--link-bg-hover)] hover:text-[var(--sea-ink)]"
                        >
                            Schedule
                        </a>
                        <a
                            href="/speakers"
                            className="block rounded-lg px-3 py-2 text-sm text-[var(--sea-ink-soft)] no-underline transition hover:bg-[var(--link-bg-hover)] hover:text-[var(--sea-ink)]"
                        >
                            Speakers
                        </a>
                        <a
                            href="/talks"
                            className="block rounded-lg px-3 py-2 text-sm text-[var(--sea-ink-soft)] no-underline transition hover:bg-[var(--link-bg-hover)] hover:text-[var(--sea-ink)]"
                        >
                            Sessions
                        </a>
                    </div>
                </details>
            </nav>
        </div>
    );
}