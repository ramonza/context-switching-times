require 'rake/clean'

task :mac_deps do
	%w(erlang pypy scala).each { |package| sh "brew install -q #{package} || true" }
end

namespace :java do
	task :compile do
		mkdir_p 'java/classes'
		sh "javac -d java/classes #{FileList['java/src/**/*.java']}"
	end

	task :run => [:compile] do
		sh "java -cp java/classes ContextSwitchingTime"
	end
end

namespace :scala do
	task :compile do
		cd "scala" do
			sh 'scalac ContextSwitch.scala'
		end
	end
	task :run => :compile do
		cd "scala" do
			sh "scala -cp . ContextSwitch"
		end
	end
end

namespace :erlang do
	CLEAN.include FileList['erlang/*.beam'], FileList['erlang/*.log'], FileList['erlang/*.dump']
	task :run do
		cd 'erlang' do
			sh "erlc +native context_switch.erl"
			sh "erl -noshell -s context_switch -s init stop"
		end
	end
end

namespace :erjang do
	ERJANG = "erlang/erjang/erjang-0.2.jar"
	file ERJANG do
		cd 'erlang/erjang' do
			sh 'ant'
		end
	end
	task :run => [ERJANG] do
		cd 'erlang' do
			sh 'erjang/jerl -compile context_switch -noshell'
			sh 'erjan/jerl -noshell -s context_switch -s init stop'
		end
	end
end

namespace :haskell do
	CLEAN.include FileList['haskell/*.o'], FileList['haskell/*.hi'], FileList['haskell/context_switch']
	task :run do
		cd 'haskell' do
			sh "ghc -threaded context_switch.hs"
			sh "./context_switch"
		end
	end
end

task :run_all => ['java:run', 'erlang:run', 'scala:run', 'haskell:run']

task :default => [:run_all]