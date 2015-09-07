package cl.exe


import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class HelloWorldActor extends Actor {
	def receive = {
		case mensaje : String => println(s"Mensaje recibido: $mensaje")
		case _ => println("Hemos recibido un mensaje")
	}
}

object Application extends App {
	val system = ActorSystem("helloworldsystem")
	val actor = system.actorOf(Props[HelloWorldActor], name = "HelloWorldActor")
	actor ! "Devcon"
}
