package lets.start

import akka.actor.{Actor, ActorSystem, Props}

// Actor message
case class WhoToGreet(who: String)

//Define Greeter Actor
class Greeter extends Actor{
  def receive: Receive = {
    case WhoToGreet(who) => println(s"Hello $who!!!!")
  }
}

object HelloAkkaScala extends App {

  //Create actor system
  val actorSystem = ActorSystem("Hello-actor-system")

  //Create greeter actor
  val greeter =  actorSystem.actorOf(Props[Greeter], "greeter")

  //Send WhoToGreet message to actor through tell
  greeter ! WhoToGreet("Akka")
}
