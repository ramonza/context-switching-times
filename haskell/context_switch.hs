import Control.Concurrent
import Control.Monad

ring_size = 100

new left index = do
  right <- newEmptyMVar
  forkIO (thread index left right)
  return right

thread :: Int -> MVar Int -> MVar Int -> IO ()
thread index left right = go
  where go = do
          m <- takeMVar left
          putMVar right $! m + 1
          go

main = do
  first_var <- newMVar 0
  last_var <- foldM new first_var [2..ring_size]
  forkIO (thread 1 last_var first_var)
  threadDelay 1000000
  m <- takeMVar first_var
  print m
