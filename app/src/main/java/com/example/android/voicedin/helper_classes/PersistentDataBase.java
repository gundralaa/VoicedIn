package com.example.android.voicedin.helper_classes;

import com.example.android.voicedin.User;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by abhin on 7/25/2018.
 */

public class PersistentDataBase {

    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<UUID> ids = new ArrayList<>();

    public static void initializeUsers(){
        users.add(new User("Virginia","",1, UUID.fromString("f83d2117-e055-416c-80eb-4db7d6e8797d")));
        users.add(new User("Sierra","",2,UUID.fromString("c8bf9a96-3dea-46b6-ab26-6ccd7abe0239")));
        users.add(new User("Bella","",3,UUID.fromString("d894afa4-fe93-42cb-85d3-b7514302dcf8")));
        users.add(new User("Abhi","",4,UUID.fromString("9ae33021-a13d-44dc-868a-92304acb6f89")));

        for(User user: users){
            ids.add(user.getVoiceID());
        }
    }

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static ArrayList<UUID> getIds() {
        return ids;
    }


}
