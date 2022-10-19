package lambdaweb4s.models

case class Path(segments: List[String]) {
  def /(segment: String): Path = {
    this.copy(segments = this.segments :+ segment)
  }
  override def toString: String = this.segments.mkString("/")
}

object Path {
  val delimiter = "/"
  val Root: Path = Path.empty
  def empty: Path = Path(List())
  def apply(seqString: String*): Path = Path(seqString.toList)
  def from(path: String): Path = {
    if (path == delimiter) Root
    else Path(path.stripPrefix("/").split(delimiter).toList)
  }
  def unapplySeq(path: Path): Option[List[String]] = Option(path.segments)
}

object / {
  def unapply(path: Path): Option[(Path, String)] = {
    path.segments match {
      case _ :: _ => Some(Path(path.segments.take(path.segments.size - 1)), path.segments.last)
      case _ => None
    }
  }
}
