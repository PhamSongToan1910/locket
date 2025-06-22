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
        public static final String UNREAD_POST_COLLECTION = "unread_post";
        public static final String LAST_MESSAGE_COLLECTION = "last_message";
        public static final String DEVICE_COLLECTION = "device";
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

    public static class TYPE_OF_NOTIFICATION {
        public static final int REMOVE_POST_BY_ADMIN = 1;
    }

    public static class API {
        //post
        public static final int ADD_REACTION = 1;
        public static final int REPORT_POST = 2;
        public static final int HIDE_POST = 3;
        public static final int DELETE_POST = 4;
        public static final int ADD_POST_TO_UNREAD_POST = 5;
        public static final int CHANGE_UNREAD_POST_STATUS = 6;
        public static final int ADD_NOTIFICATION_NEW_POST = 7;
        public static final int DELETE_POST_BY_ADMIN = 12;

        //user
        public static final int UPDATE_DEVICE_TOKEN = 8;
        public static final int LOGOUT = 9;

        //Message
        public static final int UPLOAD_LAST_MESSAGE = 10;
        public static final int UPLOAD_MESSAGE = 11;
        public static final int UPDATE_UNREAD_MESSAGE = 14;
        public static final int UPDATE_LAST_MESSAGE = 15;

        //Notification
        public static final int SAVE_NOTIFICATION = 13;

        //backend
        public static final int UPDATE_POST_BY_ADMIN = 16;
        public static final int UPDATE_REPORT_POST_BY_ADMIN = 17;

    }

    public static class STATUS_REPORT_POST {
        public static final int SKIP = 1;
        public static final int PENDING = 2;
        public static final int DELETE = 3;
    }
}
