package cl.exe


import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.{RoundRobinPool, RoundRobinRouter}

import akka.actor.{ Address, AddressFromURIString }
import akka.remote.routing.RemoteRouterConfig


object Application extends App {
	val system = ActorSystem()
	val remote = system.actorOf(Props.empty.withRouter(
    		RoundRobinRouter(routees = Vector("akka.tcp://helloworldsystem@127.0.0.1:2552/user/HelloWorldActor",
    										  "akka.tcp://helloworldsystem@127.0.0.1:2553/user/HelloWorldActor")
    		)))

    1 to 10 foreach {i=> remote ! s"Desde el cliente $i"}
   	
}