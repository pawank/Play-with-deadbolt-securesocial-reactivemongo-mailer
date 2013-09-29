package models

case class ContactUs(
                      fullName: String,
                      email: String,
                      phone: Option[String],
                      message: String)
