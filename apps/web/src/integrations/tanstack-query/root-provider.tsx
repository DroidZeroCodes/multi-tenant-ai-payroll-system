import {QueryClient, QueryClientProvider} from '@tanstack/react-query'

const queryClient = new QueryClient({
  // mutationCache: new MutationCache({
  //   onSuccess: (data, variables, onMutateResult, mutation, context) => {
  //     // Invalidate all queries on successful mutation
  //     queryClient.invalidateQueries()
  //   }
  // })
})

export function getContext() {
  return {
    queryClient,
  }
}
export default function TanstackQueryProvider({ children }: { children: React.ReactNode }) {
  return (
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  )
}
