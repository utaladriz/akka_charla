package cl.exe


import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import java.io.File
import com.typesafe.config.ConfigFactory
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.routing.FromConfig
import scala.concurrent.duration._

class ActorCluster extends Actor with ActorLogging {
 
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }
  override def postStop(): Unit = cluster.unsubscribe(self)
 
  def receive = {
    case MemberUp(member) =>
      log.info("Miembro conectado: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Se detectó un miembro inalcanzable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Miembro eliminado: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignorar
  }
}

class Trabajador extends Actor {
	def receive = {
		case m : String => {
			println(m)
		}
	}
	println("Trabajador creado")
}

class Productor  extends Actor {

  import context.dispatcher
  context.system.scheduler.schedule(5 seconds, 5 seconds, self, "enviar")
  val router = context.actorOf(FromConfig.props(), name = "router")
  var contador : Int = 1

  def receive: Receive = {
    case "enviar" => {
    	println("Enviando")
    	router ! s"Mensaje $contador"
    	contador += 1
    }
  }

  println("Productor creado")

}


object Application extends App {
	val servidor = args(0)
	val rol = args(0).split("_")(0)
	println(s"Iniciando ${args(0)} rol $rol")
	val config =  ConfigFactory.parseString(s"""akka.cluster.roles = [ "$rol" ]""").withFallback(ConfigFactory.parseFile(new File("cluster.conf")).getConfig(args(0)))
	println(config)
	val system = ActorSystem("ClusterSystem",config)
	val actorCluster = system.actorOf(Props[ActorCluster], name = "ActorCluster")
	if (rol == "trabajador"){
		val trabajador = system.actorOf(Props[Trabajador], name="trabajador")
	    trabajador ! "Test"
	}
	else 
		system.actorOf(Props[Productor], name="productor")
}