package com.nerderylabs.android.nerdalert;

public class Constants {

    //Request code to use when launching the resolution activity.
    public static final int REQUEST_RESOLVE_ERROR = 1001;

    // The time-to-live when subscribing or publishing in this sample. Three minutes.
    public static final int TTL_IN_SECONDS = 3 * 60;

    public static final int TTL_IN_MILLISECONDS = TTL_IN_SECONDS * 1000;

    // Keys to get and set the current subscription and publication tasks using SharedPreferences.
    public static final String KEY_SUBSCRIPTION_TASK = "subscription_task";

    public static final String KEY_PUBLICATION_TASK = "publication_task";
    
    // Constants for subscription and publication tasks.
    public static final String TASK_SUBSCRIBE = "task_subscribe";

    public static final String TASK_UNSUBSCRIBE = "task_unsubscribe";

    public static final String TASK_PUBLISH = "task_publish";

    public static final String TASK_UNPUBLISH = "task_unpublish";

    public static final String TASK_NONE = "task_none";

    // Values used in setting state after subscribing or publishing.
    public static final int NO_LONGER_SUBSCRIBING = 12345;

    public static final int NO_LONGER_PUBLISHING = 56789;

}
