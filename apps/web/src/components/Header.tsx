import { Link } from '@tanstack/react-router'

export default function Header() {
  return (
    <header className="fixed z-50 w-full h-(--header-height) bg-primary text-primary-foreground px-2 shadow-xl">
      <nav className="page-wrap flex flex-wrap items-center gap-x-3 gap-y-2 py-3 sm:py-2">
        <h2 className="m-0 shrink-0 text-base font-semibold tracking-tight mr-2">
          <Link
            to="/"
            className="inline-flex items-center gap-2 px-3 py-1.5 font-bold font-display text-lg no-underline sm:px-4 sm:py-2"
          >
            <span className="bg-secondary text-sm text-secondary-foreground rounded-lg px-2 py-1.5">PS</span>
            Payroll
          </Link>
        </h2>

        <div className="order-3 text-neutral-300 flex w-full flex-wrap items-center gap-x-4 gap-y-1 pb-1 text-sm font-semibold sm:order-2 sm:w-auto sm:flex-nowrap sm:pb-0">
          <Link
              to="/"
              className="nav-link px-3 py-1.5"
              activeProps={{ className: 'nav-link is-active rounded-lg text-primary-foreground bg-white/10' }}
          >
            Home
          </Link>
          <Link
            to="/about"
            className="nav-link px-3 py-1.5"
            activeProps={{ className: 'nav-link is-active rounded-lg text-primary-foreground bg-white/10' }}
          >
            About
          </Link>
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
        </div>
      </nav>
    </header>
  )
}
