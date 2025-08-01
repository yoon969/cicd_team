package dev.mvc.replylike;

public class ReplyLikeVO {
    private int replylikeno;
    private int replyno;
    private int usersno;

    // ✅ 추가 필드: 좋아요 수 및 좋아요 여부
    private int likeCount; // 현재 댓글의 좋아요 총 개수
    private int liked;     // 현재 로그인한 사용자가 좋아요 눌렀는지 여부 (1 or 0)

    public int getReplylikeno() {
        return replylikeno;
    }
    public void setReplylikeno(int replylikeno) {
        this.replylikeno = replylikeno;
    }

    public int getReplyno() {
        return replyno;
    }
    public void setReplyno(int replyno) {
        this.replyno = replyno;
    }

    public int getUsersno() {
        return usersno;
    }
    public void setUsersno(int usersno) {
        this.usersno = usersno;
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
    public void setLiked(int liked) {
        this.liked = liked;
    }
}
