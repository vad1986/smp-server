package com.vertex.dataObjects;

import com.vertex.interfaces.UserStatusListener;
import java.util.ArrayList;
import java.util.HashMap;

public class UsersCollection {
    private static HashMap<String, User> loggedInUsers = new HashMap<>();

    private static ArrayList<UserStatusListener> listeners = new ArrayList<>();

    public static void addUserStatusListener(UserStatusListener listener) {
        listeners.add(listener);
    }

    public static void logInUser(User user) {
        loggedInUsers.put(user.getUserName(), user);
        notifyOnLogin(user);
    }

    private static void notifyOnLogin(User user) {
        listeners.forEach(listener -> listener.onLogin(user));
    }

    private static void notifyOnLogout(User user) {
        listeners.forEach(listener -> listener.onLogout(user));
    }

    public static void logOutUser(User user) {
        loggedInUsers.remove(user);
        notifyOnLogout(user);
    }

    public static boolean logOutUser(String userName, String key) {
        User user = userPrivateKeyMatch(userName, key);
        if (user != null) {
            loggedInUsers.remove(userName);
            notifyOnLogout(user);
            return true;
        }
        return false;
    }

    public static boolean isUserLoggedIn(User user) {
        return loggedInUsers.containsValue(user);
    }

    public static boolean isUserLoggedIn(String userName) {
        return loggedInUsers.containsKey(userName);
    }

    public static User getUserByName(String userName) {
        return loggedInUsers.get(userName);
    }

    public static User userPrivateKeyMatch(String userName, String key) {
        if (((User)loggedInUsers.get(userName)).getPrivateKey().equals(key))
            return loggedInUsers.get(userName);
        return null;
    }

    public static boolean userPermittedForAction(String userName, String key, String uri) {
        User user = loggedInUsers.get(userName);
        switch (uri) {
            case "/manager/new_task":
                if (user.getUserRole() >= 1)
                    return true;
                return false;
            case "/manager/new_user":
                if (user.getUserRole() == 4)
                    return true;
                return false;
            case "/manager/get_open_tasks":
                if (user.getUserRole() >= 1)
                    return true;
                return false;
            case "/manager/addShopLocation":
                if (user.getUserRole() == 4)
                    return true;
                return false;
            case "/manager/punchClock":
                if (user.getUserRole() >= 1)
                    return true;
                return false;
            case "manager/user-attendance/:id":
                if (user.getUserRole() >= 1)
                    return true;
                return false;
        }
        return false;
    }
}
