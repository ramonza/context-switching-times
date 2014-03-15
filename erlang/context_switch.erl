-module(context_switch).
-export([start/0]).

start() ->
	StartProc = fun(_) ->
		spawn(fun() ->
			receive
				{start, Pid} -> run(Pid)
			end
		end)
	end,
	Pids = lists:map(StartProc, lists:seq(1, 100)),
	[Head | _ ] = Pids,
	lists:foldr(fun(Pid, Next) -> Pid ! {start, Next}, Pid end, Head, Pids),
	StartTime = now(),
	Head ! {ping, 1},
	timer:sleep(1000),
	Head ! {finished, self()},
	receive
		{result, N} -> io:format("~p~n", [ (N * 1000000) / timer:now_diff(now(), StartTime)])
	end.

run(NextPid) ->
	receive
		{ping, N} ->
			NextPid ! {ping, N + 1},
			run(NextPid);
		{finished, Sender} ->
			Sender ! {result, return_value()}
	end.

return_value() ->
	receive
		{ping, N} -> N
	end.
