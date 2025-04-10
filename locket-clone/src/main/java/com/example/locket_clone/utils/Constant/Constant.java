package com.example.locket_clone.utils.Constant;

public class Constant {
    public static class COLLECTION {
        public static final String SEND_FRIEND_REQUEST_COLLECTION = "send_friend_request";
        public static final String FOLLOW_COLLECTION = "follow";
        public static final String USER_COLLECTION = "user";
        public static final String NOTIFICATION_COLLECTION = "notification";
        public static final String ROLE_COLLECTION = "authorities";
        public static final String RESOURCE_COLLECTION = "resource";
        public static final String POST_COLLECTION = "post";
        public static final String REACTION_COLLECTION = "reaction";
        public static final String COMMENT_COLLECTION = "comment";
        public static final String USER_FRIENDS_COLLECTION = "user_friends";

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
}
