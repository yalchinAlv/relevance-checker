import java.util.ArrayList;
import java.util.List;

public class Post {

    private String user;
    private String caption;
    private String url;
    private List<Comment> comments;

    public Post(String user, String caption, String url, List<Comment> comments) {
        this.user = user;
        this.caption = caption;
        this.url = url;
        this.comments = comments;
    }

    public Post(String user, String caption, String url) {
        this.user = user;
        this.caption = caption;
        this.url = url;
        this.comments = new ArrayList<Comment>();
    }

    public Post() {
        this.comments = new ArrayList<Comment>();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Post{" +
                "user='" + user + '\'' +
                ", caption='" + caption + '\'' +
                ", url='" + url + '\'' +
                ", comments=" + comments +
                '}';
    }
}
