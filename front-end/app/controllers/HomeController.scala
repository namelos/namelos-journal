package controllers

import play.api.mvc._

import scala.concurrent.ExecutionContext

class HomeController()(implicit ec: ExecutionContext) extends Controller {
  def index = Action {
    Ok(views.html.index.render())
  }
}
