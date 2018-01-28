package controllers

import javax.inject.Inject
import models._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._


class JsonController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JsonController._

  def list = Action { implicit request =>
    val u = Users.syntax("u")

    DB.readOnly { implicit session =>
      // ユーザのリストを取得
      val users = withSQL {
        select.from(Users as u).orderBy(u.id.asc)
      }.map(Users(u.resultName)).list.apply()

      // ユーザの一覧をJSONで返す
      Ok(Json.obj("users" -> users))
    }
  }

  def create = Action(parse.json) { implicit request =>
    request.body.validate[UserForm].map { form =>
      // OKの場合はユーザを登録
      DB.localTx { implicit session =>
        Users.create(form.name, form.companyId)
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
    }
  }

  def update = Action(parse.json) { implicit request =>
    request.body.validate[UserForm].map { form =>
      // OKの場合はユーザ情報を更新
      DB.localTx { implicit session =>
        Users.find(form.id.get).foreach { user =>
          Users.save(user.copy(name = form.name, companyId = form.companyId))
        }
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
    }
  }

  def remove(id: Int) = Action { implicit request =>
    DB.localTx { implicit session =>
      // ユーザを削除
      Users.find(id).foreach { user =>
        Users.destroy(user)
      }
      Ok(Json.obj("result" -> "success"))
    }
  }
}

object JsonController {
  case class UserForm(id: Option[Int], name: String, companyId: Option[Int])

  implicit val userFormReads: Reads[UserForm]  = Json.reads[UserForm]
  implicit val userFormWrites: Writes[Users] = Json.writes[Users]
}
