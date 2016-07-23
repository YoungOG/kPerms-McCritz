package com.mccritz.kperms.profiles;

public interface ProfileLoader {

    ProfileLoader onComplete(ProfileRequest<Profile> callback);
}
