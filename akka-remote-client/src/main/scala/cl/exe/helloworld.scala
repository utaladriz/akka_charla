package cl.exe


import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props


object Application extends App {
	val system = ActorSystem("Local")
  	val remote = system.actorFor("akka.tcp://helloworldsystem@127.0.0.1:2552/user/HelloWorldActor")
  	remote ! "Desde el cliente"
}