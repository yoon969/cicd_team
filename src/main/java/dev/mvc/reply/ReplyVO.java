package dev.mvc.reply;

public class ReplyVO {
  private int replyno;
  private String content;
  private String rdate;
  private int usersno;
  private int postno;
  private String parentWriter; 

  private int parentno; // ✅ 부모 댓글 번호 (0 또는 자기 자신이면 일반 댓글)

  private String usersname; // JOIN용
  private int likeCount;
  private int liked;

  public ReplyVO() {}

  public int getReplyno() {
    return replyno;
  }

  public void setReplyno(int replyno) {
    this.replyno = replyno;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getRdate() {
    return rdate;
  }

  public void setRdate(String rdate) {
    this.rdate = rdate;
  }

  public int getUsersno() {
    return usersno;
  }

  public void setUsersno(int usersno) {
    this.usersno = usersno;
  }

  public int getPostno() {
    return postno;
  }

  public void setPostno(int postno) {
    this.postno = postno;
  }

  public int getParentno() {
    return parentno;
  }

  public void setParentno(int parentno) {
    this.parentno = parentno;
  }

  public String getUsersname() {
    return usersname;
  }

  public void setUsersname(String usersname) {
    this.usersname = usersname;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public int getLiked() {
    return liked;
  }

  public boolean isLiked() {
    return liked == 1;
  }

  public void setLiked(int liked) {
    this.liked = liked;
  }

  public String getParentWriter() {
    return parentWriter;
  }

  public void setParentWriter(String parentWriter) {
    this.parentWriter = parentWriter;
  }

  @Override
  public String toString() {
    return "ReplyVO [replyno=" + replyno + ", content=" + content + ", rdate=" + rdate +
           ", usersno=" + usersno + ", postno=" + postno + ", parentno=" + parentno +
           ", usersname=" + usersname + ", likeCount=" + likeCount + ", liked=" + liked + "]";
  }
}
