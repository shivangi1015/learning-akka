package lets.start.ActorCreation

import akka.actor.{Actor, ActorSystem, Props}
import lets.start.ActorCreation.MusicController.{Play, Stop}
import lets.start.ActorCreation.MusicPlayer.{StartMusic, StopMusic}

//Music controller message
object MusicController {
  sealed trait ControllerMessage
  case object Play extends ControllerMessage
  case object Stop extends ControllerMessage

  val props = Props[MusicController]
}

//Music Controller Actor
class MusicController extends Actor{
  def receive: Receive = {
    case Play => println("Music started...")
    case Stop => println("Music stopped...")
  }
}

//Music Player Messages
object MusicPlayer {
  sealed trait PlayMessage
  case object StopMusic extends PlayMessage
  case object StartMusic extends PlayMessage
}

//Music Player Actor
class MusicPlayer extends Actor {
  def receive: Receive = {
    case StopMusic => println("I don't want to stop music")
    case StartMusic =>
      val controller = context.actorOf(MusicController.props, "controller")
      controller ! Play
    case _ => println("Unknown message")
  }
}

object Creation extends App {

  //Create creation actor system
  val system = ActorSystem("creation")

  //Create MusicPlayer actor
  val player = system.actorOf(Props[MusicPlayer], "player")

  //Send StartMusic message to the actor
  player ! StartMusic

  //Send StopMusic message to the actor
  player ! StopMusic
}
