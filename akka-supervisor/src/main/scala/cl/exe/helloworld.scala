package cl.exe


import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

class HelloWorldActor extends Actor {
	var contador : Int = 1
	def receive = {
		case mensaje : String => {
			println(s"Mensaje $contador recibido: $mensaje")
			if ((contador % 3) == 0)
			   throw new Exception("Pánico!!!!")
			contador += 1
		}
		case ex : Exception => {
			println(s"Hemos recibido una excepcion $ex")
			throw ex
		}
		case _ => println("¿Qué es esto?")
	}
	println("Actor creado")
}

class Supervisor extends Actor {
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
 
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }
 
  def receive = {
    case p: Props => sender() ! context.actorOf(p, name="Hijo")
  }
}



object Application extends App {
	import scala.util.{Success, Failure}
	implicit val timeout = Timeout(5 seconds)
	val system = ActorSystem("helloworldsystem")
	val actor = system.actorOf(Props[HelloWorldActor], name = "HelloWorldActor")
	 for(i <- 1 to 4)
	     actor ! s"Devcon $i"

	val supervisor = system.actorOf(Props[Supervisor], name = "Supervisor")

	val future = supervisor ? Props[HelloWorldActor]

	future onComplete {
  		case Success(hijo:ActorRef) => {

  			println("Interactuando con el hijo")
  			println(hijo)
  			hijo ! new ArithmeticException
			hijo ! new NullPointerException
			hijo ! new IllegalArgumentException
			hijo ! new Exception("RECORCHOLIS")
  		}
  		case Failure(t) => println("Upps: " + t.getMessage)
	}

	
}