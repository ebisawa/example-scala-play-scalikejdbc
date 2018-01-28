package models

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import java.time.{ZonedDateTime}


class SchemaVersionSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val sv = SchemaVersion.syntax("sv")

  behavior of "SchemaVersion"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = SchemaVersion.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = SchemaVersion.findBy(sqls.eq(sv.installedRank, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = SchemaVersion.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = SchemaVersion.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = SchemaVersion.findAllBy(sqls.eq(sv.installedRank, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = SchemaVersion.countBy(sqls.eq(sv.installedRank, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = SchemaVersion.create(installedRank = 123, description = "MyString", `type` = "MyString", script = "MyString", installedBy = "MyString", installedOn = null, executionTime = 123, success = false)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = SchemaVersion.findAll().head
    // TODO modify something
    val modified = entity
    val updated = SchemaVersion.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = SchemaVersion.findAll().head
    val deleted = SchemaVersion.destroy(entity)
    deleted should be(1)
    val shouldBeNone = SchemaVersion.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = SchemaVersion.findAll()
    entities.foreach(e => SchemaVersion.destroy(e))
    val batchInserted = SchemaVersion.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
