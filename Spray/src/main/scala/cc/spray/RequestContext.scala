package cc.spray

import http._

case class RequestContext(request: HttpRequest, responder: RoutingResult => Unit, unmatchedPath: String) {
  
  def withRequestTransformed(f: HttpRequest => HttpRequest): RequestContext = {
    copy(request = f(request))
  }

  def withHttpResponseTransformed(f: HttpResponse => HttpResponse): RequestContext = {
    withRoutingResultTransformed {
      _ match {
        case Right(response) => Right(f(response))
        case x@ Left(_) => x
      }
    }
  }
  
  def withRoutingResultTransformed(f: RoutingResult => RoutingResult): RequestContext = {
    withResponder { rr => responder(f(rr)) }
  }

  def withResponder(newResponder: RoutingResult => Unit) = copy(responder = newResponder)
  
  def respond(content: HttpContent) { respond(HttpResponse(content = content)) }

  def respond(response: HttpResponse) { respond(Right(response)) }
  
  def respond(rr: RoutingResult) { responder(rr) }
  
  def reject(rejections: Rejection*) { respond(Left(Set(rejections: _*))) }
  
  // can be cached
  def fail(failure: HttpFailure, reason: String = "") {
    respond(HttpResponse(HttpStatus(failure, reason)))
  }
}

object RequestContext {
  def apply(request: HttpRequest, responder: RoutingResult => Unit = { _ => }): RequestContext = {
    apply(request, responder, request.path)
  }
}