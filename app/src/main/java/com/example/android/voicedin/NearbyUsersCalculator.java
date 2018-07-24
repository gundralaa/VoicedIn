package com.example.android.voicedin;

import java.util.*;

public class NearbyUsersCalculator {
    /*
    private ArrayList<User> nearbyUsers = new ArrayList<>();
    private final double proximityThreshold = 5;
    private final double earthRadius = 6371000; //in meters

    public ArrayList<User> getNearbyUsers(User user, ArrayList<User> allUsers){
        for(User otherUser : allUsers){
            if(calculateDistance(user.getLocation(), otherUser.getLocation()) < proximityThreshold){ //if the distance from user to other user is within the threshold, add to nearby list
                nearbyUsers.add(otherUser);
            }
        }
        return nearbyUsers;
    }

    public double calculateDistance(Location location1, Location location2){
        double distance = 0; //distance between location parameters
        double dLatitude = Math.toRadians(location2.getLatitude() - location1.getLatitude()); //difference in latitudes
        double dLongitude = Math.toRadians(location2.getLongitude() - location2.getLongitude()); //difference in longitudes
        double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) +
                Math.cos(Math.toRadians(location1.getLatitude())) * Math.cos(Math.toRadians(location2.getLatitude())) *
                Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance = (double)(earthRadius * c);
        return distance;
    }
    */

}
