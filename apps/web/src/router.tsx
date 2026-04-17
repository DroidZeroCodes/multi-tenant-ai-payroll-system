import {createRouter} from '@tanstack/react-router'
import {routeTree} from './routeTree.gen'
import {getContext} from "#/integrations/tanstack-query/root-provider.tsx";

export const router = createRouter({
  routeTree,
  context: {
    ...getContext(),
    auth: undefined!,
  },
  scrollRestoration: true,
  defaultPreload: 'intent',
  defaultPreloadStaleTime: 0,
})

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
