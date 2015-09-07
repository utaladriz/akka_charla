package cl.exe


import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification
import java.util.UUID

case class Message (uuid: UUID, body : String)
case class EventMessage (topic : String, message : Message)

class SubscriberActor extends Actor {
	def receive = {
		case mensaje : Message => println(s"Mensaje recibido: ${mensaje.uuid} ${mensaje.body}")
		case _ => println("Hemos recibido un mensaje")
	}
}

class DumbEventBus extends ActorEventBus with LookupClassification { 
	 type Event = EventMessage
  	 type Classifier=String

  	 override protected def classify(event: Event): Classifier={
    		event.topic
  	  }

  
  	override protected def publish(event: Event, subscriber: Subscriber): Unit={
    	subscriber ! event.message
  	}

  	override protected def mapSize: Int = 128
} 

object Application extends App {
	val system = ActorSystem("events")
	val eventbus = new DumbEventBus
	val s1 = system.actorOf(Props[SubscriberActor], name = "s1")
	val s2 = system.actorOf(Props[SubscriberActor], name = "s2")
	val TOPIC="topic"
	eventbus.subscribe(s1, TOPIC)
	eventbus.subscribe(s2, TOPIC)
	eventbus.publish(EventMessage(TOPIC, Message(UUID.randomUUID(),"body foo")))
}
