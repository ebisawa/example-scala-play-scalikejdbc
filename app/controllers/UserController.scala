package controllers

import javax.inject.Inject
import models._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport}
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc._


class UserController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc)
  with I18nSupport {

  import UserController._

  /**
    * 一覧表示
    */
  def list = Action { implicit request =>
    val u = Users.syntax("u")

    DB.readOnly { implicit session =>
      // ユーザのリストを取得
      val users = withSQL {
        select.from(Users as u).orderBy(u.id.asc)
      }.map(Users(u.resultName)).list.apply()

      // 一覧画面を表示
      Ok(views.html.user.list(users))
    }
  }

  /**
    * 編集画面表示
    */

  def edit(id: Option[Int]) = Action { implicit request =>
    val c = Companies.syntax("c")

    DB.readOnly { implicit session =>
      // リクエストパラメータにIDが存在する場合
      val form = id match {
        // IDが渡されなかった場合は新規登録フォーム
        case None => userForm
        // IDからユーザ情報を1件取得してフォームに詰める
        case Some(id) => {
          val user = Users.find(id).get
          userForm.fill(UserForm(Some(user.id), user.name, user.companyId))
        }
      }

      // プルダウンに表示する会社のリストを取得
      val companies = withSQL {
        select.from(Companies as c).orderBy(c.id.asc)
      }.map(Companies(c.resultName)).list().apply()

      Ok(views.html.user.edit(form, companies))
    }
  }

  /**
    * 登録実行
    */
  def create = Action { implicit request =>
    DB.localTx { implicit session =>
      // リクエストの内容をバインド
      userForm.bindFromRequest.fold(
        // エラーの場合
        error => {
          BadRequest(views.html.user.edit(error, Companies.findAll()))
        },
        // OKの場合
        form  => {
          // ユーザを登録
          Users.create(form.name, form.companyId)
          // 一覧画面へリダイレクト
          Redirect(routes.UserController.list)
        }
      )
    }
  }

  /**
    * 更新実行
    */
  def update = Action { implicit request =>
    DB.localTx { implicit session =>
      // リクエストの内容をバインド
      userForm.bindFromRequest.fold(
        // エラーの場合は登録画面に戻す
        error => {
          BadRequest(views.html.user.edit(error, Companies.findAll()))
        },
        // OKの場合は登録を行い一覧画面にリダイレクトする
        form => {
          // ユーザ情報を更新
          Users.find(form.id.get).foreach { user =>
            Users.save(user.copy(name = form.name, companyId = form.companyId))
          }
          // 一覧画面にリダイレクト
          Redirect(routes.UserController.list)
        }
      )
    }
  }

  /**
    * 削除実行
    */
  def remove(id: Int) = Action { implicit request =>
    DB.localTx { implicit session =>
      // ユーザを削除
      Users.find(id).foreach { user =>
        Users.destroy(user)
      }
      // 一覧画面へリダイレクト
      Redirect(routes.UserController.list)
    }
  }
}

object UserController {
  // フォームの値を格納するケースクラス
  case class UserForm(id: Option[Int], name: String, companyId: Option[Int])

  // formから送信されたデータ ⇔ ケースクラスの変換を行う
  val userForm = Form(
    mapping(
      "id"        -> optional(number),
      "name"      -> nonEmptyText(maxLength = 20),
      "companyId" -> optional(number)
    )(UserForm.apply)(UserForm.unapply)
  )
}
