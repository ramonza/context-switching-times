import scala.actors.Actor
import scala.actors.Actor._

object ContextSwitch extends Application {
	val counters: Seq[Counter] = for (i <- 1 to 100) yield new Counter()
	counters.foldRight(counters.head) { (counter: Counter, next: Counter) =>
		counter.nextActor = next
		counter
	}
	counters.foreach { counter => counter.start }
	counters.head ! NextVal(0)
	Thread.sleep(1000)
	counters.head ! Quit()
}

class Counter extends Actor {

	var nextActor: Actor = null

	def act() {
		loop {
			react {
				case NextVal(n) =>
					if (nextActor == null) {
						println(n)
						System.exit(0)
					} else {
						nextActor ! NextVal(n + 1)
					}
				case Quit() =>
					nextActor = null
			}
		}
	}
}

case class NextVal(n: Int)
case class Quit