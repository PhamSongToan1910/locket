package com.example.locket_clone.utils.Constant;

public class Constant {
    public static class COLLECTION {
        public static final String SEND_FRIEND_REQUEST_COLLECTION = "send_friend_request";
        public static final String USER_COLLECTION = "user";
        public static final String NOTIFICATION_COLLECTION = "notification";
        public static final String ROLE_COLLECTION = "authorities";
        public static final String POST_COLLECTION = "post";
        public static final String REACTION_COLLECTION = "reaction";
        public static final String USER_FRIENDS_COLLECTION = "user_friends";
        public static final String REPORT_POSTS_COLLECTION = "report_posts";
        public static final String CONVERSATION_COLLECTION = "conversation";
        public static final String MESSAGE_COLLECTION = "message";
    }

    public static class ROLE {
        public static final String USER_ROLE = "user";
    }

    public static class TYPE_ADD_POST {
        public static final int PUBLIC = 1;
        public static final int PROTECTED = 2;
        public static final int ONLY_ME = 3;
    }

    public static class TYPE_USER {
        public static final int DELETED = 1;
        public static final int IS_NOT_DELETED = 0;
    }

    public static class TYPE_REACTION {
        public static final int TYM = 1;
        public static final int FIRE = 2;
        public static final int HAHA = 3;
        public static final int CRY = 4;
    }

    public static class TYPE_GET_POST {
        public static final int PUBLIC = 0;
        public static final int FRIEND_DETAIL = 1;
        public static final int ME = 2;
    }

    public static class API {
        public static final int ADD_REACTION = 1;
        public static final int REPORT_POST = 2;
        public static final int HIDE_POST = 3;
        public static final int DELETE_POST = 4;
    }
}
