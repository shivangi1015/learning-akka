package lets.start.talkingtoactor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import lets.start.talkingtoactor.Checker.{BlackUser, CheckUser, WhiteUser}
import lets.start.talkingtoactor.Recorder.NewUser
import lets.start.talkingtoactor.Storage.AddUser
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

case class User(name: String, email: String)

object Recorder {

  sealed trait RecorderMessage

  //Recorder message
  case class NewUser(user: User) extends RecorderMessage

  def props(checker: ActorRef, storage: ActorRef): Props = {
    Props(new Recorder(checker, storage))
  }
}

object Checker {

  sealed trait CheckerMessage

  //Checker Message
  case class CheckUser(user: User) extends CheckerMessage

  sealed trait CheckerResponse

  //Checker response
  case class BlackUser(user: User) extends CheckerMessage

  case class WhiteUser(user: User) extends CheckerMessage

}

object Storage {

  sealed trait StorageMessage

  //Storage message
  case class  AddUser(user: User) extends StorageMessage

}

class Storage extends Actor {

  var users = List.empty[User]

  def receive: Receive = {
    case AddUser(user) =>
      println(s"User Added: $user")
      users = user :: users
  }
}

class Checker extends Actor {

  val blackList = List("shiv")

  println("black list")
  def receive: Receive = {
    case CheckUser(user) if blackList.contains(user.name) => println("res: " + blackList.contains(user.name))
      println(s"User: $user in blackList")
      sender() ! BlackUser

    case CheckUser(user) => println("res: " + blackList.contains(user.name))
      println(s"User: $user not in blacklist")
      sender() ! WhiteUser
  }
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout: Timeout = Timeout(5 seconds)
  def receive: Receive = {
    case NewUser(u) => println("user:: " + u)
      checker ? CheckUser map {
        case WhiteUser(user) =>
          storage ! AddUser(user)
        case BlackUser(user) =>
          println(s"User $user in black list")
      }
  }
}

object TalkToActor extends App {

  //Create "talk-to-actor" actor system
  val system = ActorSystem("talk-to-actor")

  //Create checker Actor
  val checker = system.actorOf(Props[Checker], "checker")

  //Create Storage Actor
  val storage = system.actorOf(Props[Storage], "storage")

  //Create Recorder Actor
  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  //Send NewUser Message to Recorder
  recorder ! Recorder.NewUser(User("abc", "abc@gmail.com"))
  Thread.sleep(100)
  system.terminate()
}
