package jayn.actor.examples

import akka.actor._

case object GMessage
case object RMessage
case class IntMessage(i: Int)
case object StartMessage
case object StopMessage


class Generator(reciver: ActorRef) extends Actor {
  var count = 0
  def incrementAndPrint { count += 1; println(s"generator $count") }
  def receive = {
    case StartMessage =>
        while(count <= 10){
          incrementAndPrint
          reciver ! IntMessage(count)
        }
        reciver ! StopMessage
        println("Generator stopped")
        context.stop(self)
  }
}

class Reciever extends Actor {
  var counter = 0
  
  def receive = {
    case IntMessage(c) =>
        println(s"  reciver: $c")
        Thread.sleep(1000)
    case StopMessage =>
        println("reciver stopped")
        context.stop(self)
        context.system.terminate()
  }
}

object ActorTest{
  def main(args: Array[String]){
    val system = ActorSystem("PingPongSystem")
    val rec = system.actorOf(Props[Reciever], name = "reciver")
    val gen = system.actorOf(Props(new Generator(rec)), name = "generator")
    // start them going
    gen ! StartMessage
  }
}