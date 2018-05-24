package com.convrt.view;

public class View {

    public interface Post extends BaseView { }
    public interface PostWithReviews extends Post {}
    public interface PostWithUser extends Post {}

    public interface BaseView { }
    // interface PostWithContent extends Post { }

}
