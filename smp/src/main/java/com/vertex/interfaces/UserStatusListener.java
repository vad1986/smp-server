package com.vertex.interfaces;

import com.vertex.dataObjects.User;

public interface UserStatusListener {
    void onLogin(User paramUser);

    void onLogout(User paramUser);
}
