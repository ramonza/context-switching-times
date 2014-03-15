import Control.Concurrent
import Control.Monad

ring = 100

new l i = do
  r <- newEmptyMVar
  forkIO (thread i l r)
  return r

thread :: Int -> MVar Int -> MVar Int -> IO ()
thread i l r = go
  where go = do
          m <- takeMVar l
          putMVar r $! m + 1
          go
 
main = do
  a <- newMVar 0
  z <- foldM new a [2..ring]
  forkIO (thread 1 z a)
  threadDelay 1000000
  i <- takeMVar a
  print i
